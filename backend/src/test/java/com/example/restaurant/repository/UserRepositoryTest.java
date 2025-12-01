package com.example.restaurant.repository;

import com.example.restaurant.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveUser_success() {
        User user = new User();
        user.setRegion("서울");

        User saved = userRepository.save(user);
        assertThat(saved.getId()).isNotNull();
    }
}