# J. DevOps

## Información General
- **Proyecto**: Plataforma de Torneos E-Sport
- **Estado Actual**: Desarrollo Local
- **Objetivo**: Implementación de prácticas DevOps
- **Fecha**: Diciembre 2024

## 1. Estado Actual del Proyecto

### 1.1 Configuración Existente
```
Torneos/
├── e-sport/
│   ├── backend-torneos/
│   │   ├── pom.xml                    # Maven build configuration
│   │   ├── mvnw                       # Maven wrapper
│   │   └── src/main/resources/
│   │       ├── application.yml        # Multi-profile configuration
│   │       ├── application-dev.yml    # Development profile
│   │       ├── application-postgres.yml # PostgreSQL profile
│   │       └── application-prod.yml   # Production profile (futuro)
│   ├── frontend-torneos/
│   │   ├── package.json               # npm dependencies
│   │   ├── angular.json               # Angular CLI configuration
│   │   └── proxy.conf.json            # Development proxy
│   └── start-dev.sh                   # Development startup script
```

### 1.2 Herramientas Actuales
- **Build Backend**: Maven 3.x
- **Build Frontend**: Angular CLI + npm
- **Base de Datos**: PostgreSQL (prod) / H2 (dev)
- **Servidor**: Spring Boot embedded Tomcat
- **Desarrollo**: Scripts bash para automatización

## 2. Propuesta de Arquitectura DevOps

### 2.1 Pipeline CI/CD Completo
```
┌─────────────────────────────────────────────────────────────┐
│                    DEVELOPMENT                              │
│  ┌─────────────────┐    ┌─────────────────────────────────┐ │
│  │   Local Dev     │    │     Feature Branches           │ │
│  │   Environment   │    │     (Git Flow)                  │ │
│  └─────────────────┘    └─────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                                   │
                                   ▼ git push
┌─────────────────────────────────────────────────────────────┐
│                 CONTINUOUS INTEGRATION                      │
│  ┌─────────────────┐    ┌─────────────────────────────────┐ │
│  │   GitHub        │    │     Automated Testing          │ │
│  │   Actions       │    │     (Unit + Integration)        │ │
│  └─────────────────┘    └─────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                                   │
                                   ▼ tests pass
┌─────────────────────────────────────────────────────────────┐
│                CONTINUOUS DEPLOYMENT                        │
│  ┌─────────────────┐    ┌─────────────────────────────────┐ │
│  │   Docker        │    │     Kubernetes / Docker        │ │
│  │   Build         │    │     Compose Deployment         │ │
│  └─────────────────┘    └─────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                                   │
                                   ▼ deploy
┌─────────────────────────────────────────────────────────────┐
│                    MONITORING                               │
│  ┌─────────────────┐    ┌─────────────────────────────────┐ │
│  │   Application   │    │     Infrastructure              │ │
│  │   Monitoring    │    │     Monitoring                  │ │
│  └─────────────────┘    └─────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

## 3. Containerización con Docker

### 3.1 Dockerfile Backend
```dockerfile
# Backend Dockerfile
FROM openjdk:17-jdk-slim as builder

WORKDIR /app
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline

COPY src src
RUN ./mvnw clean package -DskipTests

FROM openjdk:17-jre-slim

WORKDIR /app
COPY --from=builder /app/target/torneos-*.jar app.jar

EXPOSE 8081

ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xmx512m -Xms256m"

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8081/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

### 3.2 Dockerfile Frontend
```dockerfile
# Frontend Dockerfile
FROM node:18-alpine as builder

WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production

COPY . .
RUN npm run build --prod

FROM nginx:alpine

COPY --from=builder /app/dist/frontend-torneos /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD curl -f http://localhost/ || exit 1

CMD ["nginx", "-g", "daemon off;"]
```

