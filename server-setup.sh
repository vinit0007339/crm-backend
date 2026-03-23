#!/bin/bash
# CRM Server Setup Script - Ubuntu 24.04
# Run as root: bash server-setup.sh

set -e
export DEBIAN_FRONTEND=noninteractive

echo "========================================="
echo "  CRM Server Setup Starting..."
echo "========================================="

# Update system
apt-get update -y && DEBIAN_FRONTEND=noninteractive apt-get upgrade -y -o Dpkg::Options::="--force-confold"

# Install essential tools
apt-get install -y curl wget git unzip software-properties-common ufw

# ---- JAVA 17 ----
echo "Installing Java 17..."
apt-get install -y openjdk-17-jdk
java -version

# ---- NODE 20 ----
echo "Installing Node.js 20..."
curl -fsSL https://deb.nodesource.com/setup_20.x | bash -
apt-get install -y nodejs
node -v && npm -v

# ---- MYSQL 8 ----
echo "Installing MySQL 8..."
apt-get install -y mysql-server
systemctl enable mysql
systemctl start mysql

# Secure MySQL and create database
mysql -e "CREATE DATABASE IF NOT EXISTS crm_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -e "CREATE USER IF NOT EXISTS 'crmuser'@'localhost' IDENTIFIED BY 'CrmPass@2024';"
mysql -e "GRANT ALL PRIVILEGES ON crm_db.* TO 'crmuser'@'localhost';"
mysql -e "FLUSH PRIVILEGES;"
echo "MySQL setup complete. DB: crm_db, User: crmuser"

# ---- NGINX ----
echo "Installing Nginx..."
apt-get install -y nginx
systemctl enable nginx
systemctl start nginx

# Frontend site config
cat > /etc/nginx/sites-available/crm-frontend << 'EOF'
server {
    listen 80;
    server_name _;

    root /var/www/crm-frontend;
    index index.html;

    # React Router - serve index.html for all routes
    location / {
        try_files $uri $uri/ /index.html;
    }

    # Proxy API calls to Spring Boot
    location /api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_connect_timeout 60s;
        proxy_read_timeout 60s;
    }

    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml;
}
EOF

# Create frontend web directory
mkdir -p /var/www/crm-frontend
echo "<h1>CRM Frontend - Deploying...</h1>" > /var/www/crm-frontend/index.html

# Enable site
ln -sf /etc/nginx/sites-available/crm-frontend /etc/nginx/sites-enabled/
rm -f /etc/nginx/sites-enabled/default
nginx -t && systemctl reload nginx
echo "Nginx configured."

# ---- SPRING BOOT SERVICE ----
echo "Setting up Spring Boot service..."
mkdir -p /opt/crm-backend

# Update application.yml with production DB credentials
mkdir -p /opt/crm-backend/config
cat > /opt/crm-backend/config/application.yml << 'EOF'
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/crm_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true
    username: crmuser
    password: CrmPass@2024
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect

server:
  port: 8080

jwt:
  secret: crmSuperSecretKeyForProductionUse2024SpringBootJwt
  expiration: 86400000

logging:
  level:
    root: INFO
EOF

# Systemd service for Spring Boot
cat > /etc/systemd/system/crm-backend.service << 'EOF'
[Unit]
Description=CRM Spring Boot Backend
After=network.target mysql.service

[Service]
Type=simple
User=root
WorkingDirectory=/opt/crm-backend
ExecStart=/usr/bin/java -jar /opt/crm-backend/crm-backend-*.jar --spring.config.location=/opt/crm-backend/config/application.yml
Restart=on-failure
RestartSec=10
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
EOF

systemctl daemon-reload
systemctl enable crm-backend
echo "Spring Boot service registered."

# ---- FIREWALL ----
echo "Configuring firewall..."
ufw --force enable
ufw allow OpenSSH
ufw allow 'Nginx Full'
ufw allow 8080/tcp
ufw status

echo ""
echo "========================================="
echo "  SERVER SETUP COMPLETE!"
echo "========================================="
echo "  IP:        $(curl -s ifconfig.me)"
echo "  Frontend:  http://$(curl -s ifconfig.me)"
echo "  Backend:   http://$(curl -s ifconfig.me):8080"
echo "  MySQL DB:  crm_db  User: crmuser  Pass: CrmPass@2024"
echo "========================================="
