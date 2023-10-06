package com.sh.documentverification.controller;

import com.sh.documentverification.services.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.hyperledger.fabric.gateway.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ca/")
@RequiredArgsConstructor
public class WalletController {

    private final AuthorizationService authorizationService;

    @PostMapping("/enroll")
    public ResponseEntity<String> enrollAdmin() {
        try {
            String result = authorizationService.enrollAdmin();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("관리자 등록에 실패했습니다. 오류 메시지: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(){
        try {
            String result = authorizationService.registerUser();
            return ResponseEntity.ok(result);
        }catch (Exception e){
            return ResponseEntity.status(500).body("관리자 등록에 실패했습니다. 에러 :" + e.getMessage());
        }
    }
}
