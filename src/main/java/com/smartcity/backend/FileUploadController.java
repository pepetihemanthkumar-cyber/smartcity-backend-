package com.smartcity.backend;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class FileUploadController {

    private final UserRepository userRepository;

    // ✅ constructor injection
    public FileUploadController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // upload folder
    private static final String UPLOAD_DIR = "uploads/";

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("avatar") MultipartFile file,
            @RequestParam("username") String username   // 🔥 NEW
    ) {

        // check empty file
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file uploaded ❌");
        }

        try {
            // create uploads folder if not exists
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // unique file name
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            String filePath = UPLOAD_DIR + fileName;

            // save file
            file.transferTo(new File(filePath));

            // 🔥 SAVE TO DATABASE
            Optional<User> userOpt = userRepository.findByUsername(username);

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setAvatar("uploads/" + fileName);
                userRepository.save(user);
            }

            // correct URL
            String fileUrl = "http://localhost:8082/uploads/" + fileName;

            return ResponseEntity.ok(fileUrl);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Upload failed ❌");
        }
    }
}