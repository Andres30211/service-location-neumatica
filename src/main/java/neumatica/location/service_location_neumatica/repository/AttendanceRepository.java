package neumatica.location.service_location_neumatica.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import neumatica.location.service_location_neumatica.entity.Attendance;


public interface AttendanceRepository
        extends JpaRepository<Attendance, UUID> {


    /*
     * Historial de un vendedor.
     */
    List<Attendance>
    findByUserIdOrderByCheckInAtDesc(
            UUID userId
    );


    /*
     * Última asistencia del vendedor.
     */
    Optional<Attendance>
    findFirstByUserIdOrderByCheckInAtDesc(
            UUID userId
    );


    /*
     * Asistencia actualmente abierta.
     */
    Optional<Attendance>
    findFirstByUserIdAndCheckOutAtIsNullOrderByCheckInAtDesc(
            UUID userId
    );


    /*
     * =========================================================
     * ASISTENCIAS DE UN DÍA
     * =========================================================
     *
     * Este método será utilizado por el administrador
     * para visualizar la asistencia de todos los vendedores
     * en una fecha determinada.
     */
    List<Attendance>
    findByAttendanceDateOrderByCheckInAtAsc(
            LocalDate attendanceDate
    );
}
