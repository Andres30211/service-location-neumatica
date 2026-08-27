package neumatica.location.service_location_neumatica.controller;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import neumatica.location.service_location_neumatica.dto.AttendanceResponse;
import neumatica.location.service_location_neumatica.dto.CheckInRequest;
import neumatica.location.service_location_neumatica.repository.AttendanceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

	@Autowired
    private AttendanceService attendanceService;


    /*
     * =====================================================
     * CHECK-IN
     * =====================================================
     *
     * POST /api/attendance/check-in
     *
     * El vendedor presiona el botón "Tomar asistencia".
     */
    @PostMapping("/check-in")
    public ResponseEntity<AttendanceResponse> checkIn(

            @AuthenticationPrincipal Jwt jwt,

            @Valid
            @RequestBody
            CheckInRequest request

    ) {

        /*
         * Obtenemos el ID directamente del JWT.
         *
         * NO lo recibimos desde Angular.
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
     * =====================================================
     * CHECK-OUT
     * =====================================================
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


    /*
     * =====================================================
     * HISTORIAL
     * =====================================================
     *
     * GET /api/attendance/me
     */
    @GetMapping("/me")
    public ResponseEntity<List<AttendanceResponse>> getMyAttendances(

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


    /*
     * =====================================================
     * ASISTENCIA ACTUAL
     * =====================================================
     *
     * GET /api/attendance/me/current
     */
    @GetMapping("/me/current")
    public ResponseEntity<AttendanceResponse> getCurrentAttendance(

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
}