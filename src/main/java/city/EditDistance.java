package city;

public interface EditDistance {
    int distance(String a, String b);
    Metrics metrics();
    String name();
}
