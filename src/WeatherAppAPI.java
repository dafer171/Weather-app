import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class WeatherAppAPI {

    // Main method that fetches weather data for a given city
    public static JSONObject fetchWeatherData(String cityName) {
        // Get location coordinates by calling the getLocationCoordinates function
        JSONArray locationData = getLocationCoordinates(cityName);

        // If the location is not found, return null
        if (locationData == null || locationData.isEmpty())
            return null;

        // Extract the location and coordinates (latitude and longitude)
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        // Construct the API URL with the coordinates
        String apiUrl = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude
                + "&hourly=temperature_2m,weather_code,wind_speed_10m,relative_humidity_2m";
        try {
            // Connect to the API and fetch the data
            HttpURLConnection connection = connectToAPI(apiUrl);
            // If the response is not successful (code 200), return null
            if (connection.getResponseCode() != 200) {
                System.out.println("Error: Unable to fetch weather data");
                return null;
            }

            // Read the API response
            StringBuilder response = new StringBuilder();
            Scanner scanner = new Scanner(connection.getInputStream());
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();

            // Parse the weather data and return the processed data
            return parseWeatherData(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Return null in case of an error
    }

    // Method that gets the coordinates (latitude and longitude) of the city
    private static JSONArray getLocationCoordinates(String cityName) {
        // Build the URL for the geocoding API
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" + cityName.replace(" ", "+")
                + "&count=1&language=en&format=json";
        try {
            // Connect to the geocoding API
            HttpURLConnection connection = connectToAPI(urlString);
            // If the response is not successful (code 200), return null
            if (connection.getResponseCode() != 200)
                return null;

            // Read the API response
            StringBuilder result = new StringBuilder();
            Scanner scanner = new Scanner(connection.getInputStream());
            while (scanner.hasNext()) {
                result.append(scanner.nextLine());
            }
            scanner.close();

            // Parse the location data and return the list of results
            return parseLocationData(result.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // Return null in case of an error
    }

    // Method that sets up the HTTP connection to the API
    private static HttpURLConnection connectToAPI(String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET"); // Set the request method to GET
        connection.connect(); // Connect to the API
        return connection;
    }

    // Method that parses the weather data JSON response from the API
    private static JSONObject parseWeatherData(String jsonData) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonData);
            JSONObject hourlyData = (JSONObject) jsonObject.get("hourly");

            // Find the index corresponding to the current time
            int currentIndex = findCurrentTimeIndex((JSONArray) hourlyData.get("time"));
            // Extract the weather information for that index
            return extractWeatherInfo(hourlyData, currentIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Return null in case of an error
    }

    // Method that parses the location data JSON response
    private static JSONArray parseLocationData(String jsonData) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonData);
            return (JSONArray) jsonObject.get("results"); // Return the search results
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Return null in case of an error
    }

    // Method that finds the index corresponding to the current time in the list of
    // times
    private static int findCurrentTimeIndex(JSONArray timeList) {
        // Get the current time in the required format
        String currentTime = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:00"));
        // Search for the index corresponding to the current time
        for (int i = 0; i < timeList.size(); i++) {
            if (timeList.get(i).equals(currentTime))
                return i;
        }
        return 0; // Return 0 if the exact time is not found
    }

    // Method that extracts weather information for a specific index
    private static JSONObject extractWeatherInfo(JSONObject hourlyData, int index) {
        JSONObject weatherInfo = new JSONObject();

        // Access the weather data from the JSON arrays
        weatherInfo.put("temperature", (double) ((JSONArray) hourlyData.get("temperature_2m")).get(index));
        weatherInfo.put("weatherCondition",
                convertWeatherCode((long) ((JSONArray) hourlyData.get("weather_code")).get(index)));
        weatherInfo.put("humidity", (long) ((JSONArray) hourlyData.get("relative_humidity_2m")).get(index));
        weatherInfo.put("windspeed", (double) ((JSONArray) hourlyData.get("wind_speed_10m")).get(index));

        return weatherInfo; // Return the weather information object
    }

    // Method that converts the numerical weather code to a human-readable
    // description
    private static String convertWeatherCode(long code) {
        // Convert the numeric code to a weather condition description
        if (code == 0)
            return "Clear"; // Clear sky
        if (code <= 3)
            return "Cloudy"; // Cloudy
        if (code <= 99)
            return "Rain"; // Rain
        if (code <= 77)
            return "Snow"; // Snow
        return "Unknown"; // Unknown condition
    }
}
