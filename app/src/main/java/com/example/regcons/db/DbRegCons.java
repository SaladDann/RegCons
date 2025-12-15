package com.example.regcons.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DbRegCons extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "regcons.db";
    private static final int DATABASE_VERSION = 2; // Incrementa la versión

    // Tabla y columnas para usuarios
    public static final String TABLE_USERS = "usuarios";
    public static final String COL_ID = "id";
    public static final String COL_USERNAME = "usuario";
    public static final String COL_PASSWORD = "password";
    public static final String COL_EMAIL = "email";

    // Tabla para avances de obra
    public static final String TABLE_AVANCES = "avances";
    public static final String COL_AVANCE_ID = "id_avance";
    public static final String COL_AVANCE_FECHA = "fecha";
    public static final String COL_AVANCE_OBRA = "obra";
    public static final String COL_AVANCE_ACTIVIDAD = "actividad";
    public static final String COL_AVANCE_PORCENTAJE = "porcentaje";
    public static final String COL_AVANCE_DESCRIPCION = "descripcion";
    public static final String COL_AVANCE_HORAS = "horas_trabajadas";
    public static final String COL_AVANCE_FOTO = "foto";
    public static final String COL_AVANCE_USUARIO = "usuario";

    // Tabla para incidentes
    public static final String TABLE_INCIDENTES = "incidentes";
    public static final String COL_INCIDENTE_ID = "id_incidente";
    public static final String COL_INCIDENTE_FECHA = "fecha";
    public static final String COL_INCIDENTE_TIPO = "tipo_incidente";
    public static final String COL_INCIDENTE_UBICACION = "ubicacion";
    public static final String COL_INCIDENTE_DESCRIPCION = "descripcion";
    public static final String COL_INCIDENTE_GRAVEDAD = "gravedad";
    public static final String COL_INCIDENTE_ESTADO = "estado";
    public static final String COL_INCIDENTE_FOTO = "foto";
    public static final String COL_INCIDENTE_USUARIO = "usuario";

    // Tabla para obras
    public static final String TABLE_OBRAS = "obras";
    public static final String COL_OBRA_ID = "id_obra";
    public static final String COL_OBRA_NOMBRE = "nombre";
    public static final String COL_OBRA_DIRECCION = "direccion";
    public static final String COL_OBRA_CLIENTE = "cliente";
    public static final String COL_OBRA_FECHA_INICIO = "fecha_inicio";
    public static final String COL_OBRA_FECHA_FIN = "fecha_fin";
    public static final String COL_OBRA_PRESUPUESTO = "presupuesto";
    public static final String COL_OBRA_ESTADO = "estado";

    // Sentencias SQL para crear las tablas
    private static final String CREATE_USERS_TABLE =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_USERNAME + " TEXT UNIQUE NOT NULL, " +
                    COL_PASSWORD + " TEXT NOT NULL, " +
                    COL_EMAIL + " TEXT" +
                    ")";

    private static final String CREATE_OBRAS_TABLE =
            "CREATE TABLE " + TABLE_OBRAS + " (" +
                    COL_OBRA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_OBRA_NOMBRE + " TEXT NOT NULL, " +
                    COL_OBRA_DIRECCION + " TEXT, " +
                    COL_OBRA_CLIENTE + " TEXT, " +
                    COL_OBRA_FECHA_INICIO + " TEXT, " +
                    COL_OBRA_FECHA_FIN + " TEXT, " +
                    COL_OBRA_PRESUPUESTO + " REAL, " +
                    COL_OBRA_ESTADO + " TEXT DEFAULT 'ACTIVA'" +
                    ")";

    private static final String CREATE_AVANCES_TABLE =
            "CREATE TABLE " + TABLE_AVANCES + " (" +
                    COL_AVANCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_AVANCE_FECHA + " TEXT NOT NULL, " +
                    COL_AVANCE_OBRA + " TEXT NOT NULL, " +
                    COL_AVANCE_ACTIVIDAD + " TEXT NOT NULL, " +
                    COL_AVANCE_PORCENTAJE + " REAL NOT NULL, " +
                    COL_AVANCE_DESCRIPCION + " TEXT, " +
                    COL_AVANCE_HORAS + " REAL DEFAULT 0, " +
                    COL_AVANCE_FOTO + " TEXT, " +
                    COL_AVANCE_USUARIO + " TEXT, " +
                    "FOREIGN KEY(" + COL_AVANCE_OBRA + ") REFERENCES " + TABLE_OBRAS + "(" + COL_OBRA_NOMBRE + ")" +
                    ")";

    private static final String CREATE_INCIDENTES_TABLE =
            "CREATE TABLE " + TABLE_INCIDENTES + " (" +
                    COL_INCIDENTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_INCIDENTE_FECHA + " TEXT NOT NULL, " +
                    COL_INCIDENTE_TIPO + " TEXT NOT NULL, " +
                    COL_INCIDENTE_UBICACION + " TEXT, " +
                    COL_INCIDENTE_DESCRIPCION + " TEXT NOT NULL, " +
                    COL_INCIDENTE_GRAVEDAD + " TEXT DEFAULT 'LEVE', " +
                    COL_INCIDENTE_ESTADO + " TEXT DEFAULT 'PENDIENTE', " +
                    COL_INCIDENTE_FOTO + " TEXT, " +
                    COL_INCIDENTE_USUARIO + " TEXT" +
                    ")";

    public DbRegCons(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_OBRAS_TABLE);
        db.execSQL(CREATE_AVANCES_TABLE);
        db.execSQL(CREATE_INCIDENTES_TABLE);

        // Insertar datos de prueba
        insertarDatosIniciales(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Si actualizas la versión, elimina las tablas antiguas
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OBRAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AVANCES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INCIDENTES);
        onCreate(db);
    }

    private void insertarDatosIniciales(SQLiteDatabase db) {
        // Insertar usuario por defecto
        ContentValues userValues = new ContentValues();
        userValues.put(COL_USERNAME, "admin");
        userValues.put(COL_PASSWORD, "admin123");
        userValues.put(COL_EMAIL, "admin@constructor.com");
        db.insert(TABLE_USERS, null, userValues);

        // Insertar obras de ejemplo
        ContentValues obra1 = new ContentValues();
        obra1.put(COL_OBRA_NOMBRE, "Torre Residencial Centro");
        obra1.put(COL_OBRA_DIRECCION, "Av. Principal 123, Centro");
        obra1.put(COL_OBRA_CLIENTE, "Constructora ABC");
        obra1.put(COL_OBRA_FECHA_INICIO, "2024-01-15");
        obra1.put(COL_OBRA_FECHA_FIN, "2024-12-20");
        obra1.put(COL_OBRA_PRESUPUESTO, 5000000);
        db.insert(TABLE_OBRAS, null, obra1);

        ContentValues obra2 = new ContentValues();
        obra2.put(COL_OBRA_NOMBRE, "Centro Comercial Norte");
        obra2.put(COL_OBRA_DIRECCION, "Calle Norte 456, Zona Industrial");
        obra2.put(COL_OBRA_CLIENTE, "Desarrolladora XYZ");
        obra2.put(COL_OBRA_FECHA_INICIO, "2024-02-01");
        obra2.put(COL_OBRA_FECHA_FIN, "2025-06-30");
        obra2.put(COL_OBRA_PRESUPUESTO, 8500000);
        db.insert(TABLE_OBRAS, null, obra2);

        // Insertar avances de ejemplo (últimos 7 días)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        String[] actividades = {
                "Cimentación", "Estructura", "Muros", "Instalaciones",
                "Acabados", "Pintura", "Instalación eléctrica"
        };

        for (int i = 6; i >= 0; i--) {
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            String fecha = sdf.format(calendar.getTime());

            ContentValues avance = new ContentValues();
            avance.put(COL_AVANCE_FECHA, fecha);
            avance.put(COL_AVANCE_OBRA, "Torre Residencial Centro");
            avance.put(COL_AVANCE_ACTIVIDAD, actividades[i % actividades.length]);
            avance.put(COL_AVANCE_PORCENTAJE, (i + 1) * 5.0); // 5% a 35%
            avance.put(COL_AVANCE_DESCRIPCION, "Avance del día " + fecha);
            avance.put(COL_AVANCE_HORAS, 8.0);
            avance.put(COL_AVANCE_USUARIO, "admin");
            db.insert(TABLE_AVANCES, null, avance);
        }

        // Insertar incidentes de ejemplo
        String[] tiposIncidentes = {"Caída", "Golpe", "Quemadura", "Electrocución", "Atrapamiento"};
        String[] nivelesGravedad = {"LEVE", "MODERADO", "GRAVE"};

        for (int i = 0; i < 5; i++) {
            ContentValues incidente = new ContentValues();
            calendar.add(Calendar.DAY_OF_YEAR, -i);
            incidente.put(COL_INCIDENTE_FECHA, sdf.format(calendar.getTime()));
            incidente.put(COL_INCIDENTE_TIPO, tiposIncidentes[i]);
            incidente.put(COL_INCIDENTE_UBICACION, "Piso " + (i + 1));
            incidente.put(COL_INCIDENTE_DESCRIPCION, "Descripción del incidente " + (i + 1));
            incidente.put(COL_INCIDENTE_GRAVEDAD, nivelesGravedad[i % nivelesGravedad.length]);
            incidente.put(COL_INCIDENTE_ESTADO, i < 2 ? "PENDIENTE" : "RESUELTO");
            incidente.put(COL_INCIDENTE_USUARIO, "admin");
            db.insert(TABLE_INCIDENTES, null, incidente);
        }
    }

    // Métodos existentes
    public boolean insertarUsuario(String username, String password, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);
        values.put(COL_EMAIL, email);

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public boolean verificarUsuario(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT 1 FROM " + TABLE_USERS +
                " WHERE " + COL_USERNAME + " = ? AND " + COL_PASSWORD + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{username, password});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    // ========== MÉTODOS PARA REPORTES ==========

    // Obtener avance por día
    public List<Object[]> obtenerAvancePorDia(Date fechaInicio, Date fechaFin) {
        List<Object[]> resultados = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        String query = "SELECT " + COL_AVANCE_FECHA + ", AVG(" + COL_AVANCE_PORCENTAJE + ") " +
                "FROM " + TABLE_AVANCES + " " +
                "WHERE " + COL_AVANCE_FECHA + " BETWEEN ? AND ? " +
                "GROUP BY " + COL_AVANCE_FECHA + " " +
                "ORDER BY " + COL_AVANCE_FECHA;

        Cursor cursor = db.rawQuery(query, new String[]{
                sdf.format(fechaInicio),
                sdf.format(fechaFin)
        });

        try {
            while (cursor.moveToNext()) {
                String fechaStr = cursor.getString(0);
                double avance = cursor.getDouble(1);
                Date fecha = sdf.parse(fechaStr);
                resultados.add(new Object[]{fecha, avance});
            }
        } catch (Exception e) {
            Log.e("DbRegCons", "Error parsing date", e);
        } finally {
            cursor.close();
        }

        return resultados;
    }

    // Obtener incidentes por tipo
    public List<Object[]> obtenerIncidentesPorTipo(Date fechaInicio, Date fechaFin) {
        List<Object[]> resultados = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        String query = "SELECT " + COL_INCIDENTE_GRAVEDAD + ", COUNT(*) " +
                "FROM " + TABLE_INCIDENTES + " " +
                "WHERE " + COL_INCIDENTE_FECHA + " BETWEEN ? AND ? " +
                "GROUP BY " + COL_INCIDENTE_GRAVEDAD + " " +
                "ORDER BY COUNT(*) DESC";

        Cursor cursor = db.rawQuery(query, new String[]{
                sdf.format(fechaInicio),
                sdf.format(fechaFin)
        });

        while (cursor.moveToNext()) {
            String tipo = cursor.getString(0);
            int cantidad = cursor.getInt(1);
            resultados.add(new Object[]{tipo, cantidad});
        }

        cursor.close();
        return resultados;
    }

    // Obtener avance promedio
    public double obtenerAvancePromedio(Date fechaInicio, Date fechaFin) {
        SQLiteDatabase db = this.getReadableDatabase();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        String query = "SELECT AVG(" + COL_AVANCE_PORCENTAJE + ") " +
                "FROM " + TABLE_AVANCES + " " +
                "WHERE " + COL_AVANCE_FECHA + " BETWEEN ? AND ?";

        Cursor cursor = db.rawQuery(query, new String[]{
                sdf.format(fechaInicio),
                sdf.format(fechaFin)
        });

        double promedio = 0;
        if (cursor.moveToFirst()) {
            promedio = cursor.getDouble(0);
        }

        cursor.close();
        return promedio;
    }
    // Agrega estos métodos a tu DbRegCons.java:

    // Obtener incidentes críticos
    public int obtenerIncidentesCriticos(Date fechaInicio, Date fechaFin) {
        SQLiteDatabase db = this.getReadableDatabase();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        String query = "SELECT COUNT(*) FROM " + TABLE_INCIDENTES +
                " WHERE " + COL_INCIDENTE_FECHA + " BETWEEN ? AND ?" +
                " AND " + COL_INCIDENTE_GRAVEDAD + " = 'GRAVE'";

        Cursor cursor = db.rawQuery(query, new String[]{
                sdf.format(fechaInicio),
                sdf.format(fechaFin)
        });

        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }

        cursor.close();
        return total;
    }

    // Obtener avances por obra específica
    public double obtenerAvancePorObra(Date fechaInicio, Date fechaFin, String obra) {
        SQLiteDatabase db = this.getReadableDatabase();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        String query = "SELECT AVG(" + COL_AVANCE_PORCENTAJE + ") FROM " + TABLE_AVANCES +
                " WHERE " + COL_AVANCE_FECHA + " BETWEEN ? AND ?" +
                " AND " + COL_AVANCE_OBRA + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{
                sdf.format(fechaInicio),
                sdf.format(fechaFin),
                obra
        });

        double promedio = 0;
        if (cursor.moveToFirst()) {
            promedio = cursor.getDouble(0);
        }

        cursor.close();
        return promedio;
    }

    // CRUD: Actualizar estado de incidente (para marcar como resuelto)
    public boolean actualizarEstadoIncidente(int idIncidente, String nuevoEstado) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_INCIDENTE_ESTADO, nuevoEstado);

        int resultado = db.update(TABLE_INCIDENTES, values,
                COL_INCIDENTE_ID + " = ?", new String[]{String.valueOf(idIncidente)});

        return resultado > 0;
    }

    // CRUD: Eliminar avance
    public boolean eliminarAvance(int idAvance) {
        SQLiteDatabase db = this.getWritableDatabase();
        int resultado = db.delete(TABLE_AVANCES,
                COL_AVANCE_ID + " = ?", new String[]{String.valueOf(idAvance)});

        return resultado > 0;
    }

    // CRUD: Insertar nuevo reporte
    public long insertarReporte(String tipo, String descripcion, Date fecha) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        values.put("tipo_reporte", tipo);
        values.put("descripcion", descripcion);
        values.put("fecha_reporte", sdf.format(fecha));

        return db.insert("reportes_generados", null, values);
    }
    // Obtener total de incidentes
    public int obtenerTotalIncidentes(Date fechaInicio, Date fechaFin) {
        SQLiteDatabase db = this.getReadableDatabase();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        String query = "SELECT COUNT(*) " +
                "FROM " + TABLE_INCIDENTES + " " +
                "WHERE " + COL_INCIDENTE_FECHA + " BETWEEN ? AND ?";

        Cursor cursor = db.rawQuery(query, new String[]{
                sdf.format(fechaInicio),
                sdf.format(fechaFin)
        });

        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }

        cursor.close();
        return total;
    }

    // Obtener avance semanal
    public double obtenerAvanceSemanal(Date fechaInicio, Date fechaFin) {
        SQLiteDatabase db = this.getReadableDatabase();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        String query = "SELECT SUM(" + COL_AVANCE_PORCENTAJE + " * " + COL_AVANCE_HORAS + ") / SUM(" + COL_AVANCE_HORAS + ") " +
                "FROM " + TABLE_AVANCES + " " +
                "WHERE " + COL_AVANCE_FECHA + " BETWEEN ? AND ? " +
                "AND " + COL_AVANCE_HORAS + " > 0";

        Cursor cursor = db.rawQuery(query, new String[]{
                sdf.format(fechaInicio),
                sdf.format(fechaFin)
        });

        double avance = 0;
        if (cursor.moveToFirst()) {
            avance = cursor.getDouble(0);
        }

        cursor.close();
        return avance;
    }

    // Obtener riesgos activos (incidentes pendientes)
    public int obtenerRiesgosActivos() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_INCIDENTES +
                " WHERE " + COL_INCIDENTE_ESTADO + " = 'PENDIENTE'";

        Cursor cursor = db.rawQuery(query, null);

        int activos = 0;
        if (cursor.moveToFirst()) {
            activos = cursor.getInt(0);
        }

        cursor.close();
        return activos;
    }

    // Obtener lista de obras
    public List<String> obtenerListaObras() {
        List<String> obras = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + COL_OBRA_NOMBRE + " FROM " + TABLE_OBRAS +
                " WHERE " + COL_OBRA_ESTADO + " = 'ACTIVA'";

        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            obras.add(cursor.getString(0));
        }

        cursor.close();
        return obras;
    }

    // Obtener detalles de una obra específica
    public Cursor obtenerDetalleObra(String nombreObra) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_OBRAS +
                " WHERE " + COL_OBRA_NOMBRE + " = ?";

        return db.rawQuery(query, new String[]{nombreObra});
    }
}