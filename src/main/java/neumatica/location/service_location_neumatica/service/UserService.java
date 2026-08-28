package neumatica.location.service_location_neumatica.service;

import java.util.UUID;

import lombok.RequiredArgsConstructor;

import neumatica.location.service_location_neumatica.client.UserClient;
import neumatica.location.service_location_neumatica.dto.UserResponse;

import org.springframework.stereotype.Service;


/*
 * Servicio encargado de comunicarse
 * con el security-service para obtener
 * información de los vendedores.
 */
@Service
@RequiredArgsConstructor
public class UserService {


    /*
     * Cliente Feign encargado de realizar
     * las peticiones HTTP.
     */
    private final UserClient userClient;


    /*
     * =========================================================
     * OBTENER USUARIO
     * =========================================================
     *
     * Busca información del vendedor
     * utilizando su UUID.
     *
     * Ejemplo:
     *
     * userId:
     *
     * 550e8400-e29b-41d4-a716-446655440000
     *
     * Resultado:
     *
     * Carlos López
     */
    public UserResponse getUserById(
            UUID userId,
            String authorization
    ) {

        return userClient.getUserById(
                userId,
                authorization
        );
    }
}

