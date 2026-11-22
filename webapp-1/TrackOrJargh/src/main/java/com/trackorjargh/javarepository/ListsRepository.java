package com.trackorjargh.javarepository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.trackorjargh.javaclass.Lists;
import com.trackorjargh.javaclass.User;

public interface ListsRepository extends JpaRepository<Lists, Long>{

		Lists findById(Long id);
		Lists findByName(String name);
		List<Lists> findByUser(User user);
		Lists findByUserAndName(User user, String name);
	    
		@Modifying
	    @Transactional
		@Query(value = "DELETE FROM LISTS WHERE USER_ID = ?1", nativeQuery = true)
		void removeListsByUserId(long id);
	    
	    @Modifying
	    @Transactional
		@Query(value = "DELETE FROM LISTS_BOOKS WHERE BOOKS_ID = ?1", nativeQuery = true)
		void removeBooksListById(long id);
	    
	    @Modifying
	    @Transactional
		@Query(value = "DELETE FROM LISTS_FILMS WHERE FILMS_ID = ?1", nativeQuery = true)
		void removeFilmsListById(long id);
	    
	    @Modifying
	    @Transactional
		@Query(value = "DELETE FROM LISTS_SHOWS WHERE SHOWS_ID = ?1", nativeQuery = true)
		void removeShowsListById(long id);
}
