package com.dbp.democarpultec.service;

import com.dbp.democarpultec.dto.VehicleRequestDto;
import com.dbp.democarpultec.dto.VehicleResponseDto;
import com.dbp.democarpultec.model.Vehicle;
import com.dbp.democarpultec.repository.VehicleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserService userService;

    public List<VehicleResponseDto> findAll() {
        return vehicleRepository.findAll().stream().map(this::toResponseDto).toList();
    }

    public VehicleResponseDto findById(Long id) {
        return toResponseDto(findEntityById(id));
    }

    public VehicleResponseDto create(VehicleRequestDto dto) {
        Vehicle vehicle = new Vehicle();
        updateEntity(vehicle, dto);
        return toResponseDto(vehicleRepository.save(vehicle));
    }

    public VehicleResponseDto update(Long id, VehicleRequestDto dto) {
        Vehicle vehicle = findEntityById(id);
        updateEntity(vehicle, dto);
        return toResponseDto(vehicleRepository.save(vehicle));
    }

    public void delete(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new EntityNotFoundException("Vehicle not found with id " + id);
        }
        vehicleRepository.deleteById(id);
    }

    public Vehicle findEntityById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with id " + id));
    }

    private void updateEntity(Vehicle vehicle, VehicleRequestDto dto) {
        vehicle.setOwner(userService.findEntityById(dto.getOwnerId()));
        vehicle.setPlate(dto.getPlate());
        vehicle.setBrand(dto.getBrand());
        vehicle.setModel(dto.getModel());
        vehicle.setColor(dto.getColor());
        vehicle.setSeats(dto.getSeats());
    }

    private VehicleResponseDto toResponseDto(Vehicle vehicle) {
        return VehicleResponseDto.builder()
                .id(vehicle.getId())
                .ownerId(vehicle.getOwner().getId())
                .plate(vehicle.getPlate())
                .brand(vehicle.getBrand())
                .model(vehicle.getModel())
                .color(vehicle.getColor())
                .seats(vehicle.getSeats())
                .build();
    }
}
