# ============================================
# ETAPA 1: Build con Maven
# ============================================
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

# Copiar archivos de Maven primero (para cache de dependencias)
COPY mvnw .
COPY mvnw.cmd .
COPY pom.xml .
COPY .mvn .mvn

# Dar permisos de ejecucion al wrapper de Maven
RUN chmod +x mvnw

# Descargar dependencias (se cachea si pom.xml no cambia)
RUN ./mvnw dependency:go-offline -B

# Copiar codigo fuente
COPY src ./src

# Compilar el proyecto (sin tests para acelerar el build)
RUN ./mvnw package -DskipTests -B

# ============================================
# ETAPA 2: Runtime ligero
# ============================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Crear usuario no-root por seguridad
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copiar el JAR desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Cambiar al usuario no-root
USER appuser

# Exponer el puerto configurado
EXPOSE 8080

# Ejecutar la aplicacion con perfil de produccion
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
