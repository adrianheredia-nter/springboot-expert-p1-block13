# Spring Boot Expert P1 - Bloque 13

API REST de clientes de seguros y sus pólizas. El objetivo es practicar tests en las distintas capas de una aplicación Spring Boot.

## Stack

- Java 17 y Spring Boot 3.2.5
- Spring Web, Spring Data JPA y Bean Validation
- H2 en memoria
- Lombok 1.18.24
- JUnit 5, Mockito, MockMvc, Rest Assured y JaCoCo

## Arranque

```bash
./mvnw spring-boot:run
```

La API queda en `http://localhost:8080`. La base de datos se vuelve a crear en cada arranque y se carga desde `src/main/resources/data.sql` con dos clientes y tres pólizas.

La consola de H2 está en `http://localhost:8080/h2-console`, con JDBC URL `jdbc:h2:mem:insurance`, usuario `sa` y contraseña vacía.

## Modelo

`ClientJPA` guarda los datos del cliente (`idClient`, `dni`, `name`, `surname`, `birthdate`) y una lista `@OneToMany` de pólizas. `PolicyJPA` tiene un `idPolicy` autogenerado, `numPolicy`, `branch`, `subBranch` y el cliente propietario con `@ManyToOne`.

Reglas que conviene tener presentes:

- `idClient` no se genera, llega en la petición. Crear un cliente con un id que ya existe devuelve 409. En el `PUT` se usa el id de la ruta, aunque el del cuerpo sigue siendo obligatorio por validación.
- El DNI se normaliza (sin espacios alrededor, en mayúsculas y sin guion) y se comprueba la letra de control. Tiene que ser único.
- Los números de póliza son únicos. Al borrar un cliente se borran también sus pólizas.
- Las fechas van en formato `yyyy-MM-dd` y `birthdate` tiene que ser una fecha pasada.

## Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/clients` | Lista los clientes con sus pólizas |
| GET | `/api/clients/{idClient}` | Devuelve un cliente |
| GET | `/api/clients/{idClient}/policies` | Pólizas de un cliente |
| POST | `/api/clients` | Crea un cliente |
| PUT | `/api/clients/{idClient}` | Actualiza un cliente |
| DELETE | `/api/clients/{idClient}` | Borra un cliente y sus pólizas |
| GET | `/api/policies` | Lista las pólizas |
| GET | `/api/policies/{idPolicy}` | Devuelve una póliza |
| POST | `/api/policies` | Crea una póliza para un cliente existente |
| DELETE | `/api/policies/{idPolicy}` | Borra una póliza |

Los `POST` responden 201 con el recurso creado y los `DELETE` responden 204.

Ejemplo:

```bash
curl -X POST http://localhost:8080/api/clients \
  -H "Content-Type: application/json" \
  -d '{"idClient": 3, "dni": "11111111H", "name": "Ana", "surname": "Lopez", "birthdate": "1992-01-15"}'

curl -X POST http://localhost:8080/api/policies \
  -H "Content-Type: application/json" \
  -d '{"numPolicy": "POL-0004", "branch": "car", "subBranch": "full", "idClient": 3}'
```

## Errores

Todos los errores tienen el formato `{"timestamp", "status", "error", "message"}`.

| Código | Cuándo |
|--------|--------|
| 400 | cuerpo mal formado, validación fallida o DNI con formato o letra incorrectos |
| 404 | el cliente o la póliza no existe |
| 409 | id de cliente, DNI o número de póliza duplicado |

Los límites de longitud de los DTO coinciden con los de las columnas, así que un texto demasiado largo se queda en un 400 y no llega a la base de datos. Si aun así la base de datos rechaza un registro por una restricción de unicidad, se responde 409.

## Tests

```bash
./mvnw clean verify
```

`verify` ejecuta todos los tests y hace fallar el build si la cobertura de líneas baja del 80%. El informe HTML queda en `target/site/jacoco/index.html`.

| Clase | Qué cubre |
|-------|-----------|
| `InsuranceApiRestAssuredTest` | Aplicación completa en un puerto aleatorio, llamada con Rest Assured |
| `ClientControllerTest`, `PolicyControllerTest` | Controllers con MockMvc y servicios mockeados, incluidas las respuestas de error |
| `ClientServiceH2Test` | Servicios y repositorios reales contra H2 (`@DataJpaTest`) |
| `ClientServiceImplTest`, `PolicyServiceImplTest` | Lógica de servicio con `@InjectMocks` y `@Mock`, y los flujos de creación comprobados con `ArgumentCaptor` |
| `DniUtilsTest` | Normalización y validación del DNI |

## SonarQube

La ruta del informe XML de JaCoCo ya está en el pom, así que después de `verify` el análisis recoge la cobertura:

```bash
./mvnw clean verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.host.url=<url> -Dsonar.token=<token>
```

## Estructura del proyecto

```
com.nter.insurance
├── constants    rutas y mensajes compartidos
├── controller   controllers REST
├── dto          records de petición y respuesta
├── entity       entidades JPA
├── exception    excepciones propias y el handler global
├── mapper       conversión entre entidades y DTO
├── repository   repositorios de Spring Data
├── service      interfaces de servicio y sus implementaciones
└── utils        utilidades para el DNI
```
