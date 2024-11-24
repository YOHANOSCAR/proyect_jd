package com.jennyduarte.sis.controllers;

import org.springframework.beans.factory.annotation.Value;
import com.jennyduarte.sis.entity.Producto;
import com.jennyduarte.sis.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;
    @Value("${ruta.imagenes}")
    private String rutaImagenes;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public String listarProductos(Model model) {
        model.addAttribute("productos", productoService.listarTodos());
        return "productos/lista";
    }

    @GetMapping("/crear")
    public String crearProductoForm(Model model) {
        model.addAttribute("producto", new Producto());
        return "productos/formulario";
    }

    @PostMapping

    public String guardarProducto(@ModelAttribute Producto producto,
                                  @RequestParam("archivoImagen") MultipartFile archivoImagen) {
        try {
            // Si hay una imagen nueva
            if (!archivoImagen.isEmpty()) {
                // Formatear el nombre del producto para usarlo como nombre del archivo
                String nombreProducto = producto.getNombre()
                        .trim()
                        .replaceAll(" ", "_")
                        .replaceAll("[^a-zA-Z0-9_]", "");
                // Agregar la extensión original del archivo
                String extension = archivoImagen.getOriginalFilename()
                        .substring(archivoImagen.getOriginalFilename().lastIndexOf('.'));
                // Nombre final del archivo
                String nombreArchivo = nombreProducto + extension;
                // Ruta donde se guardará la imagen
                Path rutaArchivo = Paths.get(rutaImagenes, nombreArchivo);

                // Eliminar imagen antigua si está editando el producto
                if (producto.getId() != null) {
                    Producto productoExistente = productoService.obtenerPorId(producto.getId());
                    if (productoExistente.getImagen() != null) {
                        Path rutaImagenAntigua = Paths.get(rutaImagenes, productoExistente.getImagen());
                        Files.deleteIfExists(rutaImagenAntigua);
                    }
                }

                // Guardar la imagen en el sistema de archivos
                archivoImagen.transferTo(rutaArchivo.toFile());
                // Establecer el nombre del archivo en el producto
                producto.setImagen(nombreArchivo);
            }

            // Guardar el producto
            productoService.guardar(producto);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al guardar la imagen: " + e.getMessage());
        }
        return "redirect:/productos";
    }
    @GetMapping("/editar/{id}")
    public String editarProductoForm(@PathVariable Long id, Model model) {
        Producto producto = productoService.obtenerPorId(id);
        model.addAttribute("producto", producto);
        return "productos/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id) {
        try {
            // Obtener el producto por ID
            Producto producto = productoService.obtenerPorId(id);
            if (producto.getImagen() != null) {
                // Ruta de la imagen asociada
                Path rutaImagen = Paths.get(rutaImagenes, producto.getImagen());
                // Elimina la imagen del sistema de archivos
                Files.deleteIfExists(rutaImagen);
            }
            // Elimina el producto de la base de datos
            productoService.eliminar(id);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al eliminar la imagen: " + e.getMessage());
        }
        return "redirect:/productos";
    }

}