package neumatica.location.service_location_neumatica.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record LocationRequest(
		
		 /*
         * Latitud:
         * -90 hasta 90
         */
        @NotNull(message = "La latitud es obligatoria")
        @DecimalMin(value = "-90.0")
        @DecimalMax(value = "90.0")
        Double latitude,

        /*
         * Longitud:
         * -180 hasta 180
         */
        @NotNull(message = "La longitud es obligatoria")
        @DecimalMin(value = "-180.0")
        @DecimalMax(value = "180.0")
        Double longitude,

        /*
         * Precisión aproximada en metros.
         */
        Double accuracy
		
		) {

}
