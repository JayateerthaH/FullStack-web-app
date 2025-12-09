# Docker Setup Instructions

## Prerequisites

Make sure Docker Desktop is installed and running:

### For Windows 10/11:
1. **Install Docker Desktop**: https://www.docker.com/products/docker-desktop
2. **Start Docker Desktop**:
   - Click Windows Start
   - Search for "Docker Desktop"
   - Click to launch
   - Wait for it to be ready (icon in system tray)

### Verify Docker is Running:
```powershell
docker --version
docker ps
```

You should see output like:
```
Docker version 25.x.x
CONTAINER ID   IMAGE     COMMAND   CREATED   STATUS    PORTS     NAMES
```

## Run the Full Stack

Once Docker Desktop is running:

```powershell
cd c:\Users\shrey\OneDrive\Desktop\FullStack-web-app

# Start all services (with Resend API key if available)
RESEND_API_KEY=your_api_key docker-compose up -d

# Or without email:
docker-compose up -d
```

### Wait for Services to Start (~1-2 minutes)
```powershell
docker-compose logs -f
```

Press `Ctrl+C` to exit logs.

### Check Status
```powershell
docker-compose ps
```

All services should show **STATUS: Up**

## Access Services

```
Frontend:   http://localhost
Backend:    http://localhost:8080
Prometheus: http://localhost:9090
Grafana:    http://localhost:3000  (admin/admin)
Database:   localhost:5432
```

## Troubleshooting

### Docker Desktop Won't Start
- Try restarting the computer
- Uninstall and reinstall Docker Desktop
- Check if WSL 2 is installed (required for Docker on Windows 10)

### Services Won't Start
```powershell
docker-compose logs backend
docker-compose logs postgres
```

### Port Already in Use
```powershell
# Kill process on port
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

## Stop All Services
```powershell
docker-compose down
```

---

**Next Steps**:
1. Start Docker Desktop
2. Run `docker-compose up -d`
3. Visit http://localhost in your browser
4. Monitor at http://localhost:3000 (Grafana)
