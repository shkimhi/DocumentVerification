package com.sh.documentverification.dao;

import com.sh.documentverification.dto.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Mapper
public interface UserMapper {
    void joinUser(User user);
    User getUserId(String UserId);
    Optional<User> findByUserId(String name);
}
