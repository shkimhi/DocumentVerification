package com.sh.documentverification.controller;

import com.sh.documentverification.services.HashService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comparison")
public class DocumentController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final HashService hashService;
    @GetMapping("/upload")
    public ResponseEntity<?> fileHashComparison(@RequestParam("file") MultipartFile file) throws IOException {
        try {
            String fileHash = hashService.calculateSHA256Hash(file.getInputStream());

            return ResponseEntity.ok("ok");
        }catch(Exception e) {
            String errorMessage = "error";
            logger.error(errorMessage);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }
}
