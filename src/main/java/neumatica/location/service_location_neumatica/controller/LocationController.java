package neumatica.location.service_location_neumatica.controller;

import java.util.List;
import java.util.UUID;

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


/*
 * Controlador encargado de gestionar
 * las ubicaciones GPS.
 *
 * NOTA:
 *
 * La ubicación normalmente se registra
 * automáticamente durante el check-in.
 *
 * Estos endpoints pueden servir para consultar
 * las ubicaciones posteriormente.
 */
@RestController
@RequestMapping("/api/locations")
public class LocationController {


    /*
     * Servicio de ubicaciones.
     */
	@Autowired
    private LocationService locationService;


    /*
     * =========================================================
     * CREAR UBICACIÓN
     * =========================================================
     *
     * POST /api/locations
     *
     * Registra manualmente una ubicación.
     *
     * Para el flujo normal de asistencia,
     * realmente no necesitamos llamar este endpoint
     * desde Angular porque el check-in ya crea
     * automáticamente la Location.
     */
    @PostMapping
    public ResponseEntity<LocationResponse> createLocation(

            @AuthenticationPrincipal Jwt jwt,

            @Valid
            @RequestBody
            LocationRequest request

    ) {


        /*
         * Obtenemos el UUID desde el JWT.
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
     * =========================================================
     * MIS UBICACIONES
     * =========================================================
     *
     * GET /api/locations/me
     *
     * Obtiene las ubicaciones del usuario
     * actualmente autenticado.
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
     * =========================================================
     * ÚLTIMA UBICACIÓN
     * =========================================================
     *
     * GET /api/locations/me/last
     *
     * Obtiene la última ubicación registrada
     * del vendedor autenticado.
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
