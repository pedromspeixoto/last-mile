# Last Mile Logistics Solution
This repository has the first development version of the Last Mile Logistics Solution.

This application aims to control and manage everything related with:

- Orders (last-mile.order-service)
- Drivers (last-mile.driver-service)
- Customers (last-mile.customer-service)
- Addresses (last-mile.address-service)
- Payments (last-mile.payment-service)

## Architecture

This applications leverages the following technologies:

- Java and Spring Boot
- Spring Cloud
- Spring Data
- Spring Security
- Spring Cloud Netflix Architecture (Zuul and Eureka)
- Hystrix for circuit breaking
- Quartz to schedule jobs and tasks
- RabbitMQ to manage asynchronous events
- PostgreSQL as our main data service
- New Relic for monitoring and log aggregation
- Filebeat to send logs to Logstash
- Logstash to parse logs and send them to New Relic
- Docker and Docker Compose to run the application in a containerized environment for local and development environments

External services that we interact with:

- Twilio
- EasyPay
- Google Maps
- Firebase
- External API to get vehicle information (make, model, year, etc)