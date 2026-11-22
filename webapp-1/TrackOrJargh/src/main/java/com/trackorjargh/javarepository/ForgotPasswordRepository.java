package com.trackorjargh.javarepository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trackorjargh.javaclass.ForgotPassword;
import com.trackorjargh.javaclass.User;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, Long>{
	ForgotPassword findBySecretAlphanumeric(String secretAlphanumeric);
	ForgotPassword findByUser(User user);
}
