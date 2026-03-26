#!/bin/bash

echo "=== Iniciando Backend Torneos ==="

# 1. Ir al directorio del backend
cd /Users/nicolas.perez/Pragma/Torneos/e-sport/backend-torneos

# 2. Iniciar backend en background
echo "Iniciando backend en puerto 8081..."
java -jar target/torneos-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev > backend.log 2>&1 &
BACKEND_PID=$!
echo "Backend iniciado con PID: $BACKEND_PID"

# 3. Esperar a que el backend esté listo
echo "Esperando a que el backend esté listo..."
sleep 15

# 4. Verificar health del backend
echo "Verificando health del backend..."
curl -s http://localhost:8081/actuator/health | jq .

echo ""
echo "=== Iniciando Frontend Torneos ==="

# 5. Ir al directorio del frontend
cd /Users/nicolas.perez/Pragma/Torneos/e-sport/frontend-torneos

# 6. Iniciar frontend en background
echo "Iniciando frontend en puerto 4200..."
npm start > frontend.log 2>&1 &
FRONTEND_PID=$!
echo "Frontend iniciado con PID: $FRONTEND_PID"

echo ""
echo "=== Proyecto Iniciado ==="
echo "Backend:  http://localhost:8081"
echo "Frontend: http://localhost:4200"
echo "Swagger:  http://localhost:8081/swagger-ui.html"
echo ""
echo "PIDs guardados:"
echo "Backend PID:  $BACKEND_PID"
echo "Frontend PID: $FRONTEND_PID"
echo ""
echo "Para detener:"
echo "  kill $BACKEND_PID $FRONTEND_PID"
echo ""
echo "Ver logs:"
echo "  Backend:  tail -f /Users/nicolas.perez/Pragma/Torneos/e-sport/backend-torneos/backend.log"
echo "  Frontend: tail -f /Users/nicolas.perez/Pragma/Torneos/e-sport/frontend-torneos/frontend.log"
