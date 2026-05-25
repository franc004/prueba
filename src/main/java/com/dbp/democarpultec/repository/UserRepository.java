package com.dbp.democarpultec.repository;

import com.dbp.democarpultec.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
