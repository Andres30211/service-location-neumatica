package neumatica.location.service_location_neumatica.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "visits",
    indexes = {
        @Index(name = "idx_visit_user_id", columnList = "user_id"),
        @Index(name = "idx_visit_visited_at", columnList = "visited_at"),
        @Index(name = "idx_visit_company_name", columnList = "company_name")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /*
     * ID del usuario proveniente del security-service.
     *
     * NO existe relación JPA.
     */
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    /*
     * Nombre de la empresa visitada.
     */
    @Column(
        name = "company_name",
        nullable = false,
        length = 200
    )
    private String companyName;

    /*
     * Comentario realizado por el vendedor.
     */
    @Column(
        name = "comment",
        nullable = false,
        columnDefinition = "TEXT"
    )
    private String comment;

    /*
     * URL de la fotografía.
     *
     * La imagen NO se almacena directamente
     * en PostgreSQL.
     */
    @Column(
        name = "image_url",
        nullable = false,
        columnDefinition = "TEXT"
    )
    private String imageUrl;

    /*
     * Coordenadas GPS.
     */
    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    /*
     * Precisión proporcionada por el GPS
     * en metros.
     */
    private Double accuracy;

    /*
     * Fecha/hora en que el vendedor
     * realizó la visita.
     */
    @Column(
        name = "visited_at",
        nullable = false
    )
    private OffsetDateTime visitedAt;

    /*
     * Fecha/hora de creación del registro.
     */
    @Column(
        name = "created_at",
        nullable = false
    )
    private OffsetDateTime createdAt;
}
