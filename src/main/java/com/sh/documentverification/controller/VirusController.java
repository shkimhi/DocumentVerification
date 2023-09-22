package com.sh.documentverification.controller;

import com.kanishka.virustotal.dto.FileScanReport;
import com.kanishka.virustotal.dto.ScanInfo;
import com.kanishka.virustotal.dto.VirusScanInfo;
import com.kanishka.virustotal.exception.APIKeyNotFoundException;
import com.kanishka.virustotal.exception.QuotaExceededException;
import com.kanishka.virustotal.exception.UnauthorizedAccessException;
import com.kanishka.virustotalv2.VirusTotalConfig;
import com.kanishka.virustotalv2.VirustotalPublicV2;
import com.kanishka.virustotalv2.VirustotalPublicV2Impl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@RestController
public class VirusController {


    @PostMapping("/virus/api")
    public void scanFile(@RequestParam("File") MultipartFile file) throws APIKeyNotFoundException {
        try {
            // Check if the uploaded file is not empty
            if (!file.isEmpty()) {
                // Create a temporary file to save the uploaded content
                File tempFile = File.createTempFile("uploaded_file", ".tmp");

                // Copy the content of the MultipartFile to the temporary file
                try (OutputStream os = new FileOutputStream(tempFile)) {
                    os.write(file.getBytes());
                }

                // Set your API key and scan the temporary file
                VirusTotalConfig.getConfigInstance().setVirusTotalAPIKey("12d8a990abb28e7e7f3bf3b5e3db432f9593ed20bc5ee809387b54864872d693");
                VirustotalPublicV2 virusTotalRef = new VirustotalPublicV2Impl();
                ScanInfo scanInfo = virusTotalRef.scanFile(tempFile);

                // Handle the scan result as needed
                System.out.println("___SCAN INFORMATION___");
                System.out.println("MD5 :\t" + scanInfo.getMd5());
                System.out.println("Perma Link :\t" + scanInfo.getPermalink());
                System.out.println("Resource :\t" + scanInfo.getResource());
                System.out.println("Scan Date :\t" + scanInfo.getScanDate());
                System.out.println("Scan Id :\t" + scanInfo.getScanId());
                System.out.println("SHA1 :\t" + scanInfo.getSha1());
                System.out.println("SHA256 :\t" + scanInfo.getSha256());
                System.out.println("Verbose Msg :\t" + scanInfo.getVerboseMessage());
                System.out.println("Response Code :\t" + scanInfo.getResponseCode());
                System.out.println("done.");

                // Clean up the temporary file
                tempFile.delete();
            } else {
                System.err.println("Uploaded file is empty.");
            }
        } catch (APIKeyNotFoundException ex) {
            System.err.println("API key not found! " + ex.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception ex) {
            System.err.println("ex" + ex.getMessage());
        }
    }

    @PostMapping("/virus/api/report")
    public void getFileScanReport(@RequestParam("res") String res) throws APIKeyNotFoundException, QuotaExceededException, UnauthorizedAccessException, IOException {
        VirusTotalConfig.getConfigInstance().setVirusTotalAPIKey("12d8a990abb28e7e7f3bf3b5e3db432f9593ed20bc5ee809387b54864872d693");
        VirustotalPublicV2 virustotalPublicV2 = new VirustotalPublicV2Impl();

        FileScanReport report = virustotalPublicV2.getScanReport(res);

        System.out.println("MD5 :\t" + report.getMd5());
        System.out.println("Perma link :\t" + report.getPermalink());
        System.out.println("Resourve :\t" + report.getResource());
        System.out.println("Scan Date :\t" + report.getScanDate());
        System.out.println("Scan Id :\t" + report.getScanId());
        System.out.println("SHA1 :\t" + report.getSha1());
        System.out.println("SHA256 :\t" + report.getSha256());
        System.out.println("Verbose Msg :\t" + report.getVerboseMessage());
        System.out.println("Response Code :\t" + report.getResponseCode());
        System.out.println("Positives :\t" + report.getPositives());
        System.out.println("Total :\t" + report.getTotal());

        Map<String, VirusScanInfo> scans = report.getScans();
        for (String key : scans.keySet()){
            VirusScanInfo virusScanInfo = scans.get(key);
            System.out.println("Scanner : " + key);
            System.out.println("\t\t Resut : " + virusScanInfo.getResult());
            System.out.println("\t\t Update : " + virusScanInfo.getUpdate());
            System.out.println("\t\t Version :" + virusScanInfo.getVersion());
        }
    }
}
