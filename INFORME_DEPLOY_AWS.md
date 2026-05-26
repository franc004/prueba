# Informe de Despliegue AWS — CarpUlTEC

**Proyecto:** CarpUlTEC — Plataforma de carpooling universitario  
**Stack:** Java 17, Spring Boot 3.4.5, PostgreSQL, Docker  
**Fecha:** 26 de mayo de 2026  
**Entorno:** AWS Academy Learner Lab  

---

## 1. Planteamiento

El objetivo fue desplegar la API REST de CarpUlTEC en AWS usando una arquitectura cloud-native con los siguientes componentes:

```
GitHub (push to main)
       │
       ▼
GitHub Actions CI/CD
       │
       ├─ Maven Build + Tests
       ├─ Docker Build + Push → Amazon ECR
       └─ Deploy → ECS Fargate
                       │
                       ├─ ALB (puerto 80 → 8080)
                       ├─ CloudWatch Logs
                       └─ RDS PostgreSQL
                                │
                             SSM Parameter Store
```

**Decisiones de arquitectura:**
- **ECS Fargate** sobre EC2: sin gestión de servidores, escala automáticamente
- **RDS PostgreSQL** sobre Docker DB: base de datos gestionada con backups automáticos
- **SSM Parameter Store** sobre variables de entorno planas: credenciales encriptadas con KMS
- **ALB** sobre exposición directa: health checks, balanceo y punto de entrada único

---

## 2. Preparación del Código

Antes del deploy se realizaron 5 modificaciones al proyecto:

### 2.1 Actuator para Health Checks
```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

```properties
# application.properties
management.endpoints.web.exposure.include=health
management.endpoint.health.show-details=never
```

El ALB requiere un endpoint HTTP para verificar si el contenedor está vivo. Sin esto el servicio ECS nunca estabiliza.

### 2.2 Dockerfile con HEALTHCHECK
```dockerfile
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1
```

### 2.3 ECS Task Definition
- Roles ajustados a `LabRole` (restricción del Learner Lab)
- Región corregida a `us-east-1`
- Health check declarado en el contenedor
- Credenciales via SSM Parameter Store

### 2.4 GitHub Actions Workflow
- Añadido `AWS_SESSION_TOKEN` (requerido por credenciales temporales del Learner Lab)
- Job de tests separado antes del deploy
- Tags de imagen con SHA del commit para trazabilidad
- Trigger `workflow_dispatch` para deploys manuales

---

## 3. Infraestructura AWS Creada

### 3.1 RDS PostgreSQL
| Parámetro | Valor |
|-----------|-------|
| Engine | PostgreSQL 16 |
| Instance | db.t3.micro |
| Storage | 20 GiB gp3 |
| Deployment | Single-AZ |
| Public access | No |
| DB name | postgres (default) |
| Endpoint | `carpultec-db.cpcwtim5ekxg.us-east-1.rds.amazonaws.com` |

> Se usó la base de datos `postgres` (default) porque el template Dev/Test no permite especificar el DB name inicial.

### 3.2 SSM Parameter Store
Tres parámetros tipo `SecureString` con KMS `alias/aws/ssm`:

| Nombre | Descripción |
|--------|-------------|
| `/carpultec/prod/spring-datasource-url` | JDBC URL completa |
| `/carpultec/prod/spring-datasource-username` | Usuario de BD |
| `/carpultec/prod/spring-datasource-password` | Contraseña de BD |

### 3.3 ECR — Elastic Container Registry
- Repositorio: `carpultec-api`
- Visibilidad: Private
- Imagen construida con Docker multi-stage (JDK build → JRE runtime)
- URI: `476003490631.dkr.ecr.us-east-1.amazonaws.com/carpultec-api:latest`

### 3.4 CloudWatch Logs
- Log group: `/ecs/backend-task`
- Retención: 7 días
- Driver: `awslogs` configurado en el task definition

### 3.5 Security Groups

| SG | Inbound | Outbound | Propósito |
|----|---------|----------|-----------|
| `alb-carpultec` | HTTP 80 desde 0.0.0.0/0 | All traffic | Recibe tráfico de internet |
| `ecs-carpultec` | TCP 8080 desde alb-carpultec | All traffic + TCP 443 | Contenedores ECS |
| SG del RDS (default) | TCP 5432 desde ecs-carpultec | All traffic | Base de datos |

### 3.6 Application Load Balancer
| Parámetro | Valor |
|-----------|-------|
| Nombre | `carpultec-alb` |
| Scheme | Internet-facing |
| Listener | HTTP:80 |
| Target Group | `carpultec-tg` (IP, puerto 8080) |
| Health check path | `/actuator/health` |
| DNS | `carpultec-alb-1194683203.us-east-1.elb.amazonaws.com` |

### 3.7 ECS Cluster y Service
| Parámetro | Valor |
|-----------|-------|
| Cluster | `carpultec-cluster` |
| Service | `carpultec-service` |
| Launch type | Fargate |
| Task definition | `backend-task:2` |
| CPU / Memory | 0.5 vCPU / 1 GiB |
| Desired tasks | 1 |
| Public IP | Enabled (para acceso a SSM y ECR) |

---

## 4. CI/CD con GitHub Actions

El workflow `.github/workflows/deploy-ecs.yml` se activa en cada push a `main`:

```
1. Job "test"   → mvn test
2. Job "deploy" → aws credentials
                → docker build + push ECR
                → render task definition con nueva imagen
                → update ECS service
                → wait for stability
