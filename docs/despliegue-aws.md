# Despliegue en AWS — The Zone

**Asignatura:** DSY1104 Desarrollo Fullstack II · Evaluación Parcial 3
**Cuenta:** AWS Academy Learner Lab · región **us-east-1**
**Fecha del despliegue:** 26-11-2026

---

## 1. Qué quedó montado

```
                  Internet
                     │
                     ▼  puerto 80
        ┌────────────────────────────┐
        │  EC2 t3.micro "HolaEC2"    │
        │  Amazon Linux 2023         │
        │  100.54.143.166            │
        │                            │
        │  nginx :80                 │
        │   ├── /          → React (archivos estáticos)
        │   ├── /api/      → proxy a 127.0.0.1:8080
        │   └── /swagger-ui → proxy a 127.0.0.1:8080
        │                            │
        │  Spring Boot :8080         │
        │  (systemd: thezone)        │
        └────────────┬───────────────┘
                     │ puerto 3306, solo dentro de la VPC
                     ▼
        ┌────────────────────────────┐
        │  RDS MySQL 8.0             │
        │  db.t3.micro · 20 GB gp3   │
        │  thezone-db...rds.amazonaws.com
        │  no accesible desde fuera  │
        └────────────────────────────┘
```

**Una sola instancia sirve las dos cosas.** El frontend compilado son archivos estáticos que
entrega nginx; la API la atiende Spring Boot en el 8080, al que solo se llega desde dentro de la
máquina. Como el navegador pide `/api` al mismo origen que la página, **no hay CORS en producción**.

## 2. Recursos creados

| Recurso | Identificador | Notas |
|---|---|---|
| EC2 | `i-03e4adbf6dfa52347` ("HolaEC2") | Ya existía; se reutilizó. t3.micro, 1 GB de RAM. |
| IP pública | `100.54.143.166` | **Cambia si la instancia se detiene y vuelve a arrancar.** |
| Security group EC2 | `sg-064b52972b7647e11` | Ya tenía abiertos 22, 80 y 443. |
| RDS | `thezone-db` | MySQL 8.0, db.t3.micro, 20 GB gp3, sin backups, single-AZ. |
| Security group RDS | `sg-07471b2cb0765fd9f` | Acepta 3306 **solo desde el SG de la EC2**. |
| Instance profile | `LabInstanceProfile` | Se asoció a la EC2 (el único que permite el Learner Lab). |

## 3. Rutas en el servidor

| Qué | Dónde |
|---|---|
| Jar del backend | `/opt/thezone/tienda.jar` |
| Variables de entorno | `/opt/thezone/thezone.env` (permisos 600) |
| Servicio systemd | `/etc/systemd/system/thezone.service` |
| Log del backend | `/var/log/thezone.log` |
| Frontend compilado | `/usr/share/nginx/thezone/` |
| Config de nginx | `/etc/nginx/conf.d/thezone.conf` |

## 4. Ajustes que hubo que hacer en la máquina

**Swap de 2 GB.** La t3.micro tiene 1 GB de RAM y la JVM sola se come buena parte. Sin swap, el
kernel mata el proceso. Se creó `/swapfile` y se dejó en `/etc/fstab` para que sobreviva a los
reinicios. La JVM además arranca acotada con `-Xms128m -Xmx512m`.

**nginx del sistema movido al 8081.** El `nginx.conf` que trae Amazon Linux define su propio
servidor en el 80; se cambió a 8081 para que el puerto 80 quede libre para la tienda.

**`react-app.conf` desactivado.** Había una configuración previa del 22-10-2026 que servía otra
aplicación React desde `/var/www/react-app` y se estaba quedando con el puerto 80. Se renombró a
`react-app.conf.desactivado`; **los archivos de esa app siguen intactos** en `/var/www/react-app`.
Para volver atrás basta devolverle el nombre y recargar nginx.

**Acceso sin llave.** No había ningún `.pem` a mano para la llave `EC2` de la instancia, así que se
entró con **EC2 Instance Connect**, que inyecta una llave pública temporal válida por 60 segundos.
También se asoció `LabInstanceProfile` por si se quisiera usar SSM más adelante.

## 5. Operación

```bash
# Estado del backend
sudo systemctl status thezone

# Reiniciar tras subir un jar nuevo
sudo systemctl restart thezone

# Ver el log en vivo
tail -f /var/log/thezone.log

# nginx
sudo nginx -t && sudo systemctl reload nginx
```

### Subir una versión nueva

```bash
# En el equipo de desarrollo
cd tienda && mvn -DskipTests package
scp -i <llave> target/tienda-0.0.1-SNAPSHOT.jar ec2-user@<ip>:/opt/thezone/tienda.jar

cd tienda-online && npm run build
tar -czf dist.tgz -C dist . && scp -i <llave> dist.tgz ec2-user@<ip>:/tmp/

# En el servidor
sudo systemctl restart thezone
sudo rm -rf /usr/share/nginx/thezone/* && sudo tar -xzf /tmp/dist.tgz -C /usr/share/nginx/thezone
sudo chown -R nginx:nginx /usr/share/nginx/thezone
```

## 6. Verificación del despliegue

Todo lo siguiente se comprobó contra la IP pública, no en local:

| Prueba | Resultado |
|---|---|
| `GET /api/categorias` | 200, las 4 categorías |
| `GET /api/productos` | 200, los 8 productos con sus precios |
| `POST /api/auth/login` (admin) | 200, token emitido, rol `administrador` |
| `POST /api/productos` con token | 201 |
| `DELETE /api/productos/{id}` con token | 204 |
| `POST /api/productos` sin token | 403 |
| `/swagger-ui.html` | 200 |
| `/admin` (ruta de React Router) | 200 |
| Navegador real: portada y catálogo | 12 tarjetas, sin errores en consola |
| Navegador real: login y sesión tras F5 | Se mantiene en `/admin` |

## 7. Limitaciones del Learner Lab

Esto es importante para planificar la defensa:

- **Las credenciales del CLI caducan** al cerrar la sesión del laboratorio y cambian en cada
  arranque.
- **Al terminar la sesión del lab, la EC2 se detiene.** Hay que arrancarla de nuevo desde la
  consola, y entonces **la IP pública cambia**: habrá que actualizar `TIENDA_CORS_ORIGENES` en
  `/opt/thezone/thezone.env` y reiniciar el servicio. Una Elastic IP lo evitaría, si el lab la
  permite.
- **El presupuesto es acotado.** Conviene detener la EC2 y la RDS cuando no se usen.
- Para la presentación: levantar el lab con anticipación y verificar que
  `http://<ip>/api/categorias` responda **antes** de empezar.

## 8. Credenciales del entorno

| Qué | Valor |
|---|---|
| Usuario de MySQL en RDS | `admin` |
| Base de datos | `thezone` |
| Cuenta administrador de la app | `admin@thezone.cl` / `Admin123` |
| Cuenta cliente de la app | `pedro@example.com` / `Cliente123` |

La contraseña de RDS y el secreto del JWT están en `/opt/thezone/thezone.env`, fuera del
repositorio. Son valores de un entorno académico y temporal: en un sistema real irían en AWS
Secrets Manager o en Parameter Store.
