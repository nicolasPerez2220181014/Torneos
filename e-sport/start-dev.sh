#!/bin/bash

echo "🚀 INICIANDO ENTORNO DE DESARROLLO"
echo "=================================="

# Función para limpiar procesos al salir
cleanup() {
    echo -e "\n🛑 Deteniendo servicios..."
    pkill -f "spring-boot:run"
    pkill -f "ng serve"
    exit 0
}

# Capturar Ctrl+C
trap cleanup SIGINT

# 1. Iniciar Backend
echo -e "\n📦 INICIANDO BACKEND (Puerto 8081)..."
cd backend-torneos
mvn clean compile
mvn spring-boot:run -Dspring.profiles.active=dev > backend.log 2>&1 &
BACKEND_PID=$!

# 2. Esperar que el backend inicie
echo "⏳ Esperando backend..."
sleep 15

# 3. Verificar backend
echo "🔍 Verificando backend..."
if curl -s http://localhost:8081/actuator/health > /dev/null; then
    echo "✅ Backend iniciado correctamente"
else
    echo "❌ Error iniciando backend"
    exit 1
fi

# 4. Iniciar Frontend
echo -e "\n🎨 INICIANDO FRONTEND (Puerto 4200)..."
cd ../frontend-torneos
npm start &
FRONTEND_PID=$!

echo -e "\n🎯 SERVICIOS INICIADOS:"
echo "   Backend:  http://localhost:8081"
echo "   Frontend: http://localhost:4200"
echo "   Swagger:  http://localhost:8081/swagger-ui.html"
echo -e "\n💡 Presiona Ctrl+C para detener todos los servicios"

# Mantener el script corriendo
wait