package com.dbp.democarpultec.repository;

import com.dbp.democarpultec.model.Publication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublicationRepository extends JpaRepository<Publication, Long> {
}
