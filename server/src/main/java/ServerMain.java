import server.ChessServer;

public class ServerMain {
    public static void main(String[] args) {
        ChessServer chessServer = new ChessServer();
        chessServer.run(8080);

        System.out.println("♕ 240 Chess Server");
    }
}