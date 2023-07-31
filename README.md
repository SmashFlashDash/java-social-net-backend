# social-net-backend

## [common](./common/README.md)

## [communications-service](./communications-service/README.md)

## [db-management](./db-management/README.md)

## [gateway-service](./gateway-service/README.md)

## [user-service](./user-service/README.md)

## [.localdev](./.localdev/README.md)

## Инструкция по созданию релизов (manual deploy)
1 Создать от dev ветку с форматом имени release_1.0_yyyy-mm-dd (где 0 - номер спринта)

2 Создать MR в ветку master, слить после апрува

3 Убедиться, что образы сервисов обновились в docker hub

4 Cкопировать docker-compose в специальную папку на удаленном сервере

5 Скопировать папку миграций в специальную папку на удаленном сервере

6 Выполнить docker-compose на сервере

7 Проверить доступность фронта и работоспособность сервисов

## Инструкция по созданию hotfix (частичное обновление сервера)
1 Выполнить билд проекта

2 Выполнить docker build нужного сервиса

3 Выполнить docker push этого сервиса

4 (если нужно) скопировать на сервер docker-compose файл и папку с миграциями

5 Выполнить docker-compose на сервере

6 Проверить доступность фронта и работоспособность сервисов


## Развертывание на удаленном сервере:

1. **Подключиться к удаленному серверу:**
    
    Используйте следующую команду, чтобы установить безопасное соединение:
    ```
    ssh -i <путь к файлу приватного ключа> server_username@server_host
    ```
    где \socialnet - путь к вашему приватному ssh ключу

2. **Создайте новый каталог для вашего проекта с помощью команды mkdir:**
    ```
    mkdir project
    ```

3. **Установите Docker:**

   - **Обновите существующий список пакетов:**
    ```
    sudo apt update
    ```
   - **Установите предварительные пакеты:**
    ```
    sudo apt install apt-transport-https ca-certificates curl software-properties-common
    ```
   (Они позволят apt использовать пакеты через HTTPS)
   - **Добавьте официальный GPG-ключ Docker:**
    ```
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/trusted.gpg.d/docker.gpg
    ```
   - **Добавьте репозиторий Docker в источники APT:**
    ```
    sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
    ```
   - **Обновите базу данных пакетов:**

   (Это включит пакеты Docker из недавно добавленного репозитория:)
    ```
    sudo apt update
    ```
   - **Убедитесь, что вы устанавливаете Docker из репозитория Docker, а не репозитория Ubuntu по умолчанию:**
    ```
    apt-cache policy docker-ce
    ```
    Вывод должен выглядеть следующим образом:
    ```
    docker-ce:
        Installed: (none)
        Candidate: 5:20.10.6~3-0~ubuntu-focal
        Version table:
            5:20.10.6~3-0~ubuntu-focal 500
                500 https://download.docker.com/linux/ubuntu focal/stable amd64 Packages
    ```
   - **Установите Docker:**
    ```
    sudo apt install docker-ce
    ```
   - **Убедитесь, что Docker работает:**
    ```
    sudo systemctl status docker
    ```

4. **Установите Docker Compose:**
    - **Скачать Docker Compose:**
      
      ```
      sudo curl -L "https://github.com/docker/compose/releases/<путь к выбранному двоичному файлу для соответствующей архитектуры сервера>" -o /usr/bin/docker-compose
      ```
    - **Сделать Docker Compose исполняемым:**
      ```
      sudo chmod +x /usr/bin/docker-compose
      ```
    - **Проверьте установку Docker Compose:**
      
      Если версия отображается, установка прошла успешно:
      ```
      docker-compose --version
      ```

5. **Перенос файлов проекта:** (с терминала на локальной машине)
    - **Копировать каталоги:**
      Используйте команду `scp`, чтобы рекурсивно скопировать необходимые каталоги (например, миграции или тестовые скрипты) в каталог проекта на удаленном сервере:
      ```
      scp -r -i <путь к файлу приватного ключа> ./db-management/src/main/resources/changelog server_username@server_host:~<путь к папке проекта из корневого каталога>
      ```
    - **Копировать отдельные файлы:**
      Точно так же скопируйте отдельные файлы, такие как `.env` или `docker-compose.yml`:
      ```
      scp -i <путь к файлу приватного ключа> ./docker-compose.yml server_username@server_host:~<путь к папке проекта из корневого каталога>
      ```

6. **Запустить сервисы:**
   Используйте Docker Compose для запуска сервисов в автономном режиме:
    ```
    docker-compose up -d
    ```
    Если возникли проблемы с разрешениями, добавьте пользователя в группу докера и перезайдите на удаленный сервер:
    ```
    sudo usermod -aG docker ${USER}
    ```