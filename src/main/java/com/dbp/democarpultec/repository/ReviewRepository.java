package com.dbp.democarpultec.repository;

import com.dbp.democarpultec.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
