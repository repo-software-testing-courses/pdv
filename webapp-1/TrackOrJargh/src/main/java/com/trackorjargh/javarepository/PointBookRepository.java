package com.trackorjargh.javarepository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.trackorjargh.javaclass.Book;
import com.trackorjargh.javaclass.PointBook;
import com.trackorjargh.javaclass.User;

public interface PointBookRepository extends JpaRepository<PointBook, Long>{
	PointBook findByUserAndBook(User user, Book book);
	List<PointBook> findByBook(Book book);
	
    @Modifying
    @Transactional
	@Query(value = "DELETE FROM POINT_BOOK WHERE USER_ID = ?1", nativeQuery = true)
	void removePointsBooksByUserId(long id);
    
    @Modifying
    @Transactional
	@Query(value = "DELETE FROM POINT_BOOK WHERE BOOK_ID = ?1", nativeQuery = true)
	void removePointsBooksByBookId(long id);
    
    PointBook findById(Long id);
}
