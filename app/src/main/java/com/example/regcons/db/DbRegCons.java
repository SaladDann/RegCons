package com.example.regcons.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbRegCons extends SQLiteOpenHelper {

    // Constantes para la base de datos
    private static final String DATABASE_NAME = "regcons.db";
    private static final int DATABASE_VERSION = 1;
    public static final String ESTADO_PENDIENTE = "PENDIENTE";

    // Tabla Usuario
    public static final String TABLE_USERS = "usuarios";
    public static final String COL_ID = "id";
    public static final String COL_USERNAME = "usuario";
    public static final String COL_PASSWORD = "password";
    public static final String COL_EMAIL = "email";

    // Tabla Obras
    public static final String TABLE_OBRAS = "obras";
    public static final String COL_OBRA_ID = "obra_id";
    public static final String COL_OBRA_NOMBRE = "nombre";
    public static final String COL_OBRA_ESTADO = "estado";
    public static final String COL_OBRA_DESCRIPCION = "descripcion";

    // Tabla Avance
    public static final String TABLE_AVANCES = "avances";
    public static final String COL_AVANCE_ID = "avance_id";
    public static final String COL_AVANCE_OBRA_ID = "id_obra";
    public static final String COL_AVANCE_NOMBRE = "nombre";
    public static final String COL_AVANCE_FINALIZADO = "finalizado";
    public static final String COL_AVANCE_FOTO = "foto_path";
    public static final String COL_AVANCE_FECHA = "fecha";
    public static final String COL_AVANCE_DESCRIPCION = "descripcion";



    // Sentencia SQL para crear la tablas
    // Tabla Usuarios
    private static final String CREATE_USERS_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_USERS + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_USERNAME + " TEXT UNIQUE, " +
                    COL_PASSWORD + " TEXT, " +
                    COL_EMAIL + " TEXT" +
                    ")";

    // Tabla Obras
    private static final String CREATE_OBRAS_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_OBRAS + " (" +
                    COL_OBRA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_OBRA_NOMBRE + " TEXT NOT NULL, " +
                    COL_OBRA_ESTADO + " TEXT CHECK(" + COL_OBRA_ESTADO +
                    " IN ('PENDIENTE','FINALIZADA')) NOT NULL DEFAULT 'PENDIENTE', " +
                    COL_OBRA_DESCRIPCION + " TEXT" +
                    ")";

    // Tabla Avances
    private static final String CREATE_AVANCES_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_AVANCES + " (" +
                    COL_AVANCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_AVANCE_OBRA_ID + " INTEGER NOT NULL, " +
                    COL_AVANCE_NOMBRE + " TEXT NOT NULL, " +
                    COL_AVANCE_FINALIZADO + " INTEGER DEFAULT 0, " +
                    COL_AVANCE_FOTO + " TEXT, " +
                    COL_AVANCE_FECHA + " TEXT, " + // <-- ¡NUEVO!
                    COL_AVANCE_DESCRIPCION + " TEXT, " + // <-- ¡NUEVO!
                    "FOREIGN KEY(" + COL_AVANCE_OBRA_ID + ") REFERENCES " +
                    TABLE_OBRAS + "(" + COL_OBRA_ID + ")" +
                    ")";

    // Constructor
    public DbRegCons(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Métodos de SQLiteOpenHelper
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_OBRAS_TABLE);
        db.execSQL(CREATE_AVANCES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AVANCES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OBRAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Métodos personalizados
    // Insertar usuario
    public boolean insertarUsuario(String username, String password, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);
        values.put(COL_EMAIL, email);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    // Verificar login
    public boolean verificarUsuario(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT 1 FROM " + TABLE_USERS +
                " WHERE " + COL_USERNAME + " = ? AND " + COL_PASSWORD + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{username, password});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();

        return exists;
    }

    // CRUDS tabla obras
    public long insertarObra(String nombre, String descripcion) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_OBRA_NOMBRE, nombre);
        values.put(COL_OBRA_DESCRIPCION, descripcion);
        values.put(COL_OBRA_ESTADO, ESTADO_PENDIENTE);

        long id = db.insert(TABLE_OBRAS, null, values);
        db.close();

        return id;
    }

    public Cursor obtenerObras() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_OBRAS, null);
    }
    public Cursor obtenerObraPorId(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_OBRAS + " WHERE " + COL_OBRA_ID + " = ?", new String[]{String.valueOf(id)});
    }
    public Cursor buscarObrasPorNombre(String texto) {
        return getReadableDatabase().rawQuery(
                "SELECT * FROM obras WHERE nombre LIKE ?",
                new String[]{"%" + texto + "%"}
        );
    }
    public int actualizarObra(int id, ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Ejecutar la actualización con los valores que se pasen en 'values'
        int rows = db.update(
                TABLE_OBRAS,
                values,
                COL_OBRA_ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        db.close();
        return rows;
    }

    public int eliminarObra(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_OBRAS, COL_OBRA_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    public Cursor obtenerObrasPendientes() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COL_OBRA_ESTADO + " = ?";
        String[] selectionArgs = {ESTADO_PENDIENTE};

        Cursor cursor = db.query(
                TABLE_OBRAS,
                new String[]{COL_OBRA_ID, COL_OBRA_NOMBRE, COL_OBRA_ESTADO, COL_OBRA_DESCRIPCION},
                selection,
                selectionArgs,
                null,
                null,
                COL_OBRA_ID + " ASC"
        );

        return cursor;
    }

    // CRUDS tabla avances
    public long insertarAvance(int idObra, String nombre, String descripcion, boolean finalizado, String fotoPath, String fecha) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_AVANCE_OBRA_ID, idObra);
        values.put(COL_AVANCE_NOMBRE, nombre);
        values.put(COL_AVANCE_DESCRIPCION, descripcion); // <-- ¡NUEVO!
        values.put(COL_AVANCE_FECHA, fecha);             // <-- ¡NUEVO!
        values.put(COL_AVANCE_FINALIZADO, finalizado ? 1 : 0);
        values.put(COL_AVANCE_FOTO, fotoPath);

        long id = db.insert(TABLE_AVANCES, null, values);
        db.close();
        return id;
    }
    public Cursor obtenerAvancePorId(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_AVANCES + " WHERE " + COL_AVANCE_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public Cursor obtenerAvancesPorObra(int idObra) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_AVANCES +
                        " WHERE " + COL_AVANCE_OBRA_ID + " = ?",
                new String[]{String.valueOf(idObra)}
        );
    }

    public int actualizarAvance(int id, int idObra, String nombre, boolean finalizado, String fotoPath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_AVANCE_OBRA_ID, idObra);
        values.put(COL_AVANCE_NOMBRE, nombre);
        values.put(COL_AVANCE_FINALIZADO, finalizado ? 1 : 0);
        values.put(COL_AVANCE_FOTO, fotoPath);
        int rows = db.update(TABLE_AVANCES, values, COL_AVANCE_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }
    public int eliminarAvance(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_AVANCES, COL_AVANCE_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    public void eliminarAvancesPorObra(int idObra) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_AVANCES, COL_AVANCE_OBRA_ID+ "=?",
                new String[]{String.valueOf(idObra)});
    }





    //metodo para calcular el porcentaje de avance de una obra y usarlo en las barras de progreso
    public int obtenerPorcentajeAvance(int idObra) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Obtener el total de avances y el número de avances finalizados
        Cursor total = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_AVANCES +
                        " WHERE " + COL_AVANCE_OBRA_ID + " = ?",
                new String[]{String.valueOf(idObra)}
        );
        // Obtener el número de avances finalizados
        Cursor finalizados = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_AVANCES +
                        " WHERE " + COL_AVANCE_OBRA_ID + " = ? AND " +
                        COL_AVANCE_FINALIZADO + " = 1",
                new String[]{String.valueOf(idObra)}
        );
        // Calcular el porcentaje de avance
        int totalCount = 0;
        if (total.moveToFirst()) {
            totalCount = total.getInt(0);
        }
        // Si no hay avances, devolver 0
        if (totalCount == 0) {
            total.close();
            finalizados.close();
            return 0;
        }
        // Si hay avances, obtener el número de avances finalizados
        int doneCount = 0;
        if (finalizados.moveToFirst()) {
            doneCount = finalizados.getInt(0);
        }

        total.close();
        finalizados.close();
        double porcentaje = ((double) doneCount / totalCount) * 100;

        // Redondear el porcentaje a dos decimales
        return (int) Math.round(porcentaje);
    }

}