### 3.3 Docker Compose para Desarrollo
```yaml
# docker-compose.dev.yml
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: torneo
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: 1234
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 10s
      timeout: 5s
      retries: 5

  backend:
    build:
      context: ./backend-torneos
      dockerfile: Dockerfile
    environment:
      SPRING_PROFILES_ACTIVE: postgres
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/torneo
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: 1234
    ports:
      - "8081:8081"
    depends_on:
      postgres:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8081/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3

  frontend:
    build:
      context: ./frontend-torneos
      dockerfile: Dockerfile
    ports:
      - "80:80"
    depends_on:
      - backend
    environment:
      API_URL: http://backend:8081

volumes:
  postgres_data:
```

### 3.4 Docker Compose para Producción
```yaml
# docker-compose.prod.yml
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: ${DB_NAME}
      POSTGRES_USER: ${DB_USER}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./backup:/backup
    restart: unless-stopped
    networks:
      - app-network

  backend:
    image: torneos-backend:${VERSION}
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/${DB_NAME}
      SPRING_DATASOURCE_USERNAME: ${DB_USER}
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
    depends_on:
      - postgres
    restart: unless-stopped
    networks:
      - app-network
    labels:
      - "traefik.enable=true"
      - "traefik.http.routers.backend.rule=Host(`api.torneos.com`)"

  frontend:
    image: torneos-frontend:${VERSION}
    environment:
      API_URL: https://api.torneos.com
    restart: unless-stopped
    networks:
      - app-network
    labels:
      - "traefik.enable=true"
      - "traefik.http.routers.frontend.rule=Host(`torneos.com`)"

  traefik:
    image: traefik:v2.10
    command:
      - "--api.dashboard=true"
      - "--providers.docker=true"
      - "--entrypoints.web.address=:80"
      - "--entrypoints.websecure.address=:443"
      - "--certificatesresolvers.letsencrypt.acme.email=${ACME_EMAIL}"
      - "--certificatesresolvers.letsencrypt.acme.storage=/acme.json"
      - "--certificatesresolvers.letsencrypt.acme.httpchallenge.entrypoint=web"
    ports:
      - "80:80"
      - "443:443"
      - "8080:8080"
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
      - ./acme.json:/acme.json
    networks:
      - app-network

networks:
  app-network:
    driver: bridge

volumes:
  postgres_data:
```

## 4. Pipeline CI/CD con GitHub Actions

### 4.1 Workflow Principal
```yaml
# .github/workflows/ci-cd.yml
name: CI/CD Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

env:
  REGISTRY: ghcr.io
  IMAGE_NAME: ${{ github.repository }}

jobs:
  test-backend:
    runs-on: ubuntu-latest
    
    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_PASSWORD: postgres
          POSTGRES_DB: test_db
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 5432:5432
    
    steps:
    - uses: actions/checkout@v4
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Cache Maven dependencies
      uses: actions/cache@v3
      with:
        path: ~/.m2
        key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
    
    - name: Run backend tests
      run: |
        cd backend-torneos
        ./mvnw clean test
    
    - name: Generate test report
      uses: dorny/test-reporter@v1
      if: success() || failure()
      with:
        name: Backend Tests
        path: backend-torneos/target/surefire-reports/*.xml
        reporter: java-junit

  test-frontend:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v4
    
    - name: Set up Node.js
      uses: actions/setup-node@v3
      with:
        node-version: '18'
        cache: 'npm'
        cache-dependency-path: frontend-torneos/package-lock.json
    
    - name: Install dependencies
      run: |
        cd frontend-torneos
        npm ci
    
    - name: Run frontend tests
      run: |
        cd frontend-torneos
        npm run test -- --watch=false --browsers=ChromeHeadless
    
    - name: Run linting
      run: |
        cd frontend-torneos
        npm run lint

  build-and-push:
    needs: [test-backend, test-frontend]
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    
    steps:
    - uses: actions/checkout@v4
    
    - name: Log in to Container Registry
      uses: docker/login-action@v2
      with:
        registry: ${{ env.REGISTRY }}
        username: ${{ github.actor }}
        password: ${{ secrets.GITHUB_TOKEN }}
    
    - name: Extract metadata
      id: meta
      uses: docker/metadata-action@v4
      with:
        images: ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}
        tags: |
          type=ref,event=branch
          type=sha,prefix={{branch}}-
    
    - name: Build and push backend image
      uses: docker/build-push-action@v4
      with:
        context: ./backend-torneos
        push: true
        tags: ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}-backend:${{ steps.meta.outputs.tags }}
    
    - name: Build and push frontend image
      uses: docker/build-push-action@v4
      with:
        context: ./frontend-torneos
        push: true
        tags: ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}-frontend:${{ steps.meta.outputs.tags }}

  deploy:
    needs: build-and-push
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    
    steps:
    - name: Deploy to production
      uses: appleboy/ssh-action@v0.1.5
      with:
        host: ${{ secrets.HOST }}
        username: ${{ secrets.USERNAME }}
        key: ${{ secrets.SSH_KEY }}
        script: |
          cd /opt/torneos
          docker-compose -f docker-compose.prod.yml pull
          docker-compose -f docker-compose.prod.yml up -d
          docker system prune -f
```

