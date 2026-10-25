package neumatica.location.service_location_neumatica.controller;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import neumatica.location.service_location_neumatica.dto.VisitRequest;
import neumatica.location.service_location_neumatica.record.RegisterCheckoutRequest;
import neumatica.location.service_location_neumatica.record.VisitCreateListDeleteResponse;
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
     * OBTENER VISITA
     * =========================================================
     */
    
    @GetMapping("/{visitId}/delete-info")
    public ResponseEntity<VisitCreateListDeleteResponse> getVisitDeleteInfo(@PathVariable UUID visitId,
		@RequestHeader("Authorization") String authorization){
    	
    	return ResponseEntity.ok(this.visitService.getVisitDeleteInfo(visitId, authorization));
    }
    
    /*
     * =========================================================
     * ELIMINAR VISITA
     * =========================================================
     */
    
    @DeleteMapping("/{visitId}")
    public ResponseEntity<?> deleteVisit(@PathVariable UUID visitId) {
    	
    	this.visitService.deleteVisit(visitId);
    	
    	return ResponseEntity.noContent().build();
    }

    /*
     * =========================================================
     * CREAR VISITA
     * =========================================================
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VisitCreateListDeleteResponse> createVisit(

        @Valid
        @RequestPart("data")
        VisitRequest request,

        @RequestPart("image")
        MultipartFile image,

        @AuthenticationPrincipal
        Jwt jwt,

        @RequestHeader("Authorization")
        String authorization) throws Exception {

        UUID userId = UUID.fromString(jwt.getSubject());

        VisitCreateListDeleteResponse response = visitService.createVisit(request, image, userId, authorization);

        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{id}/salida")
    public ResponseEntity<VisitCreateListDeleteResponse> registrarSalida(@PathVariable UUID id,

    		@AuthenticationPrincipal
            Jwt jwt,
            
    		@RequestHeader("Authorization")
            String authorization,
            
            @Valid @RequestBody RegisterCheckoutRequest request) {
    	
    	UUID userId = UUID.fromString(jwt.getSubject());

    	VisitCreateListDeleteResponse response = this.visitService.registrarSalida(id, userId, authorization, request);

        return ResponseEntity.ok(response);
    }


    /*
     * =========================================================
     * MIS VISITAS
     * =========================================================
     */
    @GetMapping("/me")
    public ResponseEntity<List<VisitCreateListDeleteResponse>> getMyVisits(

        @AuthenticationPrincipal
        Jwt jwt,

        @RequestHeader("Authorization")
        String authorization) {

        UUID userId = UUID.fromString(jwt.getSubject());


        return ResponseEntity.ok(this.visitService.getMyVisits(userId, authorization));
    }


    /*
     * =========================================================
     * TODAS LAS VISITAS
     * =========================================================
     */
    @GetMapping
    public ResponseEntity<List<VisitCreateListDeleteResponse>> getAllVisits(

        @RequestHeader("Authorization")
        String authorization) {

        return ResponseEntity.ok(visitService.getAllVisits(authorization));
    }

}
