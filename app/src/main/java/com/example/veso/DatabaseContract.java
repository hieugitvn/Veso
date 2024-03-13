package com.example.veso;

import android.provider.BaseColumns;

public final class DatabaseContract {
    // Không cho phép tạo đối tượng của lớp này
    private DatabaseContract() {
    }

    // Lớp con này định nghĩa cấu trúc bảng và các cột trong cơ sở dữ liệu
    public static class TicketEntry implements BaseColumns {
        // Tên bảng trong cơ sở dữ liệu
        public static final String TABLE_NAME = "ticket";
        // Tên cột chứa dữ liệu về các số trong vé số
        public static final String COLUMN_NAME_NUMBERS = "numbers";
    }
}
