package com.sh.documentverification.controller;

import com.sh.documentverification.services.DocToPdfService;
import com.sh.documentverification.services.HashService;
import com.sh.documentverification.services.LedgerService;
import com.sh.documentverification.services.SftpService;
import com.spire.doc.Document;
import com.spire.doc.PictureWatermark;
import com.spire.doc.PrivateFontPath;
import com.spire.doc.ToPdfParameterList;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static java.lang.Thread.sleep;


@Tag(name = "Sftp API", description = "Sftp 파일 업로드 / 다운로드 API")
@RestController
@RequestMapping("/api/sftp")
@RequiredArgsConstructor
public class SftpController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SftpService sftpService;
    private final DocToPdfService docToPdfService;
    private final HashService hashService;


    @Operation(summary = "파일 업로드", description = "파일을 입력받아 sftp를 이용해 파일서버 및 블록체인 원장에 업로드 합니다.")
    @Parameter(name = "file", description = "업로드할 파일")
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException, NoSuchAlgorithmException, InterruptedException {
        try {
            //pdf로 변환
            //docToPdfService.DocToPdf(file);

            // 파일의 SHA-256 해시 계산
            //String sha256Hash = calculateSHA256Hash(file.getInputStream());
            String sha256Hash = hashService.calculateSHA256Hash(file.getInputStream());


            //sftp 업로드 및 원장 등록
            sftpService.sftpFileUpload(file.getInputStream(), file.getOriginalFilename(), sha256Hash);
            sftpService.disconnect();

            String message = "파일 업로드 성공";
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            String errorMessage = "파일 업로드 실패 " + e.getMessage();
            logger.error(errorMessage);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }
    // PDF 변환 API
    @Operation(summary = "PDF 변환", description = "PDF로 변환합니다.")
    @Parameter(name = "file", description = "변환할 파일")
    @PostMapping("/convert")
    public ResponseEntity<String> convertToPdf(@RequestParam("file") MultipartFile file) {
        try {
            // PDF 변환 로직
            docToPdfService.DocToPdf(file);

            String message = "PDF 변환 성공";
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            String errorMessage = "PDF 변환 실패: " + e.getMessage();
            logger.error(errorMessage);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @Operation(summary = "파일 다운로드", description = "sftp를 이용해 파일서버에서 로컬로 다운로드 합니다.")
    @Parameter(name = "remoteFilePath", description = "파일서버 경로 및 다운로드 할 파일명 ex)/home/test/Downloads/test.txt")
    @Parameter(name = "localFilePath", description = "다운로드 한 파일을 저장할 경로 ex)/home/test2/Documents/")
    @GetMapping("/download")
    public String downloadFile(@RequestParam("remoteFilePath") String remoteFilePath, @RequestParam("localFilePath") String localFilePath) throws Exception {
        try {
            sftpService.sftpFileDownload(remoteFilePath, localFilePath);
            sftpService.disconnect();
            return "파일 다운로드 성공";
        } catch (Exception e) {
            String errorMessage = "파일 다운로드 실패 :" + e.getMessage();
            logger.error(errorMessage);
            return errorMessage;
        }
    }

}
