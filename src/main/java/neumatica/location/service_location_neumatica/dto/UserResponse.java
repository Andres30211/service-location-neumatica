package neumatica.location.service_location_neumatica.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserResponse {

    private UUID id;

    private String name;

    private String email;
}
