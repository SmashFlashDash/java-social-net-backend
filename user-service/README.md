# user-service

Сервис **user-service** отвечает за авторизацию пользователей, управление профилями, настройки и управление списком друзей. Он обеспечивает безопасность доступа, позволяет пользователям настраивать свои профили, а также управлять связями с другими пользователями.

## Конфигурация сервиса

| property                                                          | var                                               | text                                                                                           |
|-------------------------------------------------------------------|---------------------------------------------------|------------------------------------------------------------------------------------------------|
| security.user.name                                                | admin                                             | Имя пользователя                                                                               |
| security.user.password                                            | 12345678                                          | Пароль пользователя                                                                            |
| security.oauth2.client.registration.github.clientId               | CREATE!!!                                         | Идентификатор клиента GitHub                                                                   |
| security.oauth2.client.registration.github.clientSecret           | CREATE!!!                                         | Секрет клиента GitHub                                                                          |
| security.oauth2.client.registration.github.authorizationGrantType | authorization_code                                | Тип авторизации (authorization_code)                                                           |
| security.oauth2.client.registration.github.redirectUri            | http://109.184.122.25:8080/oauth2/callback/github | URI перенаправления для GitHub                                                                 |
| security.oauth2.client.registration.github.scope[0]               | read:user                                         | Область доступа 1                                                                              |
| security.oauth2.client.registration.github.scope[1]               | user:email                                        | Область доступа 2                                                                              |
| security.oauth2.provider.github.authorizationUri                  | https://github.com/login/oauth/authorize          | URI авторизации GitHub                                                                         |
| security.oauth2.provider.github.tokenUri                          | https://github.com/login/oauth/access_token       | URI токена доступа GitHub                                                                      |
| security.oauth2.provider.github.userInfoUri                       | https://api.github.com/user                       | URI информации о пользователе GitHub                                                           |
| security.oauth2.provider.github.userNameAttribute                 | id                                                | Атрибут имени пользователя GitHub                                                              |
| jwt.secret                                                        | team38                                            | Секретный ключ для JWT-токена                                                                  |
| server.servlet.session.tracking-modes                             | cookie                                            | Режим отслеживания сессии                                                                      |
| preferences.friendship-recommendations.age-limit-top              |5                                                  | Верхняя граница возраста для рекоммендаций дружбы по умолчанию<br/> (от возраста пользователя) |
| preferences.friendship-recommendations.age-limit-bottom           |5                                                  | Нижняя граница возраста для рекоммендаций дружбы по умолчанию<br/> (от возраста пользователя)  |
 
Для того, чтобы запустить сервис локально, необходимо:
1. Подключить базу данных (см. [localdev/README.md](../.localdev/README.md) или [db-management/README.md](../db-management/README.md))
2. Настроить конфигурацию:
- Откройте меню "Run" в верхней панели навигации IntelliJ IDEA.

- Выберите пункт "Edit Configurations".

- В окне "Run/Debug Configurations" найдите конфигурацию запуска **user-service**.

- В разделе "VM Options" нажмите на кнопку "Modify Options".

- Выберите пункт "Add VM Options".

- В поле ввода введите необходимые переменные в формате:
```
-Dspring.datasource.url=jdbc:postgresql://localhost:5433/socialnet  -Dspring.datasource.username=postgres  -Dspring.datasource.password=postgres -Dspring.mail.username=code_lounge@mail.ru -Dspring.mail.password=rBLLBxQs6U1DqHdxtpC5 -Dapplication.base-url=http://localhost:8088 -DgeoService.urlData=https://simplemaps.com/static/data/world-cities/basic/simplemaps_worldcities_basicv1.76.zip -Dgeonames.userName=oconner

```
3. Запустить сервис.