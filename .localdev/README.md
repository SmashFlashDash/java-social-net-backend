# social-net-backend

***
### Docker hub
Логин: javapro38

Пароль: javapro38_team
***
## Build: postgre, rabbitmq, frontend
Требуется:
- установленный Docker

В терминале из корня проекта запустить  
```
docker-compose up -d
```
или 
```
docker-compose -f docker-compose.yml up -d
```  
Будут созданы контейнеры postgre и frontend. 

Порты:  
- postgre: 5432 
- frontend: 8088

В postgre созданы бд socialnet, develop.

Терминалы:
- postgre
    ```bash
    docker exec -it postgres psql -U postgres
    ```
- frontend
    ```bash
    docker exec -it frontend bash
    ```
Остановка контейнеров (из корня проекта):
```
docker-compose down
```
или
```
docker-compose -f docker-compose.yml down
```
***

## Gitlab-runner manual
Необходимо:
- скачать с dockerhub контейнерный образ gitlab-runner: 
```
docker pull gitlab/gitlab-runner:latest
```


- создать volume gitlab-runner-config, запустить gitlab-runner в Docker:
    ```
    docker volume create gitlab-runner-config
    docker run -d --name gitlab-runner --restart always -v /var/run/docker.sock:/var/run/docker.sock -v gitlab-runner-config:/etc/gitlab-runner gitlab/gitlab-runner:latest
    ```
  **-d**: Запуск контейнера в фоновом режиме (daemon mode).  
  **--name gitlab-runner**: Задает имя контейнера GitLab Runner.  
  **--restart always**: Указывает Docker перезапустить контейнер GitLab Runner автоматически при его остановке.  
  **-v /var/run/docker.sock:/var/run/docker.sock**: Монтирует сокет Docker в контейнер GitLab Runner, что позволяет ему запускать другие контейнеры.  
  **-v gitlab-runner-config:/etc/gitlab-runner**: Монтирует volume docker внутрь контейнера.


- зарегестрировать gitlab-runner:
    ```
    docker exec -it gitlab-runner gitlab-runner register
    ```
    При регистрации вам будут заданы ряд вопросов. Введите следующую информацию:
    **GitLab instance URL**: Укажите https://gitlab.skillbox.ru/.  
    **Registration token**: GR1348941YgqF_QWGyeg4e7ro5dPu.  
    **Runner description**: Укажите описание для GitLab Runner, например "My Docker Runner".  
    **Runner tags**: Можете указать теги для GitLab Runner, если они вам нужны.  
    **Executor**: Выберите тип исполнителя, который вы хотите использовать. Для запуска заданий в контейнерах Docker выберите docker.  
    **Docker image**: Если вы выбрали исполнитель docker, укажите образ Docker, который будет использоваться для запуска заданий. Например, docker:latest.  
    **Docker volumes**: Укажите любые дополнительные примонтированные тома, если они вам нужны.  
      

- в файле **/etc/gitlab-runner/config.toml** добавьте/измените следующие значения:  
  privileged = true  
  volumes = ["gitlab-runner-builds:/builds", "gitlab-runner-cache:/cache","/var/run/docker.sock:/var/run/docker.sock"]
  ```
  docker exec -it gitlab-runner bash
  cat > /etc
  ```

- после внесения изменений в **config.toml** перезагрузите контейнер.
- после завершения регистрации GitLab Runner будет связан с вашим GitLab-сервером и готов к выполнению заданий CI/CD.

- сообщите данные из **config.toml** администратору Gitlab.
***

### Конфигурация базы данных
| property             | var       | text                                            |
|----------------------|-----------|-------------------------------------------------|
| db.postgres.host     |           | Хост базы данных (по умолчанию: localhost:5433) |
| db.postgres.name     | socialnet | Имя базы данных                                 |
| db.postgres.username | postgres  | Имя пользователя базы данных                    |
| db.postgres.password | postgres  | Пароль пользователя базы данных                 |
***


### Конфигурация aws-s3 (yandex object storage) для хранения файлов
| property                               | var                                      | text                                                                        |
|----------------------------------------|------------------------------------------|-----------------------------------------------------------------------------|
| yandexObjectStorage.endpoint           | https://storage.yandexcloud.net/         | эндпоинт yandex cloud используемый зависимость aws-s3-sdk                   |
| yandexObjectStorage.accessKey          | YCAJEg9dsfQhSCJAMoJ_i4CK2                | Идентификатор статического ключа доступа к сервисному аккаунту yandex cloud |
| yandexObjectStorage.secretKey          | YCMMT0nO2JO1tAMqmxFK28KZijMN0C1BRzokJQdB | Cтатический ключа доступа к сервисному аккаунту yandex cloud                |
| yandexObjectStorage.bucketName         | team38bucket                             | Имя бакета в object storage                                                 |
| spring.servlet.multipart.max-file-size | 5MB                                      | Установить максимальынй размер файла для tomcat                             |
***


### Сборка проекта

Для сборки и подключеня к бд проекта выполните следующие шаги:

1. Поднять контейнер (указано в блоке **Build**, написанным выше) или запустить, если уже установлен
2. Выполнить команду из корня проекта: 
```
mvn clean install -P localhost
```
***
## Frontend
Для корректной работы необходимо выполнить следующие шаги:
1. Обновить frontend:
```
docker-compose up -d frontend
```
2. В созданом контейнере поменять конфиг:
   - пройдите в терминале контейнера по адресу: /etc/nginx/conf.d 
   - откройте default.conf
   - внесите следующее содержимое:
```
server {
 listen 80;
 server_name frontend;

 location / {
   root /usr/share/nginx/html;
   try_files $uri /index.html;
 }

location /api/v1/ {
   proxy_pass http://host.docker.internal:8080;
 }
}
```
3. Перезапустите контейнер.