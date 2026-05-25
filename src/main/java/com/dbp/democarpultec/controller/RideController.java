package com.dbp.democarpultec.controller;

import com.dbp.democarpultec.dto.RideRequestDto;
import com.dbp.democarpultec.dto.RideResponseDto;
import com.dbp.democarpultec.service.RideService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rides")
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;

    @GetMapping
    public List<RideResponseDto> findAll() {
        return rideService.findAll();
    }

    @GetMapping("/{id}")
    public RideResponseDto findById(@PathVariable Long id) {
        return rideService.findById(id);
    }

    @PostMapping
    public ResponseEntity<RideResponseDto> create(@Valid @RequestBody RideRequestDto ride) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rideService.create(ride));
    }

    @PutMapping("/{id}")
    public RideResponseDto update(@PathVariable Long id, @Valid @RequestBody RideRequestDto ride) {
        return rideService.update(id, ride);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        rideService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
