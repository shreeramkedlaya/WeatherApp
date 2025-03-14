package com.example.weatherapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private TextView cityTv, temperatureTv, humidityTv, windTv, descriptionTv;
    private LinearLayout detailsLl, humidityLl, windLl;
    private ImageView humidityIv, windIv, weatherIv;
    private EditText cityNameEt;
    private Button fetchWeatherBt;
    private static final String API_KEY = "bdad18db16029725ac8354808fbe349a";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityTv = findViewById(R.id.cityTv);
        temperatureTv = findViewById(R.id.temperatureTv);
        humidityTv = findViewById(R.id.humidityTv);
        windTv = findViewById(R.id.windTv);
        descriptionTv = findViewById(R.id.descriptionTv);
        humidityIv = findViewById(R.id.humidityIv);
        windIv = findViewById(R.id.windIv);
        weatherIv = findViewById(R.id.weatherIv);
        cityNameEt = findViewById(R.id.cityNameEt);
        fetchWeatherBt = findViewById(R.id.fetchWeatherBt);

        fetchWeatherBt.setOnClickListener(v -> {
            String cityName = cityNameEt.getText().toString();
            System.out.println(cityName);
            if (!cityName.isEmpty()) {
                fetchWeatherData(cityName);
            } else {
                cityNameEt.setError("Please Enter a City Name");
            }
        });
    }

    private void fetchWeatherData(String cityName) {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + API_KEY + "&units=metric";
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            try {
                Response response = client.newCall(request).execute();
                String result = response.body().string();
                runOnUiThread(() -> updateUI(result));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void updateUI(String result) {
        if (result != null) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject main = jsonObject.getJSONObject("main");
                double temperature = main.getDouble("temp");
                double humidity = main.getDouble("humidity");
                double windSpeed = jsonObject.getJSONObject("wind").getDouble("speed");
                String description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                String iconCode = jsonObject.getJSONArray("weather").getJSONObject(0).getString("icon");
                String resourceName = "ic_" + iconCode;
                int resId = getResources().getIdentifier(resourceName, "drawable", getPackageName());
                weatherIv.setImageResource(resId);
                cityTv.setText(jsonObject.getString("name"));
                temperatureTv.setText(String.format("%.0f°", temperature));
                humidityTv.setText(String.format("%.0f°", humidity));
                windTv.setText(String.format("%.0f°", windSpeed));
                descriptionTv.setText(description);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}