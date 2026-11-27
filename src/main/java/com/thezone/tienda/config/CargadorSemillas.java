package com.thezone.tienda.config;

import com.thezone.tienda.modelo.Categoria;
import com.thezone.tienda.modelo.Producto;
import com.thezone.tienda.modelo.Rol;
import com.thezone.tienda.modelo.Usuario;
import com.thezone.tienda.repositorio.CategoriaRepositorio;
import com.thezone.tienda.repositorio.ProductoRepositorio;
import com.thezone.tienda.repositorio.UsuarioRepositorio;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@ConditionalOnProperty(name = "tienda.semillas.activas", havingValue = "true")
public class CargadorSemillas {

    @Bean
    public ApplicationRunner sembrar(
            CategoriaRepositorio categorias,
            ProductoRepositorio productos,
            UsuarioRepositorio usuarios,
            PasswordEncoder codificador) {

        return argumentos -> {
            if (categorias.count() == 0) {
                sembrarCategorias(categorias);
            }

            if (productos.count() == 0) {
                sembrarProductos(categorias, productos);
            }

            if (usuarios.count() == 0) {
                sembrarUsuarios(usuarios, codificador);
            }
        };
    }

    private void sembrarCategorias(CategoriaRepositorio categorias) {
        categorias.save(categoria("teclados", "Teclados",
                "Mecánicos, de membrana y compactos para cada tipo de escritorio.",
                "/assets/img/teclado.jpg"));
        categorias.save(categoria("mouse", "Mouse",
                "Sensores de alta precisión, con cable e inalámbricos.",
                "/assets/img/mouse.jpg"));
        categorias.save(categoria("notebooks", "Notebooks",
                "Equipos portátiles con tarjeta dedicada y pantallas de alta tasa de refresco.",
                "/assets/img/laptop.webp"));
        categorias.save(categoria("accesorios", "Accesorios",
                "Mousepads, audífonos y todo lo que completa el setup.",
                "/assets/img/portada.jpg"));
    }

    private void sembrarProductos(CategoriaRepositorio categorias, ProductoRepositorio productos) {
        Categoria teclados = categorias.findBySlug("teclados").orElseThrow();
        Categoria mouse = categorias.findBySlug("mouse").orElseThrow();
        Categoria notebooks = categorias.findBySlug("notebooks").orElseThrow();
        Categoria accesorios = categorias.findBySlug("accesorios").orElseThrow();

        productos.save(producto("Teclado mecánico Zone K80 RGB", teclados, 59990, 47990, 12, "12 meses",
                "/assets/img/teclado.jpg",
                "Teclado mecánico de tamaño completo con switches rojos, retroiluminación RGB por tecla y estructura de aluminio."));
        productos.save(producto("Teclado compacto Zone K60 TKL", teclados, 39990, null, 3, "12 meses",
                "/assets/img/teclado.jpg",
                "Formato sin teclado numérico, ideal para escritorios pequeños y para jugadores que mueven mucho el mouse."));
        productos.save(producto("Mouse gamer Zone M5 Pro", mouse, 29990, 24990, 20, "12 meses",
                "/assets/img/mouse.jpg",
                "Mouse de 16000 DPI ajustables, seis botones programables y sensor óptico de alta precisión."));
        productos.save(producto("Mouse inalámbrico Zone M2 Air", mouse, 24990, null, 2, "12 meses",
                "/assets/img/mouse.jpg",
                "Conexión de 2.4 GHz con receptor USB, 70 horas de autonomía y peso de 68 gramos."));
        productos.save(producto("Notebook gamer Zone Raptor 15", notebooks, 899990, null, 5, "24 meses",
                "/assets/img/laptop.webp",
                "Notebook de 15 pulgadas con pantalla de 144 Hz, 16 GB de memoria RAM y disco sólido de 512 GB."));
        productos.save(producto("Notebook Zone Raptor 17 Ultra", notebooks, 1299990, 1149990, 1, "24 meses",
                "/assets/img/laptop.webp",
                "Pantalla de 17 pulgadas a 165 Hz, 32 GB de memoria RAM y refrigeración por cámara de vapor."));
        productos.save(producto("Mousepad XL Zone Grid", accesorios, 12990, 9990, 30, "6 meses",
                "/assets/img/portada.jpg",
                "Superficie de 900 x 400 mm con base antideslizante y bordes cosidos que evitan el desgaste."));
        productos.save(producto("Audífonos Zone H3 Surround", accesorios, 49990, null, 8, "12 meses",
                "/assets/img/portada.jpg",
                "Sonido envolvente 7.1 por software, micrófono retráctil con cancelación de ruido y almohadillas de espuma viscoelástica."));
    }

    private void sembrarUsuarios(UsuarioRepositorio usuarios, PasswordEncoder codificador) {
        Usuario administrador = new Usuario();
        administrador.setNombre("Patricio Ormazabal");
        administrador.setCorreo("admin@thezone.cl");
        administrador.setClave(codificador.encode("Admin123"));
        administrador.setRol(Rol.ADMINISTRADOR);
        administrador.setRegion("Metropolitana de Santiago");
        administrador.setComuna("Providencia");
        administrador.setCalle("Av. Providencia 1234");
        administrador.setActivo(true);
        usuarios.save(administrador);

        Usuario cliente = new Usuario();
        cliente.setNombre("Pedro Hacker");
        cliente.setCorreo("pedro@example.com");
        cliente.setClave(codificador.encode("Cliente123"));
        cliente.setRol(Rol.CLIENTE);
        cliente.setRegion("Metropolitana de Santiago");
        cliente.setComuna("Maipú");
        cliente.setCalle("Los Crisantemos 456");
        cliente.setActivo(true);
        usuarios.save(cliente);
    }

    private Categoria categoria(String slug, String nombre, String descripcion, String imagen) {
        Categoria categoria = new Categoria();
        categoria.setSlug(slug);
        categoria.setNombre(nombre);
        categoria.setDescripcion(descripcion);
        categoria.setImagen(imagen);
        return categoria;
    }

    private Producto producto(
            String nombre,
            Categoria categoria,
            Integer precio,
            Integer oferta,
            Integer stock,
            String garantia,
            String imagen,
            String descripcion) {

        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setCategoria(categoria);
        producto.setPrecio(precio);
        producto.setOferta(oferta);
        producto.setStock(stock);
        producto.setMarca("Zone");
        producto.setGarantia(garantia);
        producto.setImagen(imagen);
        producto.setDescripcion(descripcion);
        return producto;
    }
}
