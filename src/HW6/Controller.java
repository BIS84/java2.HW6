package HW6;

import HW6.enums.Functionality;
import HW6.enums.Periods;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Controller {

    WeatherProvider weatherProvider = new AccuWeatherProvider();
    Map<Integer, Functionality> variantResult = new HashMap();

    public Controller() {

        variantResult.put(1, Functionality.GET_WEATHER_IN_NEXT_5_DAYS);
        variantResult.put(2, Functionality.GET_WEATHER_FROM_DB);
    }

    public void onUserInput(String input) throws IOException, SQLException {
        int command = Integer.parseInt(input);
        if (!variantResult.containsKey(command)) {
            throw new IOException("There is no command for command-key " + command);
        }


        switch (variantResult.get(command)) {
            case GET_WEATHER_IN_NEXT_5_DAYS:
                getWeather();
                MainApp.print5DayWeather();
                break;
        }
        switch (variantResult.get(command)) {
            case GET_WEATHER_FROM_DB:
                WeatherRepository.readWeatherData();
                break;
        }
    }

    public void getWeather() throws IOException {
        weatherProvider.getWeather(Periods.FIVE_DAYS);
    }
}