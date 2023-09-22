package com.sh.documentverification.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class User {
    private String UserId;
    private String UserPw;
}
