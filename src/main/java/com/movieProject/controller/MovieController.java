package com.movieProject.controller;

import com.movieProject.DTO.MovieDTO;
import com.movieProject.DTO.MovieDTO.*;
import com.movieProject.exception.MovieNotFoundException;
import com.movieProject.service.MovieService;
import com.movieProject.service.TMDBService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;
    private final TMDBService tmdbService;

    @Operation(summary="모든 영화조회", description = "모든 영화를 조회한다.",
            responses = {@ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "404", description = "조회 실패")})
    @GetMapping("/movies")
    public ResponseEntity<List<MovieDTO>> getAllMovies(){
        try{
            List<MovieDTO> movies=movieService.getAllMovies();
            return new ResponseEntity<>(movies, HttpStatus.OK);
        }catch(MovieNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Operation(summary="영화제목으로 조회", description = "경로에 {title}를 입력받아 영화를 조회한다.",
            responses = {@ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "404", description = "조회 실패")})
    @GetMapping("/movies/{title}")
    public ResponseEntity<List<MovieDTO>> getTitleMovie(@PathVariable String title){
        try{
            List<MovieDTO> movies = movieService.getMovieByTitle(title);
            return new ResponseEntity<>(movies, HttpStatus.OK);
        }catch (MovieNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary="영화 id로 조회", description = "경로에 {id}를 입력받아 영화를 조회한다.",
            responses = {@ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "404", description = "조회 실패")})
    @GetMapping("/movies/{id}")
    public ResponseEntity<MovieDTO> getIdMovie(@PathVariable Long id){
        try{
            MovieDTO movie = movieService.getMovieById(id);
            return  new ResponseEntity<>(movie, HttpStatus.OK);
        }catch(MovieNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary="tmdb에서 영화데이터 불러오기", description = "경로에 {page}를 입력받아 영화를 불러온다",
            responses = {@ApiResponse(responseCode = "200", description = "성공"),
                    @ApiResponse(responseCode = "500", description = "실패")})
    @PostMapping("/movies/popular/{page}")
    public ResponseEntity<String> savePopularMovies(@PathVariable int page){
        try{
            List<MovieDTO> movies = tmdbService.getPopularMovies(page);
            movieService.savePopularMovies(movies);
            return  new ResponseEntity<>("Popular movies saved successfully", HttpStatus.OK);
        }catch (IOException e){
            e.printStackTrace();  // 에러 로그를 확인
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }
}
