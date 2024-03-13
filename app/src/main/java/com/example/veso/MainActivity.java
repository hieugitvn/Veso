package com.example.veso;
import android.content.Context;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    private Button button;
    private Button historyButton;
    private Button clearButton;
    private TextView textViewResult;
    private DatabaseHelper dbHelper;
    private Button pasteButton; // Thêm nút dán

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        // Khởi tạo các view và gán id
        editText = findViewById(R.id.editText);
        button = findViewById(R.id.button);
        historyButton = findViewById(R.id.historyButton);
        clearButton = findViewById(R.id.clearButton);
        textViewResult = findViewById(R.id.textViewResult);
        pasteButton = findViewById(R.id.pasteButton); // Tìm và gán ID cho nút Dán

        // Xử lý sự kiện khi button được nhấn
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = editText.getText().toString();
                List<List<Integer>> groupedAndSortedNumbers = parseAndGroupNumbers(inputText);

                // Thêm các vé số vào cơ sở dữ liệu
                for (List<Integer> ticket : groupedAndSortedNumbers) {
                    insertTicket(ticket);
                }

                // Hiển thị kết quả
                displayResult(groupedAndSortedNumbers);
            }
        });

        // Xử lý sự kiện khi nút lịch sử được nhấn
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị lịch sử
                displayHistory();
            }
        });

        // Xử lý sự kiện khi nút xóa lịch sử được nhấn
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xóa lịch sử
                clearHistory();
            }
        });

        // Xử lý sự kiện khi nút dán được nhấn
        pasteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pasteFromClipboard(); // Gọi phương thức pasteFromClipboard()
            }
        });
    }

    // Phương thức này thực hiện phân tích và nhóm các số từ văn bản đầu vào
    private List<List<Integer>> parseAndGroupNumbers(String inputText) {
        List<List<Integer>> result = new ArrayList<>();
        // Loại bỏ các ký tự không phải là số và khoảng trắng
        String sanitizedText = inputText.replaceAll("[^0-9\\s]", "");
        String[] numberStrings = sanitizedText.split("\\s+");

        List<Integer> numbers = new ArrayList<>();
        for (String numberString : numberStrings) {
            try {
                int number = Integer.parseInt(numberString);
                numbers.add(number);
            } catch (NumberFormatException e) {
                // Bỏ qua các số không hợp lệ
            }
        }

        // Chia thành các nhóm 6 số
        int groupSize = 6;
        for (int i = 0; i < numbers.size(); i += groupSize) {
            int endIndex = Math.min(i + groupSize, numbers.size());
            List<Integer> group = new ArrayList<>(numbers.subList(i, endIndex));

            // Sắp xếp các số trong nhóm
            Collections.sort(group);

            result.add(group);
        }

        return result;
    }

    // Phương thức này thêm các số vé vào cơ sở dữ liệu
    private void insertTicket(List<Integer> numbers) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        // Chuyển đổi List các số thành chuỗi được phân tách bằng dấu phẩy
        StringBuilder numbersString = new StringBuilder();
        for (int i = 0; i < numbers.size(); i++) {
            numbersString.append(numbers.get(i));
            if (i < numbers.size() - 1) {
                numbersString.append(",");
            }
        }
        values.put(DatabaseContract.TicketEntry.COLUMN_NAME_NUMBERS, numbersString.toString());
        db.insert(DatabaseContract.TicketEntry.TABLE_NAME, null, values);
        db.close();
    }

    // Phương thức này hiển thị kết quả của vé số
    private void displayResult(List<List<Integer>> groupedAndSortedNumbers) {
        StringBuilder resultText = new StringBuilder();
        for (int i = 0; i < groupedAndSortedNumbers.size(); i++) {
            resultText.append("Vé ").append(i + 1).append(": ").append(groupedAndSortedNumbers.get(i)).append("\n\n");
        }
        textViewResult.setText(resultText.toString());
    }


// Phương thức này hiển thị lịch sử các vé số
    private void displayHistory() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseContract.TicketEntry.TABLE_NAME, null);
        StringBuilder historyText = new StringBuilder();
        int i = 0;
        if (cursor != null) {
            int numbersColumnIndex = cursor.getColumnIndex(DatabaseContract.TicketEntry.COLUMN_NAME_NUMBERS);
            if (numbersColumnIndex != -1 && cursor.moveToFirst()) {
                do {
                    String numbersString = cursor.getString(numbersColumnIndex);
                    // Sửa đổi chuỗi vé số để hiển thị theo định dạng mong muốn
                    String formattedNumbers = "Vé " + ++i + ": (" + numbersString + ")";
                    historyText.append(formattedNumbers).append("\n\n");
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
        textViewResult.setText(historyText.toString());
    }



    // Phương thức này xóa lịch sử các vé số
    private void clearHistory() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseContract.TicketEntry.TABLE_NAME, null, null);
        db.close();
        textViewResult.setText("Lịch sử đã được xóa.");
    }

    // Phương thức này dán nội dung từ Clipboard vào EditText
    private void pasteFromClipboard() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null && clipboard.hasPrimaryClip()) {
            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
            if (item != null) {
                String textToPaste = item.getText().toString();
                editText.setText(textToPaste);
            }
        }
    }
}
