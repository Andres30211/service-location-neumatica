package neumatica.location.service_location_neumatica.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import neumatica.location.service_location_neumatica.entity.Location;

public record LocationResponse(
		
		UUID id,

        UUID userId,

        Double latitude,

        Double longitude,

        Double accuracy,

        LocalDateTime createdAt
	
	) {
	
	public static LocationResponse fromEntity(Location location) {

        return new LocationResponse(
                location.getId(),
                location.getUserId(),
                location.getLatitude(),
                location.getLongitude(),
                location.getAccuracy(),
                location.getCreatedAt()
        );
    }

}
