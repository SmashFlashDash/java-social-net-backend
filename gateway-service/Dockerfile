# Указываем базовый образ, содержащий Java runtime
FROM openjdk:17-jdk

# Создаём переменную, указывающую на местоположение JAR-файла приложения
ARG JAR_FILE=target/*.jar

# Копируем JAR-файл приложения в контейнер
COPY ${JAR_FILE} gateway-service.jar

# Указываем команду для запуска приложения
ENTRYPOINT ["java","-Dspring.services.user.url=${user_service_url}","-Dspring.services.communications.url=${communications_service_url}","-Dspring.cors.origin=${cors_origin}","-Dspring.servlet.multipart.max-file-size=${max_upload_file_size}","-jar", "gateway-service.jar"]

# Сообщаем Docker, что контейнер прослушивает трафик на указанном порту
EXPOSE 8080