package neumatica.location.service_location_neumatica.controller;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import neumatica.location.service_location_neumatica.dto.VisitRequest;
import neumatica.location.service_location_neumatica.dto.VisitResponse;
import neumatica.location.service_location_neumatica.service.VisitService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/visits")
@RequiredArgsConstructor
public class VisitController {

    private final VisitService visitService;


    /*
     * =========================================================
     * CREAR VISITA
     * =========================================================
     */
    @PostMapping(
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<VisitResponse> createVisit(

        @Valid
        @RequestPart("data")
        VisitRequest request,

        @RequestPart("image")
        MultipartFile image,

        @AuthenticationPrincipal
        Jwt jwt,

        @RequestHeader("Authorization")
        String authorization

    ) throws Exception {

        UUID userId =
            UUID.fromString(
                jwt.getSubject()
            );


        VisitResponse response =
            visitService.createVisit(
                request,
                image,
                userId,
                authorization
            );


        return ResponseEntity.ok(response);
    }


    /*
     * =========================================================
     * MIS VISITAS
     * =========================================================
     */
    @GetMapping("/me")
    public ResponseEntity<List<VisitResponse>> getMyVisits(

        @AuthenticationPrincipal
        Jwt jwt,

        @RequestHeader("Authorization")
        String authorization

    ) {

        UUID userId =
            UUID.fromString(
                jwt.getSubject()
            );


        return ResponseEntity.ok(
            visitService.getMyVisits(
                userId,
                authorization
            )
        );
    }


    /*
     * =========================================================
     * TODAS LAS VISITAS
     * =========================================================
     */
    @GetMapping
    public ResponseEntity<List<VisitResponse>> getAllVisits(

        @RequestHeader("Authorization")
        String authorization

    ) {

        return ResponseEntity.ok(
            visitService.getAllVisits(
                authorization
            )
        );
    }


    /*
     * =========================================================
     * OBTENER VISITA
     * =========================================================
     */
    @GetMapping("/{id}")
    public ResponseEntity<VisitResponse> getVisit(

        @PathVariable UUID id,

        @RequestHeader("Authorization")
        String authorization

    ) {

        return ResponseEntity.ok(
            visitService.getById(
                id,
                authorization
            )
        );
    }
}
