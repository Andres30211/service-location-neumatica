package neumatica.location.service_location_neumatica.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import neumatica.location.service_location_neumatica.dto.AttendanceResponse;
import neumatica.location.service_location_neumatica.dto.CheckInRequest;
import neumatica.location.service_location_neumatica.repository.AttendanceService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;


/*
 * Controlador encargado de las asistencias.
 */
@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {


    private final AttendanceService attendanceService;


    /*
     * =========================================================
     * CHECK-IN
     * =========================================================
     *
     * POST /api/attendance/check-in
     */
    @PostMapping("/check-in")
    public ResponseEntity<AttendanceResponse> checkIn(

            @AuthenticationPrincipal Jwt jwt,

            @Valid
            @RequestBody
            CheckInRequest request

    ) {


        /*
         * El UUID del vendedor está
         * dentro del "sub" del JWT.
         */
        UUID userId =
                UUID.fromString(
                        jwt.getSubject()
                );


        return ResponseEntity.ok(

                attendanceService.checkIn(
                        userId,
                        request
                )
        );
    }


    /*
     * =========================================================
     * CHECK-OUT
     * =========================================================
     *
     * POST /api/attendance/check-out
     */
    @PostMapping("/check-out")
    public ResponseEntity<AttendanceResponse> checkOut(

            @AuthenticationPrincipal Jwt jwt

    ) {


        UUID userId =
                UUID.fromString(
                        jwt.getSubject()
                );


        return ResponseEntity.ok(

                attendanceService.checkOut(
                        userId
                )
        );
    }


    /**
     * =========================================================
     * MIS ASISTENCIAS
     * =========================================================
     *
     * GET /api/attendance/me
     */
    @GetMapping("/me")
    public ResponseEntity<List<AttendanceResponse>>
    getMyAttendances(

            @AuthenticationPrincipal Jwt jwt

    ) {


        UUID userId =
                UUID.fromString(
                        jwt.getSubject()
                );


        return ResponseEntity.ok(

                attendanceService.getUserAttendances(
                        userId
                )
        );
    }


    /**
     * =========================================================
     * ASISTENCIA ACTUAL
     * =========================================================
     *
     * GET /api/attendance/me/current
     */
    @GetMapping("/me/current")
    public ResponseEntity<AttendanceResponse>
    getCurrentAttendance(

            @AuthenticationPrincipal Jwt jwt

    ) {


        UUID userId =
                UUID.fromString(
                        jwt.getSubject()
                );


        return ResponseEntity.ok(

                attendanceService.getCurrentAttendance(
                        userId
                )
        );
    }


    /**
     * =========================================================
     * ASISTENCIAS DE UNA FECHA
     * =========================================================
     *
     * GET /api/attendance/date/2026-08-28
     *
     * Este endpoint será utilizado por el administrador.
     *
     * Devuelve:
     *
     * - vendedor
     * - hora de entrada
     * - hora de salida
     * - ubicación
     * - precisión GPS
     * - si estaba dentro de la empresa
     */
    @GetMapping("/date/{date}")
    public ResponseEntity<List<AttendanceResponse>>
    getAttendancesByDate(

            @PathVariable
            LocalDate date,

            @AuthenticationPrincipal
            Jwt jwt

    ) {


        /*
         * Recuperamos el JWT completo.
         *
         * Lo enviamos al security-service
         * para consultar los vendedores.
         */
        String authorization =
                "Bearer " + jwt.getTokenValue();


        return ResponseEntity.ok(

                attendanceService.getAttendancesByDate(
                        date,
                        authorization
                )
        );
    }
}
