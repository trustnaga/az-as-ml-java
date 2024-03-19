package com.example.restservice.greeting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.DefaultAzureCredential;
import com.fasterxml.jackson.databind.ser.std.StdKeySerializers.Default;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;

import jakarta.validation.Valid;

import java.net.URL;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;

@Service
public class FontDoorService {

    @Value("${subscriptionId}")
    private String subscriptionId;
    @Value("${resourceGroupName}")
    private String resourceGroupName;
    @Value("${frontDoorName}")
    private String frontDoorName;
    @Value("${frontendEndpointName}")
    private String frontendEndpointName;

    @Autowired
    private AzureTokenService azureTokenService;
    
    //create a method to call the GET https://management.azure.com/subscriptions/{subscriptionId}/resourceGroups/{resourceGroupName}/providers/Microsoft.Network/frontDoors/{frontDoorName}/frontendEndpoints/{frontendEndpointName}?api-version=2019-05-01
    public String getFrontDoorFrontendEndpoint() throws IOException {
        URL url = new URL("https://management.azure.com/subscriptions/"+subscriptionId+
            "/resourceGroups/"+resourceGroupName+
            "/providers/Microsoft.Cdn/profiles/"+frontDoorName+
            "/afdEndpoints/"+frontendEndpointName+"/purge?api-version=2023-05-01");
            //https://management.azure.com/subscriptions/d0b01c3c-d30a-40c7-b269-c8b09f48ff23/resourceGroups/frontdoor-rg/providers/Microsoft.Cdn/profiles
            // /frontdoor-demo/afdEndpoints/fddemo/purge?api-version=2023-05-01
        System.out.println("**********URL: " + url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");

        //System.out.printf("**********Token code: " + azureTokenService.getAccessToken());
       conn.setRequestProperty("Authorization", "Bearer " + azureTokenService.getAccessToken());
        conn.setRequestProperty("Accept","application/json");
        conn.setRequestProperty("Content-Type", "application/json");

        conn.setDoOutput(true);
        String jsonInputString = "{\"contentPaths\":[\"/*\"],\"domains\":[\"fddemo-dnbacydwfufcbpbn.b02.azurefd.net\"]}";
        System.out.println("**********reqest: " + jsonInputString);
        BufferedWriter out = 
            new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
        out.write(jsonInputString);
        out.close();
        int httpResponseCode = conn.getResponseCode();
         if(httpResponseCode == 202) {

            StringBuilder response;
            try(BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()))){

                String inputLine;
                response = new StringBuilder();
                while (( inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }
            String asyncOpString = conn.getHeaderField("Azure-AsyncOperation");
            
            System.out.println("**********Response: " + asyncOpString);
            conn.disconnect();
            return asyncOpString;
        } else {
            System.err.println("**********Connection returned HTTP code: " + httpResponseCode + " with message: " + conn.getResponseMessage());
            return String.format("Connection returned HTTP code: %s with message: %s",
                    httpResponseCode, conn.getResponseMessage());
        }

    }
}
