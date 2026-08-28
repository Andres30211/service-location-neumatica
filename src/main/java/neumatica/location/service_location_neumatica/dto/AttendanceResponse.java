package neumatica.location.service_location_neumatica.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import neumatica.location.service_location_neumatica.entity.Attendance;


/*
 * DTO utilizado para enviar una asistencia
 * hacia Angular.
 *
 * Incluye:
 *
 * - información de la asistencia
 * - información de la ubicación
 * - nombre del vendedor
 */
public record AttendanceResponse(

        UUID id,

        UUID userId,

        String userName,

        LocalDate attendanceDate,

        LocalDateTime checkInAt,

        LocalDateTime checkOutAt,

        Boolean insideCompany,

        Double latitude,

        Double longitude,

        Double accuracy

) {


    /*
     * Crear respuesta SIN nombre de usuario.
     *
     * Este método puede utilizarse cuando solamente
     * necesitamos información de la asistencia.
     */
    public static AttendanceResponse fromEntity(

            Attendance attendance

    ) {

        return new AttendanceResponse(

                attendance.getId(),

                attendance.getUserId(),

                null,

                attendance.getAttendanceDate(),

                attendance.getCheckInAt(),

                attendance.getCheckOutAt(),

                attendance.getInsideCompany(),

                attendance.getLocation().getLatitude(),

                attendance.getLocation().getLongitude(),

                attendance.getLocation().getAccuracy()
        );
    }


    /*
     * Crear respuesta incluyendo
     * el nombre del vendedor.
     */
    public static AttendanceResponse fromEntity(

            Attendance attendance,

            String userName

    ) {

        return new AttendanceResponse(

                attendance.getId(),

                attendance.getUserId(),

                userName,

                attendance.getAttendanceDate(),

                attendance.getCheckInAt(),

                attendance.getCheckOutAt(),

                attendance.getInsideCompany(),

                attendance.getLocation().getLatitude(),

                attendance.getLocation().getLongitude(),

                attendance.getLocation().getAccuracy()
        );
    }
}
