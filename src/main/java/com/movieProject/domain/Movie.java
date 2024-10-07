package com.movieProject.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter @Setter
@Entity
public class Movie {
    @Id @GeneratedValue
    private Long id;  //TMDB 영화 ID

    private String title;  //영화 제목
    @Column(length = 5000)
    private String overview;  // 영화줄거리
    private String releaseDate;  //영화 출시일
    private Double voteAverage;  //평균 평점
    private Integer voteCount;  // 평점 수
    private String posterPath;  // 포스터 경로
    private Boolean adult;


    @ManyToMany(fetch= FetchType.LAZY)
    @JoinTable(
            name="movie_genre",
            joinColumns=@JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> genres;
}
