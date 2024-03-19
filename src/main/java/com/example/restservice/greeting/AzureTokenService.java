package com.example.restservice.greeting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.azure.core.credential.TokenCredential;
import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;

@Service
public class AzureTokenService {

   
    public String getAccessToken() {
        try {
            DefaultAzureCredential tokenCredential=new DefaultAzureCredentialBuilder().build();
            TokenRequestContext requestContext = new TokenRequestContext().addScopes("https://management.azure.com/.default");
            return tokenCredential.getToken(requestContext).block().getToken();
        } catch (Exception e) {
            // Handle exceptions
            return null;
        }
    }
}
