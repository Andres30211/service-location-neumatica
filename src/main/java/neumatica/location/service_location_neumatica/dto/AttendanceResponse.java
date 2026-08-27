package neumatica.location.service_location_neumatica.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import neumatica.location.service_location_neumatica.entity.Attendance;

public record AttendanceResponse(

        UUID id,

        UUID userId,

        UUID locationId,

        Double latitude,

        Double longitude,

        Double accuracy,

        LocalDateTime checkInAt,

        LocalDateTime checkOutAt,

        Boolean insideCompany

) {

    public static AttendanceResponse fromEntity(Attendance attendance) {

        return new AttendanceResponse(

                attendance.getId(),

                attendance.getUserId(),

                attendance.getLocation().getId(),

                attendance.getLocation().getLatitude(),

                attendance.getLocation().getLongitude(),

                attendance.getLocation().getAccuracy(),

                attendance.getCheckInAt(),

                attendance.getCheckOutAt(),

                attendance.getInsideCompany()
        );
    }
}
