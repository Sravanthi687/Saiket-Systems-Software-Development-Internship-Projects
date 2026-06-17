import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SimpleBlogApp {
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String CYAN = "\u001B[36m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String PURPLE = "\u001B[35m";
    private static final String BG_PURPLE = "\u001B[45m";

    private static final String FILE_PATH = "blog_posts.txt";
    private static List<Post> posts = new ArrayList<>();

    static class Post {
        int id;
        String title;
        String category;
        String content;

        Post(int id, String title, String category, String content) {
            this.id = id;
            this.title = title;
            this.category = category;
            this.content = content;
        }

        // Convert post to line representation for local storage file
        String toFileLine() {
            // Escapes tab and newlines to prevent file layout corruption
            String escapedTitle = title.replace("\t", " ").replace("\n", " ");
            String escapedCategory = category.replace("\t", " ").replace("\n", " ");
            String escapedContent = content.replace("\t", " ").replace("\n", "\\n");
            return id + "\t" + escapedTitle + "\t" + escapedCategory + "\t" + escapedContent;
        }

        static Post fromFileLine(String line) {
            String[] parts = line.split("\t");
            if (parts.length < 4) return null;
            int id = Integer.parseInt(parts[0]);
            String title = parts[1];
            String category = parts[2];
            String content = parts[3].replace("\\n", "\n");
            return new Post(id, title, category, content);
        }
    }

    public static void main(String[] args) {
        loadPostsFromFile();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            printMenu();
            System.out.print(CYAN + "Select an action (1-5): " + RESET);
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    listPosts();
                    break;
                case "2":
                    readPost(scanner);
                    break;
                case "3":
                    createPost(scanner);
                    break;
                case "4":
                    deletePost(scanner);
                    break;
                case "5":
                    System.out.println(GREEN + "\nThank you for using Saiket Systems Blog App! Exiting..." + RESET);
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
        System.out.println("\n" + BG_PURPLE + BOLD + " =================================================== " + RESET);
        System.out.println(BG_PURPLE + BOLD + "               SAIKET SYSTEMS SIMPLE BLOG APP        " + RESET);
        System.out.println(BG_PURPLE + BOLD + " =================================================== " + RESET + "\n");
        System.out.println("1. List All Blog Posts");
        System.out.println("2. Read a Specific Post");
        System.out.println("3. Create a New Post");
        System.out.println("4. Delete a Post");
        System.out.println("5. Exit\n");
    }

    private static void listPosts() {
        System.out.println("\n" + YELLOW + BOLD + "--------------------- BLOG POSTS ---------------------" + RESET);
        if (posts.isEmpty()) {
            System.out.println(" No posts found. Select option 3 to create one!");
        } else {
            for (Post post : posts) {
                System.out.printf(BOLD + "[%d] %s" + RESET + " (Category: %s)\n", post.id, post.title, post.category);
            }
        }
        System.out.println(YELLOW + "------------------------------------------------------" + RESET);
    }

    private static void readPost(Scanner scanner) {
        listPosts();
        if (posts.isEmpty()) return;

        System.out.print(CYAN + "\nEnter the Post ID to read: " + RESET);
        try {
            int targetId = Integer.parseInt(scanner.nextLine().trim());
            Post targetPost = findPostById(targetId);

            if (targetPost != null) {
                System.out.println("\n" + GREEN + BOLD + "======================================================" + RESET);
                System.out.println(BOLD + " TITLE   : " + RESET + CYAN + BOLD + targetPost.title + RESET);
                System.out.println(BOLD + " CATEGORY: " + RESET + YELLOW + targetPost.category + RESET);
                System.out.println(GREEN + "------------------------------------------------------" + RESET);
                System.out.println(targetPost.content);
                System.out.println(GREEN + "======================================================" + RESET);
            } else {
                System.out.println(RED + "Post ID not found." + RESET);
            }
        } catch (NumberFormatException e) {
            System.out.println(RED + "Please enter a valid numeric Post ID." + RESET);
        }
    }

    private static void createPost(Scanner scanner) {
        System.out.println("\n" + GREEN + BOLD + "---------------- CREATE NEW POST ----------------" + RESET);
        
        System.out.print(CYAN + "Enter Post Title: " + RESET);
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) {
            System.out.println(RED + "Title cannot be empty." + RESET);
            return;
        }

        System.out.print(CYAN + "Enter Post Category: " + RESET);
        String category = scanner.nextLine().trim();
        if (category.isEmpty()) {
            System.out.println(RED + "Category cannot be empty." + RESET);
            return;
        }

        System.out.println(CYAN + "Enter Post Content (Type ':w' on a new line and press Enter to save):" + RESET);
        StringBuilder contentBuilder = new StringBuilder();
        while (true) {
            String line = scanner.nextLine();
            if (line.trim().equals(":w")) {
                break;
            }
            contentBuilder.append(line).append("\n");
        }
        String content = contentBuilder.toString().trim();

        int newId = posts.isEmpty() ? 1 : posts.get(posts.size() - 1).id + 1;
        Post newPost = new Post(newId, title, category, content);
        posts.add(newPost);
        savePostsToFile();

        System.out.println(GREEN + "\nPost successfully created and saved to local storage!" + RESET);
    }

    private static void deletePost(Scanner scanner) {
        listPosts();
        if (posts.isEmpty()) return;

        System.out.print(CYAN + "\nEnter the Post ID to delete: " + RESET);
        try {
            int targetId = Integer.parseInt(scanner.nextLine().trim());
            Post targetPost = findPostById(targetId);

            if (targetPost != null) {
                posts.remove(targetPost);
                savePostsToFile();
                System.out.println(GREEN + "Post ID [" + targetId + "] successfully deleted!" + RESET);
            } else {
                System.out.println(RED + "Post ID not found." + RESET);
            }
        } catch (NumberFormatException e) {
            System.out.println(RED + "Please enter a valid numeric Post ID." + RESET);
        }
    }

    private static Post findPostById(int id) {
        for (Post p : posts) {
            if (p.id == id) {
                return p;
            }
        }
        return null;
    }

    private static void loadPostsFromFile() {
        posts.clear();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            // Seed a few initial demo posts if file does not exist
            posts.add(new Post(1, "Welcome to Saiket Systems", "Introduction", "We are a prominent technology company specializing in Cloud, AI, and Blockchain."));
            posts.add(new Post(2, "My Internship Journey", "Experience", "Starting my software engineering internship tasks today. Exciting times ahead!"));
            savePostsToFile();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Post post = Post.fromFileLine(line);
                if (post != null) {
                    posts.add(post);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading blog posts: " + e.getMessage());
        }
    }

    private static void savePostsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Post post : posts) {
                writer.write(post.toFileLine());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving blog posts: " + e.getMessage());
        }
    }
}
