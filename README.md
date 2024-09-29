
# Money Transfer Service
Этот проект реализует интерфейс для сервиса перевода денег между банковскими картами с комиссией. Приложение написано на Java с использованием Spring Boot и может быть запущено в Docker-контейнере.

## Запуск проекта

### Локальный запуск
Для запуска приложения выполните команду:

```bash
java -jar target/Money-1.0-SNAPSHOT.jar
```

### Запуск через Docker
Сначала соберите Docker-образ:

```bash
docker build -t money-transfer-app .
```

Запустите контейнер с приложением:

```bash
docker run -d -p 5500:5500 money-transfer-app
```

Где:
- `-p 5500:5500` — проброс порта, чтобы приложение было доступно на порту 5500.

Приложение будет доступно по адресу: `http://localhost:5500`.

### Запуск через Docker-compose
Сначала соберите Docker-образ:

```bash
docker-compose build
```

Запустите контейнер с приложением:

```bash
docker-compose up
```

## Описание API

Приложение предоставляет API для обработки запросов на перевод и подтверждение операций.

### 1. Перевод средств
- **Endpoint**: `/transfer`
- **Method**: `POST`
- **Описание**: Выполняет запрос на перевод средств с одной карты на другую.

Пример запроса:

```bash
curl -X POST http://localhost:5500/transfer \
  -H "Content-Type: application/json" \
  -d '{
    "cardFromNumber": "1111222233334444",
    "cardFromCVV": "123", 
    "cardFromValidTill": "12/25",
    "cardToNumber": "5555666677778888",
    "amount": {
      "value": 2222200,
      "currency": "RUR"
    }
  }'
```

Тело запроса:

```json
{
  "cardFromNumber": "1111222233334444",
  "cardFromCVV": "123",
  "cardFromValidTill": "12/25",
  "cardToNumber": "5555666677778888",
  "amount": {
    "value": 2222200,
    "currency": "RUR"
  }
}

```

Пример успешного ответа:

```json
{
  "operationId": "123e4567-e89b-12d3-a456-426614174000"
}
```

Пример неуспешного ответа:

```json
{
  "error": "Invalid transfer request."
}
```

### 2. Подтверждение операции
- **Endpoint**: `/confirm`
- **Method**: `POST`
- **Описание**: Подтверждает операцию перевода средств по предоставленному operationId.

Пример запроса:

```bash
curl -X POST http://localhost:5500/confirm \
  -H "Content-Type: application/json" \
  -d '{
    "operationId": "123e4567-e89b-12d3-a456-426614174000"
  }'
```

Тело запроса:

```json
{
  "operationId": "123e4567-e89b-12d3-a456-426614174000"
}
```

Пример успешного ответа:

```json
{
  "operationId": "123e4567-e89b-12d3-a456-426614174000"
}
```

## Логи

Логи транзакций записываются в файл `transfer_log.txt`, который находится в директории `/app/logs` (в контейнере).

Пример записи в лог:

```yaml
2024-09-26 12:34:56 | From: 1111222233334444 | To: 5555666677778888 | Amount: 22222.00 RUR | Commission: 222.22 | Operation ID: 123e4567-e89b-12d3-a456-426614174000 | Status: SUCCESS
```

## Порт
По умолчанию приложение работает на порту 5500. Если хотите изменить порт, вы можете передать его через переменные среды:

```bash
docker run -d -p 8080:5500 -e SERVER_PORT=8080 money-transfer-app
```