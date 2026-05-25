package com.dbp.democarpultec.controller;

import com.dbp.democarpultec.dto.VehicleRequestDto;
import com.dbp.democarpultec.dto.VehicleResponseDto;
import com.dbp.democarpultec.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping
    public List<VehicleResponseDto> findAll() {
        return vehicleService.findAll();
    }

    @GetMapping("/{id}")
    public VehicleResponseDto findById(@PathVariable Long id) {
        return vehicleService.findById(id);
    }

    @PostMapping
    public ResponseEntity<VehicleResponseDto> create(@Valid @RequestBody VehicleRequestDto vehicle) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleService.create(vehicle));
    }

    @PutMapping("/{id}")
    public VehicleResponseDto update(@PathVariable Long id, @Valid @RequestBody VehicleRequestDto vehicle) {
        return vehicleService.update(id, vehicle);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
