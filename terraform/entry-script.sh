#!/bin/bash
set -euxo pipefail

dnf install -y docker
systemctl enable --now docker
usermod -aG docker ec2-user

mkdir -p -m 0755 /usr/local/lib/docker/cli-plugins

# install Docker Compose plugin
curl -fsSL \
  https://github.com/docker/compose/releases/latest/download/docker-compose-linux-x86_64 \
  -o /usr/local/lib/docker/cli-plugins/docker-compose
chmod +x /usr/local/lib/docker/cli-plugins/docker-compose