package com.sh.documentverification.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Document {
    private String filename;
    private String username;
    private String filehash;
    private String filedate;
    
}
