package com.movieProject.config;

import com.movieProject.filters.JwtRequestFilter;
import com.movieProject.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@EnableWebSecurity
public class SecurityConfig  {

    //PasswordEncorder Bean 설정(BCrypt 사용)
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager Bean 설정 (JWT 인증에 필요)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // SecurityFilterChain 설정 - 보안 설정을 여기서 구성
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtRequestFilter jwtRequestFilter) throws Exception{
        http
            .cors(cors -> corsConfigurationSource())
            .csrf(csrf-> csrf.disable()) // JWT를 사용할 때는 CSRF비활성화
            .authorizeHttpRequests((requests) -> requests
                 .requestMatchers("/user",
                         "/login",
                         "/v3/api-docs/**",   // OpenAPI 문서 경로
                         "/swagger-ui/**",    // Swagger UI 경로
                         "/swagger-ui.html",  // Swagger UI HTML 경로
                         "/webjars/**"
                 ,"localhost:3000").permitAll() //인증 엔드포인트는 모두 접근 가능
                 .anyRequest().authenticated()  //나머지는 인증 필요
            )
            .sessionManagement(session-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); //JWT 사용시 세션을 사용하지 않음

        // JWT 필터를 UsernamePasswordAuthenticationFilter 이전에 추가
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:3000");  // 프론트엔드가 실행 중인 도메인 허용
        configuration.addAllowedMethod("*");  // 모든 HTTP 메서드 허용
        configuration.addAllowedHeader("*");  // 모든 헤더 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


}
