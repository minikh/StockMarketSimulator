# Build
    ./gradlew clean build -x test
# Run
    java -jar build/libs/sms-0.0.1-SNAPSHOT.jar


# Shell    
#### stockName orderType count price
    shell:>add AAA BUY 10 10
    shell:>cancel AAA ae902f28-6c5b-47e4-92d1-d140e5b4c186
    shell:>add AAA SELL 25 10

# Swagger
    http://localhost:8080/api/swagger-ui/index.html?url=/api/api-docs
