package com.example.veso;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Phiên bản cơ sở dữ liệu
    public static final int DATABASE_VERSION = 1;
    // Tên cơ sở dữ liệu
    public static final String DATABASE_NAME = "Veso.db";

    // Câu lệnh SQL để tạo bảng trong cơ sở dữ liệu
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DatabaseContract.TicketEntry.TABLE_NAME + " (" +
                    DatabaseContract.TicketEntry._ID + " INTEGER PRIMARY KEY," +
                    DatabaseContract.TicketEntry.COLUMN_NAME_NUMBERS + " TEXT)";

    // Câu lệnh SQL để xóa bảng khỏi cơ sở dữ liệu
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DatabaseContract.TicketEntry.TABLE_NAME;

    public DatabaseHelper(Context context) {
        // Khởi tạo DatabaseHelper với tên và phiên bản cơ sở dữ liệu
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Phương thức này được gọi khi cơ sở dữ liệu được tạo
    public void onCreate(SQLiteDatabase db) {
        // Thực thi câu lệnh SQL để tạo bảng
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    // Phương thức này được gọi khi cần nâng cấp cơ sở dữ liệu
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa bảng cũ nếu tồn tại
        db.execSQL(SQL_DELETE_ENTRIES);
        // Tạo lại cơ sở dữ liệu
        onCreate(db);
    }

    // Phương thức này được gọi khi cần giảm phiên bản cơ sở dữ liệu
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Thực hiện nâng cấp cơ sở dữ liệu
        onUpgrade(db, oldVersion, newVersion);
    }
}
