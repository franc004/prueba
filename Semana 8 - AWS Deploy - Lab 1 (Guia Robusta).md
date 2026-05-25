C S 2 0 3 1 — D B P
AWS Deploy: Guía Robusta
Spring Boot + React/Next.js sobre AWS Academy
ECR • ECS Fargate • RDS • ALB
GitHub Actions • Dockerfile • Amplify / S3+CloudFront • Dokploy
S e m a n a 8 • L a b 1 . 0 3 U T E C • U n i v e r s i d a d d e I n g e n i e r í a y T e c n o l o g í a


Objetivos de la sesión
Al finalizar, podrás llevar una app full-stack desde tu laptop hasta producción en AWS.
Entender AWS Academy Dockerizar tu Spring Boot
1 2
Acceder al Learner Lab, conocer sus limitaciones y obtener Dockerfile multi-stage, .dockerignore, healthcheck y variables de
credenciales temporales. entorno.
Stack principal: ECR + ECS + RDS CI/CD con GitHub Actions
3 4
Repositorio de imágenes, Task Definition Fargate, Service detrás de Workflow que builda, pushea a ECR y actualiza el Service. Manejo de
ALB y base PostgreSQL. session token del Lab.
Frontend en cloud Alternativa: Dokploy
5 6
React+Vite a S3+CloudFront o Amplify. Next.js a Amplify Hosting o ECS. PaaS self-hosted sobre EC2 para deploys con UI tipo Vercel/Heroku.
CS 2031 — DBP • AWS Deploy Introducción 2 / 36


![image_2_1](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_2/image_2_1.png)


![image_2_2](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_2/image_2_2.png)


![image_2_3](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_2/image_2_3.png)


![image_2_4](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_2/image_2_4.png)


![image_2_5](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_2/image_2_5.png)


![image_2_6](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_2/image_2_6.png)


Roadmap del deploy
El camino que recorreremos en esta sesión, de izquierda a derecha.
1 2 3 4 5 6 7
Dockerizar AWS Setup RDS ECR ECS CI/CD Frontend
Dockerfile + build local Credenciales del Lab PostgreSQL gestionado Subir imagen Docker Task Def + Service + ALB GitHub Actions S3/CloudFront / Amplify
Importante: los pasos 3-5 los haremos primero manualmente desde la consola AWS para entender qué está pasando, y luego automatizaremos lo
automatizable desde GitHub Actions.
CS 2031 — DBP • AWS Deploy Introducción 3 / 36


![image_3_1](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_3/image_3_1.png)


![image_3_2](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_3/image_3_2.png)


![image_3_3](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_3/image_3_3.png)


![image_3_4](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_3/image_3_4.png)


![image_3_5](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_3/image_3_5.png)


![image_3_6](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_3/image_3_6.png)


![image_3_7](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_3/image_3_7.png)


![image_3_8](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_3/image_3_8.png)


¿Dónde publicar mi aplicación?
Tres caminos para servir tu app a usuarios reales.
Hosting tradicional Cloud público Self-hosting
$ $ – $$$ $ – $$
● Servidor compartido (Hostinger, GoDaddy, ● AWS, Azure, GCP, Oracle Cloud ● VPS (Hetzner, DigitalOcean) o servidor on-
etc.) prem
● IaaS + PaaS + SaaS, múltiples regiones
● Soporta HTML/PHP/Node/Java/Ruby según ● Configurado por ti: Docker, Nginx, DB,
● Escalable, pago por uso
plan backups
● Aquí trabajaremos: AWS Academy
● DB pequeña/mediana incluida ● Dokploy / Coolify / Plain Docker
● IaaS limitado, sin escalado real ● Más control, más responsabilidad
CS 2031 — DBP • AWS Deploy Introducción 4 / 36


![image_4_1](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_4/image_4_1.png)


![image_4_2](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_4/image_4_2.png)


![image_4_3](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_4/image_4_3.png)


AWS Academy Learner Lab
El entorno sandbox que UTEC nos provee — gratis pero con reglas.
¿Qué es? Estados del Lab
Un entorno AWS real (no simulado) que se accede desde Canvas →
AWS Academy Learner Lab. Cada estudiante recibe una cuenta efímera ROJO
con ~$100-200 USD en créditos y servicios limitados.
Lab apagado. Recursos suspendidos.
AMARILLO
Cómo entrar
Arrancando o apagándose. Esperar.
1. Entrar a Canvas → curso → Modules → AWS Academy Learner Lab
2. Click en 'Start Lab' y esperar el punto verde (rojo = apagado)
VERDE
3. Click en 'AWS' (junto al semáforo) → se abre la consola AWS
Lab activo. Estás consumiendo créditos.
4. Click en 'AWS Details' → ver credenciales temporales
Cada sesión dura 4 horas.
Cuando el tiempo se acaba o presionas 'End Lab', las EC2 se apagan y las
credenciales se invalidan, pero los recursos persistentes (RDS, ECR, S3) se
mantienen y siguen consumiendo créditos.
CS 2031 — DBP • AWS Deploy AWS Academy 5 / 36


![image_5_1](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_5/image_5_1.png)


![image_5_2](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_5/image_5_2.png)


![image_5_3](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_5/image_5_3.png)


Limitaciones del Learner Lab
Lo que NO podrás hacer (y que cambia cómo diseñamos el deploy).
Concepto Lo que está limitado Implicancia práctica
Región Solo us-east-1 Crear todos los recursos en N. Virginia
IAM Users No permitido Usaremos credenciales temporales del Lab
IAM Roles Solo LabRole (preexistente) ECS, Lambda, etc. usan LabRole como execution role
OIDC Provider No se puede crear GitHub Actions usará access keys (no OIDC)
Sesión Máx. 4 horas continuas Las credenciales se renuevan cada sesión
Créditos ~$50–$200 USD totales Apagar todo al terminar. Sin freebies extra
EKS / Lambda@Edge Bloqueados Usar ECS Fargate o Lambda regular
EC2 grande Solo t2/t3.micro/small recomendado No lanzar instancias 'large' o GPU
CS 2031 — DBP • AWS Deploy AWS Academy 6 / 36


