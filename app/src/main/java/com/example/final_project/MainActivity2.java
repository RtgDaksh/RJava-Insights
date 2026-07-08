package com.example.final_project;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.final_project.network.RetrofitClient;
import com.example.final_project.network.models.HelloResponse;
import com.example.final_project.network.models.PredictResponse;

import java.io.InputStream;

import okhttp3.ResponseBody;
import retrofit2.*;
import retrofit2.Callback;

public class MainActivity2 extends AppCompatActivity {

    private EditText input;
    private TextView result;
    private ImageView imageView;
    private Button btnPredict, btnPlot;

    Spinner spinner;

    String[] graphTypes = {
            "line",
            "scatter",
            "both",
            "bar",
            "histogram"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);

        //hellocall();

        input = findViewById(R.id.inputValue);
        result = findViewById(R.id.resultText);
        imageView = findViewById(R.id.imageView);
        spinner = findViewById(R.id.spinnerType);
        btnPredict = findViewById(R.id.btnPredict);
        btnPlot = findViewById(R.id.btnPlot);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        graphTypes
                );

        spinner.setAdapter(adapter);

        btnPredict.setOnClickListener(v -> {
            int x;

            try {
                x = Integer.parseInt(input.getText().toString());
            } catch (Exception e) {
                x = 1;
            }

            callPredict(x);
        });

        btnPlot.setOnClickListener(v -> loadPlot());
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
//                            Toast.makeText(MainActivity2.this,
//                                    response.body().getMessage(),
//                                    Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(MainActivity2.this,
//                                    "Response Failed",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<HelloResponse> call,
//                                          Throwable t) {
//
//                        Toast.makeText(MainActivity2.this,
//                                "API NOT RUNNING",
//                                Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

    private void callPredict(int x) {

        RetrofitClient.getApi().predict(x)
                .enqueue(new Callback<PredictResponse>() {
                    @Override
                    public void onResponse(Call<PredictResponse> call,
                                           Response<PredictResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            result.setText("Output: " +
                                    response.body().getOutput() + " Data Points will be generated");
                        }
                    }

                    @Override
                    public void onFailure(Call<PredictResponse> call,
                                          Throwable t) {
                        result.setText("Error: " + t.getMessage());
                    }
                });
    }

    private void loadPlot() {

        String plottype =
                spinner.getSelectedItem().toString();

        int value=Integer.parseInt(input.getText().toString());

        RetrofitClient.getApi().getPlot(value,plottype)
                .enqueue(new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(Call<ResponseBody> call,
                                           Response<ResponseBody> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            try {
                                InputStream stream =
                                        response.body().byteStream();

                                Bitmap bitmap =
                                        BitmapFactory.decodeStream(stream);

                                imageView.setImageBitmap(bitmap);

                                result.setText("Plot Loaded");

                            } catch (Exception e) {
                                result.setText("Image Error: " + e.getMessage());
                            }

                        } else {
                            result.setText("Server Error: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call,
                                          Throwable t) {
                        result.setText("Failed: " + t.getMessage());
                    }
                });
    }
}