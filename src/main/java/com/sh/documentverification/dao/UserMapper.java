package com.sh.documentverification.dao;

import com.sh.documentverification.dto.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    void joinUser(User user);
    User getUserId(String UserId);
}
