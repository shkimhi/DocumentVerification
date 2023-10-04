package com.sh.documentverification.controller;

import com.sh.documentverification.dto.File;
import com.sh.documentverification.dto.Result;
import com.sh.documentverification.services.LedgerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.hyperledger.fabric.gateway.ContractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Tag(name = "Ledger API", description = "블록체인 원장에 FileHash 저장 / 원장에 저장된 FileHash 검증")
@RestController
@RequestMapping("/api/ledger/")
public class LedgerController {

    private LedgerService ledgerService;

    @Autowired
    public LedgerController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @Operation(summary = "블록 생성", description = "파일해쉬 및 파일명 저장")
    @Parameter(name = "result", description = "key, filename, filehash, filedate, username")
    @PostMapping("/create")
    public ResponseEntity<?> createFile(@RequestBody Result result) {
        try {
            String id = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String key = UUID.randomUUID().toString();
            result.setKey(key);
/*
            ledgerService.createFile(result);
*/

            String message = "원장에 커밋이 성공 하였습니다.";
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            String errorMessage = "원장에 커밋이 실패 하였습니다. " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @PostMapping("/query")
    public ResponseEntity<?> queryFile(@RequestParam("key") String key) {
        try {
            List<Result> message = ledgerService.queryFile(key);
            return ResponseEntity.ok(message);
        } catch (ContractException | IOException e) {
            String errorMessage = "불러오지 못했습니다." + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }
    @PostMapping("/allquery")
    public ResponseEntity<?> queryAllHashFile() {
        try {
            List<Result> result =  ledgerService.queryAllHashFile();
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            String errorMessage = "불러오지 못했습니다." + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

}
