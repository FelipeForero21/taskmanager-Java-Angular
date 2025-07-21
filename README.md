# Task Manager API & Frontend

## Descripción General

Este proyecto es un sistema de gestión de tareas compuesto por un backend (API REST en Spring Boot) y un frontend (Angular). La base de datos ya está desplegada, pero se documentan todas las tablas y relaciones utilizadas.

---

## Backend (Spring Boot)

### Requisitos
- Java 21 o superior
- Gradle Wrapper (incluido)
- Acceso a la base de datos SQL Server (ya desplegada)

### Instalación y Ejecución
1. Abre una terminal en la carpeta `Backend`.
2. Ejecuta el script de inicio rápido:
   
   ```sh
   .\start-project.bat
   ```
   
   Esto verificará Java, limpiará, compilará y levantará la API en modo local.

3. Accede a:
   - Aplicación: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui/index.html
   - Health Check: http://localhost:8080/actuator/health

### Tablas y Relaciones

#### Users
- `UserId` (PK, UUID)
- `Email` (único)
- `PasswordHash`
- `FirstName`, `LastName`
- `IsActive`, `CreatedAt`, `UpdatedAt`, etc.

#### Tasks
- `TaskId` (PK, UUID)
- `Title`, `Description`
- `TaskStatusId` (FK a TaskStatuses)
- `TaskPriorityId` (FK a TaskPriorities)
- `CategoryId` (FK a Categories)
- `CreatedBy` (FK a Users)
- `AssignedTo` (FK a Users)
- `DueDate`, `CompletedAt`, `EstimatedHours`, `ActualHours`, etc.

#### TaskStatuses
- `TaskStatusId` (PK)
- `StatusName`, `StatusDescription`, `ColorHex`, etc.

#### TaskPriorities
- `TaskPriorityId` (PK)
- `PriorityName`, `PriorityDescription`, `PriorityLevel`, `ColorHex`, etc.

#### Categories
- `CategoryId` (PK)
- `CategoryName`, `CategoryDescription`, `ColorHex`, `IconName`, etc.

#### UserSessions
- `SessionId` (PK, UUID)
- `UserId` (FK a Users)
- `TokenHash`, `DeviceInfo`, `IpAddress`, `IsActive`, etc.

#### UserPreferences
- `PreferenceId` (PK, UUID)
- `UserId` (FK único a Users)
- `Theme`, `Language`, `NotificationsEnabled`, `EmailNotifications`, `TasksPerPage`, `DefaultTaskView`, etc.

### Relaciones principales
- **Tasks** tiene FK a **TaskStatuses**, **TaskPriorities**, **Categories**, y dos FK a **Users** (creador y asignado).
- **UserSessions** y **UserPreferences** tienen FK a **Users**.

---

## Frontend (Angular)

### Requisitos
- Node.js 18+
- npm 9+

### Instalación
1. Abre una terminal en la carpeta `Frontend`.
2. Instala las dependencias:
   
   ```sh
   npm install --force
   ```

### Ejecución
1. Inicia la aplicación:
   
   ```sh
   npm start
   ```
2. Accede a [http://localhost:4200](http://localhost:4200)

### Configuración
- El frontend está configurado para consumir la API en `http://localhost:8080/api`.
- Puedes modificar la URL en `src/environments/environment.ts` si es necesario.

---

## Notas
- La base de datos **ya está desplegada** y no se incluye script de creación.
- Consulta la documentación Swagger para ver los endpoints disponibles.
- Para cualquier problema, revisa los logs de la terminal y la configuración en `application.properties` (backend).

---
## Video Evidencia
- Accede a [Video de Demostración](https://youtu.be/Bu6_FDNK99M)




**Desarrollado por Felipe Forero** 