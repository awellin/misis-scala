Проект: реализовать распределенную систему Reactive Pizza
Требования:
- 5 сервисов: Меню, Корзина, Заказы, Терминал, Кухня
- 2 бота: Официант, Повар. Работают по таймауту, реагируя на свои события. Таймаут повара задается технологической картой.
- Коммуникация между сервисами, ботами и пользователем согласно приложенной схеме (осуждение было на занятии)
- Выполнение команд сервисами по http api (команда на схеме - сплошная стрелка)
- Обработка событий по модели pub/sub, с использованием Apache Kafka (событие - пунктирная стрелка)
- Сервисы должны быть выполнены на scala
- Проект должен деплоится docker-compose с билдом докерфайлов
  (детали могут уточняться, на занятии буду комментировать)

Как система должна работать:
1. Все сервисы должны запуститься одной командой: `docker-compose start`
2. Формируем меню, выполняя все запросы из `menu/create.http`
3. Формуруем корзину и оформляем заказ, выполняя все запросы из cart/create.http
4. После того как заказ сформирован система должна без участия пользователя обработать заказ согласно требованиям.
   Обработка всех событий должна логироваться всеми сервисами.
   Смены статусов заказа должны логироваться в сервисе order.
5. В результате обработки заказа, в сервисе order должна появиться запись о том, что заказ выполнен.


Полезности для Kafka:
1. Просмотр всех топиков по констюмерам: 
  `docker exec -t docker_kafka-pizza_1  kafka-consumer-groups.sh --bootstrap-server localhost:9092 --all-groups --describe`
2. Сброс offset для консьюмера:   
  `docker exec -t docker_kafka-pizza_1  kafka-consumer-groups.sh --bootstrap-server localhost:9092 --topic AccountUpdated --group account-1 --reset-offsets --to-earliest --execute`
2. Сдвиг offset для консьюмера:   
  `docker exec -t docker_kafka-pizza_1  kafka-consumer-groups.sh --bootstrap-server localhost:9092 --topic AccountUpdated --group account-1 --reset-offsets --shift-by -1 --execute`