Credenciales temporales del Lab
El botón 'AWS Details' te entrega 3 valores que cambian en cada sesión.
~/.aws/credentials
Pasos en la consola del Lab
[default]
aws_access_key_id=ASIA....EXAMPLE
1. Click en 'AWS Details' (esquina superior).
aws_secret_access_key=wJalrXU....EXAMPLEKEY
aws_session_token=FwoGZXIvYXdzE....EXAMPLE
2. En la sección 'Cloud Access' → 'AWS CLI', click 'Show'.
3. Verás 3 variables: aws_access_key_id, aws_secret_access_key y
aws_session_token.
4. Copia el bloque completo (sin las comillas).
5. Pégalo en ~/.aws/credentials, sección [default].
bash
# 1) Verificar identidad
aws sts get-caller-identity
# 2) Configurar región default
aws configure set region us-east-1
# 3) Listar buckets (test)
aws s3 ls
CS 2031 — DBP • AWS Deploy AWS Academy 7 / 36


![image_7_1](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_7/image_7_1.png)


![image_7_2](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_7/image_7_2.png)


![image_7_3](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_7/image_7_3.png)


Servicios disponibles en el Lab
Catálogo de servicios que usaremos vs. los que están vetados.
✓ Sí disponibles ✗ Bloqueados o restringidos
● EC2 Instancias t2/t3.micro/small. Free tier. ● IAM Users / Groups Solo LabRole preexistente.
● ECR Repositorio privado de Docker images. ● OIDC Providers No para GitHub Actions OIDC.
● ECS Cluster Fargate o EC2 launch type. ● EKS Kubernetes managed bloqueado.
● RDS PostgreSQL / MySQL / MariaDB. db.t3.micro. ● Lambda@Edge No se puede asociar a CloudFront.
● S3 Bucket público o privado para frontend o assets. ● Route 53 dominios No puedes registrar dominios.
● CloudFront CDN delante de S3 (frontend). ● Bedrock / SageMaker Cuotas mínimas o vetados.
● Amplify Hosting Build & host de SPAs / Next.js. ● EC2 'large+' Solo familias micro/small recomendado.
● CloudWatch Logs y métricas. Default para ECS/Lambda. ● Reserved Instances No tiene sentido en sesiones de 4h.
● ALB / NLB Load balancer para ECS. ● Soporte / Cases No abrir tickets a AWS Support.
● VPC, Subnets, SG Networking básico. ● Cross-region transfer Quédate en us-east-1.
CS 2031 — DBP • AWS Deploy AWS Academy 8 / 36


![image_8_1](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_8/image_8_1.png)


![image_8_2](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_8/image_8_2.png)


