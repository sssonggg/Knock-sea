package com.knocksea.see.user.service;

import com.knocksea.see.user.entity.User;
import com.knocksea.see.user.entity.UserGrade;
import com.knocksea.see.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.knocksea.see.user.entity.UserGrade.COMMON;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    UserRepository userRepository;

    @Test
    public void saveUserTest(){
        User saveUser = User.builder()
                .userEmail("bbbb@naver.com")
                .userPassword("bbbb1234!")
                .userName("짱구")
                .userPhone("010-1111-1111")
                .userAddress("마포구")
                .userFullAddress("어쩌고 저쩌고")
                .userGrade(COMMON)
                .userPoint(0)
                .build();

        userRepository.save(saveUser);

    }
}