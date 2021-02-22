package HW6;

import HW6.enums.Functionality;
import HW6.enums.Periods;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Controller {

    WeatherProvider weatherProvider = new AccuWeatherProvider();
    Map<Integer, Functionality> variantResult = new HashMap();

    public Controller() {
        variantResult.put(1, Functionality.GET_WEATHER_IN_NEXT_5_DAYS);
    }

    public void onUserInput(String input) throws IOException {
        int command = Integer.parseInt(input);
        if (!variantResult.containsKey(command)) {
            throw new IOException("There is no command for command-key " + command);
        }

        switch (variantResult.get(command)) {
            case GET_WEATHER_IN_NEXT_5_DAYS:
                getWeather();
                break;
        }
    }

    public void getWeather() throws IOException {
        weatherProvider.getWeather(Periods.FIVE_DAYS);
    }
}