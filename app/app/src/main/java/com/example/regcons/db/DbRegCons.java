package com.example.regcons.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbRegCons extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "regcons.db";
    //private static final int DATABASE_VERSION = 1;
    private static final int DATABASE_VERSION = 2;

    // Tabla y columnas
    public static final String TABLE_USERS = "usuarios";
    public static final String COL_ID = "id";
    public static final String COL_USERNAME = "usuario";
    public static final String COL_PASSWORD = "password";
    public static final String COL_EMAIL = "email";
    public static final String TABLE_REPORTES = "reportes_seguridad";
    public static final String COL_REPORTE_ID = "id_reporte";
    public static final String COL_TIPO = "tipo_reporte";
    public static final String COL_DESCRIPCION = "descripcion";
    public static final String COL_SEVERIDAD = "severidad";
    public static final String COL_FECHA = "fecha_timestamp";
    public static final String COL_FOTOS_URIS = "fotos_uris";
    public static final String COL_SINCRONIZADO = "sincronizado";


    private static final String CREATE_USERS_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_USERS + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_USERNAME + " TEXT UNIQUE, " +
                    COL_PASSWORD + " TEXT, " +
                    COL_EMAIL + " TEXT" +
                    ")";

    private static final String CREATE_REPORTES_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_REPORTES + " (" +
                    COL_REPORTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_TIPO + " TEXT NOT NULL, " +
                    COL_DESCRIPCION + " TEXT, " +
                    COL_SEVERIDAD + " TEXT, " +
                    COL_FECHA + " INTEGER, " +
                    COL_FOTOS_URIS + " TEXT, " +
                    COL_SINCRONIZADO + " INTEGER DEFAULT 0" +
                    ")";

    public DbRegCons(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_REPORTES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORTES);
        onCreate(db);
    }

    // Insertar usuario
    public boolean insertarUsuario(String username, String password, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);
        values.put(COL_EMAIL, email);


        long result = db.insert(TABLE_USERS, null, values);
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

        return exists;
    }

    public long insertarReporte(String tipo, String descripcion, String severidad,
                                long fechaTimestamp, String fotosUris) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_TIPO, tipo);
        values.put(COL_DESCRIPCION, descripcion);
        values.put(COL_SEVERIDAD, severidad);
        values.put(COL_FECHA, fechaTimestamp);
        values.put(COL_FOTOS_URIS, fotosUris);
        values.put(COL_SINCRONIZADO, 0); // Por defecto, NO sincronizado

        // El método insert devuelve el ID de la fila insertada, o -1 si falla.
        return db.insert(TABLE_REPORTES, null, values);
    }

    public Cursor obtenerReportesPendientes() {
        SQLiteDatabase db = this.getReadableDatabase();

        // 1. Definimos las columnas que queremos consultar (opcional, pero buena práctica)
        String[] columns = {
                COL_REPORTE_ID,
                COL_TIPO,
                COL_DESCRIPCION,
                COL_SEVERIDAD,
                COL_FECHA,
                COL_FOTOS_URIS
        };

        // 2. Definimos la condición: SINCRONIZADO = 0
        String selection = COL_SINCRONIZADO + " = ?";
        String[] selectionArgs = {"0"};

        // 3. Ejecutamos la consulta
        Cursor cursor = db.query(
                TABLE_REPORTES,     // La tabla a consultar
                columns,            // Las columnas a devolver
                selection,          // La cláusula WHERE
                selectionArgs,      // Los valores para la cláusula WHERE
                null,               // GROUP BY
                null,               // HAVING
                COL_FECHA + " ASC"  // Ordenar por fecha, los más viejos primero
        );

        return cursor;
    }

    public int marcarReporteComoSincronizado(long idReporte) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_SINCRONIZADO, 1); // Establecer a 1

        // La cláusula WHERE: actualizar solo la fila con el ID proporcionado
        String whereClause = COL_REPORTE_ID + " = ?";
        String[] whereArgs = {String.valueOf(idReporte)};

        // Ejecutamos la actualización
        return db.update(
                TABLE_REPORTES, // La tabla
                values,         // Los nuevos valores
                whereClause,    // La cláusula WHERE
                whereArgs       // Los argumentos WHERE
        );
    }

    public int eliminarReporte(long idReporte) {
        SQLiteDatabase db = this.getWritableDatabase();

        // La cláusula WHERE: eliminar solo la fila con el ID proporcionado
        String whereClause = COL_REPORTE_ID + " = ?";
        String[] whereArgs = {String.valueOf(idReporte)};

        // Ejecutamos la eliminación
        return db.delete(
                TABLE_REPORTES, // La tabla
                whereClause,    // La cláusula WHERE
                whereArgs       // Los argumentos WHERE
        );
    }

    public int actualizarReporte(long idReporte, String nuevaDescripcion, String nuevaSeveridad) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_DESCRIPCION, nuevaDescripcion);
        values.put(COL_SEVERIDAD, nuevaSeveridad);

        // No cambiamos el estado de sincronización ni la fecha, solo el contenido

        String whereClause = COL_REPORTE_ID + " = ?";
        String[] whereArgs = {String.valueOf(idReporte)};

        return db.update(
                TABLE_REPORTES,
                values,
                whereClause,
                whereArgs
        );
    }
}
