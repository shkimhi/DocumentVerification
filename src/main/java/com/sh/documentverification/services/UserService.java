package com.sh.documentverification.services;

import com.sh.documentverification.dao.UserMapper;
import com.sh.documentverification.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMappler;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    public void signUp(User user){
        user.setUserPw(passwordEncoder.encode(user.getUserPw()));
        userMappler.joinUser(user);
    }
    public User getUserId(String UserId){
        return userMappler.getUserId(UserId);
    }
    public PasswordEncoder passwordEncoder() {
        return this.passwordEncoder;
    }


}