Arquitectura — Opción A: ECS Fargate
El stack recomendado: cloud-native, sin gestionar EC2.
Developer GitHub Actions Amazon ECR
git push build + test + push Docker images
pull image
ECS Cluster (Fargate)
Service: backend Application Load Balancer
Amazon RDS
Task Definition + IPs → Target Group :8080
JDBC
Desired count: 1–2 → Health check /actuator/health
PostgreSQL :5432 (privado)
Spring Boot :8080 → DNS público asignado
Browser S3 + CloudFront ALB → ECS
Tip
GET / /api/*
usuario final o Amplify Hosting API backend Todo en us-east-1 y la misma VPC.
CS 2031 — DBP • AWS Deploy Arquitectura 9 / 36


![image_9_1](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_9/image_9_1.png)


![image_9_2](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_9/image_9_2.png)


![image_9_3](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_9/image_9_3.png)


![image_9_4](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_9/image_9_4.png)


![image_9_5](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_9/image_9_5.png)


![image_9_6](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_9/image_9_6.png)


![image_9_7](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_9/image_9_7.png)


![image_9_8](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_9/image_9_8.png)


![image_9_9](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_9/image_9_9.png)


Arquitectura — Opción B: Dokploy en EC2
Alternativa self-hosted tipo PaaS, mucho más simple para empezar.
EC2 t3.small (Ubuntu 22.04) — públicamente accesible
Developer GitHub repo
Puertos abiertos: 22 (SSH), 80, 443, 3000 (UI Dokploy)
git push to main webhook a Dokploy
Dokploy (Docker + Traefik)
spring-boot-api react-front
Browser
:8080 → /api Nginx :80 → /
tu-dominio.com
HTTPS
postgres traefik
:5432 (interno) Reverse proxy + TLS
Ventaja: un solo VPS, UI tipo Heroku, deploys con un click desde Cuidado en el Lab: tienes que apagar la EC2 al cerrar sesión o gastarás créditos rápido. Sin Elastic IP, la IP
GitHub. pública cambia al reiniciar.
CS 2031 — DBP • AWS Deploy Arquitectura 10 / 36


![image_10_1](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_10/image_10_1.png)


![image_10_2](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_10/image_10_2.png)


![image_10_3](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_10/image_10_3.png)


![image_10_4](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_10/image_10_4.png)


![image_10_5](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_10/image_10_5.png)


![image_10_6](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_10/image_10_6.png)


![image_10_7](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_10/image_10_7.png)


![image_10_8](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_10/image_10_8.png)


![image_10_9](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_10/image_10_9.png)


![image_10_10](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_10/image_10_10.png)


¿ECS Fargate o Dokploy?
Las dos rutas son válidas. Cada una optimiza para algo distinto.
Criterio ECS Fargate + RDS Dokploy en EC2
Curva de aprendizaje Alta (varios servicios AWS) Baja (UI tipo Heroku)
Tiempo a primer deploy 1–2 horas 20–30 minutos
Escalado Automático por Service Manual / subir tamaño EC2
Costo en Lab Medio (RDS + Fargate + ALB) Bajo (1 EC2 corriendo)
CI/CD GitHub Actions → ECR → update-service Push a main → webhook Dokploy
TLS / dominio ACM + ALB + Route 53 Traefik + Let's Encrypt automático
Producción real Estándar industria Buena para MVP / proyectos pequeños
Lo que aprendes AWS cloud-native end-to-end Operar tu propio PaaS sobre Docker
Recomendación del curso: todos hacen Opción A (ECS Fargate) y entregan eso. Opción B (Dokploy) es opcional para quienes quieran experimentar con un PaaS
self-hosted.
CS 2031 — DBP • AWS Deploy Arquitectura 11 / 36


![image_11_1](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_11/image_11_1.png)


Preparar tu Spring Boot — estructura
Antes de tocar AWS, tu repo debe tener estos archivos.
estructura del repo
Checklist antes de dockerizar
my-api/
├── src/
│ ├── main/ ✓ spring-boot-starter-actuator añadido
│ │ ├── java/com/utec/...
│ │ └── resources/ ✓ Endpoint /actuator/health expuesto
│ │ ├── application.yml
│ │ └── application-prod.yml ← perfil de producción ✓ DB credentials desde variables de entorno
│ └── test/
├── pom.xml ← Maven
✓ server.port leído de PORT (o 8080 default)
├── Dockerfile ← multi-stage
├── .dockerignore
✓ Profile activo: prod (vía SPRING_PROFILES_ACTIVE)
└── .github/workflows/
└── deploy-backend.yml ← CI/CD
✓ CORS configurado para tu dominio de frontend
✓ Logs a stdout (no a archivo)
✓ spring-boot-maven-plugin presente
Por qué importa: tu app no puede tener URLs, usuarios ni passwords hardcoded. Todo lo que cambia entre dev/prod va en variables de entorno.
CS 2031 — DBP • AWS Deploy Spring Boot 12 / 36


![image_12_1](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_12/image_12_1.png)


![image_12_2](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_12/image_12_2.png)


![image_12_3](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_12/image_12_3.png)


application-prod.yml
Configuración leída desde variables de entorno — nada hardcoded.
src/main/resources/application-prod.yml
Variables de entorno
server:
port: ${PORT:8080}
PORT
spring:
datasource:
DB_URL
url: ${DB_URL} # jdbc:postgresql://<rds-endpoint>:5432/appdb
username: ${DB_USER}
DB_USER
password: ${DB_PASSWORD}
driver-class-name: org.postgresql.Driver
jpa: DB_PASSWORD
hibernate:
ddl-auto: validate # nunca create/update en prod
JWT_SECRET
properties:
hibernate.dialect: org.hibernate.dialect.PostgreSQLDialect
CORS_ORIGINS
show-sql: false
management: SPRING_PROFILES_ACTIVE
endpoints.web.exposure.include: health,info
endpoint.health.probes.enabled: true
→ se inyectan en la Task Definition de ECS
app:
jwt:
secret: ${JWT_SECRET}
cors:
allowed-origins: ${CORS_ORIGINS}
CS 2031 — DBP • AWS Deploy Spring Boot 13 / 36


![image_13_1](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_13/image_13_1.png)


![image_13_2](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_13/image_13_2.png)


Dockerfile multi-stage
Una imagen final pequeña (~200 MB) sin Maven dentro.
Dockerfile
Decisiones clave
# === Stage 1: build con Maven ===
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app Multi-stage
Builder pesado fuera de la imagen final.
# Cache de dependencias (copiar primero el pom)
COPY pom.xml .
Capa pom.xml separada
RUN mvn -B -q dependency:go-offline
Cache de deps si solo cambias código.
COPY src ./src
RUN mvn -B -q -DskipTests package
JRE 21 jammy
# === Stage 2: runtime ligero === Sin JDK ni Maven en runtime.
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
Usuario 1001
No root → mejor seguridad.
RUN useradd -r -u 1001 -g root spring
USER 1001
MaxRAMPercentage
COPY --from=builder /app/target/*.jar app.jar
JVM detecta cgroup correctamente.
EXPOSE 8080
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75" HEALTHCHECK
ENV SPRING_PROFILES_ACTIVE=prod ECS y Docker saben si está sano.
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
EXPOSE 8080
CMD curl -fsS http://localhost:8080/actuator/health || exit 1
Documenta el puerto al orquestador.
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]
CS 2031 — DBP • AWS Deploy Spring Boot 14 / 36


![image_14_1](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_14/image_14_1.png)


![image_14_2](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_14/image_14_2.png)


Probar la imagen localmente
.dockerignore para que el build sea rápido y limpio, y luego: build + run.
.dockerignore bash
target/ # Build
.git docker build -t my-api:dev .
.gitignore
# Levantar Postgres local (para probar)
.idea
docker run -d --name pg \
*.iml
-e POSTGRES_DB=appdb \
.vscode
-e POSTGRES_USER=app \
.mvn/wrapper/maven-wrapper.jar
-e POSTGRES_PASSWORD=secret \
README.md
-p 5432:5432 postgres:16
docs/
**/.DS_Store # Levantar la API conectada al Postgres
docker run --rm -p 8080:8080 \
-e DB_URL=jdbc:postgresql://host.docker.internal:5432/appdb \
-e DB_USER=app \
-e DB_PASSWORD=secret \
-e JWT_SECRET=dev-only \
-e CORS_ORIGINS=http://localhost:5173 \
Si funciona acá…
my-api:dev
…funcionará en ECS.
# Probar
curl http://localhost:8080/actuator/health
Si rompe acá, no avances. Fix antes de subir nada a
AWS.
CS 2031 — DBP • AWS Deploy Spring Boot 15 / 36


![image_15_1](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_15/image_15_1.png)


![image_15_2](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_15/image_15_2.png)


![image_15_3](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_15/image_15_3.png)


