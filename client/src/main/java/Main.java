import ui.Client;

public class Main {
    public static void main(String[] args) {
        try {
            new Client().repl();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}