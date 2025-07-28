# HATEOAS Waiter Service ⚡

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Data REST](https://img.shields.io/badge/Spring%20Data%20REST-3.2.5-blue.svg)](https://spring.io/projects/spring-data-rest)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## 專案介紹

這是一個基於 **Spring Data REST** 和 **HATEOAS**（Hypertext Application Language）的咖啡廳訂單管理系統。專案展示了如何使用 Spring Boot 快速構建符合 REST 規範的 Web 服務，並透過超媒體驅動的方式提供 API 導航功能。

### 🎯 核心功能
- **咖啡商品管理**：提供咖啡品項的 CRUD 操作
- **訂單管理**：處理客戶訂單的建立與狀態追蹤
- **HATEOAS 支援**：自動生成超媒體連結，提供 API 導航
- **分頁與搜尋**：支援分頁查詢和自定義搜尋功能

### 💡 為什麼選擇此專案？
- **快速開發**：Spring Data REST 自動生成 RESTful API
- **標準化**：符合 HATEOAS 規範，提供一致的 API 體驗
- **可擴展性**：易於添加新的資源和功能
- **開發效率**：減少重複的 Controller 程式碼

### 🎯 專案特色

- **自動化 API 生成**：基於 JPA Repository 自動產生 REST 端點
- **超媒體驅動**：每個資源都包含相關連結，便於 API 導航
- **多格式支援**：支援 JSON 和 XML 格式回應
- **分頁與排序**：內建分頁、排序和搜尋功能
- **貨幣處理**：整合 Joda Money 處理價格資訊

## 技術棧

### 核心框架
- **Spring Boot 3.2.5** - 快速開發框架
- **Spring Data JPA** - 資料持久化層
- **Spring Data REST** - 自動生成 RESTful API
- **Hibernate 6.4.4** - ORM 框架

### 開發工具與輔助
- **Joda Money 2.0.2** - 貨幣處理庫
- **Jackson 2.19.1** - JSON/XML 序列化
- **H2 Database** - 內嵌式資料庫
- **Lombok** - 減少樣板程式碼
- **Apache Commons Lang3** - 工具類庫

## 專案結構

```
hateoas-waiter-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── tw/fengqing/spring/springbucks/waiter/
│   │   │       ├── model/
│   │   │       │   ├── BaseEntity.java          # 基礎實體類別
│   │   │       │   ├── Coffee.java              # 咖啡實體類別
│   │   │       │   ├── CoffeeOrder.java         # 訂單實體類別
│   │   │       │   ├── OrderState.java          # 訂單狀態列舉
│   │   │       │   └── MoneyConverter.java      # 貨幣轉換器
│   │   │       ├── repository/
│   │   │       │   ├── CoffeeRepository.java     # 咖啡資料存取層
│   │   │       │   └── CoffeeOrderRepository.java # 訂單資料存取層
│   │   │       ├── support/
│   │   │       │   ├── MoneySerializer.java     # 貨幣序列化器
│   │   │       │   └── MoneyDeserializer.java   # 貨幣反序列化器
│   │   │       └── WaiterServiceApplication.java # 主應用程式類別
│   │   └── resources/
│   │       ├── application.properties            # 應用程式設定檔
│   │       ├── schema.sql                       # 資料庫結構定義
│   │       └── data.sql                         # 初始資料
│   └── test/
│       └── java/
│           └── tw/fengqing/spring/springbucks/waiter/
│               └── WaiterServiceApplicationTests.java
├── pom.xml                                      # Maven 專案配置
└── README.md                                    # 專案說明文件
```

## 快速開始

### 前置需求
- **Java 21** 或更高版本
- **Maven 3.6** 或更高版本
- **IDE**：建議使用 IntelliJ IDEA 或 Eclipse

### 安裝與執行

1. **克隆此倉庫：**
```bash
git clone https://github.com/SpringMicroservicesCourse/hateoas-waiter-service.git
```

2. **進入專案目錄：**
```bash
cd hateoas-waiter-service
```

3. **編譯專案：**
```bash
mvn compile
```

4. **執行應用程式：**
```bash
mvn spring-boot:run
```

5. **驗證服務：**
```bash
# 測試 API 根路徑
curl http://localhost:8080/api

# 查看咖啡列表
curl http://localhost:8080/api/coffee

# 查看訂單列表
curl http://localhost:8080/api/coffeeOrders
```

## API 端點說明

### 基礎路徑
所有 API 端點都以 `/api` 為基礎路徑

### 主要端點

| 端點 | 方法 | 說明 | 範例 |
|------|------|------|------|
| `/api` | GET | API 根路徑，顯示所有可用資源 | `curl http://localhost:8080/api` |
| `/api/coffee` | GET | 取得所有咖啡品項 | `curl http://localhost:8080/api/coffee` |
| `/api/coffee/{id}` | GET | 取得特定咖啡品項 | `curl http://localhost:8080/api/coffee/1` |
| `/api/coffee/search/findByName?name=latte` | GET | 搜尋特定名稱的咖啡 | `curl "http://localhost:8080/api/coffee/search/findByName?name=latte"` |
| `/api/coffeeOrders` | GET | 取得所有訂單 | `curl http://localhost:8080/api/coffeeOrders` |
| `/api/coffeeOrders/{id}` | GET | 取得特定訂單 | `curl http://localhost:8080/api/coffeeOrders/1` |

### 分頁與排序
```bash
# 分頁查詢（每頁 3 筆，第 0 頁）
curl "http://localhost:8080/api/coffee?page=0&size=3"

# 排序查詢（依 ID 降序）
curl "http://localhost:8080/api/coffee?sort=id,desc"
```

## 進階說明

### 環境變數
```properties
# 資料庫設定（使用 H2 內嵌資料庫）
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver

# JPA 設定
spring.jpa.hibernate.ddl-auto=none
spring.jpa.properties.hibernate.show_sql=true
spring.jpa.properties.hibernate.format_sql=true

# REST API 設定
spring.data.rest.base-path=/api
```

### 重要程式碼說明

#### 1. Repository 配置
```java
@RepositoryRestResource(path = "coffee")
public interface CoffeeRepository extends JpaRepository<Coffee, Long> {
    // 根據名稱列表查詢咖啡，並按 ID 排序
    List<Coffee> findByNameInOrderById(List<String> list);
    
    // 根據單一名稱查詢咖啡
    Coffee findByName(String name);
}
```

#### 2. 實體類別設計
```java
@Entity
@Table(name = "T_COFFEE")
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Coffee extends BaseEntity implements Serializable {
    private String name;
    
    // 使用自定義轉換器處理貨幣類型
    @Convert(converter = MoneyConverter.class)
    private Money price;
}
```

#### 3. 應用程式配置
```java
@SpringBootApplication
@EnableCaching
public class WaiterServiceApplication {
    
    // 配置 Hibernate 模組，處理延遲載入
    @Bean
    public Hibernate6Module hibernate6Module() {
        return new Hibernate6Module();
    }
    
    // 配置 REST API 基礎路徑和 ID 暴露
    @Bean
    public RepositoryRestConfigurer repositoryRestConfigurer() {
        return new RepositoryRestConfigurer() {
            @Override
            public void configureRepositoryRestConfiguration(
                RepositoryRestConfiguration config, CorsRegistry cors) {
                // 設定 API 基礎路徑
                config.setBasePath("/api");
                // 暴露實體 ID 欄位
                config.exposeIdsFor(Coffee.class, CoffeeOrder.class);
            }
        };
    }
}
```

## 參考資源

- [Spring Data REST 官方文件](https://docs.spring.io/spring-data/rest/docs/current/reference/html/)
- [HATEOAS 規範說明](https://restfulapi.net/hateoas/)
- [Spring Boot 官方指南](https://spring.io/guides)
- [Joda Money 文件](https://www.joda.org/joda-money/)

## 注意事項與最佳實踐

### ⚠️ 重要提醒

| 項目 | 說明 | 建議做法 |
|------|------|----------|
| 資料庫連線 | 生產環境資料庫配置 | 使用外部資料庫（如 PostgreSQL） |
| 安全性 | API 認證與授權 | 整合 Spring Security |
| 效能 | 大量資料處理 | 實作快取機制 |
| 監控 | 應用程式監控 | 整合 Spring Boot Actuator |

### 🔒 最佳實踐指南

- **API 版本控制**：在 URL 路徑中加入版本號（如 `/api/v1/coffee`）
- **錯誤處理**：實作統一的錯誤回應格式
- **API 文件**：整合 Swagger/OpenAPI 自動生成文件
- **測試覆蓋率**：撰寫完整的單元測試和整合測試
- **程式碼品質**：使用 Checkstyle 和 SpotBugs 確保程式碼品質

### 📝 程式碼註解規範

```java
/**
 * 咖啡實體類別
 * 
 * 此類別代表咖啡廳中的咖啡品項，包含：
 * - 咖啡名稱
 * - 價格資訊（使用 Joda Money 處理）
 * - 建立和更新時間戳記
 * 
 * @author 風清雲談
 * @since 1.0.0
 */
@Entity
@Table(name = "T_COFFEE")
public class Coffee extends BaseEntity implements Serializable {
    
    /**
     * 咖啡名稱
     * 例如：espresso、latte、cappuccino
     */
    private String name;
    
    /**
     * 咖啡價格
     * 使用 Joda Money 處理貨幣，支援多幣別
     */
    @Convert(converter = MoneyConverter.class)
    private Money price;
}
```

## 授權說明

本專案採用 MIT 授權條款，詳見 LICENSE 檔案。

## 關於我們

我們主要專注在敏捷專案管理、物聯網（IoT）應用開發和領域驅動設計（DDD）。喜歡把先進技術和實務經驗結合，打造好用又靈活的軟體解決方案。

## 聯繫我們

- **FB 粉絲頁**：[風清雲談 | Facebook](https://www.facebook.com/profile.php?id=61576838896062)
- **LinkedIn**：[linkedin.com/in/chu-kuo-lung](https://www.linkedin.com/in/chu-kuo-lung)
- **YouTube 頻道**：[雲談風清 - YouTube](https://www.youtube.com/channel/UCXDqLTdCMiCJ1j8xGRfwEig)
- **風清雲談 部落格**：[風清雲談](https://blog.fengqing.tw/)
- **電子郵件**：[fengqing.tw@gmail.com](mailto:fengqing.tw@gmail.com)

---

**📅 最後更新：2025-07-28**  
**👨‍💻 維護者：風清雲談** 