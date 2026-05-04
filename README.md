#Thông tin nhóm:
-Lê Trần Gia Huy
-Huỳnh Đức Phú
-Đặng Nguyễn Tiến Phát
-Nguyễn Anh Tùng
-Lê Nguyễn Quỳnh

# KTTK Food Ordering

Hệ thống `KTTK Food Ordering` là một dự án microservices mô phỏng bài toán đặt món ăn trực tuyến. Repository này hiện đóng vai trò bộ khung ban đầu cho kiến trúc hệ thống, sử dụng Spring Boot và Spring Cloud để tách các domain thành nhiều service độc lập, kết nối với nhau thông qua service discovery.

Mục tiêu của repository:

- Làm nền tảng để phát triển hệ thống đặt món theo kiến trúc microservices
- Tách biệt rõ từng domain như người dùng, món ăn, đơn hàng, thanh toán/thông báo
- Cung cấp một `API Gateway` để làm điểm vào tập trung
- Dùng `Eureka Server` để các service có thể đăng ký và tìm thấy nhau

## 1. Kiến trúc tổng quan

Hệ thống hiện được tổ chức thành 6 module độc lập:

- `eureka-server`: service registry
- `gateway-server`: cổng vào hệ thống
- `user-service`: quản lý thông tin người dùng
- `food-service`: quản lý món ăn
- `order-service`: quản lý đơn hàng
- `payment-notif-service`: xử lý thanh toán hoặc thông báo

Luồng tổng quát:

1. Client gửi request vào `gateway-server`
2. `gateway-server` định tuyến request tới service phù hợp
3. Mỗi service đăng ký với `eureka-server`
4. Các service có thể tìm nhau thông qua Eureka thay vì hard-code địa chỉ

## 2. Cấu trúc thư mục

```text
KTTK-Food-Ordering/
├── eureka-server/
├── gateway-server/
├── food-service/
├── order-service/
├── payment-notif-service/
├── user-service/
└── README.md
```

Mỗi module là một Spring Boot application riêng, có:

- `pom.xml`
- `mvnw`, `mvnw.cmd`
- `src/main/java`
- `src/main/resources/application.properties`
- `src/test/java`

## 3. Mô tả từng service

### 3.1 Eureka Server

Vai trò:

- Đăng ký và theo dõi trạng thái các service
- Cho phép các service khác tra cứu nhau bằng `service name`

Thông tin hiện tại:

- Tên application: `eureka-server`
- Port: `8761`
- Không tự đăng ký vào chính nó
- Không lấy registry từ service khác

### 3.2 Gateway Server

Vai trò:

- Là điểm truy cập tập trung cho client
- Có thể dùng để routing, filter, auth, rate limiting, logging

Thông tin hiện tại:

- Tên application: `gateway-server`
- Port: `8080`
- Có bật discovery client
- Đã thêm dependency liên quan đến security và oauth2 client

Lưu ý:

- Chưa có cấu hình route cụ thể trong `application.properties`
- Chưa có class cấu hình security hoặc gateway filters

### 3.3 User Service

Vai trò dự kiến:

- Quản lý tài khoản người dùng
- Đăng ký, đăng nhập, hồ sơ người dùng, địa chỉ giao hàng

Thông tin hiện tại:

- Tên application: `user-service`
- Port: `8081`

### 3.4 Food Service

Vai trò dự kiến:

- Quản lý danh sách món ăn
- Danh mục món, giá, mô tả, trạng thái còn hàng

Thông tin hiện tại:

- Tên application: `food-service`
- Port: `8082`

### 3.5 Order Service

Vai trò dự kiến:

- Quản lý giỏ hàng và đơn đặt món
- Theo dõi trạng thái đơn hàng

Thông tin hiện tại:

- Tên application: `order-service`
- Port: `8083`

### 3.6 Payment Notification Service

Vai trò dự kiến:

- Xử lý logic thanh toán
- Gửi thông báo sau thanh toán hoặc cập nhật trạng thái đơn

Thông tin hiện tại:

- Tên application: `payment-notif-service`
- Port: `8084`

## 4. Công nghệ sử dụng

- Java `25`
- Spring Boot `4.0.5`
- Spring Cloud `2025.1.1`
- Spring Boot Actuator
- Spring Cloud Netflix Eureka
- Spring Cloud Gateway WebFlux
- Spring Security
- OAuth2 Client
- Maven Wrapper
- JUnit 5

## 5. Hiện trạng codebase

Repository hiện đang ở mức khởi tạo kiến trúc và dependency. Phần lớn code hiện tại là scaffold được tạo từ Spring Initializr hoặc tương đương.

Đã có:

- Class khởi động cho từng service
- Cấu hình `application.properties` cơ bản
- Eureka Server hoạt động ở mức cấu hình
- Các service client đã trỏ tới Eureka
- Test khởi động context cho từng module

Chưa có:

- REST controller
- DTO / entity / repository / service layer
- Kết nối cơ sở dữ liệu
- Validation
- Exception handling
- Logging strategy
- API contract
- Gateway route config
- Authentication / authorization hoàn chỉnh
- Giao tiếp liên service
- Docker / Docker Compose
- CI/CD pipeline

Kết luận ngắn:

Đây là bộ khung kiến trúc tốt để tiếp tục phát triển, nhưng chưa phải ứng dụng hoàn chỉnh.

## 6. Cấu hình hiện tại

### 6.1 Port mặc định

| Module | Application Name | Port |
|---|---|---:|
| eureka-server | `eureka-server` | 8761 |
| gateway-server | `gateway-server` | 8080 |
| user-service | `user-service` | 8081 |
| food-service | `food-service` | 8082 |
| order-service | `order-service` | 8083 |
| payment-notif-service | `payment-notif-service` | 8084 |

