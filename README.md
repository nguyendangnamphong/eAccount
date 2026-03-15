# eAccount - Microservice Quản lý Tài khoản và Phân quyền

## 1. Tổng quan Dự án
Microservice **eAccount** là thành phần cốt lõi trong hệ sinh thái eOffice, chịu trách nhiệm quản lý danh tính người dùng (Identity), hồ sơ nhân viên (Profile), ma trận quyền hạn (Permissions) và cung cấp các dịch vụ xác thực nội bộ cho toàn bộ các Microservices khác (eForm, eFlow).

---

## 2. Công nghệ và Hạ tầng (Technology Stack)
Dự án được xây dựng trên nền tảng hiện đại, kế thừa từ JHipster:

- **Ngôn ngữ & Framework chính**:
  - **Java 21**: Tối ưu hóa hiệu năng và tính năng ngôn ngữ mới nhất.
  - **Spring Boot 3.3.5**: Framework nền tảng cho Microservices.
  - **JHipster 8.7.2**: Khung nền tảng quản trị tiên tiến.
- **Cơ sở dữ liệu & Lưu trữ**:
  - **MySQL**: Hệ quản trị CSDL quan hệ chính.
  - **Liquibase**: Quản lý phiên bản và cấu trúc database.
  - **Hazelcast**: Cơ chế caching phân tán.
- **Bảo mật & API**:
  - **Spring Security (OAuth2 / JWT)**: Bảo mật phân tán qua JSON Web Token.
  - **Springdoc OpenAPI (Swagger)**: Tự động tạo tài liệu API.
- **Hạ tầng & Build**:
  - **Maven**: Công cụ quản lý build project.
  - **Undertow**: Máy chủ web nhúng hiệu năng cao.
  - **Docker / Jib**: Đóng gói ứng dụng dưới dạng Container.

---

## 3. Thiết kế Cơ sở dữ liệu (Database Design)

### 3.1. Thực thể `User` (Mặc định)
- Quản lý các thông tin cốt lõi: `email`, `password_hash`, `activated`.

### 3.2. Thực thể `UserProfile` (Mở rộng)
- **Mối quan hệ**: One-to-One với `User`.
- **Thông tin chi tiết**: `phone`, `dob`, `gender`, `position`, `job`, `department`, `avatar`.

### 3.3. Thực thể `UserToken`
- Quản lý trạng thái đăng nhập và thu hồi quyền truy cập (`token_str`, `expiry_date`, `is_revoked`).

### 3.4. Ma trận Quyền (Permission Matrix)
Quyền được thiết kế theo mô hình **Âm (-1) / Dương (1-5)**:
- **-1**: Quyền quản lý cá nhân (Mặc định).
- **1**: Quản lý nhân sự (Tạo tài khoản).
- **2**: Quyền hồ sơ (eForm).
- **3**: Quyền luồng (eFlow).
- **4**: Quản lý tài khoản (Tìm kiếm/Xóa mọi tài khoản).
- **5**: Quản lý truy cập (Gán/Gỡ quyền).

---

## 4. Kiến trúc Tầng Service (Business Logic)

1. **PermissionManagementService**: Quản lý logic đồng bộ quyền (Sync/Overwrite).
2. **AccountManagementService**: Xử lý tạo nhân viên mới, sinh mật khẩu và xóa tài khoản có kiểm tra thẩm quyền.
3. **AuthInterService & TokenManagementService**: Cung cấp dịch vụ xác thực nội bộ và quản lý vòng đời Token.
4. **UserProfileService**: Quản lý hồ sơ cá nhân và tích hợp lưu trữ.

---

## 5. Danh mục API Chi tiết

### 5.1. Nhóm Quản trị Nhân sự & Tài khoản
- `POST /api/account/profile`: Tạo mới/Cập nhật nhân sự.
- `POST /api/management/account/search`: Tìm kiếm user.
- `POST /api/management/account/delete`: Xóa tài khoản.

### 5.2. Nhóm Phân quyền
- `GET /api/permissions/system-roles`: Danh sách quyền hệ thống.
- `POST /api/permissions/sync`: Đồng bộ hóa danh sách quyền.
- `POST /api/permissions/search-user-roles`: Xem quyền của user.

### 5.3. Nhóm Inter-service (Nội bộ)
- `POST /api/internal/auth/generate-token`: Tạo token nội bộ.
- `POST /api/internal/auth/validate-token`: Validate token.
- `POST /api/internal/permissions/check-access`: Kiểm tra quyền truy cập.

---

## 6. Hướng dẫn Phát triển & Triển khai

### Phát triển (Development)
Để chạy ứng dụng ở chế độ dev, thực hiện lệnh:
```bash
./mvnw
```

### Đóng gói (Production)
Để đóng gói thành file JAR:
```bash
./mvnw -Pprod clean verify
```

Để chạy với Docker:
```bash
docker compose -f src/main/docker/app.yml up -d
```

### Kiểm thử (Testing)
Chạy bộ kiểm thử tự động:
```bash
./mvnw verify
```

---
© 2026 eOffice Project - eAccount Microservice.
