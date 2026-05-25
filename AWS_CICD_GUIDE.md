# Backend Docker en AWS con ECR, ECS y GitHub Actions

Esta guia despliega la API Spring Boot de CarpUlTEC en AWS usando:

- Docker para empaquetar la aplicacion.
- Amazon ECR para guardar la imagen.
- Amazon ECS Fargate para ejecutar el contenedor.
- GitHub Actions para CI/CD.
- RDS PostgreSQL y SSM Parameter Store para configuracion segura.

## Arquitectura recomendada

```text
GitHub -> GitHub Actions -> ECR -> ECS Fargate -> RDS PostgreSQL
                                      |
                                      +-> CloudWatch Logs
                                      +-> SSM Parameter Store
                                      +-> Application Load Balancer
```

## 1. Crear repositorio ECR

```bash
aws ecr create-repository \
  --repository-name carpultec-api \
  --region <AWS_REGION>
```

## 2. Crear parametros seguros

Guarda los datos de RDS en SSM Parameter Store. Usa `SecureString` para usuario y password.

```bash
aws ssm put-parameter \
  --name /carpultec/prod/spring-datasource-url \
  --type String \
  --value "jdbc:postgresql://<RDS_ENDPOINT>:5432/<DB_NAME>" \
  --region <AWS_REGION>

aws ssm put-parameter \
  --name /carpultec/prod/spring-datasource-username \
  --type SecureString \
  --value "<DB_USER>" \
  --region <AWS_REGION>

aws ssm put-parameter \
  --name /carpultec/prod/spring-datasource-password \
  --type SecureString \
  --value "<DB_PASSWORD>" \
  --region <AWS_REGION>
```

## 3. Crear log group

```bash
aws logs create-log-group \
  --log-group-name /ecs/carpultec-api \
  --region <AWS_REGION>
```

## 4. Completar task definition

Edita [aws/ecs-task-definition.json](C:/Users/Ary/Desktop/carpUlTEC2/aws/ecs-task-definition.json) y reemplaza:

- `<ACCOUNT_ID>`
- `<AWS_REGION>`
- Los ARNs de roles si usas nombres distintos.

El workflow de GitHub Actions reemplaza automaticamente el campo `image` con la imagen nueva publicada en ECR.

## 5. Crear ECS cluster y service

Puedes crearlo desde la consola de AWS:

- Launch type: `Fargate`
- Cluster: `carpultec-cluster`
- Service: `carpultec-api-service`
- Task family: `carpultec-api`
- Container port: `8080`
- Desired tasks: `1`
- Networking: subnets privadas si usas ALB, security groups con salida a RDS.

Para exponerlo publicamente, crea un Application Load Balancer:

- Listener HTTP `80` o HTTPS `443`.
- Target group tipo `IP`.
- Health check recomendado: `/api/users` si quieres validar DB, o un endpoint `/actuator/health` si agregas Spring Actuator.

## 6. Configurar secretos de GitHub Actions

En GitHub: `Settings -> Secrets and variables -> Actions -> New repository secret`.

Agrega:

```text
AWS_ACCESS_KEY_ID
AWS_SECRET_ACCESS_KEY
AWS_REGION
ECS_CLUSTER
ECS_SERVICE
```

Valores sugeridos:

```text
AWS_REGION=us-east-1
ECS_CLUSTER=carpultec-cluster
ECS_SERVICE=carpultec-api-service
```

## 7. CI/CD

El workflow [.github/workflows/deploy-ecs.yml](C:/Users/Ary/Desktop/carpUlTEC2/.github/workflows/deploy-ecs.yml):

1. Ejecuta `./mvnw -B test`.
2. Construye la imagen Docker.
3. Publica la imagen en ECR con tag del commit.
4. Renderiza la task definition con la imagen nueva.
5. Actualiza el servicio ECS y espera estabilidad.

El despliegue corre en cada push a `main` y tambien manualmente con `workflow_dispatch`.

## Otros servicios AWS utiles

- RDS PostgreSQL: base de datos administrada para produccion.
- Secrets Manager: alternativa a SSM para rotacion avanzada de secretos.
- CloudWatch Logs: logs centralizados de ECS.
- Application Load Balancer: balanceo, HTTPS y health checks.
- ACM: certificados TLS gratis para el ALB.
- Route 53: dominio y DNS.
- VPC: red privada para ECS y RDS.
- IAM: permisos minimos para GitHub Actions y ECS task execution.

## Comandos locales

```bash
docker compose up --build -d
docker compose ps
docker compose logs api --tail 80
docker compose down
```
