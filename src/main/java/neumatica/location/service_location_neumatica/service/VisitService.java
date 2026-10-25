package neumatica.location.service_location_neumatica.service;

import lombok.RequiredArgsConstructor;

import neumatica.location.service_location_neumatica.dto.UserResponse;
import neumatica.location.service_location_neumatica.dto.VisitRequest;
import neumatica.location.service_location_neumatica.entity.Visit;
import neumatica.location.service_location_neumatica.enums.StateVisit;
import neumatica.location.service_location_neumatica.record.RegisterCheckoutRequest;
import neumatica.location.service_location_neumatica.record.VisitCreateListDeleteResponse;
import neumatica.location.service_location_neumatica.repository.VisitRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
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
    private final Path uploadDirectory = Paths.get("uploads", "visits");


    /*
     * =========================================================
     * CREAR VISITA
     * =========================================================
     */

    @Transactional
    public VisitCreateListDeleteResponse createVisit(VisitRequest request,
            MultipartFile image,
            UUID userId,
            String authorization) throws IOException {

        /*
         * -----------------------------------------------------
         * 1. Validar imagen
         * -----------------------------------------------------
         */

        this.validateImage(image);


        /*
         * -----------------------------------------------------
         * 2. Consultar usuario mediante security-service
         * -----------------------------------------------------
         */

        UserResponse user = this.userService.getUserById(userId,authorization);


        /*
         * -----------------------------------------------------
         * 3. Guardar imagen físicamente
         * -----------------------------------------------------
         */

        String imageUrl = this.saveImage(image);


        /*
         * -----------------------------------------------------
         * 4. Crear entidad
         * -----------------------------------------------------
         */
Visit visit = Visit.builder()
                        .userId(userId)
                        .companyName(request.getCompanyName().trim())
                        .comment(request.getComment().trim())
                        .imageUrl(imageUrl)
                        .latitude(request.getLatitude())
                        .longitude(request.getLongitude())
                        .accuracy(request.getAccuracy())
                        .visitedAt(OffsetDateTime.now())
                        .createdAt(OffsetDateTime.now())
                        .departureLatitude(null)
                        .departureLongitude(null)
                        .precisionGpsOutput(null)
                        .departureDateTime(null)
                        .state(StateVisit.ON_GOING)
                        .build();


        /*
         * -----------------------------------------------------
         * 5. Guardar visita en base de datos
         * -----------------------------------------------------
         */

        Visit saved = this.visitRepository.save(visit);


        /*
         * -----------------------------------------------------
         * 6. Responder
         * -----------------------------------------------------
         */

        return toVisitResponse(saved,user);
    }
    
    @Transactional
    public VisitCreateListDeleteResponse registrarSalida(UUID id,
    		UUID userId,
            String authorization,
    		RegisterCheckoutRequest request) {

        Visit visit = this.visitRepository.findById(id)
            .orElseThrow(() ->
                new EntityNotFoundException("Visita no encontrada"));

        if (visit.getState() != StateVisit.ON_GOING) {
            throw new RuntimeException(
                "La visita ya está finalizada.");
        }
        
        UserResponse user = this.userService.getUserById(userId,authorization);

        visit.setDepartureLatitude(request.departureLatitude());
        visit.setDepartureLongitude(request.departureLongitude());
        visit.setPrecisionGpsOutput(request.precisionGpsOutput());

        // El servidor determina la hora de salida.
        visit.setDepartureDateTime(LocalDateTime.now());

        visit.setState(StateVisit.FINALIZED);

        this.visitRepository.save(visit);

        return this.toVisitResponse(visit, user);
    }


    /*
     * =========================================================
     * MIS VISITAS
     * =========================================================
     */

    public List<VisitCreateListDeleteResponse> getMyVisits(UUID userId, String authorization) {

        UserResponse user = userService.getUserById(userId, authorization);


        return this.visitRepository.findByUserIdOrderByVisitedAtDesc(userId)
                .stream()
                .map(visit -> this.toVisitResponse(visit, user))
                .toList();
    }


    /*
     * =========================================================
     * TODAS LAS VISITAS
     * =========================================================
     */

    public List<VisitCreateListDeleteResponse> getAllVisits(String authorization) {

        return visitRepository
                .findAllByOrderByVisitedAtDesc()
                .stream()
                .map(visit -> {

                	UserResponse user = this.userService.getUserById(visit.getUserId(), authorization);

                	return this.toVisitResponse(visit, user);
                }).toList();
    }


    /*
     * =========================================================
     * BUSCAR VISITA POR ID
     * =========================================================
     */
    
    public VisitCreateListDeleteResponse getVisitDeleteInfo(UUID visitId, String authorization) {

        Visit visit = this.visitRepository.findById(visitId)
                        .orElseThrow(() -> new RuntimeException("Visita no encontrada."));

        UserResponse user = this.userService.getUserById(visit.getUserId(), authorization);

        return new VisitCreateListDeleteResponse(visit.getId(),
        		visit.getUserId(),
        		user.getName(),
        		visit.getCompanyName(),
        		visit.getComment(),
        		null,
        		null,
        		null,
        		null,
        		null,
        		visit.getCreatedAt(),
        		null,
        		null,
        		null,
        		null,
        		null);
    }

    /*public VisitResponse getById(UUID visitId, String authorization) {

        Visit visit = this.visitRepository.findById(visitId)
                        .orElseThrow(() -> new RuntimeException("Visita no encontrada."));

        UserResponse user = this.userService.getUserById(visit.getUserId(), authorization);

        return toResponse(visit,user);
    }*/
    
    /*
     * =========================================================
     * ELIMINAR VISITA
     * =========================================================
     */
    
    public void deleteVisit(UUID visitId) {

        Visit visit = visitRepository.findById(visitId)
            .orElseThrow(() ->
                new EntityNotFoundException(
                    "No se encontró la visita con id: " + visitId
                )
            );

        String imageUrl = visit.getImageUrl();

        // Eliminar el registro de la visita.
        visitRepository.delete(visit);

        // Eliminar la imagen asociada, si existe.
        if (imageUrl != null && !imageUrl.isBlank()) {
            try {
                // Obtener únicamente el nombre del archivo.
                String fileName = Paths.get(imageUrl)
                    .getFileName()
                    .toString();

                // Directorio físico donde se guardan las imágenes.
                Path uploadPath = Paths.get("uploads")
                    .toAbsolutePath()
                    .normalize();

                Path visitsPath = uploadPath.resolve("visits").normalize();

                Path imagePath = visitsPath.resolve(fileName).normalize();

                // Validar que el archivo permanezca dentro del directorio.
                if (!imagePath.startsWith(visitsPath)) {
                    throw new SecurityException(
                        "Ruta de imagen no permitida"
                    );
                }

                // Eliminar el archivo si existe.
                Files.deleteIfExists(imagePath);

            } catch (IOException e) {
                throw new IllegalStateException(
                    "No se pudo eliminar la imagen de la visita.",
                    e
                );
            }
        
        }
    }


    /*
     * =========================================================
     * GUARDAR IMAGEN
     * =========================================================
     */

    private String saveImage(MultipartFile image) throws IOException {

        /*
         * -----------------------------------------------------
         * Crear carpeta si no existe
         * -----------------------------------------------------
         */

        Files.createDirectories(uploadDirectory);

        /*
         * -----------------------------------------------------
         * Obtener extensión original
         * -----------------------------------------------------
         */

        String originalFilename = image.getOriginalFilename();


        String extension = "";


        if (originalFilename != null && originalFilename.contains(".")) {

            extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
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

        String fileName = UUID.randomUUID() + extension;

        /*
         * -----------------------------------------------------
         * Ruta final
         * -----------------------------------------------------
         */

        Path destination = uploadDirectory.resolve(fileName);

        /*
         * -----------------------------------------------------
         * Copiar archivo
         * -----------------------------------------------------
         */

        Files.copy(image.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        /*
         * -----------------------------------------------------
         * Retornar ruta que se almacenará en BD
         * -----------------------------------------------------
         *
         * Ejemplo:
         *
         * /uploads/visits/550e8400-e29b-41d4-a716-446655440000.jpg
         */
        
        System.out.println("/uploads/visits/" + fileName);

        return "/uploads/visits/" + fileName;
    }


    /*
     * =========================================================
     * CONVERTIR ENTITY → DTO
     * =========================================================
     */

    private VisitCreateListDeleteResponse toVisitResponse(Visit visit, UserResponse user) {

        return new VisitCreateListDeleteResponse(visit.getId(),
        		visit.getUserId(),
        		user != null ? user.getName() : "Usuario",
        		visit.getCompanyName(),
        		visit.getComment(),
        		visit.getImageUrl(),
        		visit.getLatitude(),
        		visit.getLongitude(),
        		visit.getAccuracy(),
        		visit.getVisitedAt(),
        		visit.getCreatedAt(),
        		visit.getDepartureLatitude(),
        		visit.getDepartureLongitude(),
        		visit.getPrecisionGpsOutput(),
        		visit.getDepartureDateTime(),
        		visit.getState());
    }


    /*
     * =========================================================
     * VALIDAR IMAGEN
     * =========================================================
     */

    private void validateImage(MultipartFile image) {

        /*
         * -----------------------------------------------------
         * La imagen es obligatoria
         * -----------------------------------------------------
         */

        if (image == null || image.isEmpty()) {

            throw new IllegalArgumentException("Debes subir una fotografía.");
        }


        /*
         * -----------------------------------------------------
         * Validar tipo MIME
         * -----------------------------------------------------
         */

        String contentType = image.getContentType();

        if (contentType == null || !contentType.startsWith("image/")) {

            throw new IllegalArgumentException("El archivo debe ser una imagen.");
        }

        /*
         * -----------------------------------------------------
         * Máximo 5 MB
         * -----------------------------------------------------
         */

        long maxSize = 5L * 1024 * 1024;

        if (image.getSize() > maxSize) {

            throw new IllegalArgumentException("La imagen no puede superar 5 MB.");
        }
    }
}