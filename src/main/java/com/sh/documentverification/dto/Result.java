package com.sh.documentverification.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Result {

    private String key;
    private File record;

    public Result(){
        this.record =new File();
    }

}
