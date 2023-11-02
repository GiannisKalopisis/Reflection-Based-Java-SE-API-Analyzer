import java.text.SimpleDateFormat;
import java.util.*;


/**
 * This class provides utility methods for common operations such as sorting, parsing parameters,
 * and manipulating maps. It includes methods for sorting lists and maps, parsing a top-N parameter
 * from command-line arguments, reversing map keys, and measuring elapsed time.
 */
public class Utils {

    static long startTime = 0;

    public static Map<String, List<MethodInfo>> groupByMethodName(List<MethodInfo> methodInfoList) {
        Map<String, List<MethodInfo>> methodInfoMap = new HashMap<>();

        for (MethodInfo methodInfo : methodInfoList) {
            String methodName = methodInfo.getMethodName();

            // Get the list of MethodInfo objects with the same methodName
            List<MethodInfo> groupList = methodInfoMap.computeIfAbsent(methodName, k -> new ArrayList<>());

            groupList.add(methodInfo);
        }
        return methodInfoMap;
    }

    /**
     * Sorts a list using the provided comparator.
     *
     * @param <T>        The type of elements in the list.
     * @param list       The list to be sorted.
     * @param comparator The comparator used for sorting.
     */
    public static <T> void sortList(List<T> list, Comparator<? super T> comparator) {
        list.sort(comparator);
    }

    /**
     * Sorts a map by the size of its values in descending order.
     *
     * @param <K> The type of keys in the map.
     * @param <V> The type of values in the map, which should be a collection.
     * @param map The map to be sorted.
     * @return A new map sorted by collection size in descending order.
     */
    public static <K, V extends Collection<?>> Map<K, V> sortByCollectionSizeDescending(Map<K, V> map) {
        List<Map.Entry<K, V>> entries = new ArrayList<>(map.entrySet());

        // Sort the entries by the size of the collections in descending order.
        entries.sort((entry1, entry2) -> Integer.compare(entry2.getValue().size(), entry1.getValue().size()));

        Map<K, V> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : entries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    /**
     * Sorts a map by the size of its values in ascending order.
     *
     * @param <K> The type of keys in the map.
     * @param <V> The type of values in the map, which should be a collection.
     * @param map The map to be sorted.
     * @return A new map sorted by collection size in ascending order.
     */
    public static <K, V extends Collection<?>> Map<K, V> sortByCollectionSizeAscending(Map<K, V> map) {
        List<Map.Entry<K, V>> entries = new ArrayList<>(map.entrySet());

        // Sort the entries by the size of the collections in ascending order.
        entries.sort(Comparator.comparingInt(entry -> entry.getValue().size()));

        Map<K, V> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : entries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    /**
     * Sorts a map by the values in descending order.
     *
     * @param <K> The type of keys in the map.
     * @param <V> The type of values in the map, which should be comparable.
     * @param map The map to be sorted.
     * @return A new map sorted by values in descending order.
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValueDesc(Map<K, V> map) {
        List<Map.Entry<K, V>> entryList = new ArrayList<>(map.entrySet());

        entryList.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        Map<K, V> sortedMap = new LinkedHashMap<>();
        entryList.forEach(entry -> sortedMap.put(entry.getKey(), entry.getValue()));

        return sortedMap;
    }

    /**
     * Parses a top-N parameter from an array of strings and provides a default value if parsing fails.
     *
     * @param args The array of strings containing program arguments.
     * @return The top-N parameter value, with a default of 5 if parsing fails.
     */
    public static int topNParameterParser(String[] args) {
        int topN;
        try {
            topN = Integer.parseInt(args[0]);
            System.out.print("Top-N parameter: " + topN);
            if (topN <= 0) {
                System.out.println(" (printing all results)\n");
            } else {
                System.out.println("\n");
            }
        } catch (NullPointerException | IndexOutOfBoundsException | NumberFormatException exception) {
            System.err.println(exception.getMessage());
            System.out.println("Default top-N parameter: 5");
            topN = 5;
        }
        return topN;
    }

    /**
     * Reverses the keys of a map while keeping the values intact.
     *
     * @param <K>    The type of keys in the map.
     * @param <V>    The type of values in the map.
     * @param source The source map to be reversed.
     * @return A new map with the keys reversed, preserving the original values.
     */
    public static <K, V> Map<K, V> reverseMapKeys(Map<K, V> source) {
        Map<K, V> result = new LinkedHashMap<>();
        List<K> keys = new ArrayList<>(source.keySet());
        Collections.reverse(keys);
        for (K key : keys) {
            result.put(key, source.get(key));
        }
        return result;
    }

    /**
     * Records the start time for measuring elapsed time.
     */
    public static void startTimeCounter() {
        startTime = System.currentTimeMillis();
    }

    /**
     * Records the end time, calculates elapsed time, and prints it in the "mm:ss:SSS" format.
     */
    public static long endTimeCounter() {
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss:SSS");
        String formattedTime = sdf.format(new Date(elapsedTime));
        System.out.println("Elapsed Time: " + formattedTime);
        return elapsedTime;
    }
}
