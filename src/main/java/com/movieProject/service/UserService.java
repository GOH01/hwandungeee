package com.movieProject.service;

import com.movieProject.domain.User;
import com.movieProject.exception.DuplicateUserException;
import com.movieProject.exception.IdNotFoundException;
import com.movieProject.exception.InvalidCredentialException;
import com.movieProject.repository.UserRepository;
import com.movieProject.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;

    public User tokenToUser(String token){
        String userId=jwtTokenUtil.extractUserId(token);

        if (jwtTokenUtil.validateToken(token)) {
            return userRepository.findByUserId(userId);
        }
        return null;
    }

    @Transactional
    public User signUp(String userName, String userId, String email, String password, String phoneNumber, String birthDate){
        User user=userRepository.findByUserId(userId);
        User user2=userRepository.findByEmail(email);
        if((user != null) || (user2 != null)){
            throw new DuplicateUserException("이미 존재하는 사용자입니다.");
        }
        String encodedPassword = passwordEncoder.encode(password);
        return userRepository.save(new User(userName, userId, email, encodedPassword, phoneNumber, birthDate));
    }

    public String login(String userId, String password){
        User user=findByUserId(userId);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialException("아이디 또는 비밀번호가 잘못되었습니다.");
        }
        return jwtTokenUtil.generateToken(user);
    }

    public User findByUserId(String userId){
        User user=userRepository.findByUserId(userId);
        if(user==null) throw new IdNotFoundException("사용자를 찾을 수 없습니다.");
        return user;
    }
    public User findByUserName(String userName){
        User user=userRepository.findByUserName(userName);
        if(user==null) throw new IdNotFoundException("사용자를 찾을 수 없습니다.");
        return user;
    }
    @Transactional
    public void deleteUser(String token){
        User user=tokenToUser(token);
        if(user==null){
            throw new IdNotFoundException("사용자를 찾을 수 없습니다.");
        }
        userRepository.delete(user);
    }
}
