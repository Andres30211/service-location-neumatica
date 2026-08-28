package neumatica.location.service_location_neumatica.dto;

import java.util.Set;
import java.util.UUID;


/*
 * DTO utilizado para recibir información
 * de un usuario proveniente del security-service.
 *
 * IMPORTANTE:
 *
 * Esto NO es una entidad JPA.
 *
 * Solamente representa los datos que
 * recibimos mediante HTTP.
 */
public record UserResponse(

        UUID id,

        String name,

        String email,

        Set<String> roles,
        
        boolean enabled

) {
}
