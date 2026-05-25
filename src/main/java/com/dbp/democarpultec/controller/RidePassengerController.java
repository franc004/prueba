package com.dbp.democarpultec.controller;

import com.dbp.democarpultec.dto.RidePassengerRequestDto;
import com.dbp.democarpultec.dto.RidePassengerResponseDto;
import com.dbp.democarpultec.service.RidePassengerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ride-passengers")
@RequiredArgsConstructor
public class RidePassengerController {

    private final RidePassengerService ridePassengerService;

    @GetMapping
    public List<RidePassengerResponseDto> findAll() {
        return ridePassengerService.findAll();
    }

    @GetMapping("/{id}")
    public RidePassengerResponseDto findById(@PathVariable Long id) {
        return ridePassengerService.findById(id);
    }

    @PostMapping
    public ResponseEntity<RidePassengerResponseDto> create(@Valid @RequestBody RidePassengerRequestDto ridePassenger) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ridePassengerService.create(ridePassenger));
    }

    @PutMapping("/{id}")
    public RidePassengerResponseDto update(@PathVariable Long id, @Valid @RequestBody RidePassengerRequestDto ridePassenger) {
        return ridePassengerService.update(id, ridePassenger);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ridePassengerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
