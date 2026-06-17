import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Scanner;

public class ApiDataDisplayer {
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String CYAN = "\u001B[36m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String PURPLE = "\u001B[35m";
    private static final String BG_CYAN = "\u001B[46m";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        while (true) {
            printMenu();
            System.out.print(CYAN + "Choose an option (1-4): " + RESET);
            String choice = scanner.nextLine().trim();

            if (choice.equals("4")) {
                System.out.println(GREEN + "\nThank you for using Saiket Systems InfoPulse Dashboard!" + RESET);
                break;
            }

            switch (choice) {
                case "1":
                    fetchWeather(client);
                    break;
                case "2":
                    fetchJoke(client);
                    break;
                case "3":
                    fetchQuote(client);
                    break;
                default:
                    System.out.println(RED + "Invalid choice! Please select 1, 2, 3, or 4." + RESET);
            }

            System.out.println(PURPLE + "\nPress Enter to return to the main menu..." + RESET);
            scanner.nextLine();
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n" + BG_CYAN + BOLD + " =================================================== " + RESET);
        System.out.println(BG_CYAN + BOLD + "               SAIKET SYSTEMS INFOPULSE API          " + RESET);
        System.out.println(BG_CYAN + BOLD + " =================================================== " + RESET + "\n");
        System.out.println("1. Fetch Live Weather Info (Open-Meteo API)");
        System.out.println("2. Fetch a Random Programming Joke");
        System.out.println("3. Fetch a Random Daily Quote");
        System.out.println("4. Exit\n");
    }

    private static void fetchWeather(HttpClient client) {
        System.out.println(YELLOW + "\nFetching current weather details for Bangalore (12.97, 77.59)..." + RESET);
        String url = "https://api.open-meteo.com/v1/forecast?latitude=12.9716&longitude=77.5946&current_weather=true";

        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String body = response.body();
                // Extract weather metrics from JSON string
                String temp = getValueFromKey(body, "temperature");
                String windspeed = getValueFromKey(body, "windspeed");
                String time = getValueFromKey(body, "time");

                System.out.println("\n" + GREEN + BOLD + "------------------- WEATHER REPORT -------------------" + RESET);
                System.out.println(BOLD + "  Location     : " + RESET + "Bangalore, India");
                System.out.println(BOLD + "  Temperature  : " + RESET + temp + " °C");
                System.out.println(BOLD + "  Wind Speed   : " + RESET + windspeed + " km/h");
                System.out.println(BOLD + "  Report Time  : " + RESET + time.replace("\"", ""));
                System.out.println(GREEN + "------------------------------------------------------" + RESET);
            } else {
                System.out.println(RED + "Failed to fetch weather data. HTTP Status: " + response.statusCode() + RESET);
            }
        } catch (Exception e) {
            System.out.println(RED + "Error fetching weather data: " + e.getMessage() + RESET);
        }
    }

    private static void fetchJoke(HttpClient client) {
        System.out.println(YELLOW + "\nFetching a programming joke..." + RESET);
        // Using Official Joke API (Programming category)
        String url = "https://official-joke-api.appspot.com/jokes/programming/random";

        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String body = response.body();
                // Parse joke fields
                String setup = getValueFromKey(body, "setup");
                String punchline = getValueFromKey(body, "punchline");

                System.out.println("\n" + GREEN + BOLD + "--------------------- JOKE CARD ---------------------" + RESET);
                System.out.println(CYAN + BOLD + "  Setup    : " + RESET + setup.replace("\"", ""));
                System.out.println(YELLOW + BOLD + "  Punchline: " + RESET + punchline.replace("\"", ""));
                System.out.println(GREEN + "-----------------------------------------------------" + RESET);
            } else {
                System.out.println(RED + "Failed to fetch joke. HTTP Status: " + response.statusCode() + RESET);
            }
        } catch (Exception e) {
            System.out.println(RED + "Error fetching joke: " + e.getMessage() + RESET);
        }
    }

    private static void fetchQuote(HttpClient client) {
        System.out.println(YELLOW + "\nFetching daily inspiration..." + RESET);
        // Using AdviceSlip API as quote fallback
        String url = "https://api.adviceslip.com/advice";

        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String body = response.body();
                String advice = getValueFromKey(body, "advice");

                System.out.println("\n" + GREEN + BOLD + "------------------- INSPIRATIONAL QUOTE -------------------" + RESET);
                System.out.println(CYAN + BOLD + "  \"" + advice.replace("\"", "") + "\"" + RESET);
                System.out.println(GREEN + "-----------------------------------------------------------" + RESET);
            } else {
                System.out.println(RED + "Failed to fetch quote. HTTP Status: " + response.statusCode() + RESET);
            }
        } catch (Exception e) {
            System.out.println(RED + "Error fetching quote: " + e.getMessage() + RESET);
        }
    }

    /**
     * Helper to search for and extract JSON value for a given key.
     * Simple robust solution for standalone files without JSON dependencies.
     */
    private static String getValueFromKey(String json, String key) {
        int index = json.indexOf("\"" + key + "\"");
        if (index == -1) return "N/A";
        
        int startPos = json.indexOf(":", index) + 1;
        int endPos;
        
        // Skip leading whitespace
        while (Character.isWhitespace(json.charAt(startPos))) {
            startPos++;
        }
        
        if (json.charAt(startPos) == '"') {
            startPos++;
            endPos = json.indexOf('"', startPos);
        } else {
            endPos = startPos;
            while (endPos < json.length() && json.charAt(endPos) != ',' && json.charAt(endPos) != '}' && json.charAt(endPos) != ']') {
                endPos++;
            }
        }
        return json.substring(startPos, endPos).trim();
    }
}
