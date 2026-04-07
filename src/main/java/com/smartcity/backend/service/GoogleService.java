package com.smartcity.backend.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class GoogleService {

    // 🔥 PUT YOUR REAL CLIENT ID HERE
    private static final String CLIENT_ID = "YOUR_GOOGLE_CLIENT_ID";

    private final GoogleIdTokenVerifier verifier;

    public GoogleService() {
        this.verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                JacksonFactory.getDefaultInstance()   // ✅ FIXED
        )
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();
    }

    public GoogleIdToken.Payload verifyToken(String tokenString) {

        try {
            GoogleIdToken idToken = verifier.verify(tokenString);

            if (idToken != null) {
                return idToken.getPayload();
            }

            return null; // ✅ better than throwing exception

        } catch (Exception e) {
            e.printStackTrace();
            return null; // ✅ safe handling
        }
    }
}