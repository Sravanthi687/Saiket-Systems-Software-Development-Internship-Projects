import java.util.Scanner;

public class LoanEmiCalculator {
    // ANSI Colors for premium terminal styling
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String CYAN = "\u001B[36m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String PURPLE = "\u001B[35m";
    private static final String BG_BLUE = "\u001B[44m";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        printHeader();

        while (true) {
            try {
                System.out.print(CYAN + "Enter Loan Principal Amount (P): " + RESET);
                double principal = scanner.nextDouble();
                if (principal <= 0) {
                    System.out.println(RED + "Principal must be greater than zero." + RESET);
                    continue;
                }

                System.out.print(CYAN + "Enter Annual Interest Rate (R in %): " + RESET);
                double annualRate = scanner.nextDouble();
                if (annualRate <= 0) {
                    System.out.println(RED + "Interest rate must be greater than zero." + RESET);
                    continue;
                }

                System.out.print(CYAN + "Enter Loan Tenure in Months (N): " + RESET);
                int tenureMonths = scanner.nextInt();
                if (tenureMonths <= 0) {
                    System.out.println(RED + "Tenure must be greater than zero." + RESET);
                    continue;
                }

                // EMI Calculation
                double monthlyRate = annualRate / 12 / 100;
                double emi;
                if (monthlyRate == 0) {
                    emi = principal / tenureMonths;
                } else {
                    emi = (principal * monthlyRate * Math.pow(1 + monthlyRate, tenureMonths)) 
                          / (Math.pow(1 + monthlyRate, tenureMonths) - 1);
                }

                double totalPayment = emi * tenureMonths;
                double totalInterest = totalPayment - principal;

                // Display Results
                printResults(principal, annualRate, tenureMonths, emi, totalInterest, totalPayment);

                // Ask for Amortization Schedule
                System.out.print(YELLOW + "\nWould you like to view the detailed Amortization Schedule? (y/n): " + RESET);
                String choice = scanner.next();
                if (choice.equalsIgnoreCase("y")) {
                    printAmortizationSchedule(principal, monthlyRate, tenureMonths, emi);
                }

                // Continue or Exit
                System.out.print(PURPLE + "\nDo you want to calculate another loan? (y/n): " + RESET);
                String runAgain = scanner.next();
                if (!runAgain.equalsIgnoreCase("y")) {
                    System.out.println(GREEN + "\nThank you for using Saiket Systems Loan EMI Calculator! Keep innovating." + RESET);
                    break;
                }
                printHeader();

            } catch (Exception e) {
                System.out.println(RED + "Invalid input. Please enter valid numbers." + RESET);
                scanner.nextLine(); // Clear buffer
            }
        }
        scanner.close();
    }

    private static void printHeader() {
        System.out.println("\n" + BG_BLUE + BOLD + " =================================================== " + RESET);
        System.out.println(BG_BLUE + BOLD + "               SAIKET SYSTEMS EMI CALCULATOR         " + RESET);
        System.out.println(BG_BLUE + BOLD + " =================================================== " + RESET + "\n");
        System.out.println(BOLD + "Provide the details below to compute your monthly payments:" + RESET);
    }

    private static void printResults(double p, double r, int n, double emi, double interest, double total) {
        System.out.println("\n" + GREEN + BOLD + "------------------- LOAN SUMMARY -------------------" + RESET);
        System.out.printf(BOLD + "  Principal Amount   : " + RESET + "₹%,.2f\n", p);
        System.out.printf(BOLD + "  Annual Rate        : " + RESET + "%.2f%%\n", r);
        System.out.printf(BOLD + "  Tenure             : " + RESET + "%d Months\n", n);
        System.out.println(GREEN + "----------------------------------------------------" + RESET);
        System.out.printf(CYAN + BOLD + "  MONTHLY EMI        : " + RESET + BOLD + "₹%,.2f\n", emi);
        System.out.printf(YELLOW + BOLD + "  TOTAL INTEREST     : " + RESET + "₹%,.2f\n", interest);
        System.out.printf(PURPLE + BOLD + "  TOTAL PAYMENT      : " + RESET + "₹%,.2f\n", total);
        System.out.println(GREEN + "----------------------------------------------------" + RESET);
    }

    private static void printAmortizationSchedule(double principal, double monthlyRate, int tenure, double emi) {
        System.out.println("\n" + CYAN + BOLD + "-------------------------------- AMORTIZATION SCHEDULE --------------------------------" + RESET);
        System.out.printf(BOLD + "%-6s | %-15s | %-12s | %-12s | %-12s | %-15s\n" + RESET, 
                          "Month", "Opening Bal", "EMI Paid", "Interest", "Principal", "Closing Bal");
        System.out.println("---------------------------------------------------------------------------------------");

        double balance = principal;
        for (int m = 1; m <= tenure; m++) {
            double interestPaid = balance * monthlyRate;
            double principalPaid = emi - interestPaid;
            double closingBalance = balance - principalPaid;
            if (closingBalance < 0 || m == tenure) {
                closingBalance = 0;
            }

            System.out.printf("%-6d | ₹%,-14.2f | ₹%,-11.2f | ₹%,-11.2f | ₹%,-11.2f | ₹%,-14.2f\n", 
                              m, balance, emi, interestPaid, principalPaid, closingBalance);

            balance = closingBalance;
        }
        System.out.println("---------------------------------------------------------------------------------------");
    }
}