### 4.2 Workflow de Testing
```yaml
# .github/workflows/test.yml
name: Automated Testing

on:
  schedule:
    - cron: '0 2 * * *'  # Daily at 2 AM
  workflow_dispatch:

jobs:
  integration-tests:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v4
    
    - name: Start services
      run: docker-compose -f docker-compose.test.yml up -d
    
    - name: Wait for services
      run: |
        timeout 300 bash -c 'until curl -f http://localhost:8081/actuator/health; do sleep 5; done'
    
    - name: Run integration tests
      run: |
        cd backend-torneos
        ./mvnw test -Dtest=**/*IntegrationTest
    
    - name: Run E2E tests
      run: |
        cd frontend-torneos
        npm run e2e
    
    - name: Cleanup
      if: always()
      run: docker-compose -f docker-compose.test.yml down -v
```

## 5. Configuración de Entornos

### 5.1 Variables de Entorno por Ambiente

#### Desarrollo
```bash
# .env.dev
DB_HOST=localhost
DB_PORT=5432
DB_NAME=torneo_dev
DB_USER=postgres
DB_PASSWORD=1234

JWT_SECRET=dev_secret_key
JWT_EXPIRATION=3600000

LOG_LEVEL=DEBUG
CORS_ORIGINS=http://localhost:4200
```

#### Staging
```bash
# .env.staging
DB_HOST=staging-db.internal
DB_PORT=5432
DB_NAME=torneo_staging
DB_USER=${DB_USER}
DB_PASSWORD=${DB_PASSWORD}

JWT_SECRET=${JWT_SECRET}
JWT_EXPIRATION=1800000

LOG_LEVEL=INFO
CORS_ORIGINS=https://staging.torneos.com
```

#### Producción
```bash
# .env.prod
DB_HOST=prod-db.internal
DB_PORT=5432
DB_NAME=torneo_prod
DB_USER=${DB_USER}
DB_PASSWORD=${DB_PASSWORD}

JWT_SECRET=${JWT_SECRET}
JWT_EXPIRATION=900000

LOG_LEVEL=WARN
CORS_ORIGINS=https://torneos.com

# Monitoring
PROMETHEUS_ENABLED=true
GRAFANA_URL=https://monitoring.torneos.com
```

### 5.2 Configuración de Secrets
```yaml
# GitHub Secrets necesarios
secrets:
  HOST: "production-server-ip"
  USERNAME: "deploy-user"
  SSH_KEY: "private-ssh-key"
  DB_USER: "production-db-user"
  DB_PASSWORD: "production-db-password"
  JWT_SECRET: "production-jwt-secret"
  ACME_EMAIL: "admin@torneos.com"
```

## 6. Monitoreo y Observabilidad

