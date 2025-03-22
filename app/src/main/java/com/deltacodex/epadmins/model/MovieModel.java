package com.deltacodex.epadmins.model;

import java.io.Serializable;

public class MovieModel implements Serializable {
    private String m_id;
    private String Movie_name;
    private String Movie_thumbnailUrl;
    private String Movie_imdb;
    private String Movie_rottenTomatoes;
    private String Movie_genre;
    private String Movie_downloadLink;
    private String Movie_trailerLink;

    public MovieModel() {
    }

    public MovieModel(String m_id, String movie_name, String movie_thumbnailUrl, String movie_imdb, String movie_rottenTomatoes, String movie_genre, String movie_downloadLink, String movie_trailerLink) {
        this.m_id = m_id;
        Movie_name = movie_name;
        Movie_thumbnailUrl = movie_thumbnailUrl;
        Movie_imdb = movie_imdb;
        Movie_rottenTomatoes = movie_rottenTomatoes;
        Movie_genre = movie_genre;
        Movie_downloadLink = movie_downloadLink;
        Movie_trailerLink = movie_trailerLink;
    }

    public String getM_id() {
        return m_id;
    }

    public void setM_id(String m_id) {
        this.m_id = m_id;
    }

    public String getMovie_name() {
        return Movie_name;
    }

    public void setMovie_name(String movie_name) {
        Movie_name = movie_name;
    }

    public String getMovie_thumbnailUrl() {
        return Movie_thumbnailUrl;
    }

    public void setMovie_thumbnailUrl(String movie_thumbnailUrl) {
        Movie_thumbnailUrl = movie_thumbnailUrl;
    }

    public String getMovie_imdb() {
        return Movie_imdb;
    }

    public void setMovie_imdb(String movie_imdb) {
        Movie_imdb = movie_imdb;
    }

    public String getMovie_rottenTomatoes() {
        return Movie_rottenTomatoes;
    }

    public void setMovie_rottenTomatoes(String movie_rottenTomatoes) {
        Movie_rottenTomatoes = movie_rottenTomatoes;
    }

    public String getMovie_genre() {
        return Movie_genre;
    }

    public void setMovie_genre(String movie_genre) {
        Movie_genre = movie_genre;
    }

    public String getMovie_downloadLink() {
        return Movie_downloadLink;
    }

    public void setMovie_downloadLink(String movie_downloadLink) {
        Movie_downloadLink = movie_downloadLink;
    }

    public String getMovie_trailerLink() {
        return Movie_trailerLink;
    }

    public void setMovie_trailerLink(String movie_trailerLink) {
        Movie_trailerLink = movie_trailerLink;
    }
}