### 6.2 Eureka registration

Tất cả service clients đang dùng:

```properties
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

Riêng `eureka-server`:

```properties
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
```

### 6.3 Gateway binding

Gateway hiện có:

```properties
server.address=0.0.0.0
```

Điều này cho phép service lắng nghe trên mọi interface mạng của máy đang chạy.

## 7. Cách chạy dự án

### 7.1 Yêu cầu môi trường

- Cài `JDK 25`
- Có quyền chạy `mvnw.cmd` trên Windows hoặc `./mvnw` trên Linux/macOS

Khuyến nghị:

- Dùng IntelliJ IDEA hoặc VS Code với Java Extension Pack

### 7.2 Thứ tự khởi động

Khởi động theo thứ tự:

1. `eureka-server`
2. `gateway-server`
3. `user-service`
4. `food-service`
5. `order-service`
6. `payment-notif-service`

### 7.3 Chạy trên Windows

```powershell
cd eureka-server
.\mvnw.cmd spring-boot:run
```

Mở terminal khác:

```powershell
cd gateway-server
.\mvnw.cmd spring-boot:run
```

Tương tự cho các service còn lại:

```powershell
cd user-service
.\mvnw.cmd spring-boot:run
```

```powershell
cd food-service
.\mvnw.cmd spring-boot:run
```

```powershell
cd order-service
.\mvnw.cmd spring-boot:run
```

```powershell
cd payment-notif-service
.\mvnw.cmd spring-boot:run
```

### 7.4 Chạy bằng IDE

Trong mỗi module, chạy class `*Application.java` tương ứng:

- `EurekaServerApplication`
- `GatewayServerApplication`
- `UserServiceApplication`
- `FoodServiceApplication`
- `OrderServiceApplication`
- `PaymentNotifServiceApplication`

## 8. Kiểm tra sau khi chạy

Sau khi khởi động `eureka-server`, truy cập:

```text
http://localhost:8761
```

Nếu các service khác chạy thành công và đăng ký được, bạn sẽ thấy chúng xuất hiện trong dashboard của Eureka.

Các địa chỉ mặc định:

- Gateway: `http://localhost:8080`
- User Service: `http://localhost:8081`
- Food Service: `http://localhost:8082`
- Order Service: `http://localhost:8083`
- Payment Notification Service: `http://localhost:8084`

Lưu ý:

- Hiện tại chưa có endpoint nghiệp vụ, nên việc kiểm tra chủ yếu là service có khởi động được và có đăng ký Eureka hay không.

## 9. Kiểm thử

Mỗi module hiện có một test đơn giản:

```java
@SpringBootTest
class XxxApplicationTests {
    @Test
    void contextLoads() {
    }
}
```

Ý nghĩa:

- Chỉ kiểm tra Spring context khởi động thành công
- Chưa kiểm thử controller, service, repository, hay luồng nghiệp vụ

Nếu muốn chạy test trong từng module:

```powershell
cd user-service
.\mvnw.cmd test
```

Thực hiện tương tự cho các module khác.

## 10. Định hướng phát triển đề xuất

### Giai đoạn 1: Hoàn thiện chức năng cốt lõi

- Thêm `Controller`, `Service`, `Repository`, `DTO`
- Tích hợp cơ sở dữ liệu cho từng service
- Xây dựng API CRUD cơ bản

### Giai đoạn 2: Hoàn thiện giao tiếp giữa các service

- Thêm route cấu hình cho `gateway-server`
- Thêm OpenFeign hoặc `WebClient`
- Chuẩn hóa request/response giữa các service

### Giai đoạn 3: Bổ sung bảo mật

- Xây dựng đăng nhập và phân quyền
- Cấu hình OAuth2 hoặc JWT đầy đủ
- Bảo vệ gateway và internal APIs

### Giai đoạn 4: Sẵn sàng triển khai

- Viết Dockerfile cho từng service
- Thêm `docker-compose.yml`
- Thiết lập config cho dev/test/prod
- Thêm logging, metrics, tracing
- Thiết lập CI/CD

## 11. Đề xuất phân chia domain dữ liệu

Ví dụ hướng thiết kế:

- `user-service`
  - User
  - Role
  - Address

- `food-service`
  - FoodItem
  - Category
  - Inventory

- `order-service`
  - Cart
  - Order
  - OrderItem
  - OrderStatus

- `payment-notif-service`
  - PaymentTransaction
  - PaymentStatus
  - NotificationLog

## 12. Hạn chế hiện tại

- Chưa có root `pom.xml` để build toàn bộ project một lần
- Chưa có cấu hình dùng chung giữa các service
- Chưa có persistence layer
- Chưa có API tài liệu hóa bằng Swagger/OpenAPI
- Chưa có cấu hình môi trường qua profile
- Chưa có chiến lược versioning API

## 13. Đề xuất cải thiện cấu trúc repository

Trong giai đoạn tiếp theo, có thể bổ sung:

- Root `pom.xml` dạng multi-module
- Thư mục `docs/`
- `docker-compose.yml`
- `.env.example`
- `postman/` hoặc `bruno/` collection
- `scripts/` để tự động khởi chạy local

## 14. Tác giả và mục đích sử dụng

Repository này phù hợp cho:

- Đồ án môn học
- Demo kiến trúc microservices
- Nền tảng để tiếp tục xây dựng hệ thống đặt món ăn

Nếu dùng cho mục tiêu production, cần bổ sung thêm nhiều lớp hoàn thiện về dữ liệu, bảo mật, quan sát hệ thống và vận hành.
