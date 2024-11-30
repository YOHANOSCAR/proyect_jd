package com.jennyduarte.sis.controllers;

import com.jennyduarte.sis.entity.Producto;
import com.jennyduarte.sis.service.ProductoService;
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

    @Value("${ruta.imagenes}") // Ruta configurada para las imágenes
    private String rutaImagenes;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
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
        return "productos/formulario";
    }

    // Guardar o actualizar un producto
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
                        .replaceAll("[^a-zA-Z0-9_]", ""); // Elimina caracteres especiales
                String extension = archivoImagen.getOriginalFilename()
                        .substring(archivoImagen.getOriginalFilename().lastIndexOf('.'));
                String nombreArchivo = nombreProducto + "_" + System.currentTimeMillis() + extension;
                Path rutaArchivo = Paths.get(rutaImagenes, nombreArchivo);

                // Eliminar imagen antigua si está editando el producto
                if (producto.getId() != null) {
                    Producto productoExistente = productoService.obtenerPorId(producto.getId());
                    if (productoExistente.getImagenUrl() != null) {
                        Path rutaImagenAntigua = Paths.get(rutaImagenes, productoExistente.getImagenUrl());
                        Files.deleteIfExists(rutaImagenAntigua);
                    }
                }

                // Guardar la imagen en el sistema de archivos
                archivoImagen.transferTo(rutaArchivo.toFile());
                producto.setImagenUrl(nombreArchivo); // Asocia el archivo al producto
            }

            // Guardar el producto en la base de datos
            productoService.guardar(producto);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al manejar la imagen: " + e.getMessage());
        }
        return "redirect:/productos";
    }

    // Mostrar formulario para editar un producto
    @GetMapping("/editar/{id}")
    public String editarProductoForm(@PathVariable Long id, Model model) {
        Producto producto = productoService.obtenerPorId(id);
        model.addAttribute("producto", producto);
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
            productoService.eliminar(id); // Elimina el producto
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al eliminar la imagen: " + e.getMessage());
        }
        return "redirect:/productos";
    }
}
