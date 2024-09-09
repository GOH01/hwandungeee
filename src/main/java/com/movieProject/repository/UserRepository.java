package com.movieProject.repository;

import com.movieProject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserId(String userId);
    User findByEmail(String email);
    User findByUserName(String userName);

    UserDetails findUserByUserId(String userId);
}
