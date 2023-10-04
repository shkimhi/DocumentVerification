package com.sh.documentverification.services;

import com.kanishka.virustotal.dto.FileScanReport;
import com.kanishka.virustotal.dto.ScanInfo;
import com.kanishka.virustotal.exception.APIKeyNotFoundException;
import com.kanishka.virustotal.exception.QuotaExceededException;
import com.kanishka.virustotal.exception.UnauthorizedAccessException;
import com.kanishka.virustotalv2.VirusTotalConfig;
import com.kanishka.virustotalv2.VirustotalPublicV2;
import com.kanishka.virustotalv2.VirustotalPublicV2Impl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Service
@RequiredArgsConstructor
public class VirusService {

    private final SftpService sftpService;

    private final String virusTotalAPIKey = "";

    public String scanFile(MultipartFile file) throws Exception{
        File tempFile = null;
        try {
            if (file.isEmpty()) {
                System.err.println("업로드된 파일이 비어 있습니다.");
                throw new IllegalArgumentException("Uploaded file is empty.");
            }

            tempFile = createTempFile(file);
            ScanInfo scanInfo = scanFileWithVirusTotal(tempFile);

            return scanInfo.getResource();

        }catch (APIKeyNotFoundException ex) {
            handleAPIKeyNotFoundException(ex);
            throw ex;
        }catch (Exception ex) {
            handleGenericException(ex);
            throw ex;
        }finally {
            deleteTempFile(tempFile);
        }
    }

    public String getFileScanReport(String res) throws Exception{
        try {
            FileScanReport report = getScanReportFromVirusTotal(res);

            printReportDetails(report);
            if (report.getPositives() > 0) {
                throw new RuntimeException("파일에서 바이러스가 발견되었습니다.");
            }if(report.getPositives() == null){
                throw new Exception("알수없는 에러가 발생했습니다.");
            }
            return report.getSha256();

        } catch (APIKeyNotFoundException ex) {
            handleAPIKeyNotFoundException(ex);
            throw ex;
        } catch (Exception ex) {
            handleGenericException(ex);
            throw ex;
        }
    }

    private File createTempFile(MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("uploaded_file", ".tmp");
        try (OutputStream os = new FileOutputStream(tempFile)) {
            os.write(file.getBytes());
        }
        return tempFile;
    }
    private void deleteTempFile(File tempFile) {
        if (tempFile != null && tempFile.exists()) {
            if (!tempFile.delete()) {
                System.err.println("임시 파일 삭제 실패 : " + tempFile.getAbsolutePath());
            }
        }
    }

    private ScanInfo scanFileWithVirusTotal(File tempFile) throws IOException, APIKeyNotFoundException, QuotaExceededException, UnauthorizedAccessException {
        VirusTotalConfig.getConfigInstance().setVirusTotalAPIKey(virusTotalAPIKey);
        VirustotalPublicV2 virusTotalRef = new VirustotalPublicV2Impl();
        return virusTotalRef.scanFile(tempFile);
    }

    private FileScanReport getScanReportFromVirusTotal(String res)
            throws IOException, APIKeyNotFoundException, QuotaExceededException, UnauthorizedAccessException {
        VirusTotalConfig.getConfigInstance().setVirusTotalAPIKey(virusTotalAPIKey);
        VirustotalPublicV2 virusTotalRef = new VirustotalPublicV2Impl();
        return virusTotalRef.getScanReport(res);
    }

    private void printReportDetails(FileScanReport report) {
        System.out.println("MD5 :\t" + report.getMd5());
        System.out.println("Perma link :\t" + report.getPermalink());
        System.out.println("Resource :\t" + report.getResource());
        System.out.println("Scan Date :\t" + report.getScanDate());
        System.out.println("Scan Id :\t" + report.getScanId());
        System.out.println("SHA256 :\t" + report.getSha256());
        System.out.println("Verbose Msg :\t" + report.getVerboseMessage());
        System.out.println("Response Code :\t" + report.getResponseCode());
        System.out.println("Positives :\t" + report.getPositives());
        System.out.println("Total :\t" + report.getTotal());
    }

    private void handleAPIKeyNotFoundException(APIKeyNotFoundException ex) {
        System.err.println("API 키를 찾을 수 없습니다! " + ex.getMessage());
    }

    private void handleGenericException(Exception ex) {
        System.err.println("예상치 못한 오류가 발생했습니다: " + ex.getMessage());
    }

}
