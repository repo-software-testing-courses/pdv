package com.trackorjargh.javarepository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.trackorjargh.javaclass.CommentFilm;
import com.trackorjargh.javaclass.Film;

public interface CommentFilmRepository extends JpaRepository<CommentFilm, Long>{
	List<CommentFilm> findByFilm(Film film);
	List<CommentFilm> findByFilmOrderByIdAsc(Film film);
    @Modifying
    @Transactional
	@Query(value = "DELETE FROM COMMENT_FILM WHERE USER_ID = ?1", nativeQuery = true)
	void removeCommentsFilmsByUserId(long id);
    
    @Modifying
    @Transactional
	@Query(value = "DELETE FROM COMMENT_FILM WHERE FILM_ID = ?1", nativeQuery = true)
	void removeCommentsFilmssByFilmId(long id);
    
    CommentFilm findById(Long id);
}