Crear RDS PostgreSQL
Base de datos managed: AWS se encarga de updates, backups y disponibilidad.
Console → RDS → Create database Security Group de RDS
Inbound: solo desde el SG de ECS (no 0.0.0.0/0).
Engine PostgreSQL
Template Free tier
Regla:
DB instance db.t3.micro
Type: PostgreSQL • Port: 5432
Storage 20 GiB gp3
Source: sg-xxxx (el SG del Service ECS)
Multi-AZ No (Free tier)
VPC Default VPC Si vas a debuggear desde tu máquina, abre 5432 temporalmente desde tu IP pública.
Public access No (solo desde ECS)
conexión
VPC SG Crear nuevo: rds-sg
# Conexión desde tu máquina (debug)
psql "host=<rds-endpoint>.us-east-1.rds.amazonaws.com \
DB name appdb
port=5432 \
dbname=appdb \
Master user appadmin
user=appadmin"
Master password (guardar en gestor)
# La URL que irá en DB_URL:
Port 5432 jdbc:postgresql://<rds-endpoint>:5432/appdb
Backups 1 día (mínimo)
Encryption Default KMS
CS 2031 — DBP • AWS Deploy RDS 16 / 36


![image_16_1](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_16/image_16_1.png)


![image_16_2](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_16/image_16_2.png)


![image_16_3](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_16/image_16_3.png)


Amazon ECR — repositorio de imágenes
Registro privado por cuenta y por región. Cada repo guarda versiones (tags) de tu imagen.
bash · crear repo
Por consola
# Crear repo desde CLI (equivalente)
aws ecr create-repository \
1. Buscar 'ECR' → 'Create repository'.
--repository-name my-api \
2. Visibility: Private. --image-scanning-configuration scanOnPush=true \
--region us-east-1
3. Repository name: my-api
4. Tag immutability: Enabled (recomendado).
5. Image scanning: Enabled (free).
6. Encryption: AES-256 default.
bash · push manual
7. Click 'Create'.
# Variables
ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
8. Copiar la URI: 1234567890.dkr.ecr.us-east-1.amazonaws.com/my-
REGION=us-east-1
api
REPO=my-api
URI=$ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com/$REPO
# 1) Login a ECR
aws ecr get-login-password --region $REGION \
| docker login --username AWS --password-stdin \
$ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com
# 2) Build, tag y push
TAG=$(date +%Y%m%d-%H%M%S)
docker build -t $REPO:latest .
docker tag $REPO:latest $URI:latest
docker tag $REPO:latest $URI:$TAG
docker push $URI:latest
docker push $URI:$TAG
CS 2031 — DBP • AWS Deploy ECR 17 / 36


![image_17_1](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_17/image_17_1.png)


![image_17_2](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_17/image_17_2.png)


![image_17_3](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_17/image_17_3.png)


Conceptos ECS antes de configurar
Tres abstracciones que tienes que tener claras.
Cluster Task Definition Service
Grupo lógico donde corren tus tasks. En Plantilla en JSON: qué imagen correr, cuánta Mantiene N tasks corriendo. Reemplaza tasks
Fargate es solo un nombre, no gestionas CPU/RAM, qué variables de entorno, qué caídas. Integra con ALB y CloudWatch.
servidores. puertos exponer.
Launch types: Fargate vs EC2
Fargate (lo que usamos): AWS aprovisiona la cápsula que corre tu contenedor. Pagas CPU·s y RAM·s. No SSH, no parches.
EC2 launch type: Tú gestionas las instancias EC2 donde corren las tasks. Más control, más trabajo. En el Lab consume más créditos.
CS 2031 — DBP • AWS Deploy ECS 18 / 36


![image_18_1](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_18/image_18_1.png)


![image_18_2](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_18/image_18_2.png)


![image_18_3](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_18/image_18_3.png)


![image_18_4](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_18/image_18_4.png)


Task Definition (JSON)
El blueprint que ECS usa para arrancar tu contenedor.
task-definition.json
Cosas a customizar
{
"family": "my-api-task",
"networkMode": "awsvpc",
"requiresCompatibilities": ["FARGATE"], ACCOUNT_ID
"cpu": "512",
tu cuenta AWS
"memory": "1024",
"executionRoleArn": "arn:aws:iam::<ACCOUNT_ID>:role/LabRole",
"taskRoleArn": "arn:aws:iam::<ACCOUNT_ID>:role/LabRole",
"containerDefinitions": [{ cpu/memory
"name": "my-api",
Fargate exige ratios válidos
"image": "<ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/my-api:latest",
"essential": true,
"portMappings": [{ "containerPort": 8080, "protocol": "tcp" }],
"environment": [ LabRole
{ "name": "SPRING_PROFILES_ACTIVE", "value": "prod" },
{ "name": "DB_URL", "value": "jdbc:postgresql://<rds>:5432/appdb" }, único role disponible en el Lab
{ "name": "DB_USER", "value": "appadmin" },
{ "name": "CORS_ORIGINS", "value": "https://d123.cloudfront.net" }
],
secrets vs env
"secrets": [
{ "name": "DB_PASSWORD", "valueFrom": "arn:aws:ssm:...db_password" }, passwords NUNCA en env
{ "name": "JWT_SECRET", "valueFrom": "arn:aws:ssm:...jwt_secret" }
],
"logConfiguration": {
SSM Parameter Store
"logDriver": "awslogs",
"options": { guarda secretos como SecureString
"awslogs-group": "/ecs/my-api",
"awslogs-region": "us-east-1",
"awslogs-stream-prefix": "ecs",
"awslogs-create-group": "true" awslogs-create-group
}
crea el log group automático
}
}]
}
CS 2031 — DBP • AWS Deploy ECS 19 / 36


![image_19_1](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_19/image_19_1.png)


![image_19_2](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_19/image_19_2.png)


Cluster + Service + ALB
Tres pasos en consola que dejan tu API online detrás de un DNS público.
1 Crear Cluster 2 Registrar Task Definition 3 Crear Service + ALB
● ECS → Clusters → Create cluster ● ECS → Task Definitions → Create new ● Cluster → Services → Create
● Nombre: my-cluster ● Pegar el JSON del slide anterior ● Launch type: Fargate
● Infrastructure: AWS Fargate ● Verificar que use LabRole ● Task definition: my-api-task:1
● Click Create. Tarda <1 min. ● Click Create. Genera revision 1. ● Desired tasks: 1
● Load balancer: Application LB
● Crear ALB nuevo, port 80 → 8080
● Target group: health /actuator/health
Resultado: tu API responde en http://my-alb-xxx.us-east-1.elb.amazonaws.com/actuator/health
CS 2031 — DBP • AWS Deploy ECS 20 / 36


