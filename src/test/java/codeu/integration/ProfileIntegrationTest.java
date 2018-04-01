package codeu.integration;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;
import java.io.IOException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class ProfileIntegrationTest {

  private final LocalServiceTestHelper appEngineTestHelper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  private ServletRunner servletRunner;
  private ServletUnitClient servletClient;

  @Before
  public void setup() throws IOException, SAXException {
    appEngineTestHelper.setUp();

    // Unfortunately, the ServletRunner constructors that take java.io.File (the
    // non-deprecated ones) have a bug, so we have to use the two argument string
    // form.
    //
    // See https://sourceforge.net/p/httpunit/mailman/message/27259892 for details.
    servletRunner = new ServletRunner("src/main/webapp/WEB-INF/web.xml", "");
    servletClient = servletRunner.newClient();
  }

  @After
  public void tearDown() {
    appEngineTestHelper.tearDown();
  }

  @Test
  public void testGetProfile_NoUser() throws IOException, SAXException {
    WebRequest request = new GetMethodWebRequest("http://dummy/profile/dummy_user");
    WebResponse response = servletClient.getResponse(request);

    Assert.assertTrue(
        "response doesn't contain \"Profile Not Found\"",
        response.getText().contains("Profile Not Found"));
  }
}
