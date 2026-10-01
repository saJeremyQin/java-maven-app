#!/usr/bin/env bash
set -euo pipefail

docker compose -f docker-compose.yaml pull
docker compose -f docker-compose.yaml up -d --remove-orphans
echo "Server is running with image: $IMAGE"
