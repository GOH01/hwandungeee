package com.movieProject.utils;

import com.movieProject.domain.User;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtTokenUtil {

    //토큰을 서명하는 데 사용될 비밀 키
    private String SECRET_KEY="mysecretkey";

    //사용자 이름을 입력받아 jwt 토큰을 생성하는 메서드
    public String generateToken(User user){
        Map<String, Object> claims = new HashMap<>();//비어있는 클레임 생성
        claims.put("userId", user.getId());
        return createToken(claims, user.getUserId()); //클레임과 유저이름을 바탕으로 토큰 생성
    }

    //jwt 토큰 생성 메서드
    private String createToken(Map<String, Object> claims, String userId){
        return Jwts.builder()
                .setClaims(claims) //클레임 설정(사용자 관련 데이터)
                .setSubject(userId) //토큰의 소유자를 설정, 보통 유저이름으로 사용
                .setIssuedAt(new Date(System.currentTimeMillis())) //토큰 발행시간 설정
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))  //토큰 만료 시간 설정 (10시간)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY.getBytes(StandardCharsets.UTF_8)) //서명 알고리즘과 비밀 키를 사용해 토큰 서명
                .compact(); //JWT 생성 및 문자열로 변환
    }

    //토큰의 유효성을 검증하는 메서드
    public Boolean validateToken(String token){
        final String extractedUserId=extractUserId(token); // 토큰에서 사용자 이름을 추출
        // 토큰이 만료되지 않았고, 추출된 사용자 이름이 입력된 사용자 이름과 동일한지 확인
        return !isTokenExpired(token);
    }

    //토큰에서 사용자 이름을 추출하는 메서드
    public String extractUserName(String token){
        return extractClaim(token, Claims::getSubject); // 클레임에서 Subject(사용자 이름)을 가져옴
    }

    public String extractUserId(String token){
        return extractClaim(token, Claims::getSubject);
    }

    //토큰에서 만료 시간을 추출하는 메서드
    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);  // 클레임에서 Expiration(만료 시간)을 가져옴
    }

    //토큰에서 특정 클레임을 추출하는 메서드
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaim(token); // 토큰에서 모든 클레임을 가져옴
        return claimsResolver.apply(claims);  // 가져온 클레임을 사용해 특정 값(Subject, Expiration 등)을 반환
    }

    //토큰에서 모든 클레임을 추출하는 메서드
    private Claims extractAllClaim(String token){
        return Jwts.parser()
                .setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8)) // 서명 검증을 위해 비밀 키 설정
                .parseClaimsJws(token) // 토큰을 파싱하여 클레임을 추출
                .getBody(); // 클레임 변환
    }

    //토큰의 만료 여부를 확인하는 메서드
    private Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date()); //현재 시간과 만료 시간을 비교하여 만료 여부를 반환
    }

}
