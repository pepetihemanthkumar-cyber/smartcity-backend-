package com.smartcity.backend.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class GoogleService {

    // ✅ YOUR CLIENT ID
    private static final String CLIENT_ID =
            "1072154065399-po1jasqhc10lmulmn38umcc13vi4eooi.apps.googleusercontent.com";

    private final GoogleIdTokenVerifier verifier;

    public GoogleService() {

        this.verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                JacksonFactory.getDefaultInstance()
        )
        .setAudience(Collections.singletonList(CLIENT_ID)) // 🔥 must match frontend
        .build();
    }

    public GoogleIdToken.Payload verifyToken(String tokenString) {

        try {

            System.out.println("🔍 Verifying Google Token...");
            System.out.println("📦 Token: " + tokenString);

            if (tokenString == null || tokenString.isEmpty()) {
                System.out.println("❌ Token is NULL or EMPTY");
                return null;
            }

            GoogleIdToken idToken = verifier.verify(tokenString);

            if (idToken == null) {
                System.out.println("❌ Invalid Google Token (verification failed)");
                return null;
            }

            GoogleIdToken.Payload payload = idToken.getPayload();

            // ✅ EXTRA SECURITY CHECK
            if (!payload.getEmailVerified()) {
                System.out.println("❌ Email not verified by Google");
                return null;
            }

            // ✅ LOG DETAILS
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String picture = (String) payload.get("picture");

            System.out.println("✅ GOOGLE LOGIN SUCCESS");
            System.out.println("📧 Email: " + email);
            System.out.println("👤 Name: " + name);
            System.out.println("🖼 Picture: " + picture);

            return payload;

        } catch (Exception e) {
            System.out.println("❌ Google Token Verification Error");
            e.printStackTrace();
            return null;
        }
    }
}