FROM gradle:jdk-21-and-22
LABEL authors="everettwilber"

COPY ./gradle .
COPY ./.gradle .
COPY ./.kotlin .
COPY ./src .
COPY ./.gitignore .
COPY ./build.gradle.kts .
COPY ./Dockerfile .
COPY ./gradle.properties .
COPY ./gradlew .
COPY ./src .
COPY ./ .

RUN ./gradlew build

ENTRYPOINT ["./gradlew", "run"]