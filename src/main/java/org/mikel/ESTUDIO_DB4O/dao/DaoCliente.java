package org.mikel.ESTUDIO_DB4O.dao;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import org.mikel.ESTUDIO_DB4O.modelo.Cliente;
import org.mikel.ESTUDIO_DB4O.modelo.Producto;

import java.util.List;

public class DaoCliente {

    public static void insertar(Cliente cliente, ObjectContainer db) {
        db.store(cliente);
    }

    public static Cliente buscarPorId(int id, ObjectContainer db) {
        // Crear un ejemplo de Cliente con el id
        Cliente ejemplo = new Cliente(id, null, null);
        // Realizar la consulta
        ObjectSet<Cliente> resultados = db.queryByExample(ejemplo);
        return resultados.isEmpty() ? null : resultados.next();
    }

    public static List<Cliente> listarTodos(ObjectContainer db) {
        return db.query(Cliente.class);
    }

    public static List<Cliente> buscarPorProducto(Producto producto, ObjectContainer db) {
        return db.query(new Predicate<Cliente>() {
            @Override
            public boolean match(Cliente cliente) {
                return cliente.getProducto().equals(producto);
            }
        });
    }


}
