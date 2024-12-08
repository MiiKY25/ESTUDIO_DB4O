package org.mikel.ESTUDIO_DB4O;

import com.db4o.ObjectContainer;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.mikel.ESTUDIO_DB4O.bbdd.ConexionBBDD;
import org.mikel.ESTUDIO_DB4O.dao.DaoCategoria;
import org.mikel.ESTUDIO_DB4O.dao.DaoCliente;
import org.mikel.ESTUDIO_DB4O.dao.DaoProducto;
import org.mikel.ESTUDIO_DB4O.modelo.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {


    public static void mostrarMenu() {
        ObjectContainer db=new ConexionBBDD().getConnection() ;
        Scanner scanner = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("1.\tCargar datos desde CSV");
            System.out.println("2:\tListar Productos inferior precio");
            System.out.println("3:\tEditar un producto");
            System.out.println("4:\tEliminar");
            System.out.println("5:\tInsertar un cliente para un producto");
            System.out.println("0:\tSALIR:");

            System.out.print("Elige una opción: ");

            while (!scanner.hasNextInt()) {
                System.out.println("Entrada no válida. Por favor, introduce un número.");
                scanner.next(); // Limpiar entrada inválida
                System.out.print("Elige una opción: ");
            }

            opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    File file = null;
                    try {
                        file = new File(Main.class.getResource("/csv/datos.csv").toURI());
                        cargarDatos(file, db);
                    } catch (Exception e) {
                        System.err.println("Error al cargar el archivo CSV: " + e.getMessage());
                    }
                    break;
                case 2:
                    listarProductosPrecio(2000,db);
                    break;
                case 3:
                    editarProducto(scanner,db);
                    break;
                case 4:
                    eliminarProducto(scanner,db);
                    break;
                case 5:
                    anadirClienteAProducto(scanner, db);
                    break;
                case 0:
                    System.out.println("Saliendo del programa...");
                    break;
                default:
                    System.out.println("Opción no válida. Por favor, elige una opción entre 0 y 5.");
            }

        } while (opcion != 0);

        scanner.close();
    }

    public static void cargarDatos(File f, ObjectContainer db) {
        try (CSVReader reader = new CSVReader(new FileReader(f))) {
            List<String[]> lineas = reader.readAll();
            lineas.remove(0); // Remover cabecera

            for (String[] linea : lineas) {
                int idProducto = Integer.parseInt(linea[0]);
                String nombreProducto = linea[1];
                double precio = Double.parseDouble(linea[2]);
                int idCategoria = Integer.parseInt(linea[3]);
                String nombreCategoria = linea[4];
                int idCliente = Integer.parseInt(linea[5]);
                String nombreCliente = linea[6];

                // Buscar o crear la categoría
                Categoria categoria = DaoCategoria.buscarPorId(idCategoria, db);
                if (categoria == null) {
                    categoria = new Categoria(idCategoria, nombreCategoria);
                    DaoCategoria.insertar(categoria, db);
                }

                // Buscar o crear el producto
                Producto producto = DaoProducto.buscarPorId(idProducto, db);
                if (producto == null) {
                    producto = new Producto(idProducto, nombreProducto, precio, categoria);
                    DaoProducto.insertar(producto, db);
                }

                // Buscar o crear el cliente
                Cliente cliente = DaoCliente.buscarPorId(idCliente, db);
                if (cliente == null) {
                    cliente = new Cliente(idCliente, nombreCliente, producto);
                    DaoCliente.insertar(cliente, db);
                }
            }

            System.out.println("Datos cargados correctamente desde el CSV.");

        } catch (FileNotFoundException e) {
            System.err.println("Archivo CSV no encontrado: " + e.getMessage());
        } catch (IOException | CsvException e) {
            System.err.println("Error al leer el archivo CSV: " + e.getMessage());
        }
    }

    public static void listarProductosPrecio(double precio, ObjectContainer db) {
        // Obtener todos los productos desde la base de datos
        List<Producto> productos = DaoProducto.listarTodos(db);

        // Filtrar los productos con precio inferior al recibido
        System.out.println("-------------------");
        System.out.println("PRODUCTOS CON PRECIO INFERIOR A: " + precio);

        boolean encontrado = false;
        for (Producto producto : productos) {
            if (producto.getPrecio() < precio) {
                // Obtener el nombre de la categoría asociada al producto
                Categoria categoria = producto.getCategoria();
                String nombreCategoria = (categoria != null) ? categoria.getNombre() : "Sin categoría";

                // Obtener los clientes asociados al producto
                List<Cliente> clientes = DaoCliente.buscarPorProducto(producto, db);

                // Mostrar la información del producto
                System.out.println("ID: " + producto.getId() + " | Nombre: " + producto.getNombre() + " | Precio: " + producto.getPrecio() + " | Categoría: " + nombreCategoria);

                // Mostrar los clientes asociados
                if (clientes.isEmpty()) {
                    System.out.println("   Sin clientes asociados.");
                } else {
                    System.out.println("   Clientes asociados:");
                    for (Cliente cliente : clientes) {
                        System.out.println("      - ID Cliente: " + cliente.getId() + " | Nombre: " + cliente.getNombre());
                    }
                }

                encontrado = true;
            }
        }

        if (!encontrado) {
            System.out.println("No se encontraron productos con precio inferior a " + precio);
        }
    }



    public static void editarProducto(Scanner scanner, ObjectContainer db) {
        // Solicitar al usuario el ID del producto que desea editar
        System.out.print("Introduce el ID del producto a editar: ");
        int idProducto = scanner.nextInt();

        // Buscar el producto por su ID
        Producto producto = DaoProducto.buscarPorId(idProducto, db);

        if (producto != null) {
            // Mostrar los detalles actuales del producto
            System.out.println("Producto encontrado:");
            System.out.println("ID: " + producto.getId() + " | Nombre: " + producto.getNombre() + " | Precio: " + producto.getPrecio());

            // Solicitar al usuario los nuevos valores
            scanner.nextLine();  // Limpiar el buffer del scanner
            System.out.print("Introduce el nuevo nombre del producto (deja en blanco para no cambiarlo): ");
            String nuevoNombre = scanner.nextLine();
            if (!nuevoNombre.isEmpty()) {
                producto.setNombre(nuevoNombre);
            }

            System.out.print("Introduce el nuevo precio del producto (deja en blanco para no cambiarlo): ");
            String nuevoPrecio = scanner.nextLine();
            if (!nuevoPrecio.isEmpty()) {
                try {
                    producto.setPrecio(Double.parseDouble(nuevoPrecio));
                } catch (NumberFormatException e) {
                    System.err.println("Precio no válido. No se modificó el precio.");
                }
            }

            // Mostrar las categorías disponibles
            List<Categoria> categorias = DaoCategoria.listarTodos(db);
            System.out.println("Categorías disponibles:");
            for (int i = 0; i < categorias.size(); i++) {
                System.out.println((i + 1) + ". " + categorias.get(i).getNombre());
            }

            // Solicitar al usuario que elija una categoría
            System.out.print("Elige el número de la categoría para asignar al producto: ");
            int numCategoria = scanner.nextInt();

            if (numCategoria > 0 && numCategoria <= categorias.size()) {
                Categoria categoriaSeleccionada = categorias.get(numCategoria - 1);  // Restamos 1 para obtener el índice correcto
                producto.setCategoria(categoriaSeleccionada);
                System.out.println("Categoría actualizada a: " + categoriaSeleccionada.getNombre());
            } else {
                System.out.println("Opción de categoría no válida.");
            }

            // Guardar el producto actualizado en la base de datos
            DaoProducto.insertar(producto, db);
            System.out.println("Producto actualizado correctamente.");
        } else {
            System.out.println("Producto no encontrado con el ID: " + idProducto);
        }
    }

    public static void eliminarProducto(Scanner scanner, ObjectContainer db) {
        // Obtener todos los productos desde la base de datos
        List<Producto> productos = DaoProducto.listarTodos(db);

        // Verificar si hay productos
        if (productos.isEmpty()) {
            System.out.println("No hay productos disponibles para eliminar.");
            return;
        }

        // Mostrar los productos con su nombre, precio y categoría, asociados a un número
        System.out.println("-------------------");
        System.out.println("PRODUCTOS DISPONIBLES PARA ELIMINAR:");
        for (int i = 0; i < productos.size(); i++) {
            Producto producto = productos.get(i);
            Categoria categoria = producto.getCategoria();
            String nombreCategoria = (categoria != null) ? categoria.getNombre() : "Sin categoría";
            System.out.println((i + 1) + ". Nombre: " + producto.getNombre() + " | Precio: " + producto.getPrecio() + " | Categoría: " + nombreCategoria);
        }

        // Solicitar al usuario el número del producto a eliminar
        System.out.print("Introduce el número del producto que deseas eliminar: ");
        int numeroProducto = scanner.nextInt();

        if (numeroProducto > 0 && numeroProducto <= productos.size()) {
            Producto productoAEliminar = productos.get(numeroProducto - 1); // Restamos 1 para obtener el índice correcto

            // Eliminar el producto de la base de datos
            DaoProducto.eliminar(productoAEliminar, db);
            System.out.println("Producto '" + productoAEliminar.getNombre() + "' eliminado correctamente.");
        } else {
            System.out.println("Opción no válida. No se eliminó ningún producto.");
        }
    }

    public static void anadirClienteAProducto(Scanner scanner, ObjectContainer db) {
        // Obtener todos los productos desde la base de datos
        List<Producto> productos = DaoProducto.listarTodos(db);

        if (productos.isEmpty()) {
            System.out.println("No hay productos disponibles para asociar clientes.");
            return;
        }

        System.out.println("-------------------");
        System.out.println("PRODUCTOS DISPONIBLES:");
        for (int i = 0; i < productos.size(); i++) {
            Producto producto = productos.get(i);
            Categoria categoria = producto.getCategoria();
            String nombreCategoria = (categoria != null) ? categoria.getNombre() : "Sin categoría";
            System.out.println((i + 1) + ". Nombre: " + producto.getNombre() + " | Precio: " + producto.getPrecio() + " | Categoría: " + nombreCategoria);
        }

        System.out.print("Introduce el número del producto al que deseas añadir un cliente: ");
        int numeroProducto = scanner.nextInt();

        if (numeroProducto > 0 && numeroProducto <= productos.size()) {
            Producto productoSeleccionado = productos.get(numeroProducto - 1);

            List<Cliente> clientes = DaoCliente.listarTodos(db);

            if (clientes.isEmpty()) {
                System.out.println("No hay clientes disponibles.");
                return;
            }

            System.out.println("-------------------");
            System.out.println("CLIENTES DISPONIBLES:");
            for (int i = 0; i < clientes.size(); i++) {
                Cliente cliente = clientes.get(i);
                System.out.println((i + 1) + ". ID: " + cliente.getId() + " | Nombre: " + cliente.getNombre());
            }

            System.out.print("Introduce el número del cliente que deseas asociar al producto: ");
            int numeroCliente = scanner.nextInt();

            if (numeroCliente > 0 && numeroCliente <= clientes.size()) {
                Cliente clienteSeleccionado = clientes.get(numeroCliente - 1);

                // Asociar el cliente al producto
                clienteSeleccionado.setProducto(productoSeleccionado);

                // Llamar al DAO para guardar los cambios
                DaoCliente.insertar(clienteSeleccionado, db);

                System.out.println("Cliente '" + clienteSeleccionado.getNombre() + "' asociado al producto '" + productoSeleccionado.getNombre() + "' correctamente.");
            } else {
                System.out.println("Opción de cliente no válida. No se realizó ninguna asociación.");
            }
        } else {
            System.out.println("Opción de producto no válida. No se realizó ninguna asociación.");
        }
    }

    public static void main(String[] args) {
        mostrarMenu();
    }
}