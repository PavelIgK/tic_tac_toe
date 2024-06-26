# Крестики нолики (апи)

## Содержание
- [Требования](#Требования)
- [API](#API)
- [Использование](#Использование)
- [Параметры для запуска приложения](#Параметры-для-запуска-приложения)
- [Дополнительно](#Дополнительно)

## Требования
- Java 17
- Docker
- Maven

## API
Api для игры в крестики нолики.

Поле в игре представлено ячейками под номерами:<br>
0 1 2<br>
3 4 5<br>
6 7 8<br>

Api приложения:<br>
Все эндпоины находятся по адресу: http://{URL}:{PORT}/{API_ENDPOINT}/

- POST http://{URL}:{PORT}/{API_ENDPOINT}/?userStart=false&gameLevel=HARD<br><br>

  - userStart - параметр для определения первого хода:<br>
    - true - вернется пустая доска и будет ожидаться ход пользователя.
    - false - вернется доска с ходом машины и будет ожидаться ход пользователя
      <br><br>
  - gameLevel - параметр с уровнем игры
    - EASY самый простой уровень игры, компьютер выбирает рандомные ячейки свободные
    - MEDIUM средний уровень, пока больше 4 свободных ячеек компьютер выбирает лучшую для себя и блокирует победу игрок
    - HARD самый сложный, компьютер всегда берет лучшую для себя ячейку, лучший исход для игрока - ничья.
    <br><br>
- PATCH http://{URL}:{PORT}/{API_ENDPOINT}/{cellId}<br><br>
    {cellId} - Ячейка в которую пользователь делает ход<br><br>

- GET http://{URL}:{PORT}/{API_ENDPOINT}/board<br><br>
  Получение доски в текущем состоянии.<br><br>
- PATH http://{URL}:{PORT}/{API_ENDPOINT}/cancelStep<br><br>
    Отменить последний ход (отменяется зод машины и пользователя)



## Использование

1. Можно собрать проект через mvn clean package после чего запустить docker compose
2. Можно запустить файл shell скрипт runTto.sh (первой строкой идет выбор 17 java по умолчанию export JAVA_HOME=`/usr/libexec/java_home -v 17.0.8`)


## Параметры для запуска приложения
- PORT - порт для приложения (по умолчанию 9090)<br>
- ATTR_NAME - атрибут для сесии пользователя, в котором будет передаваться идентификатор игры (по умолчанию gameId)
- AI_SYMBOL - символ которым играет AI (по умолчанию Х)
- USER_SYMBOL - символ которым играет пользователь (по умолчанию 0)
- API_ENDPOINT - эндпоинт по которому будет апи (по умолчению v1)
- INACTIVE_TIME - время после которого брошенная пользователем партия удалится из БД в миллисекундах (по умолчанию сутки т.е. 86400000)
- DB_URL - URL для подключения к БД
- DB_USER -  Пользователь БД
- DB_PASS - Пароль пользователя БД

## Дополнительно

Open API (сваггер) располагается на эндпоинте /tto-api.html (Например localhost:9090/tto-api.html)


