package neumatica.location.service_location_neumatica.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
public class VisitResponse {

    private UUID id;

    private UUID userId;

    private String userName;

    private String companyName;

    private String comment;

    private String imageUrl;

    private Double latitude;

    private Double longitude;

    private Double accuracy;

    private OffsetDateTime visitedAt;

    private OffsetDateTime createdAt;
}
