package codeu.utils;

import com.google.appengine.tools.cloudstorage.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;

@MultipartConfig(
  maxFileSize = 10 * 1024 * 1024, // max size for uploaded files
  maxRequestSize = 20 * 1024 * 1024, // max size for multipart/form-data
  fileSizeThreshold = 5 * 1024 * 1024 // start writing to Cloud Storage after 5MB
)

public class ImageStorage {

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
   * UploadedFilename() extracts the filename from the HTTP headers and appends a timestamp to
   * create a unique filename.
   */
  public String uploadedFilename(final Part part) {

    for (String content : part.getHeader("content-disposition").split(";")) {
      if (content.trim().startsWith("filename")) {
        DateTimeFormatter dtf = DateTimeFormat.forPattern("-YYYY-MM-dd-HHmmssSSS");
        DateTime dt = DateTime.now(DateTimeZone.UTC);
        String dtString = dt.toString(dtf);
        final String fileName =
            dtString + content.substring(content.indexOf('=') + 1).trim().replace("\"", "");

        return fileName;
      }
    }
    return null;
  }

  /** The storeImage() method writes to Cloud Storage using copy */
  public void copy(InputStream input, OutputStream output) throws IOException {
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
  public String storeImage(Part image) throws IOException {
    String filename = uploadedFilename(image);
    GcsFileOptions.Builder builder = new GcsFileOptions.Builder();

    builder.acl("public-read");
    GcsFileOptions instance = GcsFileOptions.getDefaultInstance();
    GcsOutputChannel outputChannel;
    GcsFilename gcsFile = new GcsFilename(bucket, filename);
    outputChannel = gcsService.createOrReplace(gcsFile, instance);
    copy(image.getInputStream(), Channels.newOutputStream(outputChannel));

    System.out.println("FILENAME" + filename);
    return filename; // Return the filename without GCS/bucket appendage
  }
}
