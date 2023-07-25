## gateway-service

Gateway Service (сервис шлюза) является промежуточным компонентом в архитектуре микросервисов, который отвечает за обработку внешних запросов и направление их к соответствующим микросервисам. Он играет роль точки входа для клиентских приложений или других сервисов, обеспечивая им единый и унифицированный интерфейс.

### Конфигурация сервиса


| property                       | var              | text                                                                                                        |
|--------------------------------|------------------|-------------------------------------------------------------------------------------------------------------|
| spring.cloud.gateway.routes[0] | id               | user-service                                                                                                |
| spring.cloud.gateway.routes[0] | uri              | lb://user-service                                                                                           |
| spring.cloud.gateway.routes[0] | predicates[0]    | Path=/user-service/** - предикат маршрута для запросов, начинающихся с "/user-service/"                     |
| spring.cloud.gateway.routes[1] | id               | communications-service                                                                                      |
| spring.cloud.gateway.routes[1] | uri              | lb://communications-service                                                                                 |
| spring.cloud.gateway.routes[1] | predicates[0]    | Path=/communications-service/** - предикат маршрута для запросов, начинающихся с "/communications-service/" |


Для того чтобы запустить сервис необходимо:
1. Настроить конфигурацию:
- Откройте меню "Run" в верхней панели навигации IntelliJ IDEA.

- Выберите пункт "Edit Configurations".

- В окне "Run/Debug Configurations" найдите конфигурацию запуска **user-service**.

- В разделе "VM Options" нажмите на кнопку "Modify Options".

- Выберите пункт "Add VM Options".

- В поле ввода введите необходимые переменные в формате:
```
-Dspring.services.user.url=http://localhost:8081
-Dspring.services.communications.url=http://localhost:8082
-Dspring.cors.origin=http://localhost:8088
-Dspring.servlet.multipart.max-file-size=5MB
```
2. Запустить сервис.