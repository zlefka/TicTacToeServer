# TicTacToeServer

Backend‑сервис для игры в крестики‑нолики с **двумя режимами**: PvP (два игрока) и PvE против **бота на minimax**. Реализация следует принципам **Clean Architecture**: доменная логика отделена от web‑слоя и хранения данных; пользователи и игры сохраняются в **PostgreSQL** (включая состояние доски и `GameState`), есть **JWT‑аутентификация (access/refresh)** и эндпоинты для статистики.

---

## Возможности

- **Регистрация и логин**
  - `/auth/signup` — создание пользователя
  - `/auth/login` — получение пары токенов (**access + refresh**)
- **JWT‑сессии**
  - отдельные эндпоинты для обновления **access** и ротации **refresh**
  - защищённые маршруты через Ktor Authentication (`jwt-auth`)
- **Игры**
  - создание новой игры: **против бота** или **ожидание второго игрока**
  - присоединение к доступной PvP‑игре
  - выполнение хода с валидацией координат и проверкой “чья очередь”
  - получение игры по id и список доступных игр
- **История и статистика**
  - история завершённых игр текущего пользователя
  - рейтинг игроков по win‑ratio (параметр `top`)
- **Бот на minimax**
  - выбирает оптимальные ходы, учитывая глубину (быстрее выигрывает / дольше проигрывает)

---

## Ключевые инженерные решения

- **Многослойная архитектура**
  - `domain/` — чистая бизнес‑логика (`CurrentGame`, `GameBoard`, `GameState`, сервисы)
  - `datasource/` — реализация репозиториев и схемы БД на Exposed
  - `web/` — Ktor‑слой: маршруты, DTO, сериализация, безопасность
- **Технологический стек**
  - Kotlin/JVM + Ktor 3 (Netty), JSON (kotlinx.serialization), StatusPages
  - PostgreSQL + Exposed, автосоздание таблиц при старте
  - Koin DI, JWT access/refresh
- **Расширяемость**
  - режимы игры отделены (`TwoPlayersService` vs `MinimaxComputerService`), репозитории спрятаны за интерфейсами

---

## Структура проекта

```text
src/main/kotlin/
  di/                 # DI-контейнер (Koin): wiring зависимостей
  domain/              # бизнес-логика (Entities/UseCases/Ports)
    model/             # CurrentGame, GameBoard, GameState и т.д.
    repository/        # порты (интерфейсы репозиториев)
    service/           # use-cases: PvP и minimax-бот
  datasource/          # инфраструктурный слой (адаптеры)
    database/          # таблицы Exposed
    model/             # entities для хранения/сериализации
    repository/        # реализации репозиториев (Exposed/PostgreSQL)
  web/                 # delivery слой (Ktor)
    module/            # конфигурация Ktor (JWT, JSON, DB, Koin, features)
    route/             # маршруты API
    model/             # DTO запросов/ответов
    mapper/            # маппинг domain <-> web DTO
    security/          # AuthService, JwtProvider и модели JWT
    serialization/     # сериализаторы (например, UUID)

src/main/resources/
  application.conf     # конфигурация Ktor/DB/JWT
  logback.xml          # логирование
```

---

## Быстрый старт (Windows / PowerShell)

### 1) Настроить конфигурацию

Параметры лежат в `src/main/resources/application.conf`:

- `storage.*` — подключение к PostgreSQL (по умолчанию `jdbc:postgresql://localhost:5432/tictactoe_db`, `postgres/1234`)
- `jwt.*` — issuer/audience/realm/TTL, а `jwt.secret` берётся из переменной окружения `SECRET`

Перед запуском задай `SECRET`:

```powershell
$env:SECRET = "your-super-secret-32+chars"
```

### 2) Запуск сервера

```powershell
.\gradlew.bat run
```

Сервер стартует на `http://localhost:8080`, health‑endpoint:

- `GET /` → `Server is running`

---

## API

Все ответы/запросы — JSON. Защищённые эндпоинты требуют:

`Authorization: Bearer <accessToken>`

### Маршруты

- **Auth**
  - `POST /auth/signup` — регистрация
  - `POST /auth/login` — access/refresh токены
  - `POST /auth/refresh/access` — обновить access по refresh
  - `POST /auth/refresh` — получить новую пару токенов по refresh
- **User**
  - `GET /user/me` — текущий пользователь
  - `GET /game/user/{id}` — пользователь по UUID
  - `GET /user/me/history` — завершённые игры
- **Game**
  - `POST /game/new` — создать игру (PvP или vs bot)
  - `GET /game/available` — доступные PvP‑игры
  - `POST /game/{id}/join` — присоединиться к PvP‑игре
  - `GET /game/{id}` — состояние игры (только участникам)
  - `POST /game/{id}/move` — сделать ход (`row/col` 0..2, только участникам, только в свой ход)
  - `GET /game/statistic?top=10` — рейтинг игроков по `ratio` (wins/total)

### Примеры

Логин:

```json
{ "login": "alice", "password": "password" }
```

Создание игры против бота:

```json
{ "isBot": true, "playerSymbol": "X" }
```

---

## Модель данных (в общих чертах)

- Таблица `users`: `id`, `login (unique)`, `password_hash`
- Таблица `game`:
  - `board` — сериализованное поле 3×3
  - `state_json` — сериализованный `GameState` (ожидание, очередь, победитель, ничья)
  - `user1/user2`, флаги режимов (`is_bot`, `is_two_players`), символы игроков, `created_at`

При старте приложение создаёт таблицы и **инициализирует пользователя‑бота** с логином `computer` (если его ещё нет).
