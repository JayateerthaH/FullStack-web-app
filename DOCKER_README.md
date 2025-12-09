# Attendance System - Full Stack Docker Deployment

Complete containerized attendance system with monitoring using Docker, Prometheus, and Grafana.

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Frontend (Nginx)                          │
│                      Port 80                                 │
└────────────────┬────────────────────────────────────────────┘
                 │
┌────────────────▼────────────────────────────────────────────┐
│                 Spring Boot Backend                          │
│              Port 8080 (/api/*)                              │
│         Prometheus Metrics: /actuator/prometheus             │
└────────────────┬────────────────────────────────────────────┘
                 │
┌────────────────▼────────────────────────────────────────────┐
│              PostgreSQL Database                             │
│                  Port 5432                                   │
└─────────────────────────────────────────────────────────────┘

┌──────────────────────┐    ┌──────────────────────┐
│    Prometheus        │    │      Grafana         │
│     Port 9090        │    │      Port 3000       │
│   (Metrics Store)    │    │   (Visualization)    │
└──────────────────────┘    └──────────────────────┘
```

## Prerequisites

- Docker & Docker Compose installed
- RESEND_API_KEY environment variable (for email functionality)

## Quick Start

### 1. Clone and Setup

```bash
git clone https://github.com/JayateerthaH/FullStack-web-app.git
cd FullStack-web-app
```

### 2. Run with Docker Compose

```bash
# With Resend API key
RESEND_API_KEY=your_api_key docker-compose up -d

# Or without email functionality
docker-compose up -d
```

### 3. Access Services

| Service | URL | Credentials |
|---------|-----|-------------|
| **Frontend** | http://localhost | - |
| **Backend** | http://localhost:8080/api | - |
| **Prometheus** | http://localhost:9090 | - |
| **Grafana** | http://localhost:3000 | admin/admin |
| **Database** | localhost:5432 | postgres/password |

## Using Grafana

### 1. First Login
- **URL**: http://localhost:3000
- **Username**: admin
- **Password**: admin
- Change password on first login

### 2. View Dashboards
- Datasource is auto-configured to Prometheus
- Dashboard is pre-provisioned: "Attendance System - Spring Boot Metrics"
- Shows:
  - HTTP Request Rate & Duration
  - JVM Memory & Threads
  - Database Connections
  - CPU Usage
  - Garbage Collection

### 3. Create Custom Dashboards
- Click **+** → **Create** → **Dashboard**
- Add panels with Prometheus queries:
  - `rate(http_server_requests_seconds_count[1m])` - Request rate
  - `jvm_memory_used_bytes` - Memory usage
  - `hikaricp_connections_active` - Active DB connections

## Using Prometheus

### 1. Access Prometheus
- **URL**: http://localhost:9090
- **Query Endpoint**: http://localhost:9090/api/v1/query

### 2. View Metrics
- Search for metrics in the Expression Browser
- Example queries:
  - `http_server_requests_seconds_count` - Total HTTP requests
  - `jvm_memory_used_bytes` - JVM memory
  - `process_uptime_seconds` - Application uptime

### 3. Health Check
- Backend metrics: http://localhost:8080/actuator/prometheus
- Health endpoint: http://localhost:8080/actuator/health

## Stopping Services

```bash
# Stop all services
docker-compose down

# Stop and remove volumes (clean slate)
docker-compose down -v
```

## Logs

```bash
# View all logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f backend
docker-compose logs -f postgres
docker-compose logs -f prometheus
docker-compose logs -f grafana
```

## Project Structure

```
FullStack-web-app/
├── docker-compose.yml          # Main Docker Compose config
├── Dockerfile                  # Backend build config
├── prometheus.yml              # Prometheus scrape config
├── pom.xml                     # Maven dependencies (+ Prometheus)
├── src/
│   ├── main/java/             # Spring Boot application
│   │   └── service/           # EmailService, AttendanceService, etc.
│   └── resources/
│       └── application.properties  # App config (Prometheus enabled)
├── frontend/
│   ├── Dockerfile             # Nginx frontend config
│   └── index.html             # Frontend HTML (proxies to /api)
└── grafana/
    └── provisioning/
        ├── datasources/       # Prometheus datasource config
        └── dashboards/        # Pre-built dashboards
```

## Key Features

✅ **Full Stack Containerization** - All services in Docker  
✅ **Prometheus Metrics** - Automatic Spring Boot metrics collection  
✅ **Grafana Dashboards** - Pre-configured monitoring dashboards  
✅ **Health Checks** - Auto-healing containers  
✅ **Volume Persistence** - Data survives container restarts  
✅ **Service Networking** - Internal Docker network communication  

## Monitoring Metrics

### Application Metrics
- **HTTP Requests**: Rate, duration (95th percentile), status codes
- **JVM**: Memory (heap/non-heap), threads, garbage collection
- **Database**: Active/idle connections, pool size
- **System**: CPU usage, uptime

### Alert Examples (Configure in Prometheus)
```yaml
# Alert if error rate > 5%
expr: (sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m])) / sum(rate(http_server_requests_seconds_count[5m]))) > 0.05

# Alert if memory usage > 80%
expr: (jvm_memory_used_bytes / jvm_memory_max_bytes) > 0.8

# Alert if active DB connections > 4
expr: hikaricp_connections_active > 4
```

## Troubleshooting

### Backend not starting
```bash
docker-compose logs backend
# Check if port 8080 is in use
```

### Prometheus not scraping metrics
```bash
# Check Prometheus targets
curl http://localhost:9090/api/v1/targets
# Verify backend is healthy
curl http://localhost:8080/actuator/health
```

### Frontend not loading
```bash
# Check nginx logs
docker-compose logs frontend
# Verify backend is accessible
curl http://localhost:8080/api/health
```

### Grafana dashboard not showing data
- Wait 30-60 seconds for Prometheus to scrape metrics
- Check Prometheus datasource: http://localhost:3000/datasources
- Verify metrics exist: http://localhost:9090/graph

## Performance Tuning

### For Production
1. **Increase resources** in docker-compose.yml:
   ```yaml
   backend:
     deploy:
       resources:
         limits:
           cpus: '2'
           memory: 2G
   ```

2. **Enable persistence** for Prometheus:
   ```yaml
   prometheus:
     volumes:
       - prometheus_data:/prometheus
   ```

3. **Configure Grafana** with reverse proxy for security

4. **Set up alerts** in Prometheus and notification channels

## Support

For issues or questions:
- Check logs: `docker-compose logs -f`
- Verify all services are running: `docker-compose ps`
- Restart services: `docker-compose restart`

---

**Created**: December 2024  
**Tech Stack**: Spring Boot 3.2.5, PostgreSQL 15, Prometheus, Grafana, Docker, Nginx
