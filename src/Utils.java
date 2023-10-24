import java.util.Comparator;
import java.util.List;

public class Utils {

    public static <T> void sortList(List<T> list, Comparator<? super T> comparator) {
        list.sort(comparator);
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
