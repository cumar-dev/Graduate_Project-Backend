# Graduate Project — Backend

Spring Boot REST API for the Graduate Project Management System.

## Run (development)

From the **backend** folder:

```bash
mvn spring-boot:run
```

Or use `run-backend.bat` (loads `.env` the same way).

- API base: **http://localhost:9090/api**
- Profile: `dev`
- MongoDB: Atlas — set `MONGODB_URI` in `backend/.env` (loaded automatically on startup)

**Port 9090 already in use?** A previous run is still active. Stop it:

```bat
scripts\stop-backend.bat
```

Then run `mvn spring-boot:run` again.

## Build WAR

```bash
mvn clean package
```

Deploy `target/Graduate_Project.war` to Tomcat on port **8081** with context `/Graduate_Project`.

## API overview

| Prefix              | Role        |
|---------------------|-------------|
| `/api/auth/*`       | Public      |
| `/api/student/*`    | Student     |
| `/api/supervisor/*` | Supervisor  |
| `/api/admin/*`      | Admin       |
| `/api/projects/*`   | Authenticated |

Postman collection: `postman/Graduate_Project_API.postman_collection.json`