![image_20_1](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_20/image_20_1.png)


![image_20_2](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_20/image_20_2.png)


![image_20_3](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_20/image_20_3.png)


![image_20_4](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_20/image_20_4.png)


Logs y troubleshooting
Cuando algo falle (y va a fallar), estos son tus puntos de verificación.
bash · troubleshooting
Dónde mirar primero
# Ver logs en tiempo real
aws logs tail /ecs/my-api --follow
ECS → Service → Events
1
# Listar tasks corriendo
Errores de despliegue, falta de capacidad.
aws ecs list-tasks \
--cluster my-cluster --service-name my-api-svc
ECS → Tasks → Stopped → Stopped reason
2
# Describir por qué se detuvo
Por qué murió el contenedor.
aws ecs describe-tasks \
--cluster my-cluster \
CloudWatch Logs → /ecs/my-api --tasks <task-id> \
3
--query 'tasks[0].stoppedReason'
Stack trace de Spring Boot.
# Forzar nuevo deploy (reusa última task def)
aws ecs update-service \
EC2 → Target Groups → Health checks
4
--cluster my-cluster \
Si Healthy = 0, ALB no enruta.
--service my-api-svc \
--force-new-deployment
RDS → Connectivity
5
Endpoint correcto, SG conecta con ECS.
Spring Actuator /actuator/health
6
Estado de DB y app.
CS 2031 — DBP • AWS Deploy ECS 21 / 36


![image_21_1](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_21/image_21_1.png)


![image_21_2](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_21/image_21_2.png)


GitHub Actions — Secrets del repo
Las credenciales del Lab van como Secrets, NO commiteadas.
Settings → Secrets and variables → Actions Cada sesión del Lab los invalida
Las credenciales del Learner Lab expiran cuando termina la sesión (~4h) o cuando das End
AWS_ACCESS_KEY_ID ASIA… Lab.
AWS_SECRET_ACCESS_KEY wJalr…
Workflow real:
AWS_SESSION_TOKEN FwoGZ… (obligatorio en Lab) 1) Start Lab → copiar AWS Details
2) Pegar los 3 valores en GitHub Secrets
AWS_REGION us-east-1
3) Push a main → Actions arranca y deployea
bash · auto-sync (gh CLI)
ECR_REPOSITORY my-api
# Script para sincronizar Secrets desde tu máquina (opcional)
# Requiere: gh CLI logueado al repo
1234567890.dkr.ecr.us-east-
ECR_REGISTRY
1.amazonaws.com
gh secret set AWS_ACCESS_KEY_ID --body "$AWS_ACCESS_KEY_ID"
gh secret set AWS_SECRET_ACCESS_KEY --body "$AWS_SECRET_ACCESS_KEY"
ECS_CLUSTER my-cluster gh secret set AWS_SESSION_TOKEN --body "$AWS_SESSION_TOKEN"
# Tip: copias el bloque de 'AWS Details' a un archivo creds.sh,
ECS_SERVICE my-api-svc # le pones 'export' a cada línea, source creds.sh, y luego corres
# el script de arriba para sincronizar todo en 5 segundos.
ECS_TASK_FAMILY my-api-task
CS 2031 — DBP • AWS Deploy GitHub Actions 22 / 36


![image_22_1](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_22/image_22_1.png)


![image_22_2](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_22/image_22_2.png)


![image_22_3](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_22/image_22_3.png)


Workflow: deploy-backend.yml
Build con Maven, push a ECR, update del Service. Todo en un solo job.
deploy-backend.yml (1/2) deploy-backend.yml (2/2)
name: Deploy Backend to ECS - uses: aws-actions/amazon-ecr-login@v2
on:
push: { branches: [main] } - name: Build, tag, push image
workflow_dispatch: id: img
run: |
env: TAG=$(date +%Y%m%d-%H%M%S)
AWS_REGION: ${{ secrets.AWS_REGION }} IMG=$ECR_REGISTRY/$ECR_REPOSITORY
ECR_REGISTRY: ${{ secrets.ECR_REGISTRY }} docker build -t $IMG:$TAG \
ECR_REPOSITORY: ${{ secrets.ECR_REPOSITORY }} -t $IMG:latest .
ECS_CLUSTER: ${{ secrets.ECS_CLUSTER }} docker push $IMG:$TAG
ECS_SERVICE: ${{ secrets.ECS_SERVICE }} docker push $IMG:latest
echo "image=$IMG:$TAG" >> $GITHUB_OUTPUT
jobs:
deploy: - id: td
runs-on: ubuntu-latest uses: aws-actions/amazon-ecs-render-task-definition@v1
steps: with:
- uses: actions/checkout@v4 task-definition: .aws/task-definition.json
container-name: my-api
- uses: actions/setup-java@v4 image: ${{ steps.img.outputs.image }}
with:
distribution: temurin - name: Deploy
java-version: '21' uses: aws-actions/amazon-ecs-deploy-task-definition@v2
cache: maven with:
task-definition: ${{ steps.td.outputs.task-definition }}
- run: mvn -B -DskipTests package service: ${{ env.ECS_SERVICE }}
cluster: ${{ env.ECS_CLUSTER }}
- name: AWS credentials (session token) wait-for-service-stability: true
uses: aws-actions/configure-aws-credentials@v4
with:
aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
aws-session-token: ${{ secrets.AWS_SESSION_TOKEN }}
CS 20 3 1 — D B P • a AwWs-Sr Deegpiloony: ${{ env.AWS_REGION }} GitHub Actions 23 / 36


![image_23_1](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_23/image_23_1.png)


![image_23_2](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_23/image_23_2.png)


