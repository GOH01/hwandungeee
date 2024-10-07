package com.movieProject.controller;

import com.movieProject.DTO.MovieDTO;
import com.movieProject.DTO.MovieDTO.*;
import com.movieProject.exception.MovieNotFoundException;
import com.movieProject.service.MovieService;
import com.movieProject.service.TMDBService;
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

    @GetMapping("/movies")
    public ResponseEntity<List<MovieDTO>> getAllMovies(){
        try{
            List<MovieDTO> movies=movieService.getAllMovies();
            return new ResponseEntity<>(movies, HttpStatus.OK);
        }catch(MovieNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/movies/{title}")
    public ResponseEntity<List<MovieDTO>> getTitleMovie(@PathVariable String title){
        try{
            List<MovieDTO> movies = movieService.getMovieByTitle(title);
            return new ResponseEntity<>(movies, HttpStatus.OK);
        }catch (MovieNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/movies/{id}")
    public ResponseEntity<MovieDTO> getIdMovie(@PathVariable Long id){
        try{
            MovieDTO movie = movieService.getMovieById(id);
            return  new ResponseEntity<>(movie, HttpStatus.OK);
        }catch(MovieNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
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
