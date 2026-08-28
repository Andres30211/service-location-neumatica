package neumatica.location.service_location_neumatica.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import neumatica.location.service_location_neumatica.dto.AttendanceResponse;
import neumatica.location.service_location_neumatica.dto.CheckInRequest;
import neumatica.location.service_location_neumatica.dto.UserResponse;
import neumatica.location.service_location_neumatica.entity.Attendance;
import neumatica.location.service_location_neumatica.entity.Location;
import neumatica.location.service_location_neumatica.repository.AttendanceRepository;
import neumatica.location.service_location_neumatica.repository.AttendanceService;
import neumatica.location.service_location_neumatica.repository.LocationRepository;


/*
 * Implementación del servicio de asistencias.
 */
@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {


    private final AttendanceRepository attendanceRepository;

    private final LocationRepository locationRepository;

    private final UserService userService;


    /*
     * =========================================================
     * CONFIGURACIÓN DE LA EMPRESA
     * =========================================================
     */

    private static final double COMPANY_LATITUDE = 6.244203;

    private static final double COMPANY_LONGITUDE = -75.581211;

    private static final double COMPANY_RADIUS_METERS = 100.0;


    /*
     * =========================================================
     * CHECK-IN
     * =========================================================
     */
    @Override
    @Transactional
    public AttendanceResponse checkIn(

            UUID userId,

            CheckInRequest request

    ) {


        /*
         * Verificamos si el vendedor ya tiene
         * una asistencia abierta.
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
         * =====================================================
         * CREAR LOCATION
         * =====================================================
         */
        Location location =
                Location.builder()

                        .userId(userId)

                        .latitude(
                                request.latitude()
                        )

                        .longitude(
                                request.longitude()
                        )

                        .accuracy(
                                request.accuracy()
                        )

                        .createdAt(
                                LocalDateTime.now()
                        )

                        .build();


        Location savedLocation =
                locationRepository.save(location);


        /*
         * =====================================================
         * DETERMINAR SI ESTÁ DENTRO DE LA EMPRESA
         * =====================================================
         */
        boolean insideCompany =
                isInsideCompany(

                        request.latitude(),

                        request.longitude()
                );


        /*
         * =====================================================
         * CREAR ASISTENCIA
         * =====================================================
         */
        Attendance attendance =
                Attendance.builder()

                        .userId(userId)

                        .location(savedLocation)

                        /*
                         * Guardamos explícitamente
                         * el día de la asistencia.
                         */
                        .attendanceDate(
                                LocalDate.now()
                        )

                        .checkInAt(
                                LocalDateTime.now()
                        )

                        .checkOutAt(null)

                        .insideCompany(
                                insideCompany
                        )

                        .build();


        Attendance savedAttendance =
                attendanceRepository.save(
                        attendance
                );


        /*
         * Durante el check-in no necesitamos
         * consultar nuevamente al security-service.
         *
         * El vendedor ya está autenticado.
         */
        return AttendanceResponse.fromEntity(
                savedAttendance
        );
    }


    /*
     * =========================================================
     * CHECK-OUT
     * =========================================================
     */
    @Override
    @Transactional
    public AttendanceResponse checkOut(

            UUID userId

    ) {


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


        attendance.setCheckOutAt(
                LocalDateTime.now()
        );


        Attendance updatedAttendance =
                attendanceRepository.save(
                        attendance
                );


        return AttendanceResponse.fromEntity(
                updatedAttendance
        );
    }

    
    /*
     * =========================================================
     * HISTORIAL DEL USUARIO
     * =========================================================
     */
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


    /*
     * =========================================================
     * ASISTENCIA ACTUAL
     * =========================================================
     */
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
    
    @Override
    public List<AttendanceResponse> getFindAll(){
    	return this.attendanceRepository.findAll()
    			.stream()
    			.map(AttendanceResponse::fromEntity)
    			.toList();
    }


    /*
     * =========================================================
     * ASISTENCIAS DE UNA FECHA
     * =========================================================
     *
     * Este método es el que permitirá construir
     * posteriormente el panel administrativo.
     */
    @Override
    public List<AttendanceResponse> getAttendancesByDate(

            LocalDate date,

            String authorization

    ) {


        /*
         * Buscamos todas las asistencias del día.
         */
        List<Attendance> attendances =
                attendanceRepository
                        .findByAttendanceDateOrderByCheckInAtAsc(
                                date
                        );


        /*
         * Convertimos cada asistencia
         * agregando el nombre del vendedor.
         */
        return attendances
                .stream()
                .map(attendance -> {


                    /*
                     * Obtenemos el UUID que pertenece
                     * al security-service.
                     */
                    UUID userId =
                            attendance.getUserId();


                    /*
                     * Consultamos al security-service.
                     */
                    UserResponse user =
                            userService.getUserById(
                                    userId,
                                    authorization
                            );


                    /*
                     * Construimos la respuesta
                     * incluyendo el nombre.
                     */
                    return AttendanceResponse.fromEntity(
                            attendance,
                            user.name()
                    );
                })
                .toList();
    }


    /*
     * =========================================================
     * CALCULAR DISTANCIA
     * =========================================================
     *
     * Fórmula de Haversine.
     *
     * Devuelve true cuando el vendedor
     * está dentro del radio permitido.
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
                        *
                Math.sin(latitudeDifference / 2)

                        +

                Math.cos(
                        Math.toRadians(
                                COMPANY_LATITUDE
                        )
                )

                        *

                Math.cos(
                        Math.toRadians(
                                latitude
                        )
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
                2 *
                Math.atan2(
                        Math.sqrt(a),
                        Math.sqrt(1 - a)
                );


        double distance =
                earthRadius * c;


        return distance <= COMPANY_RADIUS_METERS;
    }
}
