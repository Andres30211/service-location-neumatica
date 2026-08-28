package neumatica.location.service_location_neumatica.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import neumatica.location.service_location_neumatica.dto.AttendanceResponse;
import neumatica.location.service_location_neumatica.dto.CheckInRequest;


/*
 * Contrato del servicio de asistencias.
 */
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
     * Historial del vendedor autenticado.
     */
    List<AttendanceResponse> getUserAttendances(
            UUID userId
    );


    /*
     * Asistencia actualmente abierta.
     */
    AttendanceResponse getCurrentAttendance(
            UUID userId
    );


    /*
     * =========================================================
     * ASISTENCIAS DE UNA FECHA
     * =========================================================
     *
     * Permite al administrador consultar
     * todos los vendedores que registraron
     * asistencia durante un día.
     */
    List<AttendanceResponse> getAttendancesByDate(
            LocalDate date,
            String authorization
    );
    
    List<AttendanceResponse> getFindAll(String authorization);
    
    
}
