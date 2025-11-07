#!/bin/bash

# ==========================================================
# 🧹 Limpieza segura de volúmenes de desarrollo
# Borra caches, dependencias y datos temporales
# sin eliminar la base de datos PostgreSQL.
# ==========================================================

echo "🧩 Deteniendo contenedores..."
docker compose down --remove-orphans

echo "🧹 Eliminando volúmenes de dependencias y caché..."
# Lista todos los volúmenes, filtra y borra los que NO son de la DB
docker volume rm $(docker volume ls -q | grep -E "maven_cache_dev|frontend_node_modules_dev|redis_data_dev|rabbitmq_data_dev") 2>/dev/null

echo "✨ Limpieza completa. Base de datos preservada (postgres_data_dev)."
