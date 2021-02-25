package HW6;

public final class ApplicationGlobalState {

    private static ApplicationGlobalState INSTANCE;
    private static String selectedCity = null;
    private static String DB_FILENAME = "weather.db";
    private final String API_KEY = "1nrGCocrlgv9fXL10lakH6VO5QTQKZxC";

    private ApplicationGlobalState() {
    }

    public static ApplicationGlobalState getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ApplicationGlobalState();
        }

        return INSTANCE;
    }

    public static String getDbFileName() {
        return DB_FILENAME;
    }
    public static String getSelectedCity() {
        return selectedCity;
    }

    public void setSelectedCity(String selectedCity) {
        this.selectedCity = selectedCity;
    }

    public String getApiKey() {
        return this.API_KEY;
    }
}