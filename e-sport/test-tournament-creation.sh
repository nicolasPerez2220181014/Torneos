#!/bin/bash

echo "PRUEBA DE CREACIÓN DE TORNEOS"
echo "================================"

# Verificar que el backend esté corriendo
echo "1. Verificando backend..."
if ! curl -s http://localhost:8081/actuator/health > /dev/null; then
    echo "Backend no está corriendo en puerto 8081"
    echo "   Ejecuta: cd backend-torneos && mvn spring-boot:run -Dspring.profiles.active=dev"
    exit 1
fi
echo "Backend corriendo"

# Verificar categorías
echo -e "\n2. Verificando categorías..."
CATEGORIES=$(curl -s http://localhost:8081/api/categories)
echo "Categorías disponibles:"
echo "$CATEGORIES" | jq -r '.[] | "- ID: \(.id), Nombre: \(.name)"' 2>/dev/null || echo "$CATEGORIES"

# Verificar tipos de juego
echo -e "\n3. Verificando tipos de juego..."
GAME_TYPES=$(curl -s http://localhost:8081/api/game-types)
echo "Tipos de juego disponibles:"
echo "$GAME_TYPES" | jq -r '.[] | "- ID: \(.id), Nombre: \(.name)"' 2>/dev/null || echo "$GAME_TYPES"

# Crear torneo de prueba
echo -e "\n4. Creando torneo de prueba..."
TOURNAMENT_DATA='{
  "name": "Torneo de Prueba API",
  "description": "Torneo creado desde script de prueba",
  "startDateTime": "'$(date -u -d '+1 day' +%Y-%m-%dT%H:%M:%S.000Z)'",
  "endDateTime": "'$(date -u -d '+2 days' +%Y-%m-%dT%H:%M:%S.000Z)'",
  "maxFreeCapacity": 100,
  "isPaid": false,
  "categoryId": "1",
  "gameTypeId": "1"
}'

echo "Datos del torneo:"
echo "$TOURNAMENT_DATA" | jq . 2>/dev/null || echo "$TOURNAMENT_DATA"

# Intentar crear el torneo (necesita autenticación)
echo -e "\n5. Intentando crear torneo..."
RESPONSE=$(curl -s -X POST \
  -H "Content-Type: application/json" \
  -H "X-USER-ID: 1" \
  -d "$TOURNAMENT_DATA" \
  http://localhost:8081/api/tournaments)

echo "Respuesta del servidor:"
echo "$RESPONSE" | jq . 2>/dev/null || echo "$RESPONSE"

# Verificar si se creó
if echo "$RESPONSE" | grep -q '"id"'; then
    echo -e "\nTorneo creado exitosamente"
else
    echo -e "\nError creando torneo"
    echo "Posibles causas:"
    echo "- Falta autenticación JWT"
    echo "- IDs de categoría/juego incorrectos"
    echo "- Validaciones de fecha"
fi

echo -e "\n6. Listando torneos..."
TOURNAMENTS=$(curl -s http://localhost:8081/api/tournaments)
echo "$TOURNAMENTS" | jq . 2>/dev/null || echo "$TOURNAMENTS"

echo -e "\nINSTRUCCIONES PARA EL FRONTEND:"
echo "1. Asegúrate de estar autenticado"
echo "2. Usa las categorías y tipos de juego listados arriba"
echo "3. Verifica que las fechas sean futuras"
echo "4. Accede a: http://localhost:4200/test-tournament"