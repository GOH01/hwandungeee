package com.movieProject.service;

import com.movieProject.DTO.MovieDTO;
import com.movieProject.DTO.MovieDTO.*;
import com.movieProject.domain.Genre;
import com.movieProject.domain.Movie;
import com.movieProject.exception.MovieNotFoundException;
import com.movieProject.repository.GenreRepository;
import com.movieProject.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieService {
    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;


    @Transactional
    public void savePopularMovies(List<MovieDTO> moviesDto) {

        for (MovieDTO movieDto : moviesDto) {
            Movie movie = new Movie();
            movie.setId(movieDto.getId());
            movie.setTitle(movieDto.getTitle());
            movie.setOverview(movieDto.getOverview());
            movie.setReleaseDate(movieDto.getReleaseDate());
            movie.setVoteAverage(movieDto.getVoteAverage());
            movie.setVoteCount(movieDto.getVoteCount());
            movie.setPosterPath(movieDto.getPosterPath());
            movie.setAdult(movieDto.getAdult());

            // Genre 처리
            List<Integer> genreIds = movieDto.getGenreIds() != null ? movieDto.getGenreIds() : new ArrayList<>();
            List<Genre> genres = new ArrayList<>();
            for (Integer genreId : genreIds) {
                Optional<Genre> genre = genreRepository.findById(Long.valueOf(genreId));
                genre.ifPresent(genres::add);  // 해당 장르가 있으면 리스트에 추가
            }
            movie.setGenres(genres);

            // 데이터베이스에 저장 (중복된 영화는 업데이트 처리)
            movieRepository.save(movie);
        }
    }

    private MovieDTO convertToDto(Movie movie){
        return MovieDTO.builder()
                .id(movie.getId())
                .title((movie.getTitle()))
                .overview(movie.getOverview())
                .releaseDate(movie.getReleaseDate())
                .voteAverage(movie.getVoteAverage())
                .voteCount(movie.getVoteCount())
                .posterPath(movie.getPosterPath())
                .build();
    }

    public List<MovieDTO> getAllMovies(){
        List<Movie> movies = movieRepository.findAll();
        return movies.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public MovieDTO getMovieById(Long id){
        Movie movie = movieRepository.findById(id)
                .orElseThrow(()-> new MovieNotFoundException("Movie not found"));
        return convertToDto(movie);
    }

    public List<MovieDTO> getMovieByTitle(String title){
        List<Movie> movies = movieRepository.findByTitle(title);
        if(movies.isEmpty()) throw new MovieNotFoundException("Movies not found");
        return movies.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
