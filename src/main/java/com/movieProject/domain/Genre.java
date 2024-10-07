package com.movieProject.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
@Entity
public class Genre {
    @Id @GeneratedValue
    private Long id;
    private String name;

    @ManyToMany(mappedBy = "genres")
    private List<Movie> movies;
}
