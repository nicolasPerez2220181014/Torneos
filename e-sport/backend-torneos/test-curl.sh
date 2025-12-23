#!/bin/bash

echo "🧪 PRUEBAS DE APIS - BACKEND TORNEOS"
echo "===================================="

BASE_URL="http://localhost:8081"

# 1. HEALTH CHECK
echo -e "\n1️⃣ HEALTH CHECK"
curl -X GET "$BASE_URL/actuator/health"

# 2. CATEGORÍAS (Público)
echo -e "\n\n2️⃣ LISTAR CATEGORÍAS"
curl -X GET "$BASE_URL/api/categories"

# 3. TIPOS DE JUEGO (Público)
echo -e "\n\n3️⃣ LISTAR TIPOS DE JUEGO"
curl -X GET "$BASE_URL/api/game-types"

# 4. TORNEOS (Público)
echo -e "\n\n4️⃣ LISTAR TORNEOS"
curl -X GET "$BASE_URL/api/tournaments"

# 5. CREAR USUARIO
echo -e "\n\n5️⃣ CREAR USUARIO"
curl -X POST "$BASE_URL/api/users" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "fullName": "Usuario Test",
    "role": "USER"
  }'

# 6. LOGIN (usar usuario de datos maestros)
echo -e "\n\n6️⃣ LOGIN"
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@torneos.com",
    "password": "admin123"
  }')
echo $LOGIN_RESPONSE

# Extraer token (requiere jq)
TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.accessToken // .token // empty')

if [ -z "$TOKEN" ]; then
  echo -e "\n⚠️  No se pudo obtener token. Continuando sin autenticación..."
else
  echo -e "\n✅ Token obtenido: ${TOKEN:0:50}..."
  
  # 7. CREAR TORNEO (requiere autenticación)
  echo -e "\n\n7️⃣ CREAR TORNEO (requiere ORGANIZER)"
  curl -X POST "$BASE_URL/api/tournaments" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
      "name": "Torneo Test",
      "description": "Torneo de prueba",
      "categoryId": 1,
      "gameTypeId": 1,
      "isPaid": false,
      "maxFreeCapacity": 100,
      "startDateTime": "2024-12-25T10:00:00",
      "endDateTime": "2024-12-25T18:00:00"
    }'
fi

# 8. SWAGGER UI
echo -e "\n\n8️⃣ SWAGGER UI"
echo "Acceder a: $BASE_URL/swagger-ui.html"

# 9. ACTUATOR HEALTH
echo -e "\n\n9️⃣ ACTUATOR HEALTH"
curl -X GET "$BASE_URL/actuator/health"

echo -e "\n\n✅ PRUEBAS COMPLETADAS"