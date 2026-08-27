package neumatica.location.service_location_neumatica.controller;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import neumatica.location.service_location_neumatica.dto.LocationRequest;
import neumatica.location.service_location_neumatica.dto.LocationResponse;
import neumatica.location.service_location_neumatica.repository.LocationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

	@Autowired
    private LocationService locationService;


    /*
     * Registrar una ubicación.
     *
     * POST /api/locations
     */
    @PostMapping
    public ResponseEntity<LocationResponse> createLocation(

            @AuthenticationPrincipal Jwt jwt,

            @Valid
            @RequestBody
            LocationRequest request

    ) {

        /*
         * El "sub" del JWT contiene el ID del usuario.
         */
        UUID userId =
                UUID.fromString(
                        jwt.getSubject()
                );


        return ResponseEntity.ok(
                locationService.createLocation(
                        userId,
                        request
                )
        );
    }


    /*
     * Obtener las ubicaciones del usuario autenticado.
     *
     * GET /api/locations/me
     */
    @GetMapping("/me")
    public ResponseEntity<List<LocationResponse>> getMyLocations(

            @AuthenticationPrincipal Jwt jwt

    ) {

        UUID userId =
                UUID.fromString(
                        jwt.getSubject()
                );


        return ResponseEntity.ok(
                locationService.getLocationsByUser(
                        userId
                )
        );
    }


    /*
     * Obtener la última ubicación del usuario.
     *
     * GET /api/locations/me/last
     */
    @GetMapping("/me/last")
    public ResponseEntity<LocationResponse> getLastLocation(

            @AuthenticationPrincipal Jwt jwt

    ) {

        UUID userId =
                UUID.fromString(
                        jwt.getSubject()
                );


        return ResponseEntity.ok(
                locationService.getLastLocation(
                        userId
                )
        );
    }
}
