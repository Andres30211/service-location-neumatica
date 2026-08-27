package neumatica.location.service_location_neumatica.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import neumatica.location.service_location_neumatica.dto.AttendanceResponse;
import neumatica.location.service_location_neumatica.dto.CheckInRequest;
import neumatica.location.service_location_neumatica.entity.Attendance;
import neumatica.location.service_location_neumatica.entity.Location;
import neumatica.location.service_location_neumatica.repository.AttendanceRepository;
import neumatica.location.service_location_neumatica.repository.AttendanceService;
import neumatica.location.service_location_neumatica.repository.LocationRepository;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

	@Autowired
    private AttendanceRepository attendanceRepository;

	@Autowired
    private LocationRepository locationRepository;

    /*
     * Coordenadas de la empresa.
     *
     * CAMBIA ESTOS VALORES POR LOS DE TU EMPRESA.
     */
    private static final double COMPANY_LATITUDE = 6.244203;

    private static final double COMPANY_LONGITUDE = -75.581211;

    /*
     * Radio permitido en metros.
     *
     * Por ejemplo:
     * 100 metros alrededor de la empresa.
     */
    private static final double COMPANY_RADIUS_METERS = 100.0;


    @Override
    @Transactional
    public AttendanceResponse checkIn(
            UUID userId,
            CheckInRequest request
    ) {

        /*
         * Verificamos si ya existe una asistencia abierta.
         */
        attendanceRepository
                .findFirstByUserIdAndCheckOutAtIsNullOrderByCheckInAtDesc(
                        userId
                )
                .ifPresent(attendance -> {

                    throw new RuntimeException(
                            "El usuario ya tiene una asistencia abierta"
                    );

                });


        /*
         * Creamos la ubicación.
         */
        Location location = Location.builder()

                .userId(userId)

                .latitude(request.latitude())

                .longitude(request.longitude())

                .accuracy(request.accuracy())

                .createdAt(LocalDateTime.now())

                .build();


        /*
         * Guardamos la ubicación.
         */
        Location savedLocation =
                locationRepository.save(location);


        /*
         * Calculamos si está dentro de la empresa.
         */
        boolean insideCompany =
                isInsideCompany(
                        request.latitude(),
                        request.longitude()
                );


        /*
         * Creamos la asistencia.
         */
        Attendance attendance =
                Attendance.builder()

                        .userId(userId)

                        .location(savedLocation)

                        .checkInAt(LocalDateTime.now())

                        .checkOutAt(null)

                        .insideCompany(insideCompany)

                        .build();


        /*
         * Guardamos asistencia.
         */
        Attendance savedAttendance =
                attendanceRepository.save(attendance);


        return AttendanceResponse.fromEntity(
                savedAttendance
        );
    }


    @Override
    @Transactional
    public AttendanceResponse checkOut(
            UUID userId
    ) {

        /*
         * Buscamos la asistencia abierta.
         */
        Attendance attendance =
                attendanceRepository
                        .findFirstByUserIdAndCheckOutAtIsNullOrderByCheckInAtDesc(
                                userId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "El usuario no tiene una asistencia abierta"
                                )
                        );


        /*
         * Registramos la hora de salida.
         */
        attendance.setCheckOutAt(
                LocalDateTime.now()
        );


        Attendance updatedAttendance =
                attendanceRepository.save(attendance);


        return AttendanceResponse.fromEntity(
                updatedAttendance
        );
    }


    @Override
    public List<AttendanceResponse> getUserAttendances(
            UUID userId
    ) {

        return attendanceRepository
                .findByUserIdOrderByCheckInAtDesc(userId)
                .stream()
                .map(AttendanceResponse::fromEntity)
                .toList();
    }


    @Override
    public AttendanceResponse getCurrentAttendance(
            UUID userId
    ) {

        Attendance attendance =
                attendanceRepository
                        .findFirstByUserIdAndCheckOutAtIsNullOrderByCheckInAtDesc(
                                userId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "No existe una asistencia activa"
                                )
                        );

        return AttendanceResponse.fromEntity(
                attendance
        );
    }


    /*
     * Determina si las coordenadas están dentro
     * del radio de la empresa.
     *
     * Utilizamos la fórmula de Haversine.
     */
    private boolean isInsideCompany(
            double latitude,
            double longitude
    ) {

        double earthRadius = 6371000;

        double latitudeDifference =
                Math.toRadians(
                        latitude - COMPANY_LATITUDE
                );

        double longitudeDifference =
                Math.toRadians(
                        longitude - COMPANY_LONGITUDE
                );

        double a =
                Math.sin(latitudeDifference / 2)
                        * Math.sin(latitudeDifference / 2)
                        +
                        Math.cos(
                                Math.toRadians(
                                        COMPANY_LATITUDE
                                )
                        )
                        *
                        Math.cos(
                                Math.toRadians(latitude)
                        )
                        *
                        Math.sin(
                                longitudeDifference / 2
                        )
                        *
                        Math.sin(
                                longitudeDifference / 2
                        );

        double c =
                2 * Math.atan2(
                        Math.sqrt(a),
                        Math.sqrt(1 - a)
                );

        double distance =
                earthRadius * c;


        return distance <= COMPANY_RADIUS_METERS;
    }
}
