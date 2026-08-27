package neumatica.location.service_location_neumatica.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record CheckInRequest(
		
		@NotNull(message = "La latitud es obligatoria")
        @DecimalMin("-90.0")
        @DecimalMax("90.0")
        Double latitude,

        @NotNull(message = "La longitud es obligatoria")
        @DecimalMin("-180.0")
        @DecimalMax("180.0")
        Double longitude,

        /*
         * Precisión del GPS.
         */
        Double accuracy
	
	) {

}
