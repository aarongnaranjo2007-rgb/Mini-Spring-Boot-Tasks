# Registro de Aprendizaje y Decisiones de Diseño — Mini Tasks

## 1. Evolución por Sesiones y Ciclos TDD

Durante el desarrollo de la práctica individual **Mini Tasks**, se ha seguido una progresión incremental guiada por pruebas (TDD) en los siguientes hitos:

### Sesión 1 y 2: Entidad base y controladores en memoria
* **Objetivo:** Definir el modelo inicial y comprobar los contratos web sin levantar bases de datos externas.
* **Ciclo TDD:**
  * **Rojo:** Se escribió `TaskTest` para verificar que toda nueva tarea se inicializa con `done = false`. Falló inicialmente al no existir la clase `Task`.
  * **Verde:** Creación de la entidad `Task` con sus atributos (`id`, `title`, `done`), getters/setters y constructor por defecto.
  * **Controlador V1:** Implementación de `TaskControllerV1` usando una lista en memoria (`List<SimpleTask>`) y un contador atómico (`AtomicLong seq`).
  * **Verificación:** Pruebas con `MockMvc` (`TaskControllerV1Test`) comprobando los endpoints `GET /v1/ping`, `GET /v1/tasks` (vacío inicial) y `POST /v1/tasks` (código `201 Created`).

### Sesión 3: Búsqueda por ID y gestión de recurso inexistente (404)
* **Objetivo:** Ampliar el contrato HTTP de V1 para recuperar tareas por identificador.
* **Ciclo TDD:**
  * **Rojo:** Se añadió el método de prueba `getById_shouldReturnTaskWhenExists` y `getById_shouldReturn404WhenNotFound`. Ambas fallaron inicialmente con código de ruta no mapeada (404/405).
  * **Verde:** Se añadió el endpoint `@GetMapping("/tasks/{id}")` en `TaskControllerV1` devolviendo `ResponseEntity<SimpleTask>`. Se empleó `.filter(...)` sobre la lista en memoria junto con `.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build())`.
  * **Evolución V2:** Se incorporó `CreateTaskDTO` con validaciones `@NotBlank` y `TaskControllerV2` retornando cabeceras `Location: /v2/tasks/{id}`.

### Sesión 4: Capa de Servicio y Reglas de Negocio con TDD
* **Objetivo:** Extraer la lógica fuera del controlador, desacoplar el modelo público mediante DTOs y persistir sobre base de datos H2 mediante Spring Data JPA.
* **Ciclo TDD (`createStripsTitleEdges`):**
  * **Rojo:** En `TaskServiceTest`, se definió la prueba unitaria con Mockito (`@Mock TaskRepository`, `@InjectMocks TaskService`) que verifica que un título con espacios sobrantes en los extremos (ej. `"  Repasar Spring Boot  "`) se almacena limpio (`"Repasar Spring Boot"`) y con `done = false`. El test falló antes de implementar la clase de servicio.
  * **Verde:** Creación de `TaskService`, aplicando el método `String.strip()` sobre el valor del DTO de entrada antes de instanciar y guardar la entidad con `repository.save(...)`.

---

## 2. Separación de Responsabilidades por Capas

La arquitectura final de la aplicación respeta estrictamente el principio de responsabilidad única:

1. **Capa Web / Presentación (`Controller`):**
   * Gestiona el protocolo HTTP, las rutas REST, los parámetros de entrada (`@PathVariable`, `@RequestBody`), códigos de estado (`200`, `201`, `404`) y cabeceras (`Location`).
   * No aplica reglas de negocio ni manipula directamente la persistencia. Se comunica exclusivamente mediante DTOs (`CreateTaskDTO`, `TaskResponseDTO`).
2. **Capa de Negocio (`Service`):**
   * Implementa los casos de uso del sistema bajo `@Service` y `@Transactional`.
   * Aplica las reglas del dominio (por ejemplo, saneamiento de cadenas mediante `.strip()`).
   * Convierte las entidades de base de datos a DTOs de salida para no exponer el modelo persistente directamente al cliente.
3. **Capa de Persistencia (`Repository`):**
   * Interfaz `TaskRepository` que extiende `JpaRepository<Task, Long>`.
   * Spring Data JPA e Hibernate gestionan las operaciones CRUD sobre la base de datos H2 en memoria.

---

## 3. Estado de los Tests

Todas las suites de prueba (`TaskTest`, `TaskControllerV1Test`, `TaskControllerV2Test` y `TaskServiceTest`) finalizan en verde ejecutando:

```bash
./mvnw test
```
