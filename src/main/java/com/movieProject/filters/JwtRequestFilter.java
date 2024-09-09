package com.movieProject.filters;

import com.movieProject.service.CustomUserDetailsService;
import com.movieProject.utils.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Security;
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private JwtTokenUtil jwtTokenUtil;  // jwt 생성 및 검증을 위한 유틸리티 클래스
    private CustomUserDetailsService customUserDetailsService;  //사용자의 인증 정보를 불러오는 서비스

    @Autowired
    public JwtRequestFilter(JwtTokenUtil jwtTokenUtil, CustomUserDetailsService customUserDetailsService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.customUserDetailsService = customUserDetailsService;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {

        //요청의 헤더에서 "Authorization" 헤더를 가져옴
        final String authorizationHeader = request.getHeader("Authorization");

        String userId=null;
        String jwt=null;
        try {
            //Athorization 헤더가 존재하고, 'Bearer'로 시작하면 JWT토큰 추출
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7); //'Bearer ' 이후의 JWT 토큰 부분만 추출
                userId = jwtTokenUtil.extractUserId(jwt); // 토큰에서 사용자 이름 추출
            }

            //SecurityContext에 인증 정보가 없고, 추출된 사용자 이름이 있을 경우
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                //데이터베이스 또는 서비스에서 사용자 세부 정보를 로드
                UserDetails userDetails = this.customUserDetailsService.loadUserByUserId(userId);

                //JWT 토큰이 유효한지 확인
                if (jwtTokenUtil.validateToken(jwt)) {
                    // 유효한 JWT이면, 사용자 정보를 바탕으로 인증 토큰을 생성하고 SecurityContext에 설정
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);  // 인증 정보를 컨텍스트에 설정
                }
            }
        }catch(Exception e){
            // 예외 처리: JWT가 유효하지 않거나 만료된 경우, 로그를 남기고 401 응답 반환
            logger.warn("JWT validation failed: " + e.getMessage());
            response.setStatus((HttpServletResponse.SC_UNAUTHORIZED));
            response.getWriter().write("Unauthorized: JWT token is invalid or expired");
            return; //필터 체인 중단
        }

        // 필터 체인을 계속해서 다음 필터 또는 최종 대상(컨트롤러 등)으로 요청을 전달
        chain.doFilter(request, response);
    }
}
