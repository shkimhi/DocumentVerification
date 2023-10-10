package com.sh.documentverification.services;

import com.sh.documentverification.dao.UserMapper;
import com.sh.documentverification.dto.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UserMapper userMappler;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public void joinUser(User user) {
        user.setUserPw(passwordEncoder.encode(user.getUserPw()));
        validateDuplicateMember(user);
        userMappler.joinUser(user);

    }
    private void validateDuplicateMember(User user) {
        if(userMappler.getUserId(user.getUserId()) != null) {
            logger.error("이미 존재하는 회원입니다.");
            throw new DuplicateKeyException("이미 존재하는 회원입니다.");
        }

    }
    public User getUserId(String UserId){
        return userMappler.getUserId(UserId);
    }
    public PasswordEncoder passwordEncoder() {
        return this.passwordEncoder;
    }


}
