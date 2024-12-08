package org.mikel.ESTUDIO_DB4O.bbdd;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;

public class ConexionBBDD {

    private static ObjectContainer db = null;

    public static ObjectContainer getConnection() {
        if (db == null) {
            try {
                // Configurar y abrir conexión a la base de datos db4o
                db = Db4oEmbedded.openFile("database.db4o");
                System.out.println("Conexión a db4o creada");
            } catch (Exception e) {
                throw new RuntimeException("Error al conectar con db4o: " + e.getMessage());
            }
        }
        return db;
    }

    public static void closeConnection() {
        if (db != null) {
            db.close();
            System.out.println("Conexión a db4o cerrada");
        }
    }

  
}
