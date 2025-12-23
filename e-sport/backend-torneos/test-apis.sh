#!/bin/bash

echo "🚀 Iniciando Backend Torneos..."

# Limpiar y compilar
echo "📦 Compilando proyecto..."
mvn clean compile

# Ejecutar aplicación
echo "🔥 Ejecutando aplicación en puerto 8081..."
mvn spring-boot:run -Dspring.profiles.active=dev &

# Esperar que inicie
echo "⏳ Esperando que la aplicación inicie..."
sleep 15

# Probar APIs básicas
echo "🧪 Probando APIs..."

echo "1. Health Check:"
curl -s http://localhost:8081/actuator/health | jq .

echo -e "\n2. Swagger UI disponible en: http://localhost:8081/swagger-ui.html"

echo -e "\n3. Categorías:"
curl -s http://localhost:8081/api/categories | jq .

echo -e "\n4. Tipos de juego:"
curl -s http://localhost:8081/api/game-types | jq .

echo -e "\n✅ Pruebas básicas completadas"
echo "📋 Ver documento completo: doc/PLAN_PRUEBAS_APIS.md"