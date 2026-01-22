Visión general
La API es una aplicación REST desarrollada con Spring Boot y Maven que gestiona el modelo de almacén (palets, estanterías, baldas, ubicaciones, etc.). Sigue una arquitectura en capas (controller → service → repository) usando Spring Data JPA para persistencia sobre una base de datos relacional.
Arquitectura por capas
Capa de presentación (Controladores REST): expone endpoints HTTP JSON; validación de entrada y manejo de códigos HTTP.
Capa de servicio (Servicios): lógica de negocio, orquestación de repositorios, transacciones y reglas de integridad (p. ej. evitar ubicaciones duplicadas).
Capa de datos (Repositorios JPA): abstracción sobre la persistencia mediante interfaces que extienden JpaRepository.
Modelos/Entidades: entidades JPA que representan Palet, Estanteria, etc.
DTOs y mapeo: separación entre entidades y representación externa usando DTOs; mapeo manual o con MapStruct.
Modelo de datos (resumen)
Entidad Palet con identificador (idPalet), referencia a ubicación (estanteria, balda, posicion, delante) y metadatos (tipo, peso, fecha).
Claves y restricciones: unicidad lógica sobre la combinación de ubicación para evitar colisiones de palets.
Repositorios y consultas
Uso de Spring Data JPA: interfaces que extienden JpaRepository<Entity, IdType> para CRUD básico.
Consultas derivadas por nombre: p. ej. en src/main/java/uvigo/tfgalmacen/almacenapi/repository/PaletRepository.java se declara un método booleano que verifica si existe otro palet ocupando una ubicación determinada. Ese método aprovecha la convención de nombres de Spring Data para generar la consulta SQL automáticamente y se usa para validar antes de mover/crear un palet.
Servicios y gestión de transacciones
Los métodos de servicio que modifican estado usan @Transactional cuando se requiere consistencia en varias operaciones.
Validaciones: antes de persistir se comprueba existencia de colisiones (consulta existsBy...) y se lanza excepción controlada si hay conflicto (mapeada a 409 Conflict).
Controladores REST
Endpoints REST semánticos (p. ej. GET /palets, POST /palets, PUT /palets/{id}, DELETE /palets/{id}).
Uso de códigos HTTP adecuados: 200/201/204 para éxito, 400 para petición inválida, 404 para no encontrado, 409 para conflicto de ubicación.
Soporte opcional de paginación y filtros mediante parámetros query (Pageable).
Validación y manejo de errores
Validación con javax.validation (@Valid, @NotNull, rangos) en DTOs.
Centralización de errores con @ControllerAdvice que convierte excepciones de negocio en respuestas JSON con código y mensaje.
Seguridad y configuración
Autenticación/autorización (opcional) con Spring Security (roles ADMIN, USER) para proteger endpoints de modificación.
Configuración a través de application.properties/application.yml; perfiles para dev/test/prod.
Pruebas y calidad
Pruebas unitarias para servicios y repositories (JPA tests con @DataJpaTest).
Tests de integración de controladores con @SpringBootTest o @WebMvcTest.
Uso de herramientas estáticas (Checkstyle/SpotBugs) y CI para asegurar calidad.
Consideraciones operativas
Manejo de concurrencia: comprobaciones optimistas/pesimistas si varias peticiones pueden colisionar al asignar ubicaciones.
Logs estructurados y métricas básicas para monitorización.
Documentación de API con OpenAPI/Swagger para facilitar pruebas y entrega.