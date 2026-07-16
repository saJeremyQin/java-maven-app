#!/usr/bin/env bash
export IMAGE=$1
docker compose -f docker-compose.yaml up -d
echo "Server is running with image: $IMAGE"
