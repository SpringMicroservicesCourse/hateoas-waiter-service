# hateoas-waiter-service

> Hypermedia-driven RESTful API with Spring Data REST, auto-generated endpoints, and HATEOAS support

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring Data REST](https://img.shields.io/badge/Spring%20Data%20REST-3.4.5-blue.svg)](https://spring.io/projects/spring-data-rest)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A comprehensive demonstration of **Spring Data REST** with HATEOAS (Hypermedia As The Engine Of Application State), featuring auto-generated RESTful endpoints, hypermedia links, pagination, and Money type handling.

## Features

- Auto-generated REST endpoints from JPA repositories
- HATEOAS hypermedia links in responses
- Built-in pagination and sorting support
- Custom search endpoints from query methods
- Money type handling with Joda Money (TWD currency)
- H2 in-memory database
- JSON/XML format support
- Hibernate 6 + Jakarta EE support

## Tech Stack

- Spring Boot 3.4.5
- Spring Data REST
- Spring Data JPA
- Java 21
- Hibernate 6
- Joda Money 2.0.2
- H2 Database
- Lombok
- Jackson (JSON/XML)
- Maven 3.8+

## Getting Started

### Prerequisites

- JDK 21 or higher
- Maven 3.8+ (or use included Maven Wrapper)

### Quick Start

```bash
# Build the project
./mvnw clean compile

# Run the application
./mvnw spring-boot:run

# Verify the service
curl http://localhost:8080/ | jq
```

## API Documentation

### Root Endpoint

```bash
curl http://localhost:8080/
```

**Response:**
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

### Coffee Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/coffee` | Get all coffees (paginated) |
| GET | `/coffee/{id}` | Get coffee by ID |
| POST | `/coffee` | Create new coffee |
| PUT | `/coffee/{id}` | Update coffee |
| DELETE | `/coffee/{id}` | Delete coffee |
| GET | `/coffee/search` | List all search endpoints |

### Search Endpoints

| Endpoint | Parameters | Description |
|----------|------------|-------------|
| `/coffee/search/findByName` | `name` | Find coffee by exact name |
| `/coffee/search/findByNameIn` | `names` | Find coffees by name list |
| `/coffee/search/findByNameInOrderById` | `list` | Find coffees by name list, sorted by ID |

**Example:**

```bash
# Get all coffees
curl http://localhost:8080/coffee | jq

# Get coffee by ID
curl http://localhost:8080/coffee/1 | jq

# Search by name
curl "http://localhost:8080/coffee/search/findByName?name=latte" | jq

# Search by multiple names
curl "http://localhost:8080/coffee/search/findByNameIn?names=latte,mocha" | jq

# Search with sorting
curl "http://localhost:8080/coffee/search/findByNameInOrderById?list=mocha,latte" | jq
```

### Pagination & Sorting

```bash
# Paginated (3 items per page, page 0)
curl "http://localhost:8080/coffee?page=0&size=3" | jq

# Sorted by ID descending
curl "http://localhost:8080/coffee?sort=id,desc" | jq

# Combined: pagination + sorting
curl "http://localhost:8080/coffee?page=0&size=3&sort=id,desc" | jq
```

**Pagination Response:**
```json
{
  "_embedded": {
    "coffee": [
      {
        "_links": {...},
        "id": 5,
        "name": "macchiato",
        "price": 150.00
      },
      ...
    ]
  },
  "_links": {
    "first": {"href": "..."},
    "self": {"href": "..."},
    "next": {"href": "..."},
    "last": {"href": "..."}
  },
  "page": {
    "size": 3,
    "totalElements": 5,
    "totalPages": 2,
    "number": 0
  }
}
```

## Configuration

### Application Properties

```properties
# JPA/Hibernate configuration
spring.jpa.hibernate.ddl-auto=none
spring.jpa.properties.hibernate.show_sql=true
spring.jpa.properties.hibernate.format_sql=true

# Error handling (development only)
server.error.include-message=always
server.error.include-binding-errors=always
```

## Key Components

### Repository Configuration

```java
@RepositoryRestResource(path = "coffee")
public interface CoffeeRepository extends JpaRepository<Coffee, Long> {
    /**
     * Find coffees by name list, sorted by ID
     * Auto-generates: GET /coffee/search/findByNameInOrderById?list=latte,mocha
     */
    List<Coffee> findByNameInOrderById(List<String> list);
    
    /**
     * Find coffee by exact name
     * Auto-generates: GET /coffee/search/findByName?name=latte
     */
    Coffee findByName(String name);
    
    /**
     * Find coffees by name list (unsorted)
     * Auto-generates: GET /coffee/search/findByNameIn?names=latte,mocha
     */
    List<Coffee> findByNameIn(List<String> names);
}
```

**Auto-Generated REST Endpoints:**
- GET `/coffee` - List all coffees
- GET `/coffee/{id}` - Get coffee by ID
- POST `/coffee` - Create coffee
- PUT `/coffee/{id}` - Update coffee
- DELETE `/coffee/{id}` - Delete coffee
- GET `/coffee/search` - List custom search endpoints

### Entity Design

```java
@Entity
@Table(name = "T_COFFEE")
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Coffee extends BaseEntity {
    private String name;
    
    @Convert(converter = MoneyConverter.class)
    private Money price;
}
```

### Money Type Handling (TWD Currency)

**MoneyConverter (JPA Layer)**:
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

**MoneySerializer (JSON Layer)**:
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

**MoneyDeserializer (JSON Layer)**:
```java
@JsonComponent
public class MoneyDeserializer extends StdDeserializer<Money> {
    @Override
    public Money deserialize(JsonParser p, DeserializationContext ctxt) {
        // JSON 125.00 → Money.of(TWD, 125.00)
        return Money.of(CurrencyUnit.of("TWD"), p.getDecimalValue());
    }
}
```

**Price Conversion Flow:**
```
Database (cents) ←→ JPA (MoneyConverter) ←→ Java Object (Money) ←→ JSON (Serializer/Deserializer) ←→ API Response
    12500                                  Money.of(TWD,125.00)                                125.00
```

### Application Configuration

```java
@SpringBootApplication
@EnableCaching
public class WaiterServiceApplication {
    
    @Bean
    public Hibernate6Module hibernate6Module() {
        return new Hibernate6Module();
    }

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
    price bigint,              -- Stored in cents (10000 = 100.00 TWD)
    primary key (id)
);

-- Order table
create table t_order (
    id bigint auto_increment,
    create_time timestamp,
    update_time timestamp,
    customer varchar(255),
    state integer not null,
    primary key (id)
);

-- Order-Coffee relationship
create table t_order_coffee (
    coffee_order_id bigint not null,
    items_id bigint not null
);
```

**data.sql (Initial Data):**
```sql
-- Prices stored in minor units (cents)
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

## HATEOAS Response Format

### Single Resource

```json
{
  "_links": {
    "self": {
      "href": "http://localhost:8080/coffee/1"
    },
    "coffee": {
      "href": "http://localhost:8080/coffee/1"
    }
  },
  "id": 1,
  "name": "espresso",
  "price": 100.00,
  "createTime": "2025-10-27T10:00:00.000+08:00",
  "updateTime": "2025-10-27T10:00:00.000+08:00"
}
```

### Collection Resource

```json
{
  "_embedded": {
    "coffee": [
      {
        "_links": {...},
        "id": 1,
        "name": "espresso",
        "price": 100.00
      },
      ...
    ]
  },
  "_links": {
    "self": {"href": "http://localhost:8080/coffee"},
    "profile": {"href": "http://localhost:8080/profile/coffee"}
  },
  "page": {
    "size": 20,
    "totalElements": 5,
    "totalPages": 1,
    "number": 0
  }
}
```

## Testing

### Manual Testing

```bash
# 1. List all coffees
curl http://localhost:8080/coffee | jq '._embedded.coffee[] | {name, price}'

# Expected output:
# {
#   "name": "espresso",
#   "price": 100
# }
# {
#   "name": "latte",
#   "price": 125
# }
# ...

# 2. Create new coffee
curl -X POST http://localhost:8080/coffee \
  -H "Content-Type: application/json" \
  -d '{"name": "americano", "price": 110.00}' | jq

# 3. Update coffee
curl -X PUT http://localhost:8080/coffee/1 \
  -H "Content-Type: application/json" \
  -d '{"name": "espresso", "price": 105.00}' | jq

# 4. Delete coffee
curl -X DELETE http://localhost:8080/coffee/1

# 5. Search by name
curl "http://localhost:8080/coffee/search/findByName?name=latte" | jq
```

### Unit Tests

```bash
# Run tests
./mvnw test

# Run with coverage
./mvnw clean test jacoco:report
```

## Troubleshooting

### Common Issues

| Issue | Check Command | Solution |
|-------|--------------|----------|
| **No search endpoints** | `curl http://localhost:8080/coffee/search` | Verify repository query methods exist |
| **Wrong price format** | Check logs for Money conversion | Verify MoneyConverter uses TWD minor units (cents) |
| **H2 database error** | Check startup logs | Ensure schema.sql and data.sql loaded correctly |
| **Missing _links** | Verify Spring Data REST enabled | Check `spring-boot-starter-data-rest` dependency |

**Quick Checks**:
```bash
# Verify all endpoints
curl http://localhost:8080/ | jq '._links'

# Check coffee data with prices
curl http://localhost:8080/coffee | jq '._embedded.coffee[] | {name, price}'

# Verify search endpoints
curl http://localhost:8080/coffee/search | jq '._links'
```

## Spring Data REST vs Traditional REST

| Aspect | Traditional REST | Spring Data REST |
|--------|------------------|------------------|
| **Controller** | Manual @RestController | Auto-generated |
| **Endpoints** | Manual @RequestMapping | Auto-generated from repository |
| **Pagination** | Manual implementation | Built-in |
| **Hypermedia Links** | Manual LinkBuilder | Auto-generated |
| **Search** | Manual @GetMapping | Auto-generated from query methods |
| **HATEOAS** | Manual implementation | Built-in HAL format |

## Best Practices Demonstrated

1. **Repository-based REST**: Auto-generate endpoints from repositories
2. **HATEOAS Compliance**: Hypermedia-driven API design
3. **Money Type Handling**: Proper currency handling with Joda Money
4. **Entity Inheritance**: BaseEntity pattern for common fields
5. **Jakarta EE**: Modern Spring Boot 3.x standard
6. **Custom Query Methods**: Spring Data JPA naming conventions
7. **JSON Customization**: Custom serializers/deserializers

## Advanced Topics

### Custom Repository Configuration

```java
@RepositoryRestResource(
    path = "coffee",                    // Resource path
    collectionResourceRel = "coffees",  // Collection relation name
    itemResourceRel = "coffee"          // Item relation name
)
public interface CoffeeRepository extends JpaRepository<Coffee, Long> {
    
    @RestResource(path = "by-name", rel = "findByName")
    Coffee findByName(@Param("name") String name);
    
    @RestResource(exported = false)  // Hide from REST API
    void deleteByName(String name);
}
```

### Global REST Configuration

```java
@Configuration
public class RestConfig implements RepositoryRestConfigurer {
    
    @Override
    public void configureRepositoryRestConfiguration(
            RepositoryRestConfiguration config, CorsRegistry cors) {
        
        // Set base path
        config.setBasePath("/api");
        
        // Expose entity IDs
        config.exposeIdsFor(Coffee.class, CoffeeOrder.class);
        
        // Pagination settings
        config.setDefaultPageSize(20);
        config.setMaxPageSize(100);
        
        // Return body on create/update
        config.setReturnBodyOnCreate(true);
        config.setReturnBodyOnUpdate(true);
    }
}
```

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
│   │       └── data.sql                     # Initial data
│   └── test/
│       └── java/tw/fengqing/spring/springbucks/waiter/
│           └── WaiterServiceApplicationTests.java
└── pom.xml                                  # Maven configuration
```

## References

- [Spring Data REST Documentation](https://docs.spring.io/spring-data/rest/docs/current/reference/html/)
- [HATEOAS Specification](https://restfulapi.net/hateoas/)
- [HAL Format](https://tools.ietf.org/html/draft-kelly-json-hal)
- [Spring Data JPA Documentation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
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

**📅 Last Updated: 2025-10-27**  
**👨‍💻 Maintainer: FengQing Team**

**⭐ If this project helps you, please give it a star!**
