package com.sotatek.demo.domain.port;

import com.sotatek.demo.domain.entitiy.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Modifying
    @Query(
            value = "truncate table users",
            nativeQuery = true
    )
    void truncate();
}
