FROM openjdk:11
ENV AWS_DEV_DB_URL=jdbc:mysql://eroom-db.cpghnwiewsdn.ap-northeast-2.rds.amazonaws.com:3306/PROJECT_DEV \
    AWS_PROD_DB_URL=jdbc:mysql://eroom-db.cpghnwiewsdn.ap-northeast-2.rds.amazonaws.com:3306/PROJECT_PROD \
    AWS_TEST_DB_URL=jdbc:mysql://eroom-db.cpghnwiewsdn.ap-northeast-2.rds.amazonaws.com:3306/PROJECT_TEST \
    AWS_PASSWORD=qlalfqjsgh1! \
    AWS_USERNAME=admin \
    AWS_ACCESS_KEY_ID=AKIA2TUNUHCY6ACXD4VA \
    AWS_SECRET_ACCESS_KEY=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 \
    ACTIVE_PROFILES=dev
RUN mmkdir /thumbnail
ARG JAR_FILE=/build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]