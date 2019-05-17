package com.example.tutor.fenix;

import com.google.gson.JsonObject;
import org.fenixedu.sdk.ApplicationConfiguration;
import org.fenixedu.sdk.FenixEduClientImpl;
import org.fenixedu.sdk.FenixEduUserDetails;

public class FenixRequests {
    FenixRequests() {
        String code = "";
        // create the client from properties file
        ApplicationConfiguration config = ApplicationConfiguration.fromPropertyFilename("/fenixedu.properties");
        FenixEduClientImpl client = new FenixEduClientImpl(config);

        //you can invoke public endpoints without any access token.
        JsonObject about = client.getAbout();

        //to access user's data, you must redirect the user to the URL provided by client.getAuthenticationUrl();
        //if the user accepts it, the FenixEdu API will invoke the defined callback url passing a query param named code.
        //e.g. http://localhost:8080/authorization?code=<authorization-code>

        //get user's authorization data (access_token and refresh_token) client.
        FenixEduUserDetails userDetails = client.getUserDetailsFromCode(code);

        //when requesting user's private data, the authorization object must be passed along.
        JsonObject person = client.getPerson(userDetails.getAuthorization());
    }
}
