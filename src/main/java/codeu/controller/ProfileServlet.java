package codeu.controller;

import codeu.model.data.Activity;
import codeu.model.data.User;
import codeu.model.store.basic.ActivityStore;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.MessageStore;
import codeu.model.store.basic.UserStore;
import java.io.IOException;
import java.util.List;
import java.util.Collection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.MultipartConfig;
import java.nio.channels.Channels;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.http.Part;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;

@MultipartConfig(
  maxFileSize = 10 * 1024 * 1024, // max size for uploaded files
  maxRequestSize = 20 * 1024 * 1024, // max size for multipart/form-data
  fileSizeThreshold = 5 * 1024 * 1024 // start writing to Cloud Storage after 5MB
)
/** Servlet class responsible for the profile page. */
public class ProfileServlet extends HttpServlet {

  /** Store class that gives access to Conversations. */
  private ConversationStore conversationStore;

  /** Store class that gives access to Messages. */
  private MessageStore messageStore;

  /** Store class that gives access to Users. */
  private UserStore userStore;

  private ActivityStore activityStore;

  /** Set up state for handling profile requests. */
  @Override
  public void init() throws ServletException {
    super.init();
    setConversationStore(ConversationStore.getInstance());
    setMessageStore(MessageStore.getInstance());
    setUserStore(UserStore.getInstance());
    setActivityStore(ActivityStore.getInstance());
  }

  private final GcsService gcsService =
      GcsServiceFactory.createGcsService(
          new RetryParams.Builder()
              .initialRetryDelayMillis(10)
              .retryMaxAttempts(10)
              .totalRetryPeriodMillis(15000)
              .build());

  private static final int BUFFER_SIZE = 2 * 1024 * 1024;
  private final String bucket = "chatu-196017.appspot.com";

  /**
   * Sets the ConversationStore used by this servlet. This function provides a common setup method
   * for use by the test framework or the servlet's init() function.
   */
  void setConversationStore(ConversationStore conversationStore) {
    this.conversationStore = conversationStore;
  }

  /**
   * Sets the MessageStore used by this servlet. This function provides a common setup method for
   * use by the test framework or the servlet's init() function.
   */
  void setMessageStore(MessageStore messageStore) {
    this.messageStore = messageStore;
  }

  /**
   * Sets the UserStore used by this servlet. This function provides a common setup method for use
   * by the test framework or the servlet's init() function.
   */
  void setUserStore(UserStore userStore) {
    this.userStore = userStore;
  }

  void setActivityStore(ActivityStore activityStore) {
    this.activityStore = activityStore;
  }

  /**
   * This function fires when a user navigates to a user's profile page. It gets the username from
   * the URL, finds the corresponding User, and fetches the messages posted by that user. It then
   * forwards to profile.jsp for rendering.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    String requestUrl = request.getRequestURI();
    String name = requestUrl.substring("/profile/".length());
    User loggedInUser = userStore.getUser((String) request.getSession().getAttribute("user"));
    User user = userStore.getUser(name);
    List<Activity> activities = null;
    List<Activity> activitiesPermitted;
    if (user != null) {
      activitiesPermitted =
          loggedInUser == null
              ? activityStore.getAllPublicActivitiesWithUserIdSorted(user.getId())
              : activityStore.getAllPermittedActivitiesWithUserIdSorted(
                  user.getId(), loggedInUser.getId());
      activities = activityStore.getActivitiesPerPrivacy(user, activitiesPermitted);
    }
    
    request.setAttribute("activities", activities);
    request.setAttribute("user", user);
    request.setAttribute("loggedInUser", loggedInUser);
    request.getRequestDispatcher("/WEB-INF/view/profile.jsp").forward(request, response);
  }

  /** This function fires when a user submits the form on the profile page. */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    String requestUrl = request.getRequestURI();
    String name = requestUrl.substring("/profile/".length());
    User user = userStore.getUser(name);

    if (request.getParameter("submitBiography") != null) {
      user.setBio(request.getParameter("newBio"));
    }
    if (request.getParameter("submitProfilePic") != null) {
      Collection<Part> parts = request.getParts();
      Part image = parts.iterator().next();
      System.out.println("PART" + image);
      String fileName = storeImage(image);
      user.setProfilePicture(fileName);
    }
    response.sendRedirect(requestUrl);
  }

  /**
   * UploadedFilename() extracts the filename from the HTTP headers and appends a timestamp to
   * create a unique filename.
   */
  private String uploadedFilename(final Part part) {

    final String partHeader = part.getHeader("content-disposition");

    for (String content : part.getHeader("content-disposition").split(";")) {
      if (content.trim().startsWith("filename")) {
        // Append a date and time to the filename
        DateTimeFormatter dtf = DateTimeFormat.forPattern("-YYYY-MM-dd-HHmmssSSS");
        DateTime dt = DateTime.now(DateTimeZone.UTC);
        String dtString = dt.toString(dtf);
        final String fileName =
            dtString + content.substring(content.indexOf('=') + 1).trim().replace("\"", "");

        System.out.println("FILENAME" + fileName);
        return fileName;
      }
    }
    return null;
  }

  /** The storeImage() method writes to Cloud Storage using copy */
  private void copy(InputStream input, OutputStream output) throws IOException {

    try {
      byte[] buffer = new byte[BUFFER_SIZE];
      int bytesRead = input.read(buffer);
      while (bytesRead != -1) {
        output.write(buffer, 0, bytesRead);
        bytesRead = input.read(buffer);
      }
    } finally {
      input.close();
      output.close();
    }
  }

  /**
   * The storeImage() method sets the file permissions to public-read to make it publicly visible.
   * The image is written to Cloud Storage using the copy() function from the App Engine Tools
   * library.
   */
  private String storeImage(Part image) throws IOException {

    String filename = uploadedFilename(image); // Extract filename
    GcsFileOptions.Builder builder = new GcsFileOptions.Builder();

    builder.acl("public-read"); // Set the file to be publicly viewable
    GcsFileOptions instance = GcsFileOptions.getDefaultInstance();
    GcsOutputChannel outputChannel;
    GcsFilename gcsFile = new GcsFilename(bucket, filename);
    outputChannel = gcsService.createOrReplace(gcsFile, instance);
    // filePart
    copy(image.getInputStream(), Channels.newOutputStream(outputChannel));

    return filename; // Return the filename without GCS/bucket appendage
  }
}
