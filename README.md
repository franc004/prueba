# CarpUlTEC

CarpUlTEC es una API REST construida con Spring Boot para gestionar usuarios, vehiculos, publicaciones, viajes, pasajeros, solicitudes y resenas dentro de una aplicacion de carpooling universitario.

## Tecnologias

- Java 17
- Spring Boot 3.4.5
- Spring Web
- Spring Data JPA
- Spring Validation
- PostgreSQL
- Lombok
- JUnit 5
- Testcontainers
- Maven Wrapper

## Estructura principal

```text
src/main/java/com/dbp/democarpultec
├── controller
├── dto
├── exception
├── model
├── repository
└── service
```

## Endpoints principales

La API expone recursos REST bajo el prefijo `/api`:

- `/api/users`
- `/api/vehicles`
- `/api/rides`
- `/api/ride-passengers`
- `/api/reviews`
- `/api/publications`
- `/api/request-publications`

Cada controlador incluye operaciones CRUD basicas usando metodos `GET`, `POST`, `PUT` y `DELETE`.

## Requisitos

- JDK 17 instalado
- Docker instalado y en ejecucion para pruebas con Testcontainers
- PostgreSQL configurado si se desea ejecutar la aplicacion con una base de datos local

## Ejecutar el proyecto

En Windows:

```bash
mvnw.cmd spring-boot:run
```

En macOS/Linux:

```bash
./mvnw spring-boot:run
```

Por defecto, la aplicacion inicia en:

```text
http://localhost:8080
```

## Ejecutar pruebas

En Windows:

```bash
mvnw.cmd test
```

En macOS/Linux:

```bash
./mvnw test
```

## Configuracion

La configuracion base se encuentra en:

```text
src/main/resources/application.properties
```

Actualmente contiene el nombre de la aplicacion:

```properties
spring.application.name=demoCarpultec
```
