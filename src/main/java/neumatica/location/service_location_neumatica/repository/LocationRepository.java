package neumatica.location.service_location_neumatica.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import neumatica.location.service_location_neumatica.entity.Location;

public interface LocationRepository extends JpaRepository<Location, UUID>{
	
	 /*
     * Busca todas las ubicaciones de un usuario.
     */
    List<Location> findByUserId(UUID userId);

    /*
     * Busca las ubicaciones ordenadas de la más reciente
     * a la más antigua.
     */
    List<Location> findByUserIdOrderByCreatedAtDesc(UUID userId);

}
