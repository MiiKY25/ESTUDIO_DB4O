package org.mikel.ESTUDIO_DB4O.dao;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import org.mikel.ESTUDIO_DB4O.modelo.Producto;
import java.util.List;

public class DaoProducto {

    public static void insertar(Producto producto, ObjectContainer db) {
        db.store(producto);
    }

    public static Producto buscarPorId(int id, ObjectContainer db) {
        // Crear un ejemplo de Producto con el id
        Producto ejemplo = new Producto(id, null, 0.0, null);
        // Realizar la consulta
        ObjectSet<Producto> resultados = db.queryByExample(ejemplo);
        return resultados.isEmpty() ? null : resultados.next();
    }

    public static List<Producto> listarTodos(ObjectContainer db) {
        return db.query(Producto.class);
    }

    public static void eliminar(Producto producto, ObjectContainer db) {
        db.delete(producto);
    }
}
