package neumatica.location.service_location_neumatica.repository;

import neumatica.location.service_location_neumatica.entity.Visit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface VisitRepository
        extends JpaRepository<Visit, UUID> {

    /*
     * Visitas realizadas por un vendedor.
     */
    List<Visit> findByUserIdOrderByVisitedAtDesc(
        UUID userId
    );

    /*
     * Todas las visitas ordenadas
     * de la más reciente a la más antigua.
     */
    List<Visit> findAllByOrderByVisitedAtDesc();

    /*
     * Visitas realizadas entre dos fechas.
     */
    List<Visit> findByVisitedAtBetweenOrderByVisitedAtDesc(
        OffsetDateTime start,
        OffsetDateTime end
    );
}
