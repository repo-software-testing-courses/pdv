package com.trackorjargh.javarepository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.trackorjargh.javaclass.Film;

public interface FilmRepository extends JpaRepository<Film, Long> {
	@Query(value = "Select max(id) from Film", nativeQuery = true)
	Long findLastId();

	// SELECT TOP 10 * FROM FILM ORDER BY ID DESC
	@Query(value = "Select * from Film order by id desc limit ?1", nativeQuery = true)
	List<Film> findByLastAdded(int additions);
	
    @Query(value = "SELECT FILM.* FROM POINT_FILM INNER JOIN FILM ON POINT_FILM.FILM_ID=FILM.ID GROUP BY POINT_FILM.FILM_ID ORDER BY AVG(POINT_FILM.POINTS) DESC \n-- #pageable\n", 
    		countQuery = "SELECT COUNT(*) FROM POINT_FILM GROUP BY FILM_ID",
    		nativeQuery = true)
    Page<Film> findBestPointFilm(Pageable pageable);
    
    @Query(value = "SELECT FILM.* FROM FILM_GENDERS INNER JOIN FILM ON FILM_GENDERS.FILMS_ID=FILM.ID WHERE FILM_GENDERS.GENDERS_ID IN (SELECT ID FROM GENDER WHERE LOWER(NAME) LIKE LOWER(?1)) \n-- #pageable\n",
    		countQuery = "SELECT COUNT(FILM.ID) FROM FILM_GENDERS INNER JOIN FILM ON FILM_GENDERS.FILMS_ID=FILM.ID WHERE FILM_GENDERS.GENDERS_ID IN (SELECT ID FROM GENDER WHERE LOWER(NAME) LIKE LOWER(?1))",
    		nativeQuery = true)
    Page<Film> findFilmsByGender(String gender, Pageable pageable);
    
    @Query(value = "SELECT FILM.* FROM FILM_GENDERS INNER JOIN FILM ON FILM_GENDERS.FILMS_ID=FILM.ID WHERE FILM_GENDERS.GENDERS_ID IN (SELECT GENDERS_ID FROM FILM_GENDERS WHERE FILMS_ID = ?1) AND FILM.ID != ?1 \n-- #pageable\n",
    		countQuery = "SELECT COUNT(FILM.ID) FROM FILM_GENDERS INNER JOIN FILM ON FILM_GENDERS.FILMS_ID=FILM.ID WHERE FILM_GENDERS.GENDERS_ID IN (SELECT GENDERS_ID FROM FILM_GENDERS WHERE FILMS_ID = ?1) AND FILM.ID != ?1",
    		nativeQuery = true)
    Page<Film> findFilmsRelationsById(long id, Pageable pageable);
    
	
	Page<Film> findByNameContainingIgnoreCase(String name, Pageable pageable);
	Film findByNameIgnoreCase(String name);
	Film findById(Long id);
}
