#!/bin/bash

echo "=== Deteniendo Backend y Frontend ==="

# Matar procesos en puertos
lsof -ti:8081 | xargs kill -9 2>/dev/null
lsof -ti:4200 | xargs kill -9 2>/dev/null

echo "✓ Backend detenido (puerto 8081)"
echo "✓ Frontend detenido (puerto 4200)"
