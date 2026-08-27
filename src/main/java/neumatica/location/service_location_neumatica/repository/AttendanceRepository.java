package neumatica.location.service_location_neumatica.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import neumatica.location.service_location_neumatica.entity.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance, UUID>{
	
	/*
     * Busca las asistencias de un usuario.
     */
    List<Attendance> findByUserIdOrderByCheckInAtDesc(UUID userId);

    /*
     * Busca la última asistencia del usuario.
     */
    Optional<Attendance> findFirstByUserIdOrderByCheckInAtDesc(UUID userId);

    /*
     * Busca si el usuario tiene actualmente
     * una asistencia abierta.
     */
    Optional<Attendance> findFirstByUserIdAndCheckOutAtIsNullOrderByCheckInAtDesc(
            UUID userId
    );

}
