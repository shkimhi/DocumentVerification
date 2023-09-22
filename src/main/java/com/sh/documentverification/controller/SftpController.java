package com.sh.documentverification.controller;

import com.sh.documentverification.dto.Result;
import com.sh.documentverification.services.LedgerService;
import com.sh.documentverification.services.SftpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;


@Tag(name = "Sftp API", description = "Sftp 파일 업로드 / 다운로드 API")
@RestController
@RequestMapping("/api/sftp")
public class SftpController {

    private SftpService sftpService;
    private LedgerService ledgerService;


    @Autowired
    public SftpController(SftpService sftpService, LedgerService ledgerService){
        this.sftpService = sftpService;
        this.ledgerService = ledgerService;
    }

    @Operation(summary = "파일 업로드", description = "파일을 입력받아 sftp를 이용해 파일서버로 업로드 합니다.")
    @Parameter(name = "file", description = "업로드할 파일")
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // 업로드 파일의 InputStream을 가져와서 SFTP 서버로 업로드
            sftpService.sftpFileUpload(file.getInputStream(), file.getOriginalFilename(), sftpService.getHash(file.getInputStream()));
            System.out.println(SftpService.getHash(file.getInputStream()));
            sftpService.disconnect();
            String message = "File uploaded successfully!";
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = "File upload failed " + e.getMessage();
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
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
            return "File Download Successfully";
        }catch (Exception e){
            e.printStackTrace();
            return "File Download failed: " + e.getMessage();
        }
    }

}