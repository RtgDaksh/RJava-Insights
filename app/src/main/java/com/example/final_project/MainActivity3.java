package com.example.final_project;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import com.example.final_project.network.models.AnalyticsResponse;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.final_project.network.RetrofitClient;
import com.example.final_project.network.models.HelloResponse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.*;

public class MainActivity3 extends AppCompatActivity {

    Button btnSelect, btnUpload;
    TextView result,sum;
    ImageView imageView;

    Spinner spinner;
    String[] graphTypes = {
            "line",
            "scatter",
            "both",
            "bar",
            "histogram"
    };

    Uri selectedUri = null;

    ActivityResultLauncher<String> filePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main3);

        //hellocall();

        btnSelect = findViewById(R.id.btnSelect);
        btnUpload = findViewById(R.id.btnUpload);
        result = findViewById(R.id.resultText);
        sum=findViewById(R.id.sumText);
        spinner = findViewById(R.id.spinType);
        imageView = findViewById(R.id.imageView);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        graphTypes
                );

        spinner.setAdapter(adapter);
        final boolean[] fileEmp = {true};

        filePicker =
                registerForActivityResult(
                        new ActivityResultContracts.GetContent(),
                        uri -> {

                            Toast.makeText(this,
                                    "File selection",
                                    Toast.LENGTH_SHORT).show();
                            try{
                                InputStream inputStream=getContentResolver().openInputStream(uri);
                                BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
                                String firstLine=reader.readLine();
                                String secondLine=reader.readLine();
                                String thirdLine=reader.readLine();
                                if(firstLine==null || firstLine.trim().isEmpty())
                                {
                                    Toast.makeText(this, "SELECTED FILE IS EMPTY", Toast.LENGTH_SHORT).show();
                                    fileEmp[0] =false;
                                }
                                else if(secondLine==null || secondLine.trim().isEmpty())
                                {
                                    Toast.makeText(this, "SELECTED FILE IS EMPTY", Toast.LENGTH_SHORT).show();
                                    fileEmp[0] =false;
                                }
                                else if(thirdLine==null || thirdLine.trim().isEmpty())
                                {
                                    Toast.makeText(this, "SELECTED FILE IS EMPTY", Toast.LENGTH_SHORT).show();
                                    fileEmp[0] =false;
                                }
                                else
                                {
                                    Toast.makeText(this, "SELECTED FILE CONTAINS RECORDS", Toast.LENGTH_SHORT).show();
                                    fileEmp[0] =true;
                                }
                                inputStream.close();
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                            if (uri != null && fileEmp[0] ==true) {

                                String fileName = getFileName(uri);

                                if (fileName != null &&
                                        fileName.toLowerCase().endsWith(".csv")) {

                                    selectedUri = uri;

                                    Toast.makeText(this,
                                            "File selected",
                                            Toast.LENGTH_SHORT).show();

                                    result.setText("CSV Selected");

                                } else {
                                    result.setText("Only CSV files allowed");
                                }
                            }
                        });

        btnSelect.setOnClickListener(v -> {
            filePicker.launch("text/*");
        });

        btnUpload.setOnClickListener(v -> {
            if (selectedUri != null && fileEmp[0] ==true) {
                uploadCsv(selectedUri);
            } else {
                result.setText("Select Valid CSV First");
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

//    private void hellocall() {
//
//        RetrofitClient.getApi().hello()
//                .enqueue(new Callback<HelloResponse>() {
//                    @Override
//                    public void onResponse(Call<HelloResponse> call,
//                                           Response<HelloResponse> response) {
//
//                        if (response.isSuccessful() && response.body() != null) {
//                            Toast.makeText(MainActivity3.this,
//                                    response.body().getMessage(),
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<HelloResponse> call,
//                                          Throwable t) {
//
//                        Toast.makeText(MainActivity3.this,
//                                "API NOT RUNNING",
//                                Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

    private void uploadCsv(Uri uri) {

        try {

            InputStream inputStream =
                    getContentResolver().openInputStream(uri);

            File file =
                    new File(getCacheDir(), "upload.csv");

            FileOutputStream out =
                    new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int len;

            while ((len = inputStream.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }

            out.close();
            inputStream.close();

            RequestBody requestFile =
                    RequestBody.create(
                            MediaType.parse("text/csv"),
                            file
                    );

            MultipartBody.Part body =
                    MultipartBody.Part.createFormData(
                            "file",
                            file.getName(),
                            requestFile
                    );


            String selectedType =
                    spinner.getSelectedItem().toString();

            RequestBody graphType =
                    RequestBody.create(
                            MediaType.parse("text/plain"),
                            selectedType
                    );


            RetrofitClient.getApi()
                    .uploadCsv(body, graphType)
                    .enqueue(new Callback<AnalyticsResponse>() {

                        @Override
                        public void onResponse(
                                Call<AnalyticsResponse> call,
                                Response<AnalyticsResponse> response) {

                            if (response.isSuccessful()
                                    && response.body() != null) {

                                AnalyticsResponse data =
                                        response.body();

                                byte[] bytes =
                                        Base64.decode(
                                                data.getImage(),
                                                Base64.DEFAULT
                                        );

                                Bitmap bitmap =
                                        BitmapFactory.decodeByteArray(
                                                bytes,
                                                0,
                                                bytes.length
                                        );

                                imageView.setImageBitmap(bitmap);

                                sum.setText(
                                        "Rows: " + data.getRows() +
                                                "\nColumns: " + data.getColumns() +
                                                "\nMean: " + data.getMean() +
                                                "\nMax: " + data.getMax() +
                                                "\nMin: " + data.getMin()
                                );

                                result.setText(
                                        "Analytics Loaded (" +
                                                selectedType + ")"
                                );

                            } else {
                                result.setText("Response Error");
                            }
                        }

                        @Override
                        public void onFailure(
                                Call<AnalyticsResponse> call,
                                Throwable t) {

                            result.setText(t.getMessage());
                        }
                    });

        } catch (Exception e) {
            result.setText(e.getMessage());
        }
    }

    private String getFileName(Uri uri) {

        String result = null;

        Cursor cursor =
                getContentResolver()
                        .query(uri, null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int index =
                        cursor.getColumnIndex(
                                OpenableColumns.DISPLAY_NAME);

                if (index >= 0) {
                    result = cursor.getString(index);
                }
            }
            cursor.close();
        }

        return result;
    }
}