En AWS real: GitHub OIDC
Lo que harán cuando egresen y trabajen con una cuenta AWS real.
OIDC (NO funciona en Learner Lab)
¿Por qué OIDC?
# En AWS (real) — crear OIDC provider:
aws iam create-open-id-connect-provider \
Las access keys de larga duración son el #1 hallazgo en auditorías.
--url https://token.actions.githubusercontent.com \
Filtrarlas = riesgo real. --client-id-list sts.amazonaws.com \
--thumbprint-list <github-thumbprint>
# Crear IAM Role 'github-deployer' con trust:
Con OIDC: {
"Version": "2012-10-17",
"Statement": [{
"Effect": "Allow",
● Sin access keys en secrets. "Principal": { "Federated": "arn:aws:iam::<acc>:oidc-
provider/token.actions.githubusercontent.com" },
● Token efímero (15 min) firmado por GitHub. "Action": "sts:AssumeRoleWithWebIdentity",
"Condition": {
● Puedes restringir por repo/branch/env.
"StringEquals": { "token.actions.githubusercontent.com:aud": "sts.amazonaws.com"
},
● AWS lo trustea vía IAM Identity Provider.
"StringLike": { "token.actions.githubusercontent.com:sub":
● Recomendación de Anthropic, AWS y GitHub. "repo:OWNER/REPO:ref:refs/heads/main" }
}
}]
}
# En el workflow YAML, reemplazar el step de credenciales:
- uses: aws-actions/configure-aws-credentials@v4
with:
role-to-assume: arn:aws:iam::<acc>:role/github-deployer
aws-region: us-east-1
permissions:
id-token: write
contents: read
CS 2031 — DBP • AWS Deploy GitHub Actions 24 / 36


![image_24_1](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_24/image_24_1.png)


![image_24_2](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_24/image_24_2.png)


Frontend A — React+Vite → S3 + CloudFront
Hospedaje estático global, ~centavos al mes, súper rápido.
.github/workflows/deploy-frontend.yml
Pasos
name: Deploy Frontend (Vite → S3 + CF)
on:
1. Crear bucket S3: my-frontend-<unique>
push: { branches: [main] }
2. Block public access ON (recomendado)
jobs:
deploy:
3. Crear distribution CloudFront → origin S3
runs-on: ubuntu-latest
4. Origin Access Control (OAC) para que solo CF lea S3 steps:
- uses: actions/checkout@v4
5. Default root object: index.html - uses: actions/setup-node@v4
with: { node-version: 20, cache: npm }
6. Custom error response 403/404 → /index.html (SPA fallback)
- run: npm ci
7. Copiar el dominio CloudFront: d123abc.cloudfront.net - run: npm run build
env:
8. Configurar workflow GitHub Actions VITE_API_URL: ${{ secrets.VITE_API_URL }}
- uses: aws-actions/configure-aws-credentials@v4
with:
aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
aws-session-token: ${{ secrets.AWS_SESSION_TOKEN }}
aws-region: us-east-1
- name: Sync to S3
run: |
aws s3 sync ./dist s3://${{ secrets.S3_BUCKET }} \
--delete \
--cache-control "public, max-age=31536000, immutable" \
--exclude "index.html"
aws s3 cp ./dist/index.html s3://${{ secrets.S3_BUCKET }}/index.html \
--cache-control "no-cache"
- name: Invalidate CloudFront
CS 2031 — DBP • AWS Deploy Frontend 25 / 36
run: |
aws cloudfront create-invalidation \
--distribution-id ${{ secrets.CF_DIST_ID }} \
--paths "/*"


![image_25_1](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_25/image_25_1.png)


![image_25_2](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_25/image_25_2.png)


Frontend B — AWS Amplify Hosting
Conectas tu repo y Amplify se encarga del build y deploy. Cero infra.
amplify.yml
Setup desde la consola
version: 1
applications:
1. Amplify → Host web app → Connect repo (GitHub)
- frontend:
2. Autorizar acceso al repo phases:
preBuild:
3. Seleccionar branch: main
commands:
- npm ci
4. Detecta Vite o Next.js automáticamente
build:
5. Editar Environment variables (VITE_API_URL, …) commands:
- npm run build
6. Service role: usar LabRole
artifacts:
baseDirectory: dist # 'dist' para Vite, '.next' para
7. Save & Deploy
Next.js
8. Cada push a main = nuevo deploy automático files:
- '**/*'
cache:
paths:
- node_modules/**/*
# Para Next.js (SSR):
# baseDirectory: .next
# Amplify detecta SSR y aprovisiona Lambda@Edge automáticamente.
# OJO: en Learner Lab Lambda@Edge no está disponible →
# usar Next.js en export static, o desplegarlo en ECS.
CS 2031 — DBP • AWS Deploy Frontend 26 / 36


![image_26_1](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_26/image_26_1.png)


![image_26_2](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_26/image_26_2.png)


Frontend C — Next.js dockerizado en ECS
Si necesitas SSR puro y no quieres depender de Amplify.
next.config.js
Cambios respecto a Spring Boot
# next.config.js
module.exports = {
Puerto 3000 (Next.js) en lugar de 8080
output: 'standalone', // genera /.next/standalone para
Docker
reactStrictMode: true,
}; Imagen base node:20-alpine (no JRE)
Dockerfile (Next.js)
# ===== Builder =====
FROM node:20-alpine AS builder
Healthcheck GET /api/health (lo creas tú)
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
Variables NEXT_PUBLIC_* expuestas al cliente
ENV NEXT_TELEMETRY_DISABLED=1
RUN npm run build
# ===== Runtime =====
Task Definition cpu 256 / memory 512 suele bastar
FROM node:20-alpine
WORKDIR /app
ENV NODE_ENV=production PORT=3000
ALB target HTTP :3000
# 'standalone' deja solo lo necesario
COPY --from=builder /app/.next/standalone ./
COPY --from=builder /app/.next/static ./.next/static
COPY --from=builder /app/public ./public
Workflow Igual que backend, cambia repo y service
EXPOSE 3000
USER 1001
CMD ["node", "server.js"]
CS 2031 — DBP • AWS Deploy Frontend 27 / 36


![image_27_1](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_27/image_27_1.png)


![image_27_2](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_27/image_27_2.png)


![image_27_3](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_27/image_27_3.png)


CORS, env vars y URL del backend
Lo que siempre rompe el primer deploy: la conexión front back.
frontend/.env + lib/api.ts
Quién apunta a quién
// Frontend (Vite): src/lib/api.ts
const BASE = import.meta.env.VITE_API_URL; // build-time
Frontend en… VITE_API_URL = …
export const api = (path, opts = {}) =>
fetch(`${BASE}${path}`, {
...opts,
S3 + CloudFront https://<alb-dns>
headers: { 'Content-Type': 'application/json', ...(opts.headers ||
{}) },
credentials: 'include', Amplify Hosting https://<alb-dns>
});
// .env (NO commitear si tiene secrets reales) Next.js en ECS internal service DNS
VITE_API_URL=https://api.midominio.com
backend · CorsConfig.java
Y en el backend:
// Backend Spring Boot: CorsConfig.java
@Configuration CORS_ORIGINS=https://d123.cloudfront.net,https://main.amplify.app
public class CorsConfig {
@Bean
CorsConfigurationSource corsSource() {
var c = new CorsConfiguration();
c.setAllowedOrigins(List.of(System.getenv("CORS_ORIGINS").split(",")));
c.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
c.setAllowedHeaders(List.of("*"));
Pitfall típico: VITE_API_URL se baking-fija en build. Si cambias el ALB, debes rebuildear
c.setAllowCredentials(true);
el frontend.
c.setMaxAge(3600L);
var src = new UrlBasedCorsConfigurationSource();
src.registerCorsConfiguration("/**", c);
CS 20 3 1 — r DeBtPu r•n A WsrSc D;eploy Frontend 28 / 36
}
}


![image_28_1](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_28/image_28_1.png)


![image_28_2](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_28/image_28_2.png)


![image_28_3](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_28/image_28_3.png)


![image_28_4](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_28/image_28_4.png)


![image_28_5](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_28/image_28_5.png)


Opción 2 — Dokploy
Un PaaS open-source self-hosted: como tener tu propio Heroku.
¿Qué te da Dokploy? Cuándo usar Dokploy
✓ UI web ✓ MVP / hackathon: deploy en 30 min.
Crear apps, ver logs, escalar — sin tocar consola AWS.
✓ Proyectos personales con dominio propio.
Build desde Git
✓
Conecta repo, hace docker build automáticamente. ✓ Quieres aprender Docker en serio.
Traefik integrado
✓ ✗ App con tráfico real y escalado horizontal.
Reverse proxy + TLS Let's Encrypt + subdominios.
✗ Necesitas HA o failover automático.
Bases de datos
✓
Postgres, MySQL, Redis, Mongo con un click.
✗ Te están midiendo en skills cloud-native AWS.
Backups
✓
Snapshots de DBs y volúmenes.
Stack interno
Webhooks
✓
Deploy automático en push (GitHub, GitLab). Docker • Traefik • Buildpacks/Dockerfile • Node.js + tRPC
Multi-app
✓
Varias apps en el mismo VPS, cada una con dominio.
Web: dokploy.com • GitHub: Dokploy/dokploy
CS 2031 — DBP • AWS Deploy Dokploy 29 / 36


![image_29_1](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_29/image_29_1.png)


![image_29_2](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_29/image_29_2.png)


![image_29_3](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_29/image_29_3.png)


Dokploy — instalación en EC2
Una EC2 t3.small con Ubuntu 22.04 alcanza para todo el stack.
bash · install Dokploy
Security Group
# 1) Lanzar EC2 t3.small (Ubuntu 22.04)
# - Asignar IP pública
# - Security Group: 22, 80, 443, 3000
22 SSH Tu IP /32
# 2) SSH y actualizar
ssh -i lab.pem ubuntu@<EC2-IP>
80 HTTP 0.0.0.0/0
sudo apt update && sudo apt -y upgrade
# 3) Instalar Dokploy (instala Docker + Traefik + Dokploy)
443 HTTPS 0.0.0.0/0
curl -sSL https://dokploy.com/install.sh | sudo bash
# 4) Abrir la UI
3000 Dokploy UI Tu IP /32
# http://<EC2-IP>:3000
# Crear usuario admin en la primera carga.
# 5) (Opcional) Apuntar dominio
Importante:
# A record: app.midominio.com → <EC2-IP>
Cierra el 3000 detrás de Tailscale o un VPN cuando tengas tu app
# Dokploy emite TLS automáticamente con Let's Encrypt.
productiva.
En el Lab: Stop Instance al cerrar sesión. La EBS persiste y los datos siguen ahí cuando reinicies.
CS 2031 — DBP • AWS Deploy Dokploy 30 / 36


![image_30_1](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_30/image_30_1.png)


![image_30_2](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_30/image_30_2.png)


![image_30_3](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_30/image_30_3.png)


Dokploy — desplegar backend + DB + frontend
Tres recursos en la UI y listo.
1 Crear Postgres 2 Crear Backend 3 Crear Frontend
● Project → Create → Database ● Project → Create → Application ● Project → Create → Application
● Type: PostgreSQL 16 ● Source: GitHub repo (auth OAuth) ● Source: repo del front
● Name: appdb ● Build type: Dockerfile ● Build: Dockerfile (Nginx) o Nixpacks (Vite auto)
● User/password: autogenerados ● Branch: main ● Domain: app.midominio.com
● Copiar internal URL: ● Env vars: DB_URL, JWT_SECRET, etc. ● Env: VITE_API_URL=https://api.midominio.com
postgres://app:..@appdb:5432/appdb
● Domain: api.midominio.com
Auto-deploy on push: Dokploy registra un webhook en tu repo. Cada push a main dispara docker build + restart del contenedor — 1-2 minutos.
CS 2031 — DBP • AWS Deploy Dokploy 31 / 36


![image_31_1](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_31/image_31_1.png)


![image_31_2](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_31/image_31_2.png)


![image_31_3](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_31/image_31_3.png)


![image_31_4](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_31/image_31_4.png)


Costos en Learner Lab
Lo que más quema créditos y cómo evitarlo.
RDS db.t3.micro $0.017/h ALB $0.0225/h
≈ $12/mes + $0.008/LCU
Lo más caro si lo dejas prendido. Snapshot + delete cuando no uses. Cobra incluso sin tráfico. Borrar entre sesiones si no debuggeas.
ECS Fargate task ≈ $0.04/h EC2 t3.micro $0.0104/h
0.5 vCPU/1GB Free tier
Desired count 0 cuando termines de probar. Free tier no aplica en Academy. Stop instance al cerrar.
ECR storage $0.10/GB·mes S3 + CloudFront centavos
casi nada según tráfico
Limpia imágenes viejas, deja sólo las últimas 3. Despreciable para sitios pequeños.
Regla de oro: después de cada sesión → Stop EC2, Service desired=0, snapshot de RDS y borrar la instancia si tienes guardado el snapshot.
CS 2031 — DBP • AWS Deploy Higiene 32 / 36


![image_32_1](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_32/image_32_1.png)


![image_32_2](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_32/image_32_2.png)


![image_32_3](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_32/image_32_3.png)


![image_32_4](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_32/image_32_4.png)


![image_32_5](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_32/image_32_5.png)


![image_32_6](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_32/image_32_6.png)


![image_32_7](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_32/image_32_7.png)


Cleanup — orden correcto para borrar
Borrar al revés del orden de creación. Si te saltas un paso, el siguiente falla.
bash · teardown
Orden de eliminación
# Script de teardown rápido (entre sesiones)
aws ecs update-service \
1. ECS Service → Update → desired count 0 → Delete --cluster my-cluster \
--service my-api-svc \
2. ECS Task Definitions → Deregister (no obligatorio pero limpio)
--desired-count 0
3. ECS Cluster → Delete
# Borrar el Service después de stable
4. ALB → Delete (también su Target Group y Listener)
aws ecs delete-service \
5. RDS → snapshot final (opcional) → Delete (sin backups) --cluster my-cluster \
--service my-api-svc --force
6. Security Groups → Delete (el de ECS y el de RDS)
7. ECR → Delete repositories (o solo eliminar imágenes viejas)
# Detener task definitions huérfanas
8. S3 buckets → Empty → Delete for arn in $(aws ecs list-task-definitions \
--family-prefix my-api-task --query 'taskDefinitionArns[*]' --output
9. CloudFront distribution → Disable → Wait ~15 min → Delete
text); do
10. Parameter Store → Delete secrets aws ecs deregister-task-definition --task-definition "$arn"
>/dev/null
11. CloudWatch Log Groups → Delete
done
# Snapshot final de RDS y borrar instancia
aws rds create-db-snapshot \
--db-instance-identifier appdb \
--db-snapshot-identifier appdb-final-$(date +%Y%m%d)
aws rds delete-db-instance \
--db-instance-identifier appdb \
--skip-final-snapshot
CS 2031 — DBP • AWS Deploy Higiene 33 / 36


![image_33_1](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_33/image_33_1.png)


![image_33_2](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_33/image_33_2.png)


Buenas prácticas — resumen
Lo que separa un deploy estudiantil de uno serio.
Sí hacer No hacer
✓ 12-Factor App: config por env vars ✗ Hardcodear DB_URL, JWT_SECRET o claves AWS
✓ Multi-stage Dockerfile + usuario no-root ✗ Subir credenciales a git (usa .gitignore)
✓ Healthcheck en /actuator/health + ALB usa ese path ✗ Exponer puertos directos al mundo (usa ALB)
✓ Secretos en SSM Parameter Store, no en task def env ✗ Dejar 0.0.0.0/0 en SG de RDS
✓ Logs a stdout → CloudWatch ✗ Usar 'latest' como única tag (rollback imposible)
✓ Tag cada imagen con SHA o timestamp (latest también) ✗ Ejecutar tests en producción
✓ ddl-auto: validate (nunca update/create en prod) ✗ Cambiar la TaskDef desde la UI sin commit del JSON
✓ Borrar recursos no usados al cerrar sesión ✗ Olvidar Stop Lab → créditos quemados sin darte cuenta
CS 2031 — DBP • AWS Deploy Cierre 34 / 36


![image_34_1](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_34/image_34_1.png)


![image_34_2](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_34/image_34_2.png)


Recursos y referencias
Para profundizar después de la sesión.
Repositorios del curso Documentación AWS
github.com/CS2031-DBP/aws docs.aws.amazon.com/AmazonECS
Tutorial original AWS Academy ECS Developer Guide
github.com/CS2031-DBP/aws-deploy-template docs.aws.amazon.com/AmazonECR
Plantilla full-stack (próximamente) ECR User Guide
docs.aws.amazon.com/AmazonRDS
RDS Postgres
docs.aws.amazon.com/amplify
Amplify Hosting
GitHub Actions Dokploy y alternativas
github.com/aws-actions/configure-aws-credentials dokploy.com
Action oficial Web oficial + docs
github.com/aws-actions/amazon-ecs-deploy-task-definition coolify.io
Deploy ECS Alternativa muy similar
docs.github.com/actions/deployment/security-hardening-your-
caprover.com
deployments/configuring-openid-connect-in-amazon-web-services
OIDC con AWS Self-hosted PaaS sobre Docker Swarm
CS 2031 — DBP • AWS Deploy Cierre 35 / 36


![image_35_1](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_35/image_35_1.png)


![image_35_2](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_35/image_35_2.png)


![image_35_3](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_35/image_35_3.png)


![image_35_4](https://doc2markdown.com/images/20260525/be7fba0f-cfba-4ab0-8d60-668d4551e697/page_35/image_35_4.png)


¡Gracias!
Empiecen por dockerizar local. Después suben a ECR. Después prenden el Lab.
Y cuando terminen, Stop Lab. Siempre.
C S 2 0 3 1 — D B P • S e m a n a 8 • U T E C


