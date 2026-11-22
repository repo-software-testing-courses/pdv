package com.trackorjargh.javarepository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.trackorjargh.javaclass.CommentShow;
import com.trackorjargh.javaclass.Shows;

public interface CommentShowRepository extends JpaRepository<CommentShow, Long>{
	List<CommentShow> findByShow(Shows show);
	List<CommentShow> findByShowOrderByIdAsc(Shows show);
	
    @Modifying
    @Transactional
	@Query(value = "DELETE FROM COMMENT_SHOW WHERE USER_ID = ?1", nativeQuery = true)
	void removeCommentsShowsByUserId(long id);
    
    @Modifying
    @Transactional
	@Query(value = "DELETE FROM COMMENT_SHOW WHERE SHOW_ID = ?1", nativeQuery = true)
	void removeCommentsShowsByShowId(long id);
    
    CommentShow findById(Long id);
}
