import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextAutoCorrect {

    private Map<String, String> correctionMap;
    private Map<Pattern, String> patternMap;

    public TextAutoCorrect() {
        initializeCorrectionRules();
        initializePatternRules();
    }

    private void initializeCorrectionRules() {
        correctionMap = new HashMap<>();

        correctionMap.put("teh", "the");
        correctionMap.put("adn", "and");
        correctionMap.put("recieve", "receive");
        correctionMap.put("beleive", "believe");
        correctionMap.put("occured", "occurred");
        correctionMap.put("seperate", "separate");
        correctionMap.put("definately", "definitely");
        correctionMap.put("accomodate", "accommodate");
        correctionMap.put("acheive", "achieve");
        correctionMap.put("adress", "address");
        correctionMap.put("begining", "beginning");
        correctionMap.put("calender", "calendar");
        correctionMap.put("commited", "committed");
        correctionMap.put("concious", "conscious");
        correctionMap.put("enviroment", "environment");
        correctionMap.put("goverment", "government");
        correctionMap.put("independant", "independent");
        correctionMap.put("neccessary", "necessary");
        correctionMap.put("occassion", "occasion");
        correctionMap.put("untill", "until");
        correctionMap.put("wierd", "weird");
        correctionMap.put("thier", "their");
        correctionMap.put("freind", "friend");
        correctionMap.put("woudl", "would");
        correctionMap.put("coudl", "could");
        correctionMap.put("shoudl", "should");
    }

    private void initializePatternRules() {
        patternMap = new HashMap<>();
    }

    public String correctText(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        String[] words = text.split("\\s+");
        StringBuilder corrected = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            String correctedWord = correctWord(word);
            corrected.append(correctedWord);

            if (i < words.length - 1) {
                corrected.append(" ");
            }
        }

        return corrected.toString();
    }

    private String correctWord(String word) {
        String original = word;

        String prefix = "";
        String suffix = "";
        String cleanWord = word;

        while (!cleanWord.isEmpty() && !Character.isLetterOrDigit(cleanWord.charAt(0))) {
            prefix += cleanWord.charAt(0);
            cleanWord = cleanWord.substring(1);
        }

        while (!cleanWord.isEmpty() && !Character.isLetterOrDigit(cleanWord.charAt(cleanWord.length() - 1))) {
            suffix = cleanWord.charAt(cleanWord.length() - 1) + suffix;
            cleanWord = cleanWord.substring(0, cleanWord.length() - 1);
        }

        if (cleanWord.isEmpty()) {
            return original;
        }

        String lowerWord = cleanWord.toLowerCase();
        String corrected = cleanWord;

        if (correctionMap.containsKey(lowerWord)) {
            corrected = correctionMap.get(lowerWord);
        } else {
            corrected = fixRepeatedCharacters(cleanWord);
            corrected = fixDoubleSpaces(corrected);
        }

        corrected = preserveCase(cleanWord, corrected);

        return prefix + corrected + suffix;
    }

    private String fixRepeatedCharacters(String word) {
        if (word.length() < 3) return word;

        StringBuilder result = new StringBuilder();
        char prev = word.charAt(0);
        int count = 1;
        result.append(prev);

        for (int i = 1; i < word.length(); i++) {
            char current = word.charAt(i);
            if (current == prev) {
                count++;
                if (count <= 2) {
                    result.append(current);
                }
            } else {
                result.append(current);
                prev = current;
                count = 1;
            }
        }

        return result.toString();
    }

    private String fixDoubleSpaces(String text) {
        return text.replaceAll("\\s+", " ");
    }

    private String preserveCase(String original, String corrected) {
        if (original.isEmpty() || corrected.isEmpty()) {
            return corrected;
        }

        if (original.equals(original.toUpperCase())) {
            return corrected.toUpperCase();
        }

        if (Character.isUpperCase(original.charAt(0))) {
            return Character.toUpperCase(corrected.charAt(0)) +
                    corrected.substring(1).toLowerCase();
        }

        return corrected.toLowerCase();
    }

    public List<String> getSuggestions(String word) {
        List<String> suggestions = new ArrayList<>();
        String lowerWord = word.toLowerCase();

        if (correctionMap.containsKey(lowerWord)) {
            suggestions.add(correctionMap.get(lowerWord));
        }

        for (String key : correctionMap.keySet()) {
            if (editDistance(lowerWord, key) <= 2) {
                String correction = correctionMap.get(key);
                if (!suggestions.contains(correction)) {
                    suggestions.add(correction);
                }
            }
        }

        return suggestions;
    }

    private int editDistance(String s1, String s2) {
        int len1 = s1.length();
        int len2 = s2.length();
        int[][] dp = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1],
                            Math.min(dp[i - 1][j], dp[i][j - 1]));
                }
            }
        }

        return dp[len1][len2];
    }

    public void addCorrectionRule(String wrong, String correct) {
        correctionMap.put(wrong.toLowerCase(), correct.toLowerCase());
    }

    public void displayRules() {
        System.out.println("\n=== Correction Rules ===");
        correctionMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry ->
                        System.out.println(entry.getKey() + " -> " + entry.getValue())
                );
    }

    public static void main(String[] args) {
        TextAutoCorrect autoCorrect = new TextAutoCorrect();
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Text Auto-Correct System ===");
        System.out.println("Enter text to correct (or 'exit' to quit):");
        System.out.println("Commands:");
        System.out.println("  'rules' - Display all correction rules");
        System.out.println("  'add <wrong> <correct>' - Add custom rule");
        System.out.println("  'suggest <word>' - Get suggestions for a word");
        System.out.println();

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Goodbye!");
                break;
            }

            if (input.equalsIgnoreCase("rules")) {
                autoCorrect.displayRules();
                continue;
            }

            if (input.startsWith("add ")) {
                String[] parts = input.substring(4).split("\\s+");
                if (parts.length == 2) {
                    autoCorrect.addCorrectionRule(parts[0], parts[1]);
                    System.out.println("Rule added: " + parts[0] + " -> " + parts[1]);
                } else {
                    System.out.println("Usage: add <wrong> <correct>");
                }
                continue;
            }

            if (input.startsWith("suggest ")) {
                String word = input.substring(8).trim();
                List<String> suggestions = autoCorrect.getSuggestions(word);
                if (suggestions.isEmpty()) {
                    System.out.println("No suggestions found for: " + word);
                } else {
                    System.out.println("Suggestions for '" + word + "': " + suggestions);
                }
                continue;
            }

            String corrected = autoCorrect.correctText(input);

            if (!input.equals(corrected)) {
                System.out.println("Corrected: " + corrected);
            } else {
                System.out.println("No corrections needed.");
            }
        }

        scanner.close();
    }
}
