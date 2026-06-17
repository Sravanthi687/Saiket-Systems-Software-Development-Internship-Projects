import java.util.Random;
import java.util.Scanner;

public class NumberGuessingGame {
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String CYAN = "\u001B[36m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String PURPLE = "\u001B[35m";
    private static final String BG_GREEN = "\u001B[42m";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        while (true) {
            printHeader();
            int numberToGuess = random.nextInt(100) + 1; // 1 to 100
            int attempts = 0;
            boolean hasGuessedCorrectly = false;

            System.out.println(BOLD + "I have selected a secret number between 1 and 100." + RESET);
            System.out.println(BOLD + "Can you guess what it is?\n" + RESET);

            while (!hasGuessedCorrectly) {
                System.out.print(CYAN + "Enter your guess: " + RESET);
                String input = scanner.nextLine().trim();

                try {
                    int userGuess = Integer.parseInt(input);
                    attempts++;

                    if (userGuess < 1 || userGuess > 100) {
                        System.out.println(RED + "Please guess a number between 1 and 100." + RESET);
                        continue;
                    }

                    if (userGuess < numberToGuess) {
                        System.out.println(YELLOW + BOLD + "⚡ Too Low! Try a higher number." + RESET);
                    } else if (userGuess > numberToGuess) {
                        System.out.println(RED + BOLD + "⚡ Too High! Try a lower number." + RESET);
                    } else {
                        hasGuessedCorrectly = true;
                        System.out.println("\n" + GREEN + BOLD + "=============================================" + RESET);
                        System.out.println(GREEN + BOLD + "🎉 CONGRATULATIONS! YOU GUESSED IT! 🎉" + RESET);
                        System.out.println(BOLD + "  Secret Number    : " + RESET + GREEN + BOLD + numberToGuess + RESET);
                        System.out.println(BOLD + "  Total Attempts   : " + RESET + YELLOW + BOLD + attempts + RESET);
                        System.out.println(GREEN + BOLD + "=============================================" + RESET);
                    }

                } catch (NumberFormatException e) {
                    System.out.println(RED + "Invalid input! Please enter an integer between 1 and 100." + RESET);
                }
            }

            System.out.print(PURPLE + "\nWould you like to play again? (y/n): " + RESET);
            String playAgain = scanner.nextLine().trim();
            if (!playAgain.equalsIgnoreCase("y")) {
                System.out.println(GREEN + "\nThank you for playing Saiket Systems Number Guessing Game! Keep sharp!" + RESET);
                break;
            }
        }
        scanner.close();
    }

    private static void printHeader() {
        System.out.println("\n" + BG_GREEN + BOLD + " =================================================== " + RESET);
        System.out.println(BG_GREEN + BOLD + "            SAIKET SYSTEMS NUMBER GUESSING GAME      " + RESET);
        System.out.println(BG_GREEN + BOLD + " =================================================== " + RESET + "\n");
    }
}
