package com.example.mugikorrekokamera;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "ZerrendaDB.db";
    private static final int DB_VERSION = 2;

    // Tabla USUARIOS
    public static final String TABLE_USUARIOS = "usuarios";
    public static final String COLUMN_USUARIO_ID = "id";
    public static final String COLUMN_NOMBRE = "nombre";
    public static final String COLUMN_APELLIDOS = "apellidos";
    public static final String COLUMN_CORREO = "correo";
    public static final String COLUMN_USERNAME = "usuario";
    public static final String COLUMN_CONTRASENA = "contrasena";
    public static final String COLUMN_DNI = "dni";

    private static final String CREATE_TABLE_USUARIOS =
            "CREATE TABLE " + TABLE_USUARIOS + " (" +
                    COLUMN_USUARIO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NOMBRE + " TEXT NOT NULL, " +
                    COLUMN_APELLIDOS + " TEXT NOT NULL, " +
                    COLUMN_CORREO + " TEXT NOT NULL, " +
                    COLUMN_USERNAME + " TEXT NOT NULL, " +
                    COLUMN_CONTRASENA + " TEXT NOT NULL, " +
                    COLUMN_DNI + " TEXT NOT NULL);";

    // Tabla LOCALES
    public static final String TABLE_LOCALES = "locales";
    public static final String COLUMN_LOCAL_ID = "id";
    public static final String COLUMN_NOMBRE_LOCAL = "nombre";
    public static final String COLUMN_UBICACION = "ubicacion";
    public static final String COLUMN_DESCRIPCION_ES = "descripcion_es";
    public static final String COLUMN_DESCRIPCION_EU = "descripcion_eu";
    public static final String COLUMN_LATITUD = "latitud";
    public static final String COLUMN_LONGITUD = "longitud";

    // Tabla COLAS
    public static final String TABLE_COLAS = "colas";
    public static final String COLUMN_COLA_ID = "id";
    public static final String COLUMN_COLA_USUARIO_ID = "usuario_id";
    public static final String COLUMN_COLA_LOCAL_ID = "local_id";
    public static final String COLUMN_TURNO = "turno"; // número random
    public static final String COLUMN_TIEMPO_RESTANTE = "tiempo_restante"; // en minutos
    public static final String COLUMN_ESTADO = "estado"; // esperando, atendido, cancelado, etc.

    private static final String CREATE_TABLE_LOCALES =
            "CREATE TABLE " + TABLE_LOCALES + " (" +
                    COLUMN_LOCAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NOMBRE_LOCAL + " TEXT NOT NULL, " +
                    COLUMN_UBICACION + " TEXT NOT NULL, " +
                    COLUMN_DESCRIPCION_ES + " TEXT, " +
                    COLUMN_DESCRIPCION_EU + " TEXT, " +
                    COLUMN_LATITUD + " REAL, " +
                    COLUMN_LONGITUD + " REAL" +
                    ");";

    private static final String CREATE_TABLE_COLAS =
            "CREATE TABLE " + TABLE_COLAS + " (" +
                    COLUMN_COLA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_COLA_USUARIO_ID + " INTEGER NOT NULL, " +
                    COLUMN_COLA_LOCAL_ID + " INTEGER NOT NULL, " +
                    COLUMN_TURNO + " INTEGER NOT NULL, " +
                    COLUMN_TIEMPO_RESTANTE + " INTEGER NOT NULL, " +
                    COLUMN_ESTADO + " TEXT NOT NULL," +
                    "FOREIGN KEY(" + COLUMN_COLA_USUARIO_ID + ") REFERENCES " + TABLE_USUARIOS + "(" + COLUMN_USUARIO_ID + ")," +
                    "FOREIGN KEY(" + COLUMN_COLA_LOCAL_ID + ") REFERENCES " + TABLE_LOCALES + "(" + COLUMN_LOCAL_ID + ")" +
                    ");";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USUARIOS);
        db.execSQL(CREATE_TABLE_LOCALES);
        db.execSQL(CREATE_TABLE_COLAS); //
        insertarDatosIniciales(db);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COLAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCALES);
        onCreate(db);
    }

    private void insertarDatosIniciales(SQLiteDatabase db) {
        // Usuarios
        db.execSQL("INSERT INTO " + TABLE_USUARIOS + " (nombre, apellidos, correo, usuario, contrasena, dni) VALUES " +
                "('Administrador', 'Del Sistema', 'admin@example.com', 'a', 'a', '00000000A')," +
                "('Aitzol', 'Etxeberria', 'aitzol@example.com', 'aitzol', 'pass123', '11111111B')," +
                "('Mikel', 'Elorrieta', 'mikel@example.com', 'mikel', 'pass123', '22222222C')," +
                "('Oier', 'Larralde', 'oier@example.com', 'oier', 'pass123', '33333333D')");


        db.execSQL("INSERT INTO " + TABLE_LOCALES + " (nombre, ubicacion, descripcion_es, descripcion_eu, latitud, longitud) VALUES " +

                "('Bolera Max Center', 'Centro Comercial Max Center, Barakaldo', 'Bolera con múltiples pistas y bar.', 'Pista eta taberna duen bolatokia', 43.2957, -2.9915)," +
                "('Cines Yelmo Artea', 'Centro Comercial Artea, Leioa', 'Cines modernos con asientos reclinables.', 'Aulki erosoak dituzten zinema modernoak', 43.3332, -2.9904)," +
                "('PortAventura - Dragon Khan', 'Salou, Tarragona', 'Atracción de montaña rusa icónica.', 'Ibilbide ikonikoa duen roller coaster-a', 41.0877, 1.1512)," +
                "('PortAventura - Shambhala', 'Salou, Tarragona', 'Montaña rusa de velocidad extrema.', 'Abiadura handiko roller coaster-a', 41.0870, 1.1521)," +
                "('PortAventura - Tutuki Splash', 'Salou, Tarragona', 'Atracción acuática con mucha emoción.', 'Ur-jaitsiera emozionagarria', 41.0871, 1.1534)," +
                "('PortAventura - El Diablo', 'Salou, Tarragona', 'Tren minero con curvas cerradas.', 'Buelta askoko meatzarien trena', 41.0861, 1.1502)," +
                "('PortAventura - Stampida', 'Salou, Tarragona', 'Doble montaña rusa de madera.', 'Zurezko roller coaster bikoitza', 41.0855, 1.1518)," +
                "('PortAventura - Hurakan Condor', 'Salou, Tarragona', 'Torre de caída libre.', 'Erorketa libreko dorrea', 41.0864, 1.1513)," +
                "('FNAC Bilbao', 'Alameda Urquijo 4, Bilbao', 'Tienda de tecnología, libros y música.', 'Teknologia, liburu eta musika denda', 43.2622, -2.9350)," +
                "('Decathlon Vitoria', 'Portal de Gamarra, Vitoria-Gasteiz', 'Tienda de deporte y accesorios.', 'Kirol eta osagarri denda', 42.8662, -2.6668)," +
                "('Eroski Arrasate', 'Kurutziaga Kalea, Arrasate', 'Supermercado y productos locales.', 'Supermerkatua eta tokiko produktuak', 43.1335, -2.4957)," +
                "('Guggenheim Bilbao', 'Avenida Abandoibarra, Bilbao', 'Museo de arte contemporáneo.', 'Arte garaikideko museoa', 43.2686, -2.9338)," +
                "('Kutxabank Oficina Central', 'Gran Vía 30, Bilbao', 'Oficina bancaria principal.', 'Kutxazain nagusia', 43.2633, -2.9354)," +
                "('Hospital de Cruces', 'Plaza de Cruces, Barakaldo', 'Centro médico y de urgencias.', 'Larrialdi eta osasun zentroa', 43.2844, -2.9895)," +
                "('BBK Live Festival', 'Kobetamendi, Bilbao', 'Festival de música en la montaña.', 'Musika jaialdia mendian', 43.2591, -2.9614)," +
                "('Fan Zone Athletic - Sevilla', 'Avenida de María Luisa, Sevilla', 'Zona de aficionados del Athletic en Sevilla para la final.', 'Athletic zaleentzako gunea Sevillan finalerako', 37.3726, -5.9873)," +
                "('Fan Zone Athletic - Bilbao', 'Explanada San Mamés, Bilbao', 'Pantalla gigante y actividades para la final en Bilbao.', 'Finaleko ekitaldiak eta pantaila erraldoia Bilbon', 43.2640, -2.9495)"
        );
    }
    public Cursor getLocalById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM locales WHERE id = ?", new String[]{String.valueOf(id)});
    }
    public boolean login(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_USUARIOS +
                " WHERE " + COLUMN_USERNAME + " = ? AND " + COLUMN_CONTRASENA + " = ?";
        String[] selectionArgs = { username, password };

        Cursor cursor = db.rawQuery(query, selectionArgs);

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return exists;
    }
    public Cursor getColaPorTurno(int turno) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM colas WHERE turno = ?", new String[]{String.valueOf(turno)});
    }


    public boolean registerUser(String nombre, String apellidos, String correo, String usuario, String contrasena, String dni) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Primero comprobamos que no exista el usuario
        String query = "SELECT * FROM " + TABLE_USUARIOS + " WHERE " + COLUMN_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{usuario});

        if (cursor.getCount() > 0) {
            // Usuario ya existe
            cursor.close();
            db.close();
            return false;
        }

        cursor.close();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NOMBRE, nombre);
        values.put(COLUMN_APELLIDOS, apellidos);
        values.put(COLUMN_CORREO, correo);
        values.put(COLUMN_USERNAME, usuario);
        values.put(COLUMN_CONTRASENA, contrasena);
        values.put(COLUMN_DNI, dni);

        long result = db.insert(TABLE_USUARIOS, null, values);
        db.close();

        return result != -1;
    }
    // En tu clase DBHelper
    public Cursor getUltimaCola() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM colas ORDER BY id DESC LIMIT 1", null);
    }




}

