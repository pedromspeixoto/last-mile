# Install utils
cd lastmile.utils/
mvn clean Install
cd ..

# Build All Modules
cd lastmile.discovery-service/
mvn clean package
cd ..

cd lastmile.gateway-service/
mvn clean package
cd ..

cd lastmile.auth-service/
mvn clean package
cd ..

cd lastmile.account-service/
mvn clean package
cd ..

cd lastmile.driver-service/
mvn clean package
cd ..

cd lastmile.customer-service/
mvn clean package
cd ..

cd lastmile.order-service/
mvn clean package
cd ..

cd lastmile.address-service/
mvn clean package
cd ..

cd lastmile.payment-service/
mvn clean package
cd ..

cd lastmile.quartz-service/
mvn clean package
cd ..

cd lastmile.order-engine/
mvn clean package
cd ..

cd lastmile.notification-engine/
mvn clean package
cd ..

# Create Docker Network
docker network create lastmile-compose-network

# Run Docker Compose
docker-compose up -d --build
