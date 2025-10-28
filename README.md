# hateoas-waiter-service

> Hypermedia-driven RESTful API with Spring Data REST and auto-generated HATEOAS endpoints

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring Data REST](https://img.shields.io/badge/Spring%20Data%20REST-4.4-blue.svg)](https://spring.io/projects/spring-data-rest)
[![Spring HATEOAS](https://img.shields.io/badge/Spring%20HATEOAS-2.4-purple.svg)](https://spring.io/projects/spring-hateoas)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A comprehensive demonstration of **Spring Data REST** with HATEOAS (Hypermedia As The Engine Of Application State), featuring auto-generated RESTful endpoints, hypermedia link navigation, pagination support, and Money type handling.

## Features

- Auto-generated REST endpoints from JPA repositories (NO manual @RestController)
- HATEOAS hypermedia links (_links) in responses
- Built-in pagination and sorting support
- Custom search endpoints from repository query methods
- HAL (Hypertext Application Language) format
- Money type handling with Joda Money (TWD currency)
- Hibernate 6 + Jakarta EE support
- JSON/XML format support via content negotiation
- @RepositoryRestResource configuration
- Spring Data JPA integration

## Tech Stack

- Spring Boot 3.4.5
- Spring Data REST 4.4
- Spring HATEOAS 2.4
- Spring Data JPA
- Java 21
- Hibernate 6
- Joda Money 2.0.2
- H2 Database 2.3.232
- Jackson (JSON/XML)
- Lombok
- Maven 3.8+

## Getting Started

### Prerequisites

- JDK 21 or higher
- Maven 3.8+ (or use included Maven Wrapper)

### Quick Start

**Step 1: Run the application**

```bash
./mvnw spring-boot:run
```

**Step 2: Explore API endpoints**

```bash
# Discover all available endpoints
curl http://localhost:8080/ | jq
```

**Step 3: Query coffee data**

```bash
# Get all coffees with HATEOAS links
curl http://localhost:8080/coffee | jq
```

## Configuration

### Application Properties

```properties
# JPA/Hibernate configuration
spring.jpa.hibernate.ddl-auto=none
spring.jpa.properties.hibernate.show_sql=true
spring.jpa.properties.hibernate.format_sql=true

# Error response configuration (development only)
server.error.include-message=always
server.error.include-binding-errors=always
```

**Configuration Mapping:**
- `ddl-auto=none`: Use schema.sql for schema creation
- `show_sql=true`: Display SQL statements in console
- `include-message=always`: Include error messages (⚠️ disable in production)

### Optional Base Path Configuration

```java
// Uncomment in WaiterServiceApplication.java to enable
@Bean
public RepositoryRestConfigurer repositoryRestConfigurer() {
    return new RepositoryRestConfigurer() {
        @Override
        public void configureRepositoryRestConfiguration(
                RepositoryRestConfiguration config, CorsRegistry cors) {
            config.setBasePath("/api");  // All endpoints under /api
            config.exposeIdsFor(Coffee.class, CoffeeOrder.class);
        }
    };
}
```

**Effect:**
- Default: `http://localhost:8080/coffee`
- With base path: `http://localhost:8080/api/coffee`

## Usage

### Application Flow

```
1. Spring Boot starts
   ↓
2. H2 database initialized with schema.sql
   - Creates t_coffee, t_order, t_order_coffee tables
   - Inserts 5 coffee records via data.sql
   ↓
3. Spring Data REST auto-configuration
   ↓
4. Scans @RepositoryRestResource repositories
   ↓
5. Auto-generates REST endpoints:
   - GET    /coffee         (findAll with pagination)
   - GET    /coffee/{id}    (findById)
   - POST   /coffee         (save)
   - PUT    /coffee/{id}    (update)
   - PATCH  /coffee/{id}    (partial update)
   - DELETE /coffee/{id}    (delete)
   - GET    /coffee/search  (custom queries)
   ↓
6. Ready to serve requests with HATEOAS links
```

### Code Example

#### Repository Definition

```java
@RepositoryRestResource(path = "coffee")
public interface CoffeeRepository extends JpaRepository<Coffee, Long> {
    
    /**
     * Auto-generates: GET /coffee/search/findByNameInOrderById?list=latte,mocha
     * Returns coffees sorted by ID
     */
    List<Coffee> findByNameInOrderById(List<String> list);
    
    /**
     * Auto-generates: GET /coffee/search/findByName?name=latte
     * Returns single coffee by exact name match
     */
    Coffee findByName(String name);
    
    /**
     * Auto-generates: GET /coffee/search/findByNameIn?names=latte,mocha
     * Returns coffees matching any name in list (unsorted)
     */
    List<Coffee> findByNameIn(List<String> names);
}
```

**Auto-Generated Endpoints:**

```
Repository Method                    → REST Endpoint
─────────────────────────────────────────────────────────────────────
findByNameInOrderById(List)         → /coffee/search/findByNameInOrderById?list=...
findByName(String)                  → /coffee/search/findByName?name=...
findByNameIn(List)                  → /coffee/search/findByNameIn?names=...
```

### Sample Output

**1. Root Discovery:**

```bash
curl http://localhost:8080/ | jq
```

```json
{
  "_links": {
    "coffee": {
      "href": "http://localhost:8080/coffee{?page,size,sort}",
      "templated": true
    },
    "coffeeOrders": {
      "href": "http://localhost:8080/coffeeOrders{?page,size,sort}",
      "templated": true
    },
    "profile": {
      "href": "http://localhost:8080/profile"
    }
  }
}
```

**2. Get All Coffees (with pagination):**

```bash
curl http://localhost:8080/coffee | jq
```

```json
{
  "_embedded": {
    "coffee": [
      {
        "name": "espresso",
        "price": 100.00,
        "createTime": "2025-10-28T10:00:00.000+08:00",
        "updateTime": "2025-10-28T10:00:00.000+08:00",
        "_links": {
          "self": {
            "href": "http://localhost:8080/coffee/1"
          },
          "coffee": {
            "href": "http://localhost:8080/coffee/1"
          }
        }
      },
      {
        "name": "latte",
        "price": 125.00,
        "_links": {
          "self": {
            "href": "http://localhost:8080/coffee/2"
          }
        }
      }
    ]
  },
  "_links": {
    "self": {
      "href": "http://localhost:8080/coffee"
    },
    "profile": {
      "href": "http://localhost:8080/profile/coffee"
    }
  },
  "page": {
    "size": 20,
    "totalElements": 5,
    "totalPages": 1,
    "number": 0
  }
}
```

**Output Analysis:**
- **_embedded.coffee**: Array of coffee resources
- **_links**: Hypermedia navigation links
- **page**: Pagination metadata (size, total, page number)
- **price**: Money type serialized to decimal (100.00)

**3. Get Single Coffee:**

```bash
curl http://localhost:8080/coffee/1 | jq
```

```json
{
  "name": "espresso",
  "price": 100.00,
  "createTime": "2025-10-28T10:00:00.000+08:00",
  "updateTime": "2025-10-28T10:00:00.000+08:00",
  "_links": {
    "self": {
      "href": "http://localhost:8080/coffee/1"
    },
    "coffee": {
      "href": "http://localhost:8080/coffee/1"
    }
  }
}
```

**4. Search Endpoints Discovery:**

```bash
curl http://localhost:8080/coffee/search | jq
```

```json
{
  "_links": {
    "findByName": {
      "href": "http://localhost:8080/coffee/search/findByName{?name}",
      "templated": true
    },
    "findByNameIn": {
      "href": "http://localhost:8080/coffee/search/findByNameIn{?names}",
      "templated": true
    },
    "findByNameInOrderById": {
      "href": "http://localhost:8080/coffee/search/findByNameInOrderById{?list}",
      "templated": true
    },
    "self": {
      "href": "http://localhost:8080/coffee/search"
    }
  }
}
```

**5. Search by Name:**

```bash
curl "http://localhost:8080/coffee/search/findByName?name=latte" | jq
```

```json
{
  "name": "latte",
  "price": 125.00,
  "_links": {
    "self": {
      "href": "http://localhost:8080/coffee/2"
    },
    "coffee": {
      "href": "http://localhost:8080/coffee/2"
    }
  }
}
```

## API Documentation

### Auto-Generated CRUD Endpoints

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| GET | `/coffee` | List all coffees (paginated) | - |
| GET | `/coffee/{id}` | Get coffee by ID | - |
| POST | `/coffee` | Create new coffee | Coffee JSON |
| PUT | `/coffee/{id}` | Full update coffee | Coffee JSON |
| PATCH | `/coffee/{id}` | Partial update coffee | Coffee JSON |
| DELETE | `/coffee/{id}` | Delete coffee | - |
| GET | `/coffee/search` | List custom search endpoints | - |

### Custom Search Endpoints

| Endpoint | Parameters | Description | Example |
|----------|------------|-------------|---------|
| `/coffee/search/findByName` | `name` | Find by exact name | `?name=latte` |
| `/coffee/search/findByNameIn` | `names` | Find by name list (unsorted) | `?names=latte,mocha` |
| `/coffee/search/findByNameInOrderById` | `list` | Find by name list, sorted by ID | `?list=mocha,latte` |

### Pagination & Sorting Parameters

| Parameter | Description | Example |
|-----------|-------------|---------|
| `page` | Page number (0-based) | `?page=0` |
| `size` | Items per page | `?size=10` |
| `sort` | Sort field and direction | `?sort=name,asc` |

**Combined Example:**

```bash
# Page 0, 3 items, sorted by price descending
curl "http://localhost:8080/coffee?page=0&size=3&sort=price,desc" | jq
```

## Key Components

### CoffeeRepository

```java
@RepositoryRestResource(path = "coffee")
public interface CoffeeRepository extends JpaRepository<Coffee, Long> {
    
    /**
     * Query method naming convention:
     * findBy + Field + In + OrderBy + Field
     * 
     * Generates: GET /coffee/search/findByNameInOrderById?list=...
     */
    List<Coffee> findByNameInOrderById(List<String> list);
    
    /**
     * Simple query method
     * Generates: GET /coffee/search/findByName?name=...
     */
    Coffee findByName(String name);
    
    /**
     * List parameter query
     * Generates: GET /coffee/search/findByNameIn?names=...
     */
    List<Coffee> findByNameIn(List<String> names);
}
```

**Spring Data REST Mapping:**
- `@RepositoryRestResource(path = "coffee")` → Base path: `/coffee`
- Repository methods → Auto-generated `/coffee/search/*` endpoints
- JPA CRUD operations → Auto-generated CRUD endpoints

### Money Type Handling (TWD Currency)

**MoneyConverter (JPA Layer):**

```java
@Converter(autoApply = true)
public class MoneyConverter implements AttributeConverter<Money, Long> {
    
    @Override
    public Long convertToDatabaseColumn(Money attribute) {
        // TWD 125.00 → 12500 cents (stored in database)
        return attribute == null ? null : attribute.getAmountMinorLong();
    }

    @Override
    public Money convertToEntityAttribute(Long dbData) {
        // 12500 cents → Money.of(TWD, 125.00)
        return dbData == null ? null : Money.ofMinor(CurrencyUnit.of("TWD"), dbData);
    }
}
```

**MoneySerializer (JSON Layer):**

```java
@JsonComponent
public class MoneySerializer extends StdSerializer<Money> {
    @Override
    public void serialize(Money money, JsonGenerator jsonGenerator, 
                         SerializerProvider serializerProvider) throws IOException {
        // Money.of(TWD, 125.00) → 125.00 (JSON output)
        jsonGenerator.writeNumber(money.getAmount());
    }
}
```

**MoneyDeserializer (JSON Layer):**

```java
@JsonComponent
public class MoneyDeserializer extends StdDeserializer<Money> {
    @Override
    public Money deserialize(JsonParser p, DeserializationContext ctxt) 
            throws IOException {
        // JSON 125.00 → Money.of(TWD, 125.00)
        return Money.of(CurrencyUnit.of("TWD"), p.getDecimalValue());
    }
}
```

**Money Conversion Flow:**

```
Database (cents) ←→ JPA (MoneyConverter) ←→ Java Object (Money) ←→ JSON (Serializer/Deserializer) ←→ API Response
    12500                                  Money.of(TWD,125.00)                                125.00
```

### Application Configuration

```java
@SpringBootApplication
@EnableCaching
public class WaiterServiceApplication {
    
    /**
     * Handle Hibernate lazy-loading proxies in JSON serialization
     */
    @Bean
    public Hibernate6Module hibernate6Module() {
        return new Hibernate6Module();
    }

    /**
     * Configure Jackson ObjectMapper
     * - Pretty print JSON (indented output)
     * - Set timezone to Asia/Taipei
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonBuilderCustomizer() {
        return builder -> {
            builder.indentOutput(true);
            builder.timeZone(TimeZone.getTimeZone("Asia/Taipei"));
        };
    }
}
```

## Database Schema

**schema.sql:**

```sql
-- Coffee table
create table t_coffee (
    id bigint auto_increment,
    create_time timestamp,
    update_time timestamp,
    name varchar(255),
    price bigint,              -- Stored in cents (10000 = TWD 100.00)
    primary key (id)
);

-- Order table
create table t_order (
    id bigint auto_increment,
    create_time timestamp,
    update_time timestamp,
    customer varchar(255),
    state integer not null,    -- OrderState enum ordinal
    primary key (id)
);

-- Order-Coffee many-to-many relationship
create table t_order_coffee (
    coffee_order_id bigint not null,
    items_id bigint not null
);
```

**data.sql (Initial Data):**

```sql
-- Insert 5 coffees (prices in cents)
insert into t_coffee (name, price, create_time, update_time) 
    values ('espresso', 10000, now(), now());     -- TWD 100.00
insert into t_coffee (name, price, create_time, update_time) 
    values ('latte', 12500, now(), now());        -- TWD 125.00
insert into t_coffee (name, price, create_time, update_time) 
    values ('capuccino', 12500, now(), now());    -- TWD 125.00
insert into t_coffee (name, price, create_time, update_time) 
    values ('mocha', 15000, now(), now());        -- TWD 150.00
insert into t_coffee (name, price, create_time, update_time) 
    values ('macchiato', 15000, now(), now());    -- TWD 150.00
```

## HATEOAS Concepts

### What is HATEOAS?

**HATEOAS** = **H**ypermedia **A**s **T**he **E**ngine **O**f **A**pplication **S**tate

**Core Principle:**
- API responses include hypermedia links
- Clients navigate API using these links (NOT hardcoded URLs)
- Self-documenting API

**Benefits:**
- ✅ API discoverability
- ✅ Loose coupling between client and server
- ✅ API evolution without breaking clients
- ✅ Self-documenting resources

### HAL Format

**HAL** = **H**ypertext **A**pplication **L**anguage

**Structure:**
```json
{
  "_links": {              // Hypermedia links
    "self": {...},
    "collection": {...}
  },
  "_embedded": {           // Embedded resources
    "items": [...]
  },
  "page": {...},           // Pagination metadata
  "data": "..."            // Actual data
}
```

### Link Relations

| Relation | Description | Example |
|----------|-------------|---------|
| `self` | Link to current resource | `/coffee/1` |
| `collection` | Link to parent collection | `/coffee` |
| `next` | Next page | `/coffee?page=1` |
| `prev` | Previous page | `/coffee?page=0` |
| `first` | First page | `/coffee?page=0` |
| `last` | Last page | `/coffee?page=2` |

## CRUD Operations

### Create Coffee

```bash
curl -X POST http://localhost:8080/coffee \
  -H "Content-Type: application/json" \
  -d '{
    "name": "americano",
    "price": 110.00
  }' | jq
```

**Response:** 201 CREATED

```json
{
  "name": "americano",
  "price": 110.00,
  "createTime": "2025-10-28T15:30:00.000+08:00",
  "updateTime": "2025-10-28T15:30:00.000+08:00",
  "_links": {
    "self": {
      "href": "http://localhost:8080/coffee/6"
    },
    "coffee": {
      "href": "http://localhost:8080/coffee/6"
    }
  }
}
```

### Update Coffee (Full Update)

```bash
curl -X PUT http://localhost:8080/coffee/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "espresso",
    "price": 105.00
  }' | jq
```

**Response:** 200 OK

### Update Coffee (Partial Update)

```bash
curl -X PATCH http://localhost:8080/coffee/1 \
  -H "Content-Type: application/json" \
  -d '{
    "price": 105.00
  }' | jq
```

**Response:** 200 OK (only price updated, name unchanged)

### Delete Coffee

```bash
curl -X DELETE http://localhost:8080/coffee/1

# Response: 204 NO CONTENT
```

### Get Coffee by ID

```bash
curl http://localhost:8080/coffee/1 | jq
```

**Response:** 200 OK (with HATEOAS links)

## Search Operations

### Discover Search Endpoints

```bash
curl http://localhost:8080/coffee/search | jq
```

**Response:**
```json
{
  "_links": {
    "findByName": {
      "href": "http://localhost:8080/coffee/search/findByName{?name}",
      "templated": true
    },
    "findByNameIn": {
      "href": "http://localhost:8080/coffee/search/findByNameIn{?names}",
      "templated": true
    },
    "findByNameInOrderById": {
      "href": "http://localhost:8080/coffee/search/findByNameInOrderById{?list}",
      "templated": true
    },
    "self": {
      "href": "http://localhost:8080/coffee/search"
    }
  }
}
```

### Execute Search Queries

**1. Find by exact name:**

```bash
curl "http://localhost:8080/coffee/search/findByName?name=latte" | jq
```

**Response:** Single coffee object with _links

**2. Find by name list (unsorted):**

```bash
curl "http://localhost:8080/coffee/search/findByNameIn?names=latte,mocha" | jq
```

**Response:** Array of coffee objects (order may vary)

**3. Find by name list (sorted by ID):**

```bash
curl "http://localhost:8080/coffee/search/findByNameInOrderById?list=mocha,latte" | jq
```

**Response:** Array of coffee objects sorted by ID

```json
[
  {
    "name": "latte",
    "price": 125.00,
    "_links": {
      "self": {"href": "http://localhost:8080/coffee/2"}
    }
  },
  {
    "name": "mocha",
    "price": 150.00,
    "_links": {
      "self": {"href": "http://localhost:8080/coffee/4"}
    }
  }
]
```

## Pagination Examples

### Basic Pagination

```bash
# First page (3 items)
curl "http://localhost:8080/coffee?page=0&size=3" | jq '.page'
```

**Response:**
```json
{
  "size": 3,
  "totalElements": 5,
  "totalPages": 2,
  "number": 0
}
```

### With Sorting

```bash
# Sort by price descending
curl "http://localhost:8080/coffee?sort=price,desc" | jq '._embedded.coffee[] | {name, price}'
```

**Output:**
```json
{"name": "mocha", "price": 150.00}
{"name": "macchiato", "price": 150.00}
{"name": "latte", "price": 125.00}
{"name": "capuccino", "price": 125.00}
{"name": "espresso", "price": 100.00}
```

### Multiple Sort Fields

```bash
# Sort by price desc, then name asc
curl "http://localhost:8080/coffee?sort=price,desc&sort=name,asc" | jq
```

### Navigation Links

```bash
# Get first page
curl "http://localhost:8080/coffee?page=0&size=2" | jq '._links'
```

**Response:**
```json
{
  "first": {
    "href": "http://localhost:8080/coffee?page=0&size=2"
  },
  "self": {
    "href": "http://localhost:8080/coffee?page=0&size=2"
  },
  "next": {
    "href": "http://localhost:8080/coffee?page=1&size=2"
  },
  "last": {
    "href": "http://localhost:8080/coffee?page=2&size=2"
  }
}
```

## Repository Query Method Naming

### Naming Convention

```
findBy + Field + Condition + OrderBy + SortField + SortDirection
```

### Examples

| Method Name | SQL Equivalent | Generated Endpoint |
|-------------|----------------|-------------------|
| `findByName(String)` | `WHERE name = ?` | `/search/findByName?name=...` |
| `findByNameIn(List)` | `WHERE name IN (...)` | `/search/findByNameIn?names=...` |
| `findByNameInOrderById(List)` | `WHERE name IN (...) ORDER BY id` | `/search/findByNameInOrderById?list=...` |
| `findByPriceGreaterThan(Money)` | `WHERE price > ?` | `/search/findByPriceGreaterThan?price=...` |
| `findByNameContaining(String)` | `WHERE name LIKE %?%` | `/search/findByNameContaining?name=...` |

### Supported Keywords

| Keyword | Description | Example |
|---------|-------------|---------|
| `findBy` | Query prefix | `findByName` |
| `And` | Logical AND | `findByNameAndPrice` |
| `Or` | Logical OR | `findByNameOrPrice` |
| `In` | IN clause | `findByNameIn` |
| `NotIn` | NOT IN clause | `findByNameNotIn` |
| `Like` | LIKE clause | `findByNameLike` |
| `Containing` | LIKE %?% | `findByNameContaining` |
| `StartingWith` | LIKE ?% | `findByNameStartingWith` |
| `EndingWith` | LIKE %? | `findByNameEndingWith` |
| `GreaterThan` | > | `findByPriceGreaterThan` |
| `LessThan` | < | `findByPriceLessThan` |
| `Between` | BETWEEN | `findByPriceBetween` |
| `OrderBy` | ORDER BY | `findByNameOrderByPrice` |

## Project Structure

```
hateoas-waiter-service/
├── src/
│   ├── main/
│   │   ├── java/tw/fengqing/spring/springbucks/waiter/
│   │   │   ├── model/
│   │   │   │   ├── BaseEntity.java          # Base entity with ID & timestamps
│   │   │   │   ├── Coffee.java              # Coffee entity
│   │   │   │   ├── CoffeeOrder.java         # Order entity
│   │   │   │   ├── OrderState.java          # Order state enum
│   │   │   │   └── MoneyConverter.java      # JPA Money converter
│   │   │   ├── repository/
│   │   │   │   ├── CoffeeRepository.java    # Coffee repository (REST-exposed)
│   │   │   │   └── CoffeeOrderRepository.java # Order repository
│   │   │   ├── support/
│   │   │   │   ├── MoneySerializer.java     # JSON Money serializer
│   │   │   │   └── MoneyDeserializer.java   # JSON Money deserializer
│   │   │   └── WaiterServiceApplication.java # Main application
│   │   └── resources/
│   │       ├── application.properties        # App configuration
│   │       ├── schema.sql                   # Database schema
│   │       └── data.sql                     # Initial data (5 coffees)
│   └── test/
│       └── java/tw/fengqing/spring/springbucks/waiter/
│           └── WaiterServiceApplicationTests.java
└── pom.xml                                  # Maven configuration
```

## Testing

### Manual Testing with curl

**Test Suite:**

```bash
# 1. Discover endpoints
curl http://localhost:8080/ | jq

# 2. List all coffees
curl http://localhost:8080/coffee | jq '._embedded.coffee[] | {name, price}'

# 3. Get single coffee
curl http://localhost:8080/coffee/1 | jq

# 4. Create coffee
curl -X POST http://localhost:8080/coffee \
  -H "Content-Type: application/json" \
  -d '{"name": "americano", "price": 110.00}' | jq

# 5. Update coffee
curl -X PUT http://localhost:8080/coffee/1 \
  -H "Content-Type: application/json" \
  -d '{"name": "espresso", "price": 105.00}' | jq

# 6. Partial update
curl -X PATCH http://localhost:8080/coffee/1 \
  -H "Content-Type: application/json" \
  -d '{"price": 108.00}' | jq

# 7. Search by name
curl "http://localhost:8080/coffee/search/findByName?name=latte" | jq

# 8. Search by multiple names
curl "http://localhost:8080/coffee/search/findByNameIn?names=latte,mocha" | jq

# 9. Delete coffee
curl -X DELETE http://localhost:8080/coffee/1

# 10. Pagination
curl "http://localhost:8080/coffee?page=0&size=2&sort=price,desc" | jq
```

### Unit Tests

```bash
# Run tests
./mvnw test

# Run with coverage
./mvnw clean test jacoco:report
```

## Monitoring

### Enable Actuator

```xml
<!-- Add to pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

```properties
# Add to application.properties
management.endpoints.web.exposure.include=health,info,metrics
```

**Verify:**

```bash
curl http://localhost:8080/actuator/health | jq
```

### Debug Logging

```properties
# Enable Spring Data REST debug logging
logging.level.org.springframework.data.rest=DEBUG
logging.level.org.springframework.data.jpa=DEBUG
```

### Request Logging

```bash
# Monitor all requests
tail -f logs/spring.log | grep "Mapped"
```

## Common Issues

### Issue 1: No Search Endpoints

**Problem:** `/coffee/search` returns empty

**Cause:** No query methods defined in repository

**Solution:**

```java
@RepositoryRestResource(path = "coffee")
public interface CoffeeRepository extends JpaRepository<Coffee, Long> {
    // Add custom query methods
    Coffee findByName(String name);  // ← This generates search endpoint
}
```

### Issue 2: Wrong Price Format

**Problem:** Price shows as object instead of number

**Cause:** Missing MoneySerializer

**Solution:**

```java
@JsonComponent
public class MoneySerializer extends StdSerializer<Money> {
    @Override
    public void serialize(Money money, JsonGenerator gen, SerializerProvider provider) 
            throws IOException {
        gen.writeNumber(money.getAmount());  // ← Serialize as number
    }
}
```

### Issue 3: Missing _links

**Problem:** Responses don't include HATEOAS links

**Cause:** Wrong dependency or missing @RepositoryRestResource

**Solution:**

```xml
<!-- Ensure this dependency exists -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-rest</artifactId>
</dependency>
```

```java
// Add annotation to repository
@RepositoryRestResource(path = "coffee")
public interface CoffeeRepository extends JpaRepository<Coffee, Long> {
}
```

### Issue 4: H2 Console Not Accessible

**Problem:** Cannot access H2 console

**Solution:**

```properties
# Add to application.properties
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

**Access:**
```
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:testdb
```

## Spring Data REST vs Traditional REST

### Comparison

| Aspect | Traditional REST | Spring Data REST |
|--------|------------------|------------------|
| **Controller** | Manual @RestController | ❌ NOT needed |
| **Endpoints** | Manual @RequestMapping | ✅ Auto-generated |
| **CRUD Operations** | Manual implementation | ✅ Auto-generated |
| **Pagination** | Manual implementation | ✅ Built-in |
| **Sorting** | Manual implementation | ✅ Built-in |
| **Hypermedia Links** | Manual LinkBuilder | ✅ Auto-generated |
| **Search** | Manual @GetMapping | ✅ Auto from query methods |
| **HATEOAS** | Manual implementation | ✅ Built-in HAL format |
| **Code Amount** | High | ✅ Minimal |

### Traditional REST Example

```java
// ❌ Traditional way (lots of code)
@RestController
@RequestMapping("/coffee")
public class CoffeeController {
    
    @GetMapping
    public List<Coffee> getAll() { ... }
    
    @GetMapping("/{id}")
    public Coffee getById(@PathVariable Long id) { ... }
    
    @PostMapping
    public Coffee create(@RequestBody Coffee coffee) { ... }
    
    @PutMapping("/{id}")
    public Coffee update(@PathVariable Long id, @RequestBody Coffee coffee) { ... }
    
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { ... }
    
    @GetMapping("/search")
    public List<Coffee> findByName(@RequestParam String name) { ... }
    
    // ... many lines of code
}
```

### Spring Data REST Example

```java
// ✅ Spring Data REST way (minimal code)
@RepositoryRestResource(path = "coffee")
public interface CoffeeRepository extends JpaRepository<Coffee, Long> {
    Coffee findByName(String name);  // Auto-generates search endpoint
}

// That's it! All CRUD + Search endpoints auto-generated
```

**Lines of Code:**
- Traditional REST: ~200 lines
- Spring Data REST: ~5 lines (97.5% reduction!)

## Advanced Configuration

### Custom Resource Path

```java
@RepositoryRestResource(
    path = "coffees",                      // Resource path
    collectionResourceRel = "coffees",     // Collection relation name
    itemResourceRel = "coffee"             // Item relation name
)
public interface CoffeeRepository extends JpaRepository<Coffee, Long> {
}
```

**URLs:**
- Collection: `http://localhost:8080/coffees`
- Item: `http://localhost:8080/coffees/1`

### Custom Search Method Path

```java
@RestResource(path = "by-name", rel = "by-name")
Coffee findByName(@Param("name") String name);
```

**Generated URL:**
```
http://localhost:8080/coffee/search/by-name?name=latte
```

### Hide Endpoint from API

```java
@RestResource(exported = false)  // Don't expose via REST
void deleteByName(String name);
```

### Expose Entity IDs

```java
@Bean
public RepositoryRestConfigurer repositoryRestConfigurer() {
    return new RepositoryRestConfigurer() {
        @Override
        public void configureRepositoryRestConfiguration(
                RepositoryRestConfiguration config, CorsRegistry cors) {
            // Expose IDs in JSON response (hidden by default)
            config.exposeIdsFor(Coffee.class, CoffeeOrder.class);
        }
    };
}
```

**Effect:**
```json
// Without exposeIdsFor (default)
{
  "name": "espresso",
  "price": 100.00,
  "_links": {...}
}

// With exposeIdsFor
{
  "id": 1,              // ← ID now included
  "name": "espresso",
  "price": 100.00,
  "_links": {...}
}
```

## Dependencies

```xml
<properties>
    <java.version>21</java.version>
    <joda-money.version>2.0.2</joda-money.version>
</properties>

<dependencies>
    <!-- Spring Data REST (includes Spring HATEOAS) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-rest</artifactId>
    </dependency>
    
    <!-- Spring Data JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    
    <!-- Joda Money -->
    <dependency>
        <groupId>org.joda</groupId>
        <artifactId>joda-money</artifactId>
        <version>${joda-money.version}</version>
    </dependency>
    
    <!-- Jackson Hibernate6 (handle lazy-loading proxies) -->
    <dependency>
        <groupId>com.fasterxml.jackson.datatype</groupId>
        <artifactId>jackson-datatype-hibernate6</artifactId>
    </dependency>
    
    <!-- Jackson XML (optional, for XML support) -->
    <dependency>
        <groupId>com.fasterxml.jackson.dataformat</groupId>
        <artifactId>jackson-dataformat-xml</artifactId>
    </dependency>
    
    <!-- H2 Database -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
    
    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

## Best Practices Demonstrated

1. **Repository-based REST**: Auto-generate endpoints from JPA repositories
2. **HATEOAS Compliance**: Hypermedia-driven API design with HAL format
3. **Money Type Handling**: Proper TWD currency handling with Joda Money
4. **Entity Inheritance**: BaseEntity pattern for common fields (id, timestamps)
5. **Jakarta EE**: Modern Spring Boot 3.x with Jakarta persistence
6. **Custom Query Methods**: Spring Data JPA method naming conventions
7. **JSON Customization**: Custom serializers/deserializers for Money
8. **Hibernate6Module**: Handle lazy-loading proxies in JSON
9. **Pagination**: Built-in pagination and sorting support
10. **API Discoverability**: Self-documenting with hypermedia links

## Content Negotiation

### JSON Format (Default)

```bash
curl http://localhost:8080/coffee/1 \
  -H "Accept: application/json" | jq
```

### XML Format

```bash
curl http://localhost:8080/coffee/1 \
  -H "Accept: application/xml"
```

**Response:**
```xml
<resource>
    <links>
        <link rel="self" href="http://localhost:8080/coffee/1"/>
    </links>
    <content>
        <name>espresso</name>
        <price>100.00</price>
    </content>
</resource>
```

### HAL+JSON Format

```bash
curl http://localhost:8080/coffee/1 \
  -H "Accept: application/hal+json" | jq
```

## When to Use Spring Data REST

### ✅ Good Use Cases

- **Rapid Prototyping**: Quick API development
- **CRUD-heavy Applications**: Standard database operations
- **Internal APIs**: Backend-for-frontend services
- **Microservices**: Simple data services
- **Admin Tools**: Internal management systems

### ❌ When NOT to Use

- **Complex Business Logic**: Need custom validation/processing
- **Custom Response Format**: Non-standard JSON structure
- **Security Requirements**: Fine-grained access control
- **Performance Critical**: Need optimized queries
- **Public APIs**: Require stable, versioned contracts

**Alternative:** Use traditional @RestController for complex scenarios

## Security Considerations

### Production Configuration

```properties
# ⚠️ Disable in production
server.error.include-message=never
server.error.include-binding-errors=never
server.error.include-stacktrace=never

# Enable only specific endpoints
spring.data.rest.detection-strategy=annotated
```

### Add Spring Security

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.GET, "/coffee/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/coffee/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/coffee/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/coffee/**").hasRole("ADMIN")
        );
        return http.build();
    }
}
```

## Performance Optimization

### Enable Caching

```java
@SpringBootApplication
@EnableCaching  // Already enabled in this project
public class WaiterServiceApplication {
}
```

```java
// Add @Cacheable to repository methods (NOT supported by default)
// Use service layer for caching
@Service
public class CoffeeService {
    
    @Cacheable("coffees")
    public List<Coffee> findAll() {
        return coffeeRepository.findAll();
    }
}
```

### Projection for Performance

```java
@Projection(name = "summary", types = Coffee.class)
public interface CoffeeSummary {
    String getName();
    Money getPrice();
    // No timestamps - reduces response size
}
```

**Usage:**
```bash
curl "http://localhost:8080/coffee/1?projection=summary" | jq
```

## References

- [Spring Data REST Documentation](https://docs.spring.io/spring-data/rest/docs/current/reference/html/)
- [Spring HATEOAS Documentation](https://docs.spring.io/spring-hateoas/docs/current/reference/html/)
- [HATEOAS Specification](https://restfulapi.net/hateoas/)
- [HAL Format Specification](https://tools.ietf.org/html/draft-kelly-json-hal)
- [Richardson Maturity Model](https://martinfowler.com/articles/richardsonMaturityModel.html)
- [Spring Data JPA Query Methods](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods)
- [Joda Money Documentation](https://www.joda.org/joda-money/)

## License

MIT License - see [LICENSE](LICENSE) file for details.

## About Us

我們主要專注在敏捷專案管理、物聯網（IoT）應用開發和領域驅動設計（DDD）。喜歡把先進技術和實務經驗結合，打造好用又靈活的軟體解決方案。近來也積極結合 AI 技術，推動自動化工作流，讓開發與運維更有效率、更智慧。持續學習與分享，希望能一起推動軟體開發的創新和進步。

## Contact

**風清雲談** - 專注於敏捷專案管理、物聯網（IoT）應用開發和領域驅動設計（DDD）。

- 🌐 官方網站：[風清雲談部落格](https://blog.fengqing.tw/)
- 📘 Facebook：[風清雲談粉絲頁](https://www.facebook.com/profile.php?id=61576838896062)
- 💼 LinkedIn：[Chu Kuo-Lung](https://www.linkedin.com/in/chu-kuo-lung)
- 📺 YouTube：[雲談風清頻道](https://www.youtube.com/channel/UCXDqLTdCMiCJ1j8xGRfwEig)
- 📧 Email：[fengqing.tw@gmail.com](mailto:fengqing.tw@gmail.com)

---

**⭐ If this project helps you, please give it a star!**
