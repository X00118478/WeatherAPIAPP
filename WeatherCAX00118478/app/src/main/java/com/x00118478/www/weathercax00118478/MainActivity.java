package com.x00118478.www.weathercax00118478;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView city;
    TextView cityWeatherDescription;
    Button changeCity;
    TextView weather_icon;
    TextView weather_conditions;
    TextView wind_direction;
    TextView sunset;
    TextView wind_speed;
    TextView maximum_temperature;
    TextView minimum_temperature;
    ProgressBar loading;
    //Initialise the Weather graphics obtained from = http://erikflowers.github.io/weather-icons/
    Typeface weatherGraphics;
    String selectedCity = "Dublin, IE ";
    //TODO Generate API KEY and PASTE in the blank spot.
    //Generated from an online weather API.
    String API_KEY = "649410531b3484189953fe198d9297fa";

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fullscreen capability
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        //Connect the initialised variables to the interface by using the ID
        city = findViewById(R.id.city);
        cityWeatherDescription = findViewById(R.id.updated);
        changeCity = findViewById(R.id.changeCity);
        weather_icon = findViewById(R.id.weather_icon);
        wind_direction = findViewById(R.id.wind_direction);
        maximum_temperature = findViewById(R.id.maximum_temperature);
        minimum_temperature = findViewById(R.id.minimum_temperature);
        wind_speed = findViewById(R.id.wind_speed);
        weather_conditions = findViewById(R.id.weather_conditions);
        sunset = findViewById(R.id.sunset);
        loading = findViewById(R.id.loading);
        //Weather ICONS & Graphics are sourced from = http://erikflowers.github.io/weather-icons/ **NOT MY PROPERTY
        weatherGraphics = Typeface.createFromAsset(getAssets(), "icons/weathericons-regular-webfont.ttf");

        weather_icon.setTypeface(weatherGraphics);

        downloadCityWeather(selectedCity);

        city.setText((selectedCity).toUpperCase(Locale.US));
        cityWeatherDescription.setText(("description").toUpperCase(Locale.US));
        minimum_temperature.setText("Min\n" + "8" + "°");
        maximum_temperature.setText("Max\n" + "20" + "°");
        wind_direction.setText("Wind Direction: " + "East");
        wind_speed.setText("Wind Speed: " + "5 " + "kmp/h");
        weather_conditions.setText("High");
        sunset.setText("Low");

        //Display Dialog box to allow the user to change the city.
        //Dialog box based off https://developer.android.com/guide/topics/ui/dialogs
        changeCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBox = new AlertDialog.Builder(MainActivity.this);
                dialogBox.setTitle("Enter City Name:");
                //declare EditText for the UI
                final EditText userInputforCity = new EditText(MainActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                dialogBox.setView(userInputforCity);

                //Confirm the change and retrieve the data from the API & Display
                dialogBox.setPositiveButton("Change",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                selectedCity = userInputforCity.getText().toString();
                                userInputforCity.setText(selectedCity);
                                city.setText((selectedCity).toUpperCase(Locale.US));
                                downloadCityWeather(selectedCity);
                            }
                        });
                //Cancel the requested change
                dialogBox.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                dialogBox.show();
            }
        });

    }

    //Download the Weather information
    public void downloadCityWeather(String query) {
        if (Controller.isDeviceConnected(getApplicationContext())) {
            CallAPI retrieveWeatherTask = new CallAPI();
            retrieveWeatherTask.execute(query);
            Toast.makeText(getApplicationContext(), "Great Success!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "No Connection To The Internet.", Toast.LENGTH_LONG).show();
        }
    }

    //This is the backbone of the application
    //Based off https://www.androidauthority.com/use-remote-web-api-within-android-app-617869/
    class CallAPI extends AsyncTask<String, Void, String> {
        @Override
        //Retrieve the XML data from the API using the URL and the API Key.
        protected String doInBackground(String... strings) {
            String xml = Controller.retrieveData("http://api.openweathermap.org/data/2.5/weather?q=" + strings[0] +
                    "&units=metric&appid=" + API_KEY);
            return xml;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String xml) {

            try {
                JSONObject json = new JSONObject(xml);
                if (json != null) {
                    JSONObject weatherInformation = json.getJSONArray("weather").getJSONObject(0);
                    JSONObject mainIformation = json.getJSONObject("main");
                    JSONObject system = json.getJSONObject("sys");
                    JSONObject wind = json.getJSONObject("wind");
                    double windDegrees = wind.getDouble("deg");
                    //save the unix time stamp
                    long sunriseCode = system.getLong("sunrise");
                    long sunsetCode = system.getLong("sunset");
                    //convert to java timestamp
                    long javaSunrise = sunriseCode * 1000L;
                    long javaSunset = sunsetCode * 1000L;
                    Date date = new Date(javaSunrise);
                    Date dateSunset = new Date(javaSunset);
                    //Format to display the time in Hh:MM in twenty four hour format
                    String sunrise = new SimpleDateFormat("kk:mm").format(date);
                    String sunsetTime = new SimpleDateFormat("kk:mm").format(dateSunset);

                    city.setText(json.getString("name").toUpperCase(Locale.US) + ", " + json.getJSONObject("sys").getString("country"));
                    cityWeatherDescription.setText(weatherInformation.getString("description").toUpperCase(Locale.US));
                    minimum_temperature.setText(String.format("  Min\n" + "%.2f", mainIformation.getDouble("temp_min")) + "°");
                    maximum_temperature.setText(String.format("  Max\n" + "%.2f", mainIformation.getDouble("temp_max")) + "°");

                    if ((windDegrees >= 348.75) && (windDegrees <= 360) ||
                            (windDegrees >= 0) && (windDegrees <= 11.25)) {
                        wind_direction.setText("Wind Direction: " + "North " + windDegrees + "°");
                    } else if ((windDegrees >= 11.25) && (windDegrees <= 33.75)) {
                        wind_direction.setText("Wind Direction: " + "North-East " + windDegrees + "°");
                    } else if ((windDegrees >= 33.75) && (windDegrees <= 56.25)) {
                        wind_direction.setText("Wind Direction: " + "North-East " + windDegrees + "°");
                    } else if ((windDegrees >= 56.25) && (windDegrees <= 78.75)) {
                        wind_direction.setText("Wind Direction: " + "North-East " + windDegrees + "°");
                    } else if ((windDegrees >= 78.75) && (windDegrees <= 101.25)) {
                        wind_direction.setText("Wind Direction: " + "East " + windDegrees + "°");
                    } else if ((windDegrees >= 101.25) && (windDegrees <= 123.75)) {
                        wind_direction.setText("Wind Direction: " + "South-East " + windDegrees + "°");
                    } else if ((windDegrees >= 123.75) && (windDegrees <= 146.25)) {
                        wind_direction.setText("Wind Direction: " + "South-East " + windDegrees + "°");
                    } else if ((windDegrees >= 146.25) && (windDegrees <= 168.75)) {
                        wind_direction.setText("Wind Direction: " + "South-East " + windDegrees + "°");
                    } else if ((windDegrees >= 168.75) && (windDegrees <= 191.25)) {
                        wind_direction.setText("Wind Direction: " + "South " + +windDegrees + "°");
                    } else if ((windDegrees >= 191.25) && (windDegrees <= 213.75)) {
                        wind_direction.setText("Wind Direction: " + "South-West " + windDegrees + "°");
                    } else if ((windDegrees >= 213.75) && (windDegrees <= 236.25)) {
                        wind_direction.setText("Wind Direction: " + "South-West " + windDegrees + "°");
                    } else if ((windDegrees >= 236.25) && (windDegrees <= 258.75)) {
                        wind_direction.setText("Wind Direction: " + "South-West " + windDegrees + "°");
                    } else if ((windDegrees >= 258.75) && (windDegrees <= 281.25)) {
                        wind_direction.setText("Wind Direction: " + "West " + windDegrees + "°");
                    } else if ((windDegrees >= 281.25) && (windDegrees <= 303.75)) {
                        wind_direction.setText("Wind Direction: " + "North-West " + windDegrees + "°");
                    } else if ((windDegrees >= 303.75) && (windDegrees <= 326.25)) {
                        wind_direction.setText("Wind Direction: " + "North-West " + windDegrees + "°");
                    } else if ((windDegrees >= 326.25) && (windDegrees <= 348.75)) {
                        wind_direction.setText("Wind Direction: " + "North-West " + windDegrees + "°");
                    } else {
                        wind_direction.setText("Wind Direction: " + " Not recognised ");
                    }

                    wind_speed.setText("Wind Speed: " + wind.getString("speed") + "kmp/h");
                    weather_conditions.setText("Sunrise :" + sunrise);
                    sunset.setText("Sunset : " + sunsetTime);
                    loading.setVisibility(View.GONE);

                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Error, City Not Found ", Toast.LENGTH_SHORT).show();
            }

        }


    }
}
