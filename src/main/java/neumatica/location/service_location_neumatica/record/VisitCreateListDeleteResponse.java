package neumatica.location.service_location_neumatica.record;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

import neumatica.location.service_location_neumatica.enums.StateVisit;

public record VisitCreateListDeleteResponse(
		
		 UUID id,

	     UUID userId,

	     String userName,

	     String companyName,

	     String comment,

	     String imageUrl,

	     Double latitude,

	     Double longitude,

	     Double accuracy,

	     OffsetDateTime visitedAt,

	     OffsetDateTime createdAt,
	     
	     Double departureLatitude,
	     
		 Double departureLongitude,

		 Double precisionGpsOutput,

		 OffsetDateTime departureDateTime,

		 StateVisit state) {

}