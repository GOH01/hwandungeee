package com.movieProject.controller;

import com.movieProject.DTO.UserDTO.*;
import com.movieProject.domain.User;
import com.movieProject.exception.DuplicateUserException;
import com.movieProject.exception.IdNotFoundException;
import com.movieProject.exception.InvalidCredentialException;
import com.movieProject.service.UserService;
import com.movieProject.utils.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="User Controller", description = "사용자 관리 API")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;

    @Operation(summary="회원가입", description = "정보를 입력하고 회원가입 시도",
            responses = {@ApiResponse(responseCode = "201", description = "생성 성공 후 토큰 변환"),
                    @ApiResponse(responseCode = "409", description = "중복 아이디로 인한 생성 실패")})
    @PostMapping("/user")
    public ResponseEntity<String> signup(@RequestBody UserCreateRequest request){
        try{
            User user = userService.signUp((request.getUserName()), request.getUserId(), request.getEmail(),
                    request.getPassword(), request.getPhoneNumber(), request.getBirthDate());
            return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 성공적으로 완료되었습니다.");
        }catch(DuplicateUserException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @Operation(summary="로그인", description = "아이디와 패스워드를 입력하고 로그인 시도",
            responses = {@ApiResponse(responseCode = "200", description = "로그인 성공"),
                    @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다."),
                    @ApiResponse(responseCode = "401", description = "아이디 또는 비밀번호 오류")})
    @PostMapping("/login")
    public ResponseEntity<loginResponse> login(@RequestBody UserLoginRequest request){

        try{
            String token=userService.login(request.getUserId(), request.getPassword());
            String userName=userService.tokenToUser(token).getUserName();
            return ResponseEntity.ok(new loginResponse(token, userName));
        }catch (IdNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }catch(InvalidCredentialException e){
            return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
