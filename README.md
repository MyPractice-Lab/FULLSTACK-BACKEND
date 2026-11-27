# The Zone — Backend

API REST de la tienda de artículos gamer **The Zone**, construida para la Evaluación Parcial 3 de
**DSY1104 Desarrollo Fullstack II**.

El frontend que la consume está en un repositorio aparte (proyecto React `tienda-online`).

## Stack

| Pieza | Versión |
|---|---|
| Java | 17 |
| Spring Boot | 4.0.10 |
| Spring Security + JWT (jjwt) | 0.13.0 |
| Spring Data JPA / Hibernate | la del BOM de Spring Boot |
| MySQL | 8.0 |
| springdoc-openapi (Swagger UI) | 3.2.1 |

## Cómo levantarlo

```bash
# Base de datos en Docker
docker run -d --name thezone-mysql -p 3307:3306 \
  -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=thezone mysql:8.0

# Backend
mvn spring-boot:run
```

- API: `http://localhost:8080/api`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Al arrancar con las tablas vacías se cargan los datos iniciales: 4 categorías, 8 productos y dos
cuentas.

| Rol | Correo | Clave |
|---|---|---|
| Administrador | `admin@thezone.cl` | `Admin123` |
| Cliente | `pedro@example.com` | `Cliente123` |

## Estructura

```
com.thezone.tienda
├── modelo        Entidades JPA y embebidos
├── repositorio   Interfaces JpaRepository
├── dto           Records de entrada y salida
├── servicio      Lógica de negocio y transacciones
├── web           Controllers REST, OpenAPI y manejo de errores
├── seguridad     JWT, UserDetailsService y reglas de acceso
└── config        OpenAPI y carga de datos iniciales
```

## Endpoints

| Recurso | Lectura | Escritura |
|---|---|---|
| `/api/categorias` | pública | administrador |
| `/api/productos` | pública (`/criticos` solo administrador) | administrador |
| `/api/usuarios` | administrador; un cliente solo su cuenta | administrador |
| `/api/ordenes` | administrador; `/mias` para la cuenta autenticada | crear es público |
| `/api/auth` | `login` y `registro` públicos; `yo` autenticado | — |

El detalle completo está en Swagger y en `docs/documento-de-integracion.md`.

## Autenticación

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"correo":"admin@thezone.cl","clave":"Admin123"}'
```

La respuesta trae el token. Se envía en cada petición protegida:

```
Authorization: Bearer <token>
```

## Configuración

Las propiedades propias del proyecto están en `src/main/resources/application.properties`:

| Propiedad | Para qué |
|---|---|
| `tienda.jwt.secreto` | Clave de firma del token. **Cámbiala en el servidor.** |
| `tienda.jwt.duracion-minutos` | Vigencia del token (120 por defecto). |
| `tienda.cors.origenes` | Patrones de origen autorizados. |
| `tienda.semillas.activas` | Carga los datos iniciales si las tablas están vacías. |

Para apuntar a Amazon RDS basta cambiar `spring.datasource.url`, `username` y `password`.

## Documentación

- `docs/documento-de-integracion.md` — arquitectura, flujo de datos, endpoints y seguridad.
- `docs/manual-de-usuario.md` — manual de la aplicación con capturas.
