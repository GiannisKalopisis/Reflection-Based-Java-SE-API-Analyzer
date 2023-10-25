import java.util.*;

public class Utils {

    public static <T> void sortList(List<T> list, Comparator<? super T> comparator) {
        list.sort(comparator);
    }

    public static <K, V extends Collection<?>> Map<K, V> sortByCollectionSizeDescending(Map<K, V> map) {
        List<Map.Entry<K, V>> entries = new ArrayList<>(map.entrySet());

        // Sort the entries by the size of the collections in descending order.
        entries.sort((entry1, entry2) -> Integer.compare(entry2.getValue().size(), entry1.getValue().size()));

        // Create a new map and iterate over the sorted entries, adding each entry to the new map.
        Map<K, V> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : entries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public static int topNParameterParser(String[] args) {
        int topN;
        try {
            topN = Integer.parseInt(args[0]);
            System.out.println("Top-N parameter: " + topN);
        } catch (NullPointerException | IndexOutOfBoundsException | NumberFormatException exception) {
            System.err.println(exception.getMessage());
            System.out.println("Default top-N parameter: 5");
            topN = 5;
        }
        return topN;
    }
}
