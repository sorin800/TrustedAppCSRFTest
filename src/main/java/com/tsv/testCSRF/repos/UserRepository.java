package com.tsv.testCSRF.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tsv.testCSRF.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	User findByEmail(String email);

}
