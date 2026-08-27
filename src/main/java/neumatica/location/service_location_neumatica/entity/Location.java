package neumatica.location.service_location_neumatica.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "locations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /*
     * ID del usuario perteneciente al auth-service.
     *
     * IMPORTANTE:
     * Esto NO es una relación JPA.
     *
     * Es solamente el UUID que identifica al vendedor.
     */
    @Column(nullable = false)
    private UUID userId;

    /*
     * Latitud obtenida mediante GPS.
     */
    @Column(nullable = false)
    private Double latitude;

    /*
     * Longitud obtenida mediante GPS.
     */
    @Column(nullable = false)
    private Double longitude;

    /*
     * Precisión aproximada del GPS en metros.
     */
    private Double accuracy;

    /*
     * Fecha y hora en la que se registró la ubicación.
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
