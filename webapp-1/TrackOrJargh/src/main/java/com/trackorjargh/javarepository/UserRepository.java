package com.trackorjargh.javarepository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.trackorjargh.javaclass.User;

public interface UserRepository extends JpaRepository<User, Long>{
	
	User findByNameIgnoreCase(String name);
	User findByEmail(String email);
	
    @Modifying
    @Transactional
	@Query(value = "DELETE FROM USER_ROLES WHERE USER_ID = ?1", nativeQuery = true)
	void removeRoles(long id);
}
