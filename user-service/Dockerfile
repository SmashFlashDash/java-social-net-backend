# Указываем базовый образ, содержащий Java runtime
FROM openjdk:17-jdk

# Создаём переменную, указывающую на местоположение JAR-файла приложения
ARG JAR_FILE=target/*.jar



# Копируем JAR-файл приложения в контейнер
COPY ${JAR_FILE}  user-service.jar

# Указываем команду для запуска приложения
ENTRYPOINT ["java","-Dspring.datasource.url=jdbc:postgresql://${postgres_host}:${postgres_internal_port}/${postgres_db}","-Dspring.datasource.username=${postgres_user}","-Dspring.datasource.password=${postgres_password}","-Dspring.mail.username=${app_mail_username}","-Dspring.mail.password=${app_mail_password}","-Dapplication.base-url=${application_base_url}","-DgeoService.urlData=${geoService_urlData}","-Dgeonames.userName=${geonames_userName}","-jar","user-service.jar"]

# Сообщаем Docker, что контейнер прослушивает трафик на указанном порту
EXPOSE 8081