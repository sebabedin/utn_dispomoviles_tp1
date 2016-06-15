package extern;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UsuariosSQLiteHelper extends SQLiteOpenHelper {

    String sqlCreate = "CREATE TABLE capturas (cap_GPS_Lon TEXT, cap_GPS_Lat TEXT, cap_COM_R NUMERIC, cap_COM_P NUMERIC, cap_COM_Y NUMERIC, cap_COM NUMERIC, cap_IMU_Gyro_R NUMERIC, cap_IMU_Gyro_P NUMERIC, cap_IMU_Gyro_Y NUMERIC, cap_IMU_Acc_Z NUMERIC, cap_IMU_Acc_Y NUMERIC, cap_IMU_Acc_X NUMERIC, cap_ID INTEGER PRIMARY KEY, cap_Foto TEXT, cap_IMU NUMERIC, cap_GPS NUMERIC);";

    public UsuariosSQLiteHelper(Context contexto, String nombre,
                                SQLiteDatabase.CursorFactory factory, int version) {
        super(contexto, nombre, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {
        db.execSQL("DROP TABLE IF EXISTS capturas");
        db.execSQL(sqlCreate);
    }
}
