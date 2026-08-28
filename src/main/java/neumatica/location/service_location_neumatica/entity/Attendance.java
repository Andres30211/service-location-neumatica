package neumatica.location.service_location_neumatica.entity;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


/**
 * Entidad que representa la asistencia de un vendedor.
 *
 * IMPORTANTE:
 *
 * Esta entidad pertenece al location-service.
 *
 * El usuario NO tiene una relación JPA con User porque
 * User pertenece al security-service.
 *
 * Por eso únicamente almacenamos:
 *
 *      UUID userId
 *
 * Ese UUID corresponde al usuario existente
 * en el security-service.
 */
@Entity
@Table(
        name = "attendances",

        /*
         * Restricción de seguridad en la base de datos.
         *
         * Un mismo usuario solamente puede tener
         * UNA asistencia por día.
         *
         * Ejemplo:
         *
         * userId + 2026-08-28
         *
         * solamente puede existir una vez.
         */
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_attendance_user_date",
                        columnNames = {
                                "user_id",
                                "attendance_date"
                        }
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {


    /**
     * ID de la asistencia.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    /**
     * ID del vendedor.
     *
     * Este UUID pertenece al security-service.
     *
     * NO utilizamos @ManyToOne porque User
     * pertenece a otro microservicio.
     */
    @Column(
            name = "user_id",
            nullable = false
    )
    private UUID userId;


    /**
     * Fecha de la asistencia.
     *
     * Ejemplo:
     *
     * 2026-08-28
     *
     * Nos permite controlar que un vendedor
     * solamente registre asistencia una vez al día.
     */
    @Column(
            name = "attendance_date",
            nullable = false
    )
    private LocalDate attendanceDate;


    /**
     * Ubicación utilizada durante el check-in.
     *
     * Location pertenece al mismo microservicio,
     * por lo tanto aquí SÍ podemos utilizar
     * una relación JPA.
     */
    @OneToOne
    @JoinColumn(
            name = "location_id",
            nullable = false
    )
    private Location location;


    /**
     * Fecha y hora en la que se realizó
     * el check-in.
     */
    @Column(nullable = false)
    private LocalDateTime checkInAt;


    /**
     * Fecha y hora del check-out.
     *
     * Puede ser NULL mientras el vendedor
     * continúe con la asistencia abierta.
     */
    private LocalDateTime checkOutAt;


    /**
     * Indica si el vendedor estaba dentro
     * del radio permitido de la empresa
     * al realizar el check-in.
     *
     * true  = dentro de la empresa
     * false = fuera de la empresa
     */
    @Column(nullable = false)
    private Boolean insideCompany;
}

