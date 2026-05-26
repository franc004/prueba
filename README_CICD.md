# CarpUlTEC — Guía del Repositorio y CI/CD

**Proyecto:** CarpUlTEC — Plataforma de carpooling universitario  
**Stack:** Java 17 · Spring Boot 3.4.5 · PostgreSQL · Docker · AWS ECS Fargate  
**CI/CD:** GitHub Actions → Amazon ECR → ECS Fargate

---

## Tabla de Contenidos

1. [Estructura del Repositorio](#1-estructura-del-repositorio)
2. [Arquitectura de la Aplicación](#2-arquitectura-de-la-aplicación)
3. [Suite de Tests](#3-suite-de-tests)
4. [Cómo Correr los Tests Localmente](#4-cómo-correr-los-tests-localmente)
5. [Pipeline CI/CD con GitHub Actions](#5-pipeline-cicd-con-github-actions)
6. [Configurar el Repo para usar GitHub Actions](#6-configurar-el-repo-para-usar-github-actions)
7. [Flujo de Trabajo Diario](#7-flujo-de-trabajo-diario)
8. [Variables de Entorno y Secrets](#8-variables-de-entorno-y-secrets)
9. [Problemas Comunes](#9-problemas-comunes)

---

## 1. Estructura del Repositorio

```
carpUlTEC/
├── .github/
│   └── workflows/
│       └── deploy-ecs.yml          # Pipeline CI/CD completo
│
├── aws/
│   └── ecs-task-definition.json    # Definición de la tarea ECS Fargate
│
├── src/
│   ├── main/
│   │   ├── java/com/dbp/democarpultec/
│   │   │   ├── DemoCarpultecApplication.java   # Entry point
│   │   │   ├── controller/     # 7 REST controllers (endpoints HTTP)
│   │   │   ├── service/        # 7 servicios (lógica de negocio)
│   │   │   ├── repository/     # 7 repositorios (acceso a BD)
│   │   │   ├── model/          # 7 entidades JPA + 2 enums
│   │   │   ├── dto/            # 14 DTOs (Request/Response por entidad)
│   │   │   └── exception/      # RestExceptionHandler global
│   │   └── resources/
│   │       └── application.properties  # Config via variables de entorno
│   │
│   └── test/
│       ├── java/com/dbp/democarpultec/
│       │   ├── controller/     # 7 tests de integración de controllers
│       │   ├── service/        # 7 tests unitarios de servicios
│       │   └── repository/     # 1 test de repositorio
│       └── resources/
│           └── application.properties  # Config de test (H2 in-memory)
│
├── Dockerfile                  # Multi-stage build: JDK build → JRE runtime
├── docker-compose.yml          # Entorno local con PostgreSQL
├── pom.xml                     # Dependencias Maven
└── INFORME_DEPLOY_AWS.md       # Informe completo del despliegue AWS
```

---

## 2. Arquitectura de la Aplicación

La API sigue una arquitectura en capas estándar de Spring Boot:

```
HTTP Request
     │
     ▼
Controller (REST)          ← valida input con @Valid, mapea DTO → llama al service
     │
     ▼
Service                    ← lógica de negocio, lanza excepciones si no encuentra datos
     │
     ▼
Repository (JPA)           ← extiende JpaRepository, acceso a la BD
     │
     ▼
PostgreSQL (prod) / H2 (test)
```

### Entidades del dominio

| Entidad | Descripción |
|---------|-------------|
| `User` | Usuario de la plataforma (estudiante UTEC) |
| `Vehicle` | Vehículo registrado por un usuario |
| `Publication` | Oferta de carpooling publicada por un conductor |
| `RequestPublication` | Solicitud de pasajero para unirse a una publicación |
| `Ride` | Viaje activo |
| `RidePassenger` | Relación pasajero ↔ viaje |
| `Review` | Reseña/calificación de un viaje |

### Endpoints disponibles

Todos los recursos implementan CRUD completo bajo `/api/{recurso}`:

```
GET    /api/users
GET    /api/users/{id}
POST   /api/users
PUT    /api/users/{id}
DELETE /api/users/{id}

# Igual para: vehicles, publications, request-publications,
#             rides, ride-passengers, reviews
```

**Health check** (usado por el ALB):
```
GET /actuator/health  →  {"status":"UP"}
```

---

## 3. Suite de Tests

El proyecto tiene **16 clases de test** con más de 100 métodos, organizadas en tres capas:

### Capa 1 — Tests Unitarios de Servicio (7 clases)

Usan **Mockito** para aislar la lógica de negocio sin tocar la BD.  
Cada servicio tiene tests para: crear, obtener por ID, lanzar excepción si no existe, actualizar, eliminar.

| Clase | Tests |
|-------|-------|
| `UserServiceTest` | 5 métodos |
| `VehicleServiceTest` | 5 métodos |
| `PublicationServiceTest` | 5 métodos |
| `RequestPublicationServiceTest` | 5 métodos |
| `RideServiceTest` | 5 métodos |
| `RidePassengerServiceTest` | 5 métodos |
| `ReviewServiceTest` | 5 métodos |

Ejemplo de lo que verifican:
```
shouldCreateUserWhenValidData()
shouldReturnUserWhenIdExists()
shouldThrowExceptionWhenUserNotFound()   ← verifica que el service lanza 404
shouldUpdateUserWhenValidData()
shouldDeleteUserWhenUserExists()
```

### Capa 2 — Tests de Integración de Controller (7 clases)

Usan **`@WebMvcTest` + MockMvc + Mockito** para probar los endpoints HTTP completos: serialización JSON, códigos de respuesta, validaciones `@Valid`.

| Clase | Tests |
|-------|-------|
| `UserControllerTest` | ~10 métodos |
| `VehicleControllerTest` | 11 métodos |
| `PublicationControllerTest` | 12 métodos |
| `RequestPublicationControllerTest` | 11 métodos |
| `RideControllerTest` | 10 métodos |
| `RidePassengerControllerTest` | 11 métodos |
| `ReviewControllerTest` | 12 métodos |

Ejemplo de lo que verifican:
```
shouldReturnAllUsersWhenUsersExist()         → GET /api/users → 200
shouldReturn404WhenUserNotFound()            → GET /api/users/99 → 404
shouldCreateUserWhenValidRequest()           → POST /api/users → 201
shouldReturn400WhenRequestIsMissingFields()  → POST con body incompleto → 400
```

### Capa 3 — Test de Repositorio (1 clase)

Usa **H2 in-memory** para verificar las operaciones JPA directamente contra la BD.

| Clase | Tests |
|-------|-------|
| `UserRepositoryTest` | 5 métodos: save, findById, findAll, delete, existsById |

### Base de datos en tests

Los tests **no necesitan PostgreSQL**. El `application.properties` de test usa **H2 en memoria**:

```properties
# src/test/resources/application.properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=create-drop
```

Esto permite correr todos los tests sin ninguna dependencia externa.

---

## 4. Cómo Correr los Tests Localmente

### Requisitos

- Java 17 instalado
- Maven (o usar el wrapper incluido `./mvnw`)

### Comandos

```bash
# Correr todos los tests
./mvnw test

# Correr solo los tests de un módulo específico
./mvnw test -Dtest=UserServiceTest
./mvnw test -Dtest=UserControllerTest

# Correr con reporte detallado
./mvnw test -Dsurefire.useFile=false

# En Windows PowerShell
.\mvnw.cmd test
```

### Resultado esperado

```
[INFO] Tests run: 106, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## 5. Pipeline CI/CD con GitHub Actions

El archivo `.github/workflows/deploy-ecs.yml` define el pipeline completo.

### Triggers (cuándo se ejecuta)

```yaml
on:
  push:
    branches:
      - main          # Automático en cada push a main
  workflow_dispatch:  # Manual desde la UI de GitHub Actions
```

### Estructura del pipeline

```
┌─────────────────────────────────────┐
│  Job 1: test (ubuntu-latest)        │
│                                     │
│  1. Checkout código                 │
│  2. Setup Java 17 (Temurin)         │
│  3. ./mvnw test  (106 tests)        │
└──────────────┬──────────────────────┘
               │ needs: test (solo continúa si los tests pasan)
               ▼
┌─────────────────────────────────────┐
│  Job 2: deploy (ubuntu-latest)      │
│  environment: production            │
│                                     │
│  1. Checkout código                 │
│  2. Configure AWS credentials       │
│  3. Login to Amazon ECR             │
│  4. Docker build + push → ECR       │
│     - Tag: <commit-sha>             │
│     - Tag: latest                   │
│  5. Render task definition          │
│     (inyecta la nueva imagen)       │
│  6. Set grace period (180s)         │
│  7. Deploy task def → ECS           │
│     (espera estabilidad)            │
└─────────────────────────────────────┘
```

### Detalles de cada paso del deploy

| Paso | Qué hace |
|------|----------|
| Configure AWS credentials | Usa las 3 credenciales del Learner Lab (key, secret, **session token**) |
| Docker build | Construye imagen multi-stage: JDK para compilar → JRE solo para runtime |
| Push ECR | Sube dos tags: el SHA del commit (trazabilidad) y `latest` |
| Render task definition | Reemplaza el campo `image` en `aws/ecs-task-definition.json` con la nueva imagen |
| Set grace period | `aws ecs update-service --health-check-grace-period-seconds 180` — garantiza que el ALB no mate la tarea mientras arranca |
| Deploy | Registra nueva revisión del task definition, actualiza el ECS service, espera que estabilice |

### Tiempo aproximado de ejecución

| Fase | Tiempo |
|------|--------|
| Tests | ~2 min |
| Docker build + push | ~2 min |
| ECS deploy + health check | ~3-4 min |
| **Total** | **~7-8 min** |

---

## 6. Configurar el Repo para usar GitHub Actions

### Paso 1 — Fork o clone del repo

```bash
git clone https://github.com/franc004/prueba.git
cd prueba
```

### Paso 2 — Configurar los Secrets en GitHub

Ve a **Settings → Secrets and variables → Actions → Secrets** y agrega:

| Secret | Dónde obtenerlo |
|--------|----------------|
| `AWS_ACCESS_KEY_ID` | AWS Academy Learner Lab → "AWS Details" → "Show" |
| `AWS_SECRET_ACCESS_KEY` | AWS Academy Learner Lab → "AWS Details" → "Show" |
| `AWS_SESSION_TOKEN` | AWS Academy Learner Lab → "AWS Details" → "Show" |

> **Importante:** Las credenciales del Learner Lab **expiran cada 4 horas**. Debes actualizarlas en GitHub Secrets antes de cada sesión de trabajo.

Los tres valores van en la sección **Secrets** (no Variables). Deben estar en mayúsculas exactamente como se muestran arriba.

### Paso 3 — Verificar la infraestructura AWS activa

Antes de hacer un push, confirma que los servicios AWS estén corriendo:

```bash
# En AWS CloudShell
aws ecs describe-services \
  --cluster carpultec-cluster \
  --services carpultec-service \
  --region us-east-1 \
  --query "services[0].{status:status,running:runningCount,desired:desiredCount}"
```

Si `running: 0`, reactiva el servicio:

```bash
aws ecs update-service \
  --cluster carpultec-cluster \
  --service carpultec-service \
  --desired-count 1 \
  --region us-east-1
```

### Paso 4 — Hacer un push para disparar el pipeline

```bash
git add .
git commit -m "feat: descripción del cambio"
git push origin main
```

El pipeline se activa automáticamente. Monitoréalo en la pestaña **Actions** del repositorio.

---

## 7. Flujo de Trabajo Diario

### Al inicio de cada sesión

```bash
# 1. Iniciar el Learner Lab en AWS Academy

# 2. Actualizar los 3 secrets en GitHub
#    Settings → Secrets → Actions → editar cada uno

# 3. Reactivar el ECS service si está en 0 tareas
aws ecs update-service \
  --cluster carpultec-cluster \
  --service carpultec-service \
  --desired-count 1 \
  --region us-east-1
```

### Al finalizar cada sesión (para ahorrar créditos)

```bash
# Reducir tareas a 0 — Fargate cobra por tarea activa
aws ecs update-service \
  --cluster carpultec-cluster \
  --service carpultec-service \
  --desired-count 0 \
  --region us-east-1
```

### Disparar deploy manual sin hacer cambios en el código

Desde GitHub: **Actions → Deploy to Amazon ECS → Run workflow → Run workflow**

O por CLI:

```bash
gh workflow run deploy-ecs.yml --repo franc004/prueba
```

---

## 8. Variables de Entorno y Secrets

### En producción (ECS via SSM Parameter Store)

Las credenciales de la BD **nunca están en el código**. Se guardan en AWS SSM Parameter Store como `SecureString` y ECS las inyecta automáticamente al iniciar el contenedor:

| Variable | SSM Parameter |
|----------|--------------|
| `SPRING_DATASOURCE_URL` | `/carpultec/prod/spring-datasource-url` |
| `SPRING_DATASOURCE_USERNAME` | `/carpultec/prod/spring-datasource-username` |
| `SPRING_DATASOURCE_PASSWORD` | `/carpultec/prod/spring-datasource-password` |

### Variables de entorno directas (no secretas)

Definidas en `aws/ecs-task-definition.json`:

| Variable | Valor |
|----------|-------|
| `PORT` | `8080` |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `update` |
| `JAVA_OPTS` | `-XX:MaxRAMPercentage=75` |

### En tests locales

No se necesita ninguna variable de entorno. H2 se configura automáticamente desde `src/test/resources/application.properties`.

---

## 9. Problemas Comunes

### El workflow falla en "Deploy Amazon ECS task definition"

**Causa más común:** Las credenciales AWS expiraron.  
**Solución:** Actualizar los 3 secrets en GitHub con las credenciales nuevas del Learner Lab.

```
Settings → Secrets and variables → Actions
→ AWS_ACCESS_KEY_ID → Update
→ AWS_SECRET_ACCESS_KEY → Update  
→ AWS_SESSION_TOKEN → Update
```

### La tarea ECS falla ELB health checks

**Causa:** La app tarda ~52s en arrancar (JVM + Spring Boot + conexión a RDS).  
**Solución ya aplicada:** 
- Target group: `healthyThreshold=2`, `interval=15s` → se marca healthy a los ~82s
- ECS service: `healthCheckGracePeriodSeconds=180` → el ALB espera 180s antes de marcar la tarea como fallida
- Task definition: `startPeriod=120s` en el container health check

### `mvnw: Permission denied` en el CI

**Causa:** El archivo `mvnw` no tiene permisos de ejecución en git.  
**Solución ya aplicada:**
```bash
git update-index --chmod=+x mvnw
git commit -m "fix: grant execute permission to mvnw"
```

### El ECS service tiene 0 tareas corriendo

El Learner Lab se apaga automáticamente. Al iniciar nueva sesión:
```bash
aws ecs update-service \
  --cluster carpultec-cluster \
  --service carpultec-service \
  --desired-count 1 \
  --region us-east-1
```

### Ver logs en tiempo real

```bash
aws logs tail /ecs/backend-task --since 10m --follow --region us-east-1
```

---

## API pública

```
http://carpultec-alb-1194683203.us-east-1.elb.amazonaws.com
```

```bash
# Verificar que la API está activa
curl http://carpultec-alb-1194683203.us-east-1.elb.amazonaws.com/actuator/health
# → {"status":"UP"}
```
