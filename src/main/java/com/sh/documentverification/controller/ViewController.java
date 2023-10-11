package com.sh.documentverification.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ViewController {

    @GetMapping("/")
    public String index (){
        return "Index";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "exception", required = false) String exception,
                            Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            model.addAttribute("error", error);
            model.addAttribute("exception", exception);
            return "user/Login";
        }
        return "Index";
    }

    @GetMapping("/signup")
    public String joinPage(){
        return "user/Join";
    }

    @GetMapping("/pdf")
    public String pdfVeiw(){
        return "test";
    }
    @GetMapping("/pdftest")
    public String pdftestVeiw(){
        return "pdftest";
    }

    @GetMapping("/mypage")
    public String mypage(){
        return "user/mypage";
    }
}
