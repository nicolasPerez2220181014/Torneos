#!/bin/bash

# Script de ejemplo para testing de JWT
# Backend Torneos - Seguridad JWT

BASE_URL="http://localhost:8081"

echo "=== Testing JWT Security ==="
echo ""

# 1. Endpoint público (sin token)
echo "1. Testing endpoint público (sin autenticación):"
curl -s -X GET "$BASE_URL/api/example/public" | jq .
echo ""

# 2. Login para obtener token
echo "2. Login para obtener token:"
echo "   POST $BASE_URL/api/auth/login"
echo "   Body: {\"email\":\"organizer@test.com\",\"password\":\"password\"}"
echo ""
echo "   Ejemplo de respuesta:"
echo "   {\"token\":\"eyJhbGciOiJIUzI1NiJ9...\",\"refreshToken\":\"...\"}"
echo ""

# 3. Usar token en request
echo "3. Ejemplo de uso del token:"
echo "   TOKEN=\"eyJhbGciOiJIUzI1NiJ9...\""
echo ""
echo "   curl -X GET $BASE_URL/api/example/authenticated \\"
echo "     -H \"Authorization: Bearer \$TOKEN\""
echo ""

# 4. Endpoint protegido sin token (401)
echo "4. Testing endpoint protegido sin token (esperado: 401):"
curl -s -X GET "$BASE_URL/api/example/authenticated" | jq .
echo ""

# 5. Endpoint con rol incorrecto (403)
echo "5. Testing endpoint con rol incorrecto (esperado: 403):"
echo "   Si usas token de USER en endpoint de ORGANIZER"
echo ""

# Instrucciones
echo "=== Instrucciones ==="
echo ""
echo "Para probar con token real:"
echo ""
echo "1. Obtener token:"
echo "   TOKEN=\$(curl -s -X POST $BASE_URL/api/auth/login \\"
echo "     -H 'Content-Type: application/json' \\"
echo "     -d '{\"email\":\"organizer@test.com\",\"password\":\"password\"}' | jq -r .token)"
echo ""
echo "2. Usar token:"
echo "   curl -X GET $BASE_URL/api/example/authenticated \\"
echo "     -H \"Authorization: Bearer \$TOKEN\" | jq ."
echo ""
echo "3. Endpoint solo ORGANIZER:"
echo "   curl -X POST $BASE_URL/api/example/organizer-only \\"
echo "     -H \"Authorization: Bearer \$TOKEN\" | jq ."
echo ""

# Generar token de ejemplo (requiere jq)
echo "=== Generar Token de Ejemplo ==="
echo ""
echo "Estructura del token JWT:"
echo "Header: {\"alg\":\"HS256\",\"typ\":\"JWT\"}"
echo "Payload: {\"sub\":\"<userId>\",\"email\":\"<email>\",\"role\":\"<role>\",\"iat\":<timestamp>,\"exp\":<timestamp>}"
echo "Signature: HMACSHA256(base64UrlEncode(header) + \".\" + base64UrlEncode(payload), secret)"
echo ""