### 6.1 Stack de Monitoreo
```yaml
# monitoring/docker-compose.yml
version: '3.8'

services:
  prometheus:
    image: prom/prometheus:latest
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
      - prometheus_data:/prometheus
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus'
      - '--web.console.libraries=/etc/prometheus/console_libraries'
      - '--web.console.templates=/etc/prometheus/consoles'

  grafana:
    image: grafana/grafana:latest
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
    volumes:
      - grafana_data:/var/lib/grafana
      - ./grafana/dashboards:/etc/grafana/provisioning/dashboards
      - ./grafana/datasources:/etc/grafana/provisioning/datasources

  loki:
    image: grafana/loki:latest
    ports:
      - "3100:3100"
    volumes:
      - ./loki-config.yml:/etc/loki/local-config.yaml
    command: -config.file=/etc/loki/local-config.yaml

  promtail:
    image: grafana/promtail:latest
    volumes:
      - /var/log:/var/log:ro
      - ./promtail-config.yml:/etc/promtail/config.yml
    command: -config.file=/etc/promtail/config.yml

volumes:
  prometheus_data:
  grafana_data:
```

### 6.2 Configuración de Métricas (Backend)
```java
// Configuración de Micrometer
@Configuration
public class MetricsConfig {
    
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config().commonTags(
            "application", "torneos-backend",
            "environment", "${spring.profiles.active}"
        );
    }
    
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
}

// Métricas personalizadas
@Component
public class TournamentMetrics {
    
    private final Counter tournamentCreatedCounter;
    private final Timer tournamentCreationTimer;
    private final Gauge activeTournamentsGauge;
    
    public TournamentMetrics(MeterRegistry meterRegistry, TournamentRepository repository) {
        this.tournamentCreatedCounter = Counter.builder("tournaments.created.total")
                .description("Total tournaments created")
                .register(meterRegistry);
                
        this.tournamentCreationTimer = Timer.builder("tournaments.creation.duration")
                .description("Tournament creation duration")
                .register(meterRegistry);
                
        this.activeTournamentsGauge = Gauge.builder("tournaments.active.count")
                .description("Number of active tournaments")
                .register(meterRegistry, this, TournamentMetrics::getActiveTournamentCount);
    }
    
    private double getActiveTournamentCount(TournamentMetrics metrics) {
        return repository.countByStatus(Tournament.TournamentStatus.PUBLISHED);
    }
}
```

### 6.3 Dashboards de Grafana
```json
{
  "dashboard": {
    "title": "Torneos Platform Dashboard",
    "panels": [
      {
        "title": "Request Rate",
        "type": "graph",
        "targets": [
          {
            "expr": "rate(http_requests_total[5m])",
            "legendFormat": "{{method}} {{uri}}"
          }
        ]
      },
      {
        "title": "Response Time",
        "type": "graph",
        "targets": [
          {
            "expr": "histogram_quantile(0.95, rate(http_request_duration_seconds_bucket[5m]))",
            "legendFormat": "95th percentile"
          }
        ]
      },
      {
        "title": "Active Tournaments",
        "type": "singlestat",
        "targets": [
          {
            "expr": "tournaments_active_count",
            "legendFormat": "Active Tournaments"
          }
        ]
      }
    ]
  }
}
```

## 7. Backup y Recuperación

### 7.1 Script de Backup Automatizado
```bash
#!/bin/bash
# backup.sh

DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/backup"
DB_NAME="torneo_prod"
DB_USER="postgres"

# Database backup
docker exec postgres pg_dump -U $DB_USER -d $DB_NAME > $BACKUP_DIR/db_backup_$DATE.sql

# Compress backup
gzip $BACKUP_DIR/db_backup_$DATE.sql

# Upload to S3 (optional)
aws s3 cp $BACKUP_DIR/db_backup_$DATE.sql.gz s3://torneos-backups/

# Clean old backups (keep last 7 days)
find $BACKUP_DIR -name "db_backup_*.sql.gz" -mtime +7 -delete

echo "Backup completed: db_backup_$DATE.sql.gz"
```

### 7.2 Cron Job para Backups
```bash
# Crontab entry
0 2 * * * /opt/scripts/backup.sh >> /var/log/backup.log 2>&1
```

