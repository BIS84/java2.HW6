package HW6;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import HW6.enums.Periods;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class AccuWeatherProvider implements WeatherProvider {

    private static final String BASE_HOST = "dataservice.accuweather.com";
    private static final String API_VERSION = "v1";
    private static final String API_KEY = ApplicationGlobalState.getInstance().getApiKey();
    private static final String FORECAST = "forecasts";
    private static final String FORECAST_TYPE = "daily";
    private static final String FORECAST_PERIOD = "5day";

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void getWeather(Periods periods)  throws IOException {
        String cityKey = detectCityKey();
        OkHttpClient client = new OkHttpClient();

        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(BASE_HOST)
                .addPathSegment(FORECAST)
                .addPathSegment(API_VERSION)
                .addPathSegment(FORECAST_TYPE)
                .addPathSegment(FORECAST_PERIOD)
                .addPathSegment(cityKey)

                .addQueryParameter("apikey", API_KEY)
                .addQueryParameter("language", "ru-ru")
                .addQueryParameter("metric", "true")
                .addQueryParameter("Type", "City")
                .build();

        Request requesthttp = new Request.Builder()
                .addHeader("accept", "application/json")
                .url(url)
                .build();

        String jsonResponse = Objects.requireNonNull(client.newCall(requesthttp).execute().body()).string();
        File file = new File("./weather.json");

        try (OutputStream out = new FileOutputStream("./weather.json")) {
            out.write(jsonResponse.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String detectCityKey() throws IOException {
        String selectedCity = ApplicationGlobalState.getInstance().getSelectedCity();

        HttpUrl detectLocationURL = new HttpUrl.Builder()
                .scheme("http")
                .host(BASE_HOST)
                .addPathSegment("locations")
                .addPathSegment(API_VERSION)
                .addPathSegment("cities")
                .addPathSegment("autocomplete")
                .addQueryParameter("apikey", API_KEY)
                .addQueryParameter("q", selectedCity)
                .build();

        Request request = new Request.Builder()
                .addHeader("accept", "application/json")
                .url(detectLocationURL)
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new IOException("Невозможно прочесть информацию о городе. " +
                    "Код ответа сервера = " + response.code() + " тело ответа = " + Objects.requireNonNull(response.body()).string());
        }
        String jsonResponse = null;
        try {
            jsonResponse = response.body().string();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if (objectMapper.readTree(jsonResponse).size() > 0) {
            String cityName = objectMapper.readTree(jsonResponse).get(0).at("/LocalizedName").asText();
            String countryName = objectMapper.readTree(jsonResponse).get(0).at("/Country/LocalizedName").asText();

        } else throw new IOException("Server returns 0 cities");

        return objectMapper.readTree(jsonResponse).get(0).at("/Key").asText();
    }
}