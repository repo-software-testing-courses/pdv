package com.trackorjargh.javarepository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.trackorjargh.javaclass.Film;
import com.trackorjargh.javaclass.PointFilm;
import com.trackorjargh.javaclass.User;

public interface PointFilmRepository extends JpaRepository<PointFilm, Long>{
	PointFilm findByUserAndFilm(User user, Film film);
	List<PointFilm> findByFilm(Film film);
	
    @Modifying
    @Transactional
	@Query(value = "DELETE FROM POINT_FILM WHERE USER_ID = ?1", nativeQuery = true)
	void removePointsFilmsByUserId(long id);
    
    @Modifying
    @Transactional
	@Query(value = "DELETE FROM POINT_FILM WHERE FILM_ID = ?1", nativeQuery = true)
	void removePointsFilmsByFilmId(long id);
    
    PointFilm findById (Long id);
}
