package neumatica.location.service_location_neumatica.repository;

import java.util.List;
import java.util.UUID;

import neumatica.location.service_location_neumatica.dto.AttendanceResponse;
import neumatica.location.service_location_neumatica.dto.CheckInRequest;

public interface AttendanceService {

    /*
     * Registrar entrada.
     */
    AttendanceResponse checkIn(
            UUID userId,
            CheckInRequest request
    );

    /*
     * Registrar salida.
     */
    AttendanceResponse checkOut(
            UUID userId
    );

    /*
     * Obtener historial del usuario.
     */
    List<AttendanceResponse> getUserAttendances(
            UUID userId
    );

    /*
     * Obtener asistencia actualmente abierta.
     */
    AttendanceResponse getCurrentAttendance(
            UUID userId
    );
}
