package neumatica.location.service_location_neumatica.dto;

import jakarta.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VisitRequest {

    @NotBlank(message = "El nombre de la empresa es obligatorio")
    @Size(
        max = 200,
        message = "El nombre de la empresa no puede superar 200 caracteres"
    )
    private String companyName;

    @NotBlank(message = "El comentario es obligatorio")
    @Size(
        max = 5000,
        message = "El comentario no puede superar 5000 caracteres"
    )
    private String comment;

    @NotNull(message = "La latitud es obligatoria")
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private Double latitude;

    @NotNull(message = "La longitud es obligatoria")
    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    private Double longitude;

    private Double accuracy;
}
