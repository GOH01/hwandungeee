package com.movieProject.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter @Setter
@Entity
public class User {
    @Id @GeneratedValue
    private Long id;
    private String userName;
    @Column(unique = true)
    private String userId;
    @Column(unique = true)
    private String email;
    private String password;
    private String phoneNumber;
    private String birthDate;

    public User(String userName, String userId, String email, String password, String phoneNum, String birthDate){
        this.userName=userName;
        this.userId=userId;
        this.email=email;
        this.setPassword(password);
        this.phoneNumber=phoneNumber;
        this.birthDate=birthDate;
    }

    public void updateUser(String email, String phoneNum){
        if(email != null) this.email=email;
        if(phoneNum != null) this.phoneNumber=phoneNum;
    }

    private static final PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
    public void setPasswordEncoder(String password) { this.password=passwordEncoder.encode(password);}

    public boolean checkPassword(String rawPassword){
        System.out.println("passwordEncoder = " + passwordEncoder.matches(rawPassword,this.password));
        return passwordEncoder.matches(rawPassword, this.password);
    }
}
