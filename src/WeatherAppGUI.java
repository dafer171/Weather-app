import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import org.json.simple.JSONObject;

// Main GUI class that extends JFrame to create a window for the weather application
public class WeatherAppGUI extends JFrame {
    private JSONObject weatherData; // JSON object to store weather data

    // Constructor to set up the main frame for the application
    public WeatherAppGUI() {
        setTitle("Weather App"); // Set the window title
        setDefaultCloseOperation(EXIT_ON_CLOSE); // Close the app when the window is closed
        setSize(500, 700); // Set window size
        setLocationRelativeTo(null); // Center the window on the screen
        setLayout(null); // No layout manager
        setResizable(false); // Prevent resizing of the window

        initializeComponents(); // Initialize the components (UI elements)
    }

    // Method to initialize and set up all the UI components
    private void initializeComponents() {
        // TextField for the city input
        JTextField cityInputField = new JTextField();
        cityInputField.setBounds(30, 30, 350, 40); // Set position and size
        cityInputField.setFont(new Font("Arial", Font.PLAIN, 20)); // Set font
        add(cityInputField); // Add the text field to the frame

        // Label for displaying the weather icon
        JLabel weatherIconLabel = new JLabel(loadImage("src/assets/cloudy.png"));
        weatherIconLabel.setBounds(70, 120, 360, 200); // Set position and size
        add(weatherIconLabel); // Add the label to the frame

        // Label for displaying temperature
        JLabel temperatureLabel = new JLabel("Temperature: 20°C");
        temperatureLabel.setBounds(0, 350, 500, 60); // Set position and size
        temperatureLabel.setFont(new Font("Arial", Font.BOLD, 48)); // Set font size and style
        temperatureLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center the text
        add(temperatureLabel); // Add the label to the frame

        // Label for displaying weather description
        JLabel descriptionLabel = new JLabel("Clear");
        descriptionLabel.setBounds(0, 420, 500, 40); // Set position and size
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 30)); // Set font
        descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center the text
        add(descriptionLabel); // Add the label to the frame

        // Label for displaying humidity
        JLabel humidityLabel = new JLabel("Humidity: 75%");
        humidityLabel.setBounds(30, 510, 200, 40); // Set position and size
        humidityLabel.setFont(new Font("Arial", Font.PLAIN, 18)); // Set font
        add(humidityLabel); // Add the label to the frame

        // Label for displaying humidity image
        JLabel humidityImage = new JLabel(loadImage("src/assets/humidity.png"));
        humidityImage.setBounds(55, 550, 74, 66);
        add(humidityImage);

        // Label for displaying wind speed
        JLabel windSpeedLabel = new JLabel("Wind Speed: 12 km/h");
        windSpeedLabel.setBounds(250, 510, 200, 40); // Set position and size
        windSpeedLabel.setFont(new Font("Arial", Font.PLAIN, 18)); // Set font
        add(windSpeedLabel); // Add the label to the frame

        // Label for displaying windspeed image
        JLabel windspeedImage = new JLabel(loadImage("src/assets/windspeed.png"));
        windspeedImage.setBounds(300, 550, 74, 66);
        add(windspeedImage);

        // Search button for fetching weather data
        JButton searchButton = new JButton(loadImage("src/assets/search.png"));
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Change cursor to hand
        searchButton.setBounds(400, 30, 50, 40); // Set position and size
        searchButton.addActionListener(new ActionListener() { // Add action listener to button
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the city name entered by the user
                String city = cityInputField.getText().trim();
                if (!city.isEmpty()) { // If city name is not empty
                    weatherData = WeatherAppAPI.fetchWeatherData(city); // Fetch weather data
                    // Update UI with fetched weather data
                    updateUI(weatherData, weatherIconLabel, temperatureLabel, descriptionLabel, humidityLabel,
                            windSpeedLabel);
                }
            }
        });
        add(searchButton); // Add the button to the frame
    }

    // Method to update the UI with fetched weather data
    private void updateUI(JSONObject data, JLabel weatherIconLabel, JLabel temperatureLabel, JLabel descriptionLabel,
            JLabel humidityLabel, JLabel windSpeedLabel) {
        String condition = (String) data.get("weatherCondition"); // Get weather condition (Clear, Cloudy, etc.)
        // Switch based on the weather condition to update the icon
        switch (condition) {
            case "Clear":
                weatherIconLabel.setIcon(loadImage("src/assets/clear.png"));
                break;
            case "Cloudy":
                weatherIconLabel.setIcon(loadImage("src/assets/cloudy.png"));
                break;
            case "Rain":
                weatherIconLabel.setIcon(loadImage("src/assets/rain.png"));
                break;
            case "Snow":
                weatherIconLabel.setIcon(loadImage("src/assets/snow.png"));
                break;
            default:
                weatherIconLabel.setIcon(loadImage("src/assets/default.png"));
                break;
        }

        // Set the temperature, description, humidity, and wind speed labels with the
        // fetched data
        temperatureLabel.setText(data.get("temperature") + "°C");
        descriptionLabel.setText(condition);
        humidityLabel.setText("Humidity: " + data.get("humidity") + "%");
        windSpeedLabel.setText("Wind Speed: " + data.get("windspeed") + " km/h");
    }

    // Method to load images and return as ImageIcon
    private ImageIcon loadImage(String path) {
        try {
            BufferedImage image = ImageIO.read(new File(path)); // Read the image from file
            return new ImageIcon(image); // Return the image as an ImageIcon
        } catch (IOException e) { // Handle exceptions if image cannot be read
            e.printStackTrace();
            return null; // Return null if image fails to load
        }
    }
}
