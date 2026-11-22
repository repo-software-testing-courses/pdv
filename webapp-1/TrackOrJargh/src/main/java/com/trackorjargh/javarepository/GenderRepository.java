package com.trackorjargh.javarepository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.trackorjargh.javaclass.Book;
import com.trackorjargh.javaclass.Film;
import com.trackorjargh.javaclass.Gender;
import com.trackorjargh.javaclass.Shows;

public interface GenderRepository extends JpaRepository<Gender, Long> {

	List<Gender> findByFilms(Film film);	

	@Query(value = "SELECT * FROM GENDER WHERE ID NOT IN (SELECT GENDERS_ID FROM FILM_GENDERS WHERE FILMS_ID = ?1)", nativeQuery = true)
	List<Gender> findByNotInFilm(Long id);

	List<Gender> findByShows(Shows show);

	@Query(value = "SELECT * FROM GENDER WHERE ID NOT IN (SELECT GENDERS_ID FROM SHOWS_GENDERS WHERE SHOWS_ID = ?1)", nativeQuery = true)
	List<Gender> findByNotInShow(Long id);

	List<Gender> findByBooks(Book book);

	@Query(value = "SELECT * FROM GENDER WHERE ID NOT IN (SELECT GENDERS_ID FROM BOOK_GENDERS WHERE BOOKS_ID = ?1)", nativeQuery = true)
	List<Gender> findByNotInBook(Long id);
	
	Gender findByName(String name);
	
    @Modifying
    @Transactional
	@Query(value = "DELETE FROM BOOK_GENDERS WHERE BOOKS_ID = ?1", nativeQuery = true)
	void removePointsBooksByBookId(long id);
    
    @Modifying
    @Transactional
	@Query(value = "DELETE FROM FILM_GENDERS WHERE FILMS_ID = ?1", nativeQuery = true)
	void removePointsFilmsByFilmId(long id);
    
    @Modifying
    @Transactional
	@Query(value = "DELETE FROM SHOWS_GENDERS WHERE SHOWS_ID = ?1", nativeQuery = true)
	void removePointShowsByShowId(long id);
    
    Gender findById(int id);
}
