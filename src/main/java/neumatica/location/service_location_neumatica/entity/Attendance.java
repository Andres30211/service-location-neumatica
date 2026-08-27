package neumatica.location.service_location_neumatica.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "attendances")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /*
     * ID del vendedor.
     *
     * Pertenece al auth-service.
     */
    @Column(nullable = false)
    private UUID userId;

    /*
     * Ubicación utilizada durante el check-in.
     *
     * Aquí sí existe una relación dentro del MISMO
     * microservicio.
     */
    @OneToOne
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    /*
     * Fecha y hora de entrada.
     */
    @Column(nullable = false)
    private LocalDateTime checkInAt;

    /*
     * Fecha y hora de salida.
     *
     * Puede ser NULL hasta que el vendedor
     * realice el check-out.
     */
    private LocalDateTime checkOutAt;

    /*
     * Indica si el vendedor estaba dentro de la empresa
     * cuando registró la asistencia.
     */
    @Column(nullable = false)
    private Boolean insideCompany;
    
}
