package HW6;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.sql.SQLException;

public class MainApp {

    public static String city;

    public static void main(String[] args) throws IOException, SQLException {


        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        WeatherRepository.getConnection();
        WeatherRepository.createTableIfNotExists();

        UserInterface userInterface = new UserInterface();
        userInterface.runApplication();
    }

    private static String readUsingBufferedReader(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader( new FileReader (fileName));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("\n");
        while( ( line = reader.readLine() ) != null ) {
            stringBuilder.append( line );
            stringBuilder.append( ls );
        }

        stringBuilder.deleteCharAt(stringBuilder.length()-1);
        return stringBuilder.toString();
    }

    public static String navigatingByJsonAsNodeTree(String file, String path) throws IOException {
        String contents = readUsingBufferedReader(file);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode text = objectMapper.readTree(contents).at(path);

        return text.asText();
    }

    public static void print5DayWeather() throws IOException {
        File day1 = new File("day1.json");
        day1.createNewFile();
        File day2 = new File("./day2.json");
        day2.createNewFile();
        File day3 = new File("./day3.json");
        day3.createNewFile();
        File day4 = new File("./day4.json");
        day4.createNewFile();
        File day5 = new File("./day5.json");
        day5.createNewFile();
        File day[] = {day1, day2, day3, day4, day5};

        try (BufferedReader in = new BufferedReader(new FileReader("./weather.json"))) {
            String result;
            int i = 0;
            while ((result = in.readLine()) != null) {
                String[] subStr;
                subStr = result.split("\"Date\"");

                int m = 0;
                String[] str = new String[5];
                for ( String s : subStr ) {
                    if (m > 0) {
                        str[m - 1] = "{\"Date\"" + s;
                        if (m < 5) {
                            str[m - 1] = str[m - 1].replaceAll("[,{]+$", "");
                        } else {
                            str[m - 1] = str[m - 1].replaceAll("[]}]+$", "");
                            str[m - 1] = str[m - 1] + "}";
                        }
                        FileOutputStream fos = new
                                FileOutputStream(day[m - 1], false);
                        byte[] b = str[m - 1].getBytes();

                        fos.write(b);
                        fos.close();
                        System.out.println("В городе " + ApplicationGlobalState.getInstance().getSelectedCity()
                                + " на дату " + navigatingByJsonAsNodeTree(day[m - 1].getPath(), "/Date")
                                + " ожидается: \"" + navigatingByJsonAsNodeTree("./weather.json", "/Headline/Text")
                                + "\", температура " + navigatingByJsonAsNodeTree(day[m - 1].getPath(), "/Temperature/Maximum/Value") + "°C");
                        String date = navigatingByJsonAsNodeTree(day[m - 1].getPath(), "/Date");
                        String city = ApplicationGlobalState.getInstance().getSelectedCity();
                        String text = navigatingByJsonAsNodeTree("./weather.json", "/Headline/Text");
                        Double temperature = Double.parseDouble(navigatingByJsonAsNodeTree(day[m - 1].getPath(), "/Temperature/Maximum/Value"));

                        WeatherData weaterData = new WeatherData(city, date, text, temperature);
                        try {
                            boolean b1 = WeatherRepository.saveWeatherData(weaterData);
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }

                    }
                    m++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}