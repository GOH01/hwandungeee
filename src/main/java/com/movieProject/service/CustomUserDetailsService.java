package com.movieProject.service;

import com.movieProject.domain.User;
import com.movieProject.exception.IdNotFoundException;
import com.movieProject.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    public UserDetails loadUserByUserId(String userId) throws IdNotFoundException {
        User user=userRepository.findByUserId(userId);
        if (user==null){
            throw new IdNotFoundException("사용자를 찾을 수 없습니다.");
        }
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUserName())
                .password(user.getPassword())
                .build();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }

}
