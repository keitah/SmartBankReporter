# 💳 SmartBankReporter-SBR

📊 **SmartBankReporter-SBR** — это консольное Java-приложение для управления банковскими транзакциями.  
Оно поддерживает хранение в PostgreSQL, экспорт в CSV/JSON и удобное консольное меню.
---

## 🚀 Возможности
- ➕ Создание транзакций (DEPOSIT/DEBIT)
- ➖ Удаление транзакций
- 📜 Просмотр всех транзакций
- 💰 Подсчёт текущего баланса
- 📤 Экспорт в **CSV**
- 📤 Экспорт в **JSON**
- 💾 Сохранение в **PostgreSQL** (через Docker)

---
## UPDATE 2.0: Что нового?
1. **Добавлен класс `Account`** и таблица `accounts`.  
2. **Единый DAO (`me.keita.reporter.dao.DAO`)** вместо `TransactionDAO`/`DbDAO`.  
3. **Репозитории**:  
   - `TransactionRepository` (`findById`, `findAllByAccount`, `save`, `deleteById`)  
   - `AccountRepository` (`findByAccountNumber`, `findAll`, `save`, `deleteByAccountNumber`)  
4. **Интерфейс `PreparedStatementComposer`** для сборки `PreparedStatement` при `INSERT/UPDATE`.  
5. **Метод `findById`** для транзакций реализован в `TransactionRepository` и доступен через `BankService`.  
6. **Метод `findByAccountNumber`** для аккаунтов реализован в `AccountRepository` и доступен через `BankService`.  
7. **Фильтрация транзакций по аккаунту** — список и баланс теперь показываются только для выбранного аккаунта.  
8. **Совместимость**: добавлен метод-обёртка `setCurrentAccount` в `BankService` для старого кода.  
9. **Меню переработано**: теперь есть предменю аккаунтов и возможность удаления аккаунта прямо из меню банка.  

## 🛠️ Установка и запуск

### Требования
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) 🐳
- (опционально) [IntelliJ IDEA Ultimate](https://www.jetbrains.com/idea/) 💻



---

## Запуск PostgreSQL в Docker

```bash
docker compose up -d
```
Сборка и запуск приложения

Нужны JDK 21+ и Gradle Wrapper.
```
./gradlew clean run
```
Программа запустится в консоли. Сначала откроется меню аккаунтов.

Поднимется:
- Postgres `smartbank` (user/password: `reporter/reporter`).  
- (Опционально) pgAdmin на [http://localhost:5050](http://localhost:5050) (логин `admin@example.com` / пароль `admin`).  

При первом старте автоматически применится `schema.sql` (создаст таблицы `accounts` и `transactions`).  
Если pgAdmin не нужен — сервис можно убрать из `docker-compose.yml`.

---

## Настройка переменных окружения (опционально)

По умолчанию приложение подключается к:
- `jdbc:postgresql://localhost:5432/smartbank`  
- user: `reporter`  
- password: `reporter`

Можно переопределить:
```bash
export DB_URL=jdbc:postgresql://localhost:5432/smartbank
export DB_USER=reporter
export DB_PASSWORD=reporter
```

---

## Сборка и запуск приложения

Нужны **JDK 21+** и **Gradle Wrapper**.

```bash
./gradlew clean run
```
Программа запустится в консоли. Сначала откроется меню аккаунтов.

(для добавления в IntelliJ Idea Ultimate используйте:)

```host: localhost```
```port: 5432```
```user: reporter```
```pass: reporter```
```database: smartbank```


---
## 🎮 Использование
### Главное меню (аккаунты)

```
=== SmartBank Reporter (Accounts) ===
1. Добавить аккаунт
2. Выбрать/создать по номеру
3. Список аккаунтов и выбор
4. Выход
```

---

### Меню банка (после выбора аккаунта)

```
==== SmartBank (ACC-XXXX) ====
1. Показать все транзакции
2. Добавить депозит
3. Добавить расход
4. Баланс
5. Экспорт CSV
6. Экспорт JSON
7. Найти транзакцию по ID
8. Удалить транзакцию по ID
9. Удалить аккаунт
0. Назад
```

---

## Структура пакетов

```
me.keita.reporter.dao
  ├─ DAO.java
  ├─ PreparedStatementComposer.java
  └─ ResultSetHandler.java
me.keita.reporter.repository
  ├─ AccountRepository.java
  └─ TransactionRepository.java
me.keita.reporter.model
  ├─ Account.java
  ├─ Transaction.java
  └─ TransactionType.java
me.keita.reporter.service
  └─ BankService.java
me.keita.reporter.console
  ├─ Console.java        # предменю аккаунтов
  └─ Menu.java           # меню операций внутри аккаунта
```
