package com.dbp.democarpultec.controller;

import com.dbp.democarpultec.dto.PublicationRequestDto;
import com.dbp.democarpultec.dto.PublicationResponseDto;
import com.dbp.democarpultec.service.PublicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/publications")
@RequiredArgsConstructor
public class PublicationController {

    private final PublicationService publicationService;

    @GetMapping
    public List<PublicationResponseDto> findAll() {
        return publicationService.findAll();
    }

    @GetMapping("/{id}")
    public PublicationResponseDto findById(@PathVariable Long id) {
        return publicationService.findById(id);
    }

    @PostMapping
    public ResponseEntity<PublicationResponseDto> create(@Valid @RequestBody PublicationRequestDto publication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(publicationService.create(publication));
    }

    @PutMapping("/{id}")
    public PublicationResponseDto update(@PathVariable Long id, @Valid @RequestBody PublicationRequestDto publication) {
        return publicationService.update(id, publication);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        publicationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
