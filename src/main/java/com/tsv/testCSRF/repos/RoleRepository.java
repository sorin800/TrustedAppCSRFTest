package com.tsv.testCSRF.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tsv.testCSRF.model.Role;


public interface RoleRepository extends JpaRepository<Role, Long> {

	
}
