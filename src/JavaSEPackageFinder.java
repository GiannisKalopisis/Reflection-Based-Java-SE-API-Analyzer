import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class JavaSEPackageFinder {

    private final List<String> apiPackageList;
    private final Comparator<String> stringComparator;

    public JavaSEPackageFinder() {
        this.apiPackageList = new ArrayList<>();
        this.stringComparator = Comparator.naturalOrder();
    }

    public List<String> findUniqueAPIPackages(List<Module> modules) {
        modules.stream()
                .flatMap(module -> module.getPackages().stream())
                .filter(pkg -> pkg.startsWith("java.") || pkg.startsWith("javax."))
                .forEach(this.apiPackageList::add);
        return this.apiPackageList;
    }

    public void sortPackages() {
        Utils.sortList(this.apiPackageList, stringComparator);
    }

    public List<String> getApiPackageList() {
        return apiPackageList;
    }
}
