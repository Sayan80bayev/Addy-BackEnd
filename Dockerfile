FROM openjdk:21-jdk-slim
EXPOSE 8080
ADD ./target/demo-0.0.1-SNAPSHOT.jar app.jar

# Set environment variables with no spaces around the equal sign
ENV TIGRIS_ACCESS_KEY=${TIGRIS_ACCESS_KEY} \
    TIGRIS_BUCKET_NAME=${TIGRIS_BUCKET_NAME} \
    TIGRIS_SECRET_KEY=${TIGRIS_SECRET_KEY} \
    TIGRIS_URL=${TIGRIS_URL} \
    DATASOURCE_URL=${DATASOURCE_URL} \
    DATASOURCE_USERNAME=${DATASOURCE_USERNAME} \
    DATASOURCE_PASSWORD=${DATASOURCE_PASSWORD}

ENTRYPOINT ["sh", "-c", "java -DTIGRIS_ACCESS_KEY=$TIGRIS_ACCESS_KEY \
                           -DTIGRIS_BUCKET_NAME=$TIGRIS_BUCKET_NAME \
                           -DTIGRIS_SECRET_KEY=$TIGRIS_SECRET_KEY \
                           -DTIGRIS_URL=$TIGRIS_URL \
                           -DDATASOURCE_URL=$DATASOURCE_URL \
                           -DDATASOURCE_USERNAME=$DATASOURCE_USERNAME \
                           -DDATASOURCE_PASSWORD=$DATASOURCE_PASSWORD \
                           -jar app.jar"]