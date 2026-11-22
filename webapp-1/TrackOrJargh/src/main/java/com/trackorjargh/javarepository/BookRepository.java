package com.trackorjargh.javarepository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.trackorjargh.javaclass.Book;

public interface BookRepository extends JpaRepository<Book, Long>{
	
	@Query(value="Select max(id) from Book", nativeQuery=true)
	Long findLastId();
	
	
	//SELECT TOP 10 * FROM FILM ORDER BY ID DESC
	@Query(value="Select * from Book order by id desc limit ?1", nativeQuery=true)
	List<Book> findByLastAdded(int additions);
	Page<Book> findByNameContainingIgnoreCase(String name, Pageable pageable);
	
    @Query(value = "SELECT BOOK.* FROM POINT_BOOK INNER JOIN BOOK ON POINT_BOOK.BOOK_ID=BOOK.ID GROUP BY POINT_BOOK.BOOK_ID ORDER BY AVG(POINT_BOOK.POINTS) DESC \n-- #pageable\n", 
    		countQuery = "SELECT COUNT(*) FROM POINT_BOOK GROUP BY BOOK_ID",
    		nativeQuery = true)
    Page<Book> findBestPointBook(Pageable pageable);
    
    @Query(value = "SELECT BOOK.* FROM BOOK_GENDERS INNER JOIN BOOK ON BOOK_GENDERS.BOOKS_ID=BOOK.ID WHERE BOOK_GENDERS.GENDERS_ID IN (SELECT ID FROM GENDER WHERE LOWER(NAME) LIKE LOWER(?1)) \n-- #pageable\n",
    		countQuery = "SELECT COUNT(BOOK.ID) FROM BOOK_GENDERS INNER JOIN BOOK ON BOOK_GENDERS.BOOKS_ID=BOOK.ID WHERE BOOK_GENDERS.GENDERS_ID IN (SELECT ID FROM GENDER WHERE LOWER(NAME) LIKE LOWER(?1))",
    		nativeQuery = true)
    Page<Book> findBooksByGender(String gender, Pageable pageable);
    
    @Query(value = "SELECT BOOK.* FROM BOOK_GENDERS INNER JOIN BOOK ON BOOK_GENDERS.BOOKS_ID=BOOK.ID WHERE BOOK_GENDERS.GENDERS_ID IN (SELECT GENDERS_ID FROM FILM_GENDERS WHERE FILMS_ID = ?1) AND BOOK.ID != ?1 \n-- #pageable\n",
    		countQuery = "SELECT COUNT(BOOK.ID) FROM BOOK_GENDERS INNER JOIN BOOK ON BOOK_GENDERS.BOOKS_ID=BOOK.ID WHERE BOOK_GENDERS.GENDERS_ID IN (SELECT GENDERS_ID FROM FILM_GENDERS WHERE FILMS_ID = ?1) AND BOOK.ID != ?1",
    		nativeQuery = true)
    Page<Book> findBooksRelationsById(long id, Pageable pageable);
    
	Book findById(Long id);
	Book findByNameIgnoreCase(String name);
}

