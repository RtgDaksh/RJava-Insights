package com.example.final_project;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import android.os.Environment;


public class MainActivity4 extends AppCompatActivity {
    EditText fileNameInput, xInput, yInput;
    Button addButton, saveButton;
    StringBuilder csvBuilder = new StringBuilder();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main4);

        fileNameInput = findViewById(R.id.fileNameInput);
        xInput = findViewById(R.id.xInput);
        yInput = findViewById(R.id.yInput);
        addButton = findViewById(R.id.addButton);
        saveButton = findViewById(R.id.saveButton);

        csvBuilder.append("X,Y\n");

        addButton.setOnClickListener(v -> addRow());

        saveButton.setOnClickListener(v -> saveCsvFile());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addRow() {
        String x = xInput.getText().toString().trim();
        String y = yInput.getText().toString().trim();

        if (x.isEmpty() || y.isEmpty()) {
            Toast.makeText(this, "Enter both X and Y values", Toast.LENGTH_SHORT).show();
            return;
        }

        csvBuilder.append(x).append(",").append(y).append("\n");

        xInput.setText("");
        yInput.setText("");

        Toast.makeText(this, "Row Added", Toast.LENGTH_SHORT).show();
    }

    private void saveCsvFile() {
        String fileName = fileNameInput.getText().toString().trim();

        if (fileName.isEmpty()) {
            Toast.makeText(this, "Enter file name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!fileName.endsWith(".csv")) {
            fileName += ".csv";
        }

        try {
            if (!fileName.endsWith(".csv")) {
                fileName += ".csv";
            }

            ContentValues values = new ContentValues();
            values.put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.Files.FileColumns.MIME_TYPE, "text/csv");
            values.put(MediaStore.Files.FileColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_DOCUMENTS + "/MyCSVFiles");

            Uri uri = getContentResolver().insert(
                    MediaStore.Files.getContentUri("external"),
                    values
            );

            if (uri != null) {
                OutputStream outputStream =
                        getContentResolver().openOutputStream(uri);

                outputStream.write(csvBuilder.toString().getBytes());
                outputStream.flush();
                outputStream.close();

                Toast.makeText(this,
                        "Saved to Documents/MyCSVFiles",
                        Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Toast.makeText(this,
                    "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }
}