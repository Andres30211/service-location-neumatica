package neumatica.location.service_location_neumatica.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import neumatica.location.service_location_neumatica.dto.LocationRequest;
import neumatica.location.service_location_neumatica.dto.LocationResponse;
import neumatica.location.service_location_neumatica.entity.Location;
import neumatica.location.service_location_neumatica.repository.LocationRepository;
import neumatica.location.service_location_neumatica.repository.LocationService;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

	@Autowired
    private LocationRepository locationRepository;

    @Override
    public LocationResponse createLocation(
            UUID userId,
            LocationRequest request
    ) {

        /*
         * Creamos la ubicación.
         */
        Location location = Location.builder()

                /*
                 * IMPORTANTE:
                 * El userId viene del JWT.
                 */
                .userId(userId)

                .latitude(request.latitude())

                .longitude(request.longitude())

                .accuracy(request.accuracy())

                .createdAt(LocalDateTime.now())

                .build();

        /*
         * Guardamos en PostgreSQL.
         */
        Location savedLocation =
                locationRepository.save(location);

        return LocationResponse.fromEntity(
                savedLocation
        );
    }

    @Override
    public List<LocationResponse> getLocationsByUser(
            UUID userId
    ) {

        return locationRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(LocationResponse::fromEntity)
                .toList();
    }

    @Override
    public LocationResponse getLastLocation(
            UUID userId
    ) {

        Location location =
                locationRepository
                        .findByUserIdOrderByCreatedAtDesc(userId)
                        .stream()
                        .findFirst()
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "El usuario no tiene ubicaciones registradas"
                                )
                        );

        return LocationResponse.fromEntity(location);
    }
}