## 8. Seguridad en DevOps

### 8.1 Escaneo de Vulnerabilidades
```yaml
# .github/workflows/security.yml
name: Security Scan

on:
  schedule:
    - cron: '0 6 * * 1'  # Weekly on Monday
  workflow_dispatch:

jobs:
  dependency-check:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v4
    
    - name: Run OWASP Dependency Check
      uses: dependency-check/Dependency-Check_Action@main
      with:
        project: 'torneos'
        path: '.'
        format: 'ALL'
    
    - name: Upload results
      uses: actions/upload-artifact@v3
      with:
        name: dependency-check-report
        path: reports/

  container-scan:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v4
    
    - name: Build image
      run: docker build -t torneos-backend ./backend-torneos
    
    - name: Run Trivy vulnerability scanner
      uses: aquasecurity/trivy-action@master
      with:
        image-ref: 'torneos-backend'
        format: 'sarif'
        output: 'trivy-results.sarif'
    
    - name: Upload Trivy scan results
      uses: github/codeql-action/upload-sarif@v2
      with:
        sarif_file: 'trivy-results.sarif'
```

### 8.2 Secrets Management
```yaml
# Uso de HashiCorp Vault (futuro)
vault:
  image: vault:latest
  environment:
    VAULT_DEV_ROOT_TOKEN_ID: myroot
    VAULT_DEV_LISTEN_ADDRESS: 0.0.0.0:8200
  ports:
    - "8200:8200"
  cap_add:
    - IPC_LOCK
```

## 9. Escalabilidad y Alta Disponibilidad

### 9.1 Kubernetes Deployment (Futuro)
```yaml
# k8s/deployment.yml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: torneos-backend
spec:
  replicas: 3
  selector:
    matchLabels:
      app: torneos-backend
  template:
    metadata:
      labels:
        app: torneos-backend
    spec:
      containers:
      - name: backend
        image: ghcr.io/torneos/backend:latest
        ports:
        - containerPort: 8081
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "k8s"
        - name: DB_HOST
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: host
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8081
          initialDelaySeconds: 60
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8081
          initialDelaySeconds: 30
          periodSeconds: 10
```

### 9.2 Load Balancer Configuration
```yaml
# k8s/service.yml
apiVersion: v1
kind: Service
metadata:
  name: torneos-backend-service
spec:
  selector:
    app: torneos-backend
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8081
  type: LoadBalancer

---
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: torneos-ingress
  annotations:
    kubernetes.io/ingress.class: nginx
    cert-manager.io/cluster-issuer: letsencrypt-prod
spec:
  tls:
  - hosts:
    - api.torneos.com
    secretName: torneos-tls
  rules:
  - host: api.torneos.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: torneos-backend-service
            port:
              number: 80
```

## 10. Métricas y KPIs DevOps

### 10.1 Métricas de Deployment
- **Deployment Frequency**: Frecuencia de despliegues
- **Lead Time**: Tiempo desde commit hasta producción
- **Mean Time to Recovery (MTTR)**: Tiempo promedio de recuperación
- **Change Failure Rate**: Tasa de fallos en cambios

### 10.2 Métricas de Aplicación
- **Response Time**: Tiempo de respuesta promedio
- **Throughput**: Requests por segundo
- **Error Rate**: Tasa de errores
- **Availability**: Disponibilidad del servicio (SLA 99.9%)

### 10.3 Dashboard de KPIs
```json
{
  "kpis": {
    "deployment_frequency": "Daily",
    "lead_time": "< 2 hours",
    "mttr": "< 30 minutes",
    "change_failure_rate": "< 5%",
    "availability": "99.9%",
    "response_time_p95": "< 500ms"
  }
}
```

---

**Conclusión**: La propuesta DevOps proporciona una base sólida para la automatización completa del ciclo de vida del software, desde el desarrollo hasta la producción, con énfasis en la calidad, seguridad y observabilidad del sistema de torneos E-Sport.