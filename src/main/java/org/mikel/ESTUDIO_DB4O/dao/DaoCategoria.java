package org.mikel.ESTUDIO_DB4O.dao;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import org.mikel.ESTUDIO_DB4O.modelo.Categoria;
import java.util.List;

public class DaoCategoria {

    public static void insertar(Categoria categoria, ObjectContainer db) {
        db.store(categoria);
    }

    public static Categoria buscarPorId(int id, ObjectContainer db) {
        // Crear un ejemplo de Categoria con el id
        Categoria ejemplo = new Categoria(id, null);
        // Realizar la consulta
        ObjectSet<Categoria> resultados = db.queryByExample(ejemplo);
        return resultados.isEmpty() ? null : resultados.next();
    }

    public static List<Categoria> listarTodos(ObjectContainer db) {
        return db.query(Categoria.class);
    }
}
