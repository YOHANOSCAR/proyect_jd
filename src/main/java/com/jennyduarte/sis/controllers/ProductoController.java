package com.jennyduarte.sis.controllers;

import com.jennyduarte.sis.entity.Categoria;
import com.jennyduarte.sis.entity.Contacto;
import com.jennyduarte.sis.entity.Producto;
import com.jennyduarte.sis.entity.Temporada;
import com.jennyduarte.sis.service.CategoriaService;
import com.jennyduarte.sis.service.ContactoService;
import com.jennyduarte.sis.service.ProductoService;
import com.jennyduarte.sis.service.TemporadaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final ContactoService contactoService;
    private final TemporadaService temporadaService;

    @Value("${ruta.imagenes}") // Ruta configurada para guardar imágenes
    private String rutaImagenes;

    public ProductoController(ProductoService productoService,
                              CategoriaService categoriaService,
                              ContactoService contactoService,
                              TemporadaService temporadaService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
        this.contactoService = contactoService;
        this.temporadaService = temporadaService;
    }

    // Listar productos
    @GetMapping
    public String listarProductos(Model model) {
        model.addAttribute("productos", productoService.listarTodos());
        return "productos/lista";
    }

    // Mostrar formulario para crear un producto
    @GetMapping("/crear")
    public String crearProductoForm(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaService.listarTodos());
        model.addAttribute("proveedores", contactoService.listarProveedores()); // Solo proveedores
        model.addAttribute("temporadas", temporadaService.listarTodos());
        return "productos/formulario";
    }

    // Guardar o actualizar un producto
    @PostMapping
    public String guardarProducto(@ModelAttribute Producto producto,
                                  @RequestParam(value = "archivoImagen", required = false) MultipartFile archivoImagen) {
        try {
            if (archivoImagen != null && !archivoImagen.isEmpty()) {
                // Generar un nombre único para la imagen
                String nombreArchivo = System.currentTimeMillis() + "_" + archivoImagen.getOriginalFilename();
                Path rutaArchivo = Paths.get(rutaImagenes, nombreArchivo);

                // Si está editando un producto, eliminar la imagen anterior si existe
                if (producto.getId() != null) {
                    Producto productoExistente = productoService.obtenerPorId(producto.getId());
                    if (productoExistente.getImagenUrl() != null) {
                        Path rutaImagenAntigua = Paths.get(rutaImagenes, productoExistente.getImagenUrl());
                        Files.deleteIfExists(rutaImagenAntigua);
                    }
                }

                // Guardar la nueva imagen en el sistema de archivos
                archivoImagen.transferTo(rutaArchivo.toFile());
                producto.setImagenUrl(nombreArchivo); // Asignar la URL de la nueva imagen al producto
            } else if (producto.getId() != null) {
                // Si no se sube una imagen nueva durante la edición, mantener la imagen anterior
                Producto productoExistente = productoService.obtenerPorId(producto.getId());
                producto.setImagenUrl(productoExistente.getImagenUrl());
            }

            productoService.guardar(producto);
        } catch (IOException e) {
            throw new RuntimeException("Error al manejar la imagen: " + e.getMessage());
        }
        return "redirect:/productos";
    }

    // Mostrar formulario para editar un producto
    @GetMapping("/editar/{id}")
    public String editarProductoForm(@PathVariable Long id, Model model) {
        Producto producto = productoService.obtenerPorId(id);
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaService.listarTodos());
        model.addAttribute("proveedores", contactoService.listarProveedores()); // Solo proveedores
        model.addAttribute("temporadas", temporadaService.listarTodos());
        return "productos/formulario";
    }

    // Eliminar un producto y su imagen asociada
    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id) {
        try {
            Producto producto = productoService.obtenerPorId(id);
            if (producto.getImagenUrl() != null) {
                Path rutaImagen = Paths.get(rutaImagenes, producto.getImagenUrl());
                Files.deleteIfExists(rutaImagen); // Elimina la imagen asociada
            }
            productoService.eliminar(id); // Elimina el producto de la base de datos
        } catch (IOException e) {
            throw new RuntimeException("Error al eliminar la imagen: " + e.getMessage());
        }
        return "redirect:/productos";
    }
}
