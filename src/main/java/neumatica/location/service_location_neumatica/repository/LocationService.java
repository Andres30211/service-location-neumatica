package neumatica.location.service_location_neumatica.repository;

import java.util.List;
import java.util.UUID;

import neumatica.location.service_location_neumatica.dto.LocationRequest;
import neumatica.location.service_location_neumatica.dto.LocationResponse;

public interface LocationService {

    /*
     * Registra una ubicación para el usuario autenticado.
     */
    LocationResponse createLocation(
            UUID userId,
            LocationRequest request
    );

    /*
     * Obtiene las ubicaciones de un usuario.
     */
    List<LocationResponse> getLocationsByUser(
            UUID userId
    );

    /*
     * Obtiene la última ubicación registrada.
     */
    LocationResponse getLastLocation(
            UUID userId
    );
}
