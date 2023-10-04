package com.sh.documentverification.controller;

import com.sh.documentverification.services.SftpService;
import com.sh.documentverification.services.VirusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Upload API", description = "파일 업로드, 바이러스 검사")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/virus/")
public class VirusController {

    private final SftpService sftpService;
    private final VirusService virusService;

    @Operation(summary = "파일 업로드", description = "파일을 받아 바이러스 검사 후 sftp를 이용해 파일서버로 업로드 합니다.")
    @Parameter(name = "file", description = "업로드할 파일")
    @PostMapping("/upload")
    public ResponseEntity<String> scanFile(@RequestParam("File") MultipartFile file){
        try {
            String scanResult = virusService.scanFile(file);
            String Hash= null;
            try {
                Hash = virusService.getFileScanReport(scanResult);
            } catch (RuntimeException ex) {
                // RuntimeException이 발생하면 바이러스가 검출되었음을 클라이언트에게 알림
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
            } catch (Exception ex){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
            }

            if (scanResult != null && Hash != null) {
                sftpService.sftpFileUpload(file.getInputStream(), file.getOriginalFilename(), Hash);
                sftpService.disconnect();
                String message = "파일이 성공적으로 업로드되었습니다!";
                return ResponseEntity.ok(message);
            } else {
                String errorMessage = "바이러스 검사 또는 해시 검색 문제로 인해 파일 업로드에 실패했습니다..";
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = "파일 업로드 실패" + e.getMessage();
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }
}
