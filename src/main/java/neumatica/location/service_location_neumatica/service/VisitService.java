package neumatica.location.service_location_neumatica.service;

import lombok.RequiredArgsConstructor;

import neumatica.location.service_location_neumatica.dto.UserResponse;
import neumatica.location.service_location_neumatica.dto.VisitRequest;
import neumatica.location.service_location_neumatica.dto.VisitResponse;
import neumatica.location.service_location_neumatica.entity.Visit;
import neumatica.location.service_location_neumatica.repository.VisitRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VisitService {

    private final VisitRepository visitRepository;

    private final UserService userService;


    /*
     * =========================================================
     * CONFIGURACIÓN DE ALMACENAMIENTO
     * =========================================================
     */

    /**
     * Carpeta donde se almacenarán las fotografías.
     *
     * La carpeta quedará en:
     *
     * uploads/visits/
     *
     * respecto a la raíz desde donde se ejecuta el microservicio.
     */
    private final Path uploadDirectory =
            Paths.get("uploads", "visits");


    /*
     * =========================================================
     * CREAR VISITA
     * =========================================================
     */

    public VisitResponse createVisit(
            VisitRequest request,
            MultipartFile image,
            UUID userId,
            String authorization
    ) throws IOException {

        /*
         * -----------------------------------------------------
         * 1. Validar imagen
         * -----------------------------------------------------
         */

        validateImage(image);


        /*
         * -----------------------------------------------------
         * 2. Consultar usuario mediante security-service
         * -----------------------------------------------------
         */

        UserResponse user =
                userService.getUserById(
                        userId,
                        authorization
                );


        /*
         * -----------------------------------------------------
         * 3. Guardar imagen físicamente
         * -----------------------------------------------------
         */

        String imageUrl =
                saveImage(image);


        /*
         * -----------------------------------------------------
         * 4. Crear entidad
         * -----------------------------------------------------
         */

        Visit visit =
                Visit.builder()

                        .userId(userId)

                        .companyName(
                                request.getCompanyName().trim()
                        )

                        .comment(
                                request.getComment().trim()
                        )

                        .imageUrl(imageUrl)

                        .latitude(
                                request.getLatitude()
                        )

                        .longitude(
                                request.getLongitude()
                        )

                        .accuracy(
                                request.getAccuracy()
                        )

                        .visitedAt(
                                OffsetDateTime.now()
                        )

                        .createdAt(
                                OffsetDateTime.now()
                        )

                        .build();


        /*
         * -----------------------------------------------------
         * 5. Guardar visita en base de datos
         * -----------------------------------------------------
         */

        Visit saved =
                visitRepository.save(visit);


        /*
         * -----------------------------------------------------
         * 6. Responder
         * -----------------------------------------------------
         */

        return toResponse(
                saved,
                user
        );
    }


    /*
     * =========================================================
     * MIS VISITAS
     * =========================================================
     */

    public List<VisitResponse> getMyVisits(
            UUID userId,
            String authorization
    ) {

        UserResponse user =
                userService.getUserById(
                        userId,
                        authorization
                );


        return visitRepository
                .findByUserIdOrderByVisitedAtDesc(userId)
                .stream()
                .map(
                        visit ->
                                toResponse(
                                        visit,
                                        user
                                )
                )
                .toList();
    }


    /*
     * =========================================================
     * TODAS LAS VISITAS
     * =========================================================
     */

    public List<VisitResponse> getAllVisits(
            String authorization
    ) {

        return visitRepository
                .findAllByOrderByVisitedAtDesc()
                .stream()
                .map(
                        visit -> {

                            UserResponse user =
                                    userService.getUserById(
                                            visit.getUserId(),
                                            authorization
                                    );

                            return toResponse(
                                    visit,
                                    user
                            );
                        }
                )
                .toList();
    }


    /*
     * =========================================================
     * BUSCAR VISITA POR ID
     * =========================================================
     */

    public VisitResponse getById(
            UUID visitId,
            String authorization
    ) {

        Visit visit =
                visitRepository
                        .findById(visitId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Visita no encontrada."
                                )
                        );


        UserResponse user =
                userService.getUserById(
                        visit.getUserId(),
                        authorization
                );


        return toResponse(
                visit,
                user
        );
    }


    /*
     * =========================================================
     * GUARDAR IMAGEN
     * =========================================================
     */

    private String saveImage(
            MultipartFile image
    ) throws IOException {

        /*
         * -----------------------------------------------------
         * Crear carpeta si no existe
         * -----------------------------------------------------
         */

        Files.createDirectories(
                uploadDirectory
        );


        /*
         * -----------------------------------------------------
         * Obtener extensión original
         * -----------------------------------------------------
         */

        String originalFilename =
                image.getOriginalFilename();


        String extension = "";


        if (
                originalFilename != null &&
                originalFilename.contains(".")
        ) {

            extension =
                    originalFilename.substring(
                            originalFilename.lastIndexOf(".")
                    ).toLowerCase();
        }


        /*
         * -----------------------------------------------------
         * Generar nombre único
         * -----------------------------------------------------
         *
         * No usamos el nombre original porque podrían existir
         * archivos con el mismo nombre.
         *
         * Ejemplo:
         *
         * 550e8400-e29b-41d4-a716-446655440000.jpg
         */

        String fileName =
                UUID.randomUUID() + extension;


        /*
         * -----------------------------------------------------
         * Ruta final
         * -----------------------------------------------------
         */

        Path destination =
                uploadDirectory.resolve(fileName);


        /*
         * -----------------------------------------------------
         * Copiar archivo
         * -----------------------------------------------------
         */

        Files.copy(
                image.getInputStream(),
                destination,
                StandardCopyOption.REPLACE_EXISTING
        );


        /*
         * -----------------------------------------------------
         * Retornar ruta que se almacenará en BD
         * -----------------------------------------------------
         *
         * Ejemplo:
         *
         * /uploads/visits/550e8400-e29b-41d4-a716-446655440000.jpg
         */

        return "/uploads/visits/" + fileName;
    }


    /*
     * =========================================================
     * CONVERTIR ENTITY → DTO
     * =========================================================
     */

    private VisitResponse toResponse(
            Visit visit,
            UserResponse user
    ) {

        return VisitResponse.builder()

                .id(
                        visit.getId()
                )

                .userId(
                        visit.getUserId()
                )

                .userName(
                        user != null
                                ? user.getName()
                                : "Usuario"
                )

                .companyName(
                        visit.getCompanyName()
                )

                .comment(
                        visit.getComment()
                )

                .imageUrl(
                        visit.getImageUrl()
                )

                .latitude(
                        visit.getLatitude()
                )

                .longitude(
                        visit.getLongitude()
                )

                .accuracy(
                        visit.getAccuracy()
                )

                .visitedAt(
                        visit.getVisitedAt()
                )

                .createdAt(
                        visit.getCreatedAt()
                )

                .build();
    }


    /*
     * =========================================================
     * VALIDAR IMAGEN
     * =========================================================
     */

    private void validateImage(
            MultipartFile image
    ) {

        /*
         * -----------------------------------------------------
         * La imagen es obligatoria
         * -----------------------------------------------------
         */

        if (
                image == null ||
                image.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Debes subir una fotografía."
            );
        }


        /*
         * -----------------------------------------------------
         * Validar tipo MIME
         * -----------------------------------------------------
         */

        String contentType =
                image.getContentType();


        if (
                contentType == null ||
                !contentType.startsWith("image/")
        ) {

            throw new IllegalArgumentException(
                    "El archivo debe ser una imagen."
            );
        }


        /*
         * -----------------------------------------------------
         * Máximo 5 MB
         * -----------------------------------------------------
         */

        long maxSize =
                5L * 1024 * 1024;


        if (
                image.getSize() > maxSize
        ) {

            throw new IllegalArgumentException(
                    "La imagen no puede superar 5 MB."
            );
        }
    }
}