```

**Secrets requeridos en GitHub:**
- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`
- `AWS_SESSION_TOKEN` ← crítico para Learner Lab
- `AWS_REGION`

> Las credenciales del Learner Lab expiran cada 4 horas. Deben actualizarse en GitHub Secrets antes de cada sesión de trabajo.

---

## 5. Resultado Final

| Componente | Estado | URL/ARN |
|-----------|--------|---------|
| RDS | Running | `carpultec-db.cpcwtim5ekxg.us-east-1.rds.amazonaws.com` |
| ECR | Imagen subida | `476003490631.dkr.ecr.us-east-1.amazonaws.com/carpultec-api:latest` |
| ECS Service | Active | `carpultec-service` |
| ALB | Active | `carpultec-alb-1194683203.us-east-1.elb.amazonaws.com` |
| API Health | UP | `/actuator/health → {"status":"UP"}` |

**API pública:**
```
http://carpultec-alb-1194683203.us-east-1.elb.amazonaws.com
```

---

## 6. Consideraciones y Restricciones del Learner Lab

### Lo que NO se puede hacer en el Learner Lab:
- Crear usuarios IAM (solo existe `LabRole`)
- Configurar OIDC para GitHub Actions (requiere IAM provider)
- Usar instancias grandes de EC2 o RDS
- Cambiar el DB name después de crear la instancia RDS
- Las credenciales son temporales y expiran cada 4 horas

### Lo que NO debe hacerse en producción real:
- Usar `SPRING_JPA_HIBERNATE_DDL_AUTO=update` (usar `validate`)
- Usar la base de datos `postgres` default (crear una dedicada)
- Usar credenciales de acceso de larga duración (usar OIDC)
- Exponer el ALB sin HTTPS (agregar certificado ACM + listener 443)
- Dejar el RDS con Single-AZ (usar Multi-AZ para alta disponibilidad)

---

## 7. Pruebas desde la Terminal

Desde CloudShell o cualquier terminal con las credenciales configuradas:

### Health check de la aplicación
```bash
curl http://carpultec-alb-1194683203.us-east-1.elb.amazonaws.com/actuator/health
# Esperado: {"status":"UP"}
```

### Endpoints de la API
```bash
# Listar usuarios
curl http://carpultec-alb-1194683203.us-east-1.elb.amazonaws.com/api/users

# Listar vehículos
curl http://carpultec-alb-1194683203.us-east-1.elb.amazonaws.com/api/vehicles

# Listar rides
curl http://carpultec-alb-1194683203.us-east-1.elb.amazonaws.com/api/rides

# Listar publicaciones
curl http://carpultec-alb-1194683203.us-east-1.elb.amazonaws.com/api/publications
```

### Crear un usuario (POST)
```bash
curl -X POST http://carpultec-alb-1194683203.us-east-1.elb.amazonaws.com/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Franco",
    "lastName": "Tapia",
    "email": "franco.tapia@utec.edu.pe",
    "phone": "999888777",
    "studentCode": "202010001",
    "career": "SISTEMAS",
    "cycle": 8,
    "rating": 5.0
  }'
```

### Verificar estado del ECS Service
```bash
aws ecs describe-services \
  --cluster carpultec-cluster \
  --services carpultec-service \
  --query "services[0].{status:status,running:runningCount,desired:desiredCount}" \
  --region us-east-1
```

### Ver logs en tiempo real
```bash
aws logs get-log-events \
  --log-group-name /ecs/backend-task \
  --log-stream-name "$(aws logs describe-log-streams \
    --log-group-name /ecs/backend-task \
    --order-by LastEventTime \
    --descending \
    --max-items 1 \
    --query 'logStreams[0].logStreamName' \
    --output text --region us-east-1)" \
  --region us-east-1 \
  --limit 20 \
  --query "events[*].message"
```

### Verificar health del target group
```bash
aws elbv2 describe-target-health \
  --target-group-arn arn:aws:elasticloadbalancing:us-east-1:476003490631:targetgroup/carpultec-tg/34aa2443e0cba6ac \
  --region us-east-1 \
  --query "TargetHealthDescriptions[*].{ip:Target.Id,state:TargetHealth.State}"
```

---

## 8. Limpieza al Terminar la Sesión (para ahorrar créditos)

```bash
# Reducir tasks a 0 para no cobrar Fargate
aws ecs update-service \
  --cluster carpultec-cluster \
  --service carpultec-service \
  --desired-count 0 \
  --region us-east-1
```

Al inicio de la siguiente sesión, restaurar con `--desired-count 1` y actualizar los secrets de GitHub con las nuevas credenciales.
