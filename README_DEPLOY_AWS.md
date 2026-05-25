# Deploy en AWS con Docker

Este proyecto es una API Spring Boot con Java 17. El contenedor expone el puerto `8080` y acepta el puerto mediante la variable `PORT`, util para servicios como ECS, App Runner o Elastic Beanstalk.

## Build local

Primero crea tu archivo local `.env` a partir de la plantilla:

```bash
cp .env.example .env
```

En Windows PowerShell:

```powershell
Copy-Item .env.example .env
```

```bash
docker build -t carpultec-api .
docker run --rm --env-file .env -p 8080:8080 carpultec-api
```

Tambien puedes usar Docker Compose:

```bash
docker compose up --build
```

El `docker-compose.yml` levanta PostgreSQL local y la API. Para AWS normalmente solo subes la imagen de la API a ECR y usas RDS PostgreSQL como base de datos.

Si usas PostgreSQL/RDS, configura estas variables en `.env`:

```text
SPRING_DATASOURCE_URL=jdbc:postgresql://HOST:5432/DB
SPRING_DATASOURCE_USERNAME=USER
SPRING_DATASOURCE_PASSWORD=PASSWORD
```

## Subir a Amazon ECR

Reemplaza `AWS_REGION`, `ACCOUNT_ID` y el nombre del repositorio si deseas otro.

```bash
aws ecr create-repository --repository-name carpultec-api --region AWS_REGION

aws ecr get-login-password --region AWS_REGION | docker login --username AWS --password-stdin ACCOUNT_ID.dkr.ecr.AWS_REGION.amazonaws.com

docker build -t carpultec-api .
docker tag carpultec-api:latest ACCOUNT_ID.dkr.ecr.AWS_REGION.amazonaws.com/carpultec-api:latest
docker push ACCOUNT_ID.dkr.ecr.AWS_REGION.amazonaws.com/carpultec-api:latest
```

## Variables para el servicio en AWS

Configura estas variables en ECS, App Runner o Elastic Beanstalk:

```text
PORT=8080
SPRING_DATASOURCE_URL=jdbc:postgresql://<rds-endpoint>:5432/<database>
SPRING_DATASOURCE_USERNAME=<usuario>
SPRING_DATASOURCE_PASSWORD=<password>
JAVA_OPTS=-XX:MaxRAMPercentage=75
```

## Recomendacion de despliegue

Para un despliegue simple, usa AWS App Runner con la imagen de ECR y puerto `8080`. Para mayor control de red, secretos y escalado, usa ECS Fargate con RDS PostgreSQL en la misma VPC.

## CI/CD con GitHub Actions y ECS

Para despliegue continuo en ECS Fargate, revisa [AWS_CICD_GUIDE.md](C:/Users/Ary/Desktop/carpUlTEC2/AWS_CICD_GUIDE.md). El repo incluye:

- `.github/workflows/deploy-ecs.yml`
- `aws/ecs-task-definition.json`
