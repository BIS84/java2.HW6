package HW6;

import java.io.IOException;
import java.sql.*;

public class WeatherRepository {

    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement preparedStatement;

    static String insertWeatherQuery = "INSERT INTO weather (city, date_time, weather_text, temperature) VALUES (?,?,?,?)";
    static String insertDBQuery = "SELECT * FROM weather";

    static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + ApplicationGlobalState.getDbFileName());
        return connection;
    }





    static void createTableIfNotExists() {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS weather (\n" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "city TEXT NOT NULL,\n" +
                "date_time TEXT NOT NULL,\n" +
                "weather_text TEXT NOT NULL,\n" +
                "temperature DOUBLE NOT NULL);";

        try (Connection connection = WeatherRepository.getConnection()) {
            connection.createStatement().execute(createTableQuery);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static boolean saveWeatherData(WeatherData weatherData) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement saveWeather = connection.prepareStatement(insertWeatherQuery)) {
            saveWeather.setString(1, weatherData.getCity());
            saveWeather.setString(2, weatherData.getLocalDate());
            saveWeather.setString(3, weatherData.getText());
            saveWeather.setDouble(4, weatherData.getTemperature());
            return saveWeather.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        throw new SQLException("Failure on saving weather object");
    }

    public static void readWeatherData() throws SQLException {
        System.out.println("Вызов метода readWeatherData()");
        try {
            Connection connection = getConnection();
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM weather");

            while(resultSet.next()){
                int id = resultSet.getInt("id");

                String city = resultSet.getString("city");

                String date_time = resultSet.getString("date_time");

                String weather_text = resultSet.getString("weather_text");

                double temperature = resultSet.getDouble("temperature");
                System.out.println("| " + id + " | " + city + " | " + date_time + " | " + weather_text + " | " + temperature + " |");
            }
        }catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void closeConnection() throws SQLException {

            if(preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if(connection != null) {
                try {
                    connection.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
    }
}

