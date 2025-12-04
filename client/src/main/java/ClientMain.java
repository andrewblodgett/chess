import ui.ChessClient;

public class ClientMain {
    public static void main(String[] args) {
        try {
            new ChessClient().repl();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}