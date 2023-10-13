package com.sh.documentverification.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Result {

    private String key;
    private Document record;

    public Result(){
        this.record =new Document();
    }

}
