package com.sh.documentverification.dao;

import org.apache.ibatis.annotations.Mapper;
import org.json.simple.JSONObject;

@Mapper
public interface SftpMapper {

    void insertFile(JSONObject params);
}
