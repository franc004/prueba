package com.dbp.democarpultec.repository;

import com.dbp.democarpultec.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
}
