package com.dbp.democarpultec.controller;

import com.dbp.democarpultec.dto.RequestPublicationRequestDto;
import com.dbp.democarpultec.dto.RequestPublicationResponseDto;
import com.dbp.democarpultec.service.RequestPublicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/request-publications")
@RequiredArgsConstructor
public class RequestPublicationController {

    private final RequestPublicationService requestPublicationService;

    @GetMapping
    public List<RequestPublicationResponseDto> findAll() {
        return requestPublicationService.findAll();
    }

    @GetMapping("/{id}")
    public RequestPublicationResponseDto findById(@PathVariable Long id) {
        return requestPublicationService.findById(id);
    }

    @PostMapping
    public ResponseEntity<RequestPublicationResponseDto> create(@Valid @RequestBody RequestPublicationRequestDto requestPublication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(requestPublicationService.create(requestPublication));
    }

    @PutMapping("/{id}")
    public RequestPublicationResponseDto update(@PathVariable Long id, @Valid @RequestBody RequestPublicationRequestDto requestPublication) {
        return requestPublicationService.update(id, requestPublication);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        requestPublicationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
