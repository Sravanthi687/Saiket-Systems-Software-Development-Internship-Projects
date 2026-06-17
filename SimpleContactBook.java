import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SimpleContactBook {
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String CYAN = "\u001B[36m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String PURPLE = "\u001B[35m";
    private static final String BG_CYAN = "\u001B[46m";

    private static final String FILE_PATH = "contacts.txt";
    private static List<Contact> contacts = new ArrayList<>();

    static class Contact {
        String name;
        String phone;
        String email;

        Contact(String name, String phone, String email) {
            this.name = name;
            this.phone = phone;
            this.email = email;
        }

        String toFileLine() {
            return name.replace("\t", " ") + "\t" + phone.replace("\t", " ") + "\t" + email.replace("\t", " ");
        }

        static Contact fromFileLine(String line) {
            String[] parts = line.split("\t");
            if (parts.length < 3) return null;
            return new Contact(parts[0], parts[1], parts[2]);
        }
    }

    public static void main(String[] args) {
        loadContactsFromFile();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            printMenu();
            System.out.print(CYAN + "Select an action (1-5): " + RESET);
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    addContact(scanner);
                    break;
                case "2":
                    viewAllContacts();
                    break;
                case "3":
                    searchContacts(scanner);
                    break;
                case "4":
                    deleteContact(scanner);
                    break;
                case "5":
                    System.out.println(GREEN + "\nThank you for using Saiket Systems Contact Book! Exiting..." + RESET);
                    scanner.close();
                    return;
                default:
                    System.out.println(RED + "Invalid option. Please input 1-5." + RESET);
            }

            System.out.println(PURPLE + "\nPress Enter to continue..." + RESET);
            scanner.nextLine();
        }
    }

    private static void printMenu() {
        System.out.println("\n" + BG_CYAN + BOLD + " =================================================== " + RESET);
        System.out.println(BG_CYAN + BOLD + "               SAIKET SYSTEMS CONTACT BOOK           " + RESET);
        System.out.println(BG_CYAN + BOLD + " =================================================== " + RESET + "\n");
        System.out.println("1. Add New Contact");
        System.out.println("2. View All Contacts");
        System.out.println("3. Search Contact By Name");
        System.out.println("4. Delete a Contact");
        System.out.println("5. Exit\n");
    }

    private static void addContact(Scanner scanner) {
        System.out.println("\n" + GREEN + BOLD + "----------------- ADD NEW CONTACT -----------------" + RESET);
        
        System.out.print(CYAN + "Enter Name: " + RESET);
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println(RED + "Name cannot be empty." + RESET);
            return;
        }

        System.out.print(CYAN + "Enter Phone Number: " + RESET);
        String phone = scanner.nextLine().trim();
        if (phone.isEmpty()) {
            System.out.println(RED + "Phone number cannot be empty." + RESET);
            return;
        }

        System.out.print(CYAN + "Enter Email: " + RESET);
        String email = scanner.nextLine().trim();
        if (email.isEmpty()) {
            System.out.println(RED + "Email cannot be empty." + RESET);
            return;
        }

        contacts.add(new Contact(name, phone, email));
        saveContactsToFile();
        System.out.println(GREEN + "\nContact successfully added and saved!" + RESET);
    }

    private static void viewAllContacts() {
        System.out.println("\n" + YELLOW + BOLD + "---------------------- ALL CONTACTS ----------------------" + RESET);
        if (contacts.isEmpty()) {
            System.out.println(" No contacts found.");
        } else {
            System.out.printf(BOLD + "%-4s | %-20s | %-15s | %-25s\n" + RESET, "ID", "Name", "Phone", "Email");
            System.out.println("----------------------------------------------------------");
            for (int i = 0; i < contacts.size(); i++) {
                Contact c = contacts.get(i);
                System.out.printf("%-4d | %-20s | %-15s | %-25s\n", i + 1, c.name, c.phone, c.email);
            }
        }
        System.out.println(YELLOW + "----------------------------------------------------------" + RESET);
    }

    private static void searchContacts(Scanner scanner) {
        System.out.print(CYAN + "\nEnter name to search: " + RESET);
        String query = scanner.nextLine().trim().toLowerCase();
        if (query.isEmpty()) return;

        System.out.println("\n" + YELLOW + BOLD + "-------------------- SEARCH RESULTS --------------------" + RESET);
        boolean found = false;
        System.out.printf(BOLD + "%-4s | %-20s | %-15s | %-25s\n" + RESET, "ID", "Name", "Phone", "Email");
        System.out.println("----------------------------------------------------------");
        for (int i = 0; i < contacts.size(); i++) {
            Contact c = contacts.get(i);
            if (c.name.toLowerCase().contains(query)) {
                System.out.printf("%-4d | %-20s | %-15s | %-25s\n", i + 1, c.name, c.phone, c.email);
                found = true;
            }
        }
        if (!found) {
            System.out.println(" No matching contacts found.");
        }
        System.out.println(YELLOW + "--------------------------------------------------------" + RESET);
    }

    private static void deleteContact(Scanner scanner) {
        viewAllContacts();
        if (contacts.isEmpty()) return;

        System.out.print(CYAN + "\nEnter the Contact ID (Number) to delete: " + RESET);
        try {
            int targetId = Integer.parseInt(scanner.nextLine().trim());
            int targetIndex = targetId - 1;

            if (targetIndex >= 0 && targetIndex < contacts.size()) {
                Contact removed = contacts.remove(targetIndex);
                saveContactsToFile();
                System.out.println(GREEN + "Contact '" + removed.name + "' successfully deleted!" + RESET);
            } else {
                System.out.println(RED + "Invalid Contact ID." + RESET);
            }
        } catch (NumberFormatException e) {
            System.out.println(RED + "Please enter a valid numeric ID." + RESET);
        }
    }

    private static void loadContactsFromFile() {
        contacts.clear();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            // Seed sample contacts
            contacts.add(new Contact("Sravanthi B", "9876543210", "sravanthi@saiket.com"));
            contacts.add(new Contact("SaiKet Admin", "9900887766", "info@saiketsystems.com"));
            saveContactsToFile();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Contact c = Contact.fromFileLine(line);
                if (c != null) {
                    contacts.add(c);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading contacts: " + e.getMessage());
        }
    }

    private static void saveContactsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Contact c : contacts) {
                writer.write(c.toFileLine());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving contacts: " + e.getMessage());
        }
    }
}
