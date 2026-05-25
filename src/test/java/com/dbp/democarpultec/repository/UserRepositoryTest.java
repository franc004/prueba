package com.dbp.democarpultec.repository;

import com.dbp.democarpultec.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveUserWhenValidData() {
        User user = new User();
        user.setName("Juan");
        user.setLastName("Perez");
        user.setEmail("juan@test.com");

        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());
        assertEquals("Juan", savedUser.getName());
    }

    @Test
    void shouldFindUserByIdWhenUserExists() {
        User user = new User();
        user.setName("Carlos");
        user.setLastName("lopez");
        user.setEmail("carlos@test.com");

        User savedUser = userRepository.save(user);

        Optional<User> result = userRepository.findById(savedUser.getId());

        assertTrue(result.isPresent());
        assertEquals("Carlos", result.get().getName());
    }

    @Test
    void shouldReturnAllUsers(){
        User user1 = new User();
        user1.setName("Juan");
        user1.setLastName("Perez");
        user1.setEmail("juan@test.com");

        User user2 = new User();
        user2.setName("Carlos");
        user2.setLastName("Lopez");
        user2.setEmail("maria@test.com");

        userRepository.save(user1);
        userRepository.save(user2);

        List<User> user = userRepository.findAll();

        assertEquals(2, user.size());
    }

    @Test
    void shouldDeleteUserWhenUserExists() {
        User user = new User();
        user.setName("Pedro");
        user.setLastName("Diaz");
        user.setEmail("pedro@test.com");

        User savedUser = userRepository.save(user);

        userRepository.deleteById(savedUser.getId());

        Optional<User> result = userRepository.findById(savedUser.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnFalseWhenUserDoesNotExist() {
        boolean exists = userRepository.existsById(999L);
        assertFalse(exists);
    }
}