package com.movieProject.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieProject.DTO.MovieDTO;
import com.movieProject.DTO.MovieDTO.*;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TMDBService {
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();


    public List<MovieDTO> getPopularMovies(int page) throws IOException{
        Request request = new Request.Builder()
                .url("https://api.themoviedb.org/3/movie/now_playing?language=ko&page=1")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI5OTMwYzBiYjk0OWFiMzIwMzBiMTE2ODg2ODllYjUzNyIsIm5iZiI6MTcyNjEzMjg0OS4xOTU2NjYsInN1YiI6IjY2ZGMxZjRjYTYxOGYyNzBkMTM1NzI5YiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.u2J_GQ4MB0xLCyJirfR1LaYPr6CmdcY2IUW1IixRylk")
                .build();
        try(Response response = client.newCall(request).execute()){
            if(!response.isSuccessful()) throw new IOException("Unexpected code "+ response);

            MovieResponse movieResponse=objectMapper.readValue(Objects.requireNonNull(response.body()).string(), MovieResponse.class);
            return movieResponse.getResults();
        }
    }
}
