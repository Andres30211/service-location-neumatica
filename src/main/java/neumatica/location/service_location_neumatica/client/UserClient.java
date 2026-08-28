package neumatica.location.service_location_neumatica.client;

import java.util.UUID;

import neumatica.location.service_location_neumatica.dto.UserResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;


/*
 * Cliente HTTP que permite al location-service
 * comunicarse con el security-service.
 *
 * NO existe una relación JPA.
 *
 * La comunicación se realiza mediante HTTP.
 */
@FeignClient(
        name = "security-service",
        url = "${services.security.url}"
)
public interface UserClient {


    /*
     * =========================================================
     * OBTENER USUARIO POR ID
     * =========================================================
     *
     * El location-service envía:
     *
     * GET /api/users/{id}
     *
     * al security-service.
     */
    @GetMapping("/api/users/{id}")
    UserResponse getUserById(

            @PathVariable("id")
            UUID id,

            /*
             * Enviamos el JWT del usuario autenticado.
             *
             * Esto permite que el security-service
             * pueda validar que la petición está autorizada.
             */
            @RequestHeader("Authorization")
            String authorization

    );
}

