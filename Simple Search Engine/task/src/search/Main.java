package search;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {
    private static List<String> people = new ArrayList<String>();
    private static Map<String, Set<Integer>> dict = new HashMap<>();
    private static Scanner scanner;
    private static File file;

    public static void main(@NotNull String[] args) {
        if (args[0].equals("--data")) {
            file = new File(args[1]);
            readFile();
        }

        printMenu();
        scanner = new Scanner(System.in);
        int option = scanner.nextInt();
        scanner.nextLine();

        while (option != 0) {
            switch (option) {
                case 1:
                    findAPerson();
                    break;
                case 2:
                    findAllPeople();
                    break;
                default:
                    System.out.println("Incorrect option! Try again.");
            }
            printMenu();
            option = scanner.nextInt();
            scanner.nextLine();
        }
        System.out.println("Bye!");
    }

    private static void findAPerson() {
        System.out.println("Select a matching strategy: ALL, ANY, NONE");
        SearchEngine search = new SearchEngine();
        switch (scanner.nextLine()) {
            case "ANY":
                search.setStrategy(new SearchAny());
                break;
            case "ALL":
                search.setStrategy(new SearchAll());
                break;
            case "NONE":
                search.setStrategy(new SearchNone());
                break;
            default:
        }
        System.out.println("Enter a name or email to search all suitable people:");
        String[] wordsArray = scanner.nextLine().toLowerCase().trim().split("\\s+");
        Set<Integer> found = search.search(wordsArray, dict, people);
        if (found.size() > 0) {
            System.out.println(found.size() + " persons found:");
            found.forEach(e -> System.out.println(people.get(e)));
        } else {
            System.out.println("No matching people found.");
        }
    }

    private static void findAllPeople() {
        System.out.println("=== List of people ===");
        for (String record : people) {
            System.out.println(record);
        }
    }

    private static void readFile() {
        try (Scanner fileScanner = new Scanner(file)) {
            int lineNumber = 0;
            while (fileScanner.hasNext()) {
                String line = fileScanner.nextLine();
                people.add(line);
                String[] listOfWords = line.toLowerCase().split("\\s+");
                for (String word : listOfWords) {
                    if (dict.containsKey(word)) {
                        dict.get(word).add(lineNumber);
                    } else {
                        Set<Integer> value = new TreeSet<>();
                        value.add(lineNumber);
                        dict.put(word, value);
                    }
                }
                lineNumber++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void printMenu() {
        System.out.println("\n=== Menu ===");
        System.out.println("1. Find a person");
        System.out.println("2. Print all people");
        System.out.println("0. Exit");
    }
}

class SearchEngine {
    private SearchStrategy strategy;

    public void setStrategy(SearchStrategy strategy) {
        this.strategy = strategy;
    }

    public Set<Integer> search(String[] wordsArray, Map<String, Set<Integer>> dict, List<String> people) {
        return this.strategy.search(wordsArray, dict, people);
    }
}

interface SearchStrategy {

    Set<Integer> search(String[] wordsArray, Map<String, Set<Integer>> dict, List<String> people);

}

class SearchAny implements SearchStrategy {
    @Override
    public Set<Integer> search(String[] wordsArray, Map<String, Set<Integer>> dict, List<String> people) {
        Set<Integer> found = new TreeSet<>();
        for (String word : wordsArray) {
            found.addAll(dict.get(word));
        }
        return found;
    }
}

class SearchAll implements SearchStrategy {
    @Override
    public Set<Integer> search(String[] wordsArray, Map<String, Set<Integer>> dict, List<String> people) {
        Set<Integer> found = new TreeSet<>();
        if (dict.containsKey(wordsArray[0])) {
            found.addAll(dict.get(wordsArray[0]));
        }
        for (int i = 1; i < wordsArray.length; i++) {
            if (dict.containsKey(wordsArray[i])) {
                found.addAll(dict.get(wordsArray[i]));
            } else {
                return new TreeSet<>();
            }
        }
        return found;
    }
}

class SearchNone implements SearchStrategy {
    @Override
    public Set<Integer> search(String[] wordsArray, Map<String, Set<Integer>> dict, List<String> people) {
        Set<Integer> found = new TreeSet<>();
        for (int i = 0; i < people.size(); i++) found.add(i);
        for (String s : wordsArray) {
            found.removeAll(dict.get(s));
        }
        return found;
    }
}