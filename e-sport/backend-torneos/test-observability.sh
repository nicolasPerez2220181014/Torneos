#!/bin/bash

# Script para testing de observabilidad
# Backend Torneos

BASE_URL="http://localhost:8081"

echo "=== Testing Observability ==="
echo ""

# 1. Health Check
echo "1. Health Check:"
echo "   GET $BASE_URL/actuator/health"
curl -s "$BASE_URL/actuator/health" | jq .
echo ""

# 2. Listar métricas disponibles
echo "2. Métricas disponibles:"
echo "   GET $BASE_URL/actuator/metrics"
curl -s "$BASE_URL/actuator/metrics" | jq '.names[] | select(. | startswith("tickets") or startswith("tournaments"))'
echo ""

# 3. Métrica tickets.created
echo "3. Métrica tickets.created:"
echo "   GET $BASE_URL/actuator/metrics/tickets.created"
curl -s "$BASE_URL/actuator/metrics/tickets.created" | jq .
echo ""

# 4. Métrica tournaments.published
echo "4. Métrica tournaments.published:"
echo "   GET $BASE_URL/actuator/metrics/tournaments.published"
curl -s "$BASE_URL/actuator/metrics/tournaments.published" | jq .
echo ""

# 5. Prometheus endpoint
echo "5. Prometheus metrics (filtrado):"
echo "   GET $BASE_URL/actuator/prometheus"
curl -s "$BASE_URL/actuator/prometheus" | grep -E "^(tickets|tournaments)" | head -10
echo ""

# 6. Info endpoint
echo "6. Application Info:"
echo "   GET $BASE_URL/actuator/info"
curl -s "$BASE_URL/actuator/info" | jq .
echo ""

echo "=== Comandos útiles ==="
echo ""
echo "# Ver todas las métricas:"
echo "curl $BASE_URL/actuator/metrics | jq ."
echo ""
echo "# Ver métrica específica:"
echo "curl $BASE_URL/actuator/metrics/jvm.memory.used | jq ."
echo ""
echo "# Exportar para Prometheus:"
echo "curl $BASE_URL/actuator/prometheus > metrics.txt"
echo ""
