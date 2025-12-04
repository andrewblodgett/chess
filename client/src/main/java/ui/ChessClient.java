package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import client.ServerFacade;
import client.ServerMessageObserver;
import websocket.messages.ServerMessage;

import java.util.Map;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ChessClient implements ServerMessageObserver {

    private final ServerFacade facade;
    private String authToken;
    private State state;


    enum State {
        LOGGED_OUT("Logged out"),
        LOGGED_IN("Logged in"),
        IN_GAME("In a game");

        private final String description;
        private ChessGame.TeamColor color;

        State(String description) {
            this.description = description;
        }

        public String toString() {
            return this.description;
        }

        public void setColor(ChessGame.TeamColor color) {
            this.color = color;
        }

        public ChessGame.TeamColor getColor() {
            return color;
        }
    }

    public ChessClient() {
        facade = new ServerFacade(8080);
        facade.addObserver(this);
        authToken = "";
        state = State.LOGGED_OUT;
    }


    public void repl() {
        System.out.println("Welcome to 240 chess. Type help to get started.");
        while (true) {
            System.out.printf("%s %s %s> ", WHITE_KING, state, WHITE_KING);
            var scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            var arguments = line.strip().split(" ");
            if (state == State.IN_GAME) {
                if (!parseInGameCommand(arguments)) {
                    break;
                }
            } else {
                if (!parseBasicCommand(arguments)) {
                    break;
                }
            }
        }
    }

    public void notify(ServerMessage msg) {
        switch (msg.getServerMessageType()) {
            case NOTIFICATION, ERROR:
                System.out.println(msg.getMessage());
                break;
            case LOAD_GAME:
                displayBoard(msg.getGame().getBoard(), state.getColor());
        }
    }

    private boolean parseBasicCommand(String[] command) {
        switch (command[0].toLowerCase()) {
            case "register":
                try {
                    authToken = facade.register(command[1], command[2], command[3]);
                    state = State.LOGGED_IN;
                    displayHelp();
                } catch (Exception e) {
                    System.out.println("There was an issue with your registration. That username may already be taken.");
                }
                break;
            case "logout":
                try {
                    facade.logout(authToken);
                    state = State.LOGGED_OUT;
                } catch (Exception e) {
                    System.out.println("Logout unsuccessful.");
                }
                break;
            case "login":
                try {
                    authToken = facade.login(command[1], command[2]);
                    state = State.LOGGED_IN;
                } catch (Exception e) {
                    System.out.println("Unable to login. Verify that your username and password are correct.");
                }
                break;
            case "help":
                displayHelp();
                break;
            case "quit":
                try {
                    facade.logout(authToken);
                } catch (Exception e) {

                }
                return false;
            case "list":
                try {
                    System.out.println(facade.listGames(authToken));
                } catch (Exception e) {
                    System.out.println("Unable to fetch games. Are you logged in?");
                }
                break;
            case "join":
                try {
                    var color = command[2].equalsIgnoreCase("white") ? ChessGame.TeamColor.WHITE :
                            ChessGame.TeamColor.BLACK;
                    facade.joinGame(authToken, Long.parseLong(command[1]), color);
                    state = State.IN_GAME;
                    state.setColor(color);
                } catch (Exception e) {
                    System.out.println("Unable to join game. Verify that you have the proper game ID and player color");
                }
                break;
            case "observe":
                var board = new ChessBoard();
                board.resetBoard();
                displayBoard(board, ChessGame.TeamColor.WHITE);
                break;
            case "create":
                try {
                    facade.createGame(authToken, command[1]);
                } catch (Exception e) {
                    System.out.println("Unable to create a game. Are you logged in?");
                }
                break;
            default:
                System.out.println("You may have a typo in your command. type help to see a list of all commands.");

        }
        return true;
    }

    private boolean parseInGameCommand(String[] command) {
        switch (command[0].toLowerCase()) {
            case "help":
                displayHelp();
                break;
            case "redraw":
                break;
            case "leave":
                break;
            case "move":
                break;
            case "resign":
                break;
            case "highlight":
                break;
            default:
                System.out.println("You may have a typo in your command. type help to see a list of all commands.");

        }
        return true;
    }

    private void displayHelp() {
        switch (state) {
            case LOGGED_OUT:
                System.out.println("""
                        register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                        login <USERNAME> <PASSWORD> - to play chess
                        quit - playing chess
                        help - with possible commands
                        """);
                break;
            case LOGGED_IN:
                System.out.println("""
                        create <NAME> - to create a game
                        list - games
                        join <ID> [WHITE|BLACK] - a game
                        observe <ID> - a game
                        logout - when you are done
                        quit - playing chess
                        help - with possible commands
                        """);
                break;
            case IN_GAME:
                System.out.println("""
                        redraw - draw the most updated board
                        highlight <PIECE>- all legal moves
                        leave - the game
                        move <START> <END> - make a move on your turn
                        resign - admit defeat
                        help - with possible commands
                        """);
        }

    }

    private void displayBoard(ChessBoard board, ChessGame.TeamColor teamColor) {
        var emptySquareString = new String[]{"                      ", "                       ", "                       ",
                "                       ", "                       ", "                       ",
                "                       ", "                       "};
        Map<Integer, String> coordMap = Map.of(1, "A", 2, "B",
                3, "C",
                4, "D",
                5, "E",
                6, "F",
                7, "G",
                8, "H");
        String formattedBoard = SET_TEXT_BOLD;
        boolean isBlack = teamColor.equals(ChessGame.TeamColor.BLACK);
        for (int r = 8; r > 0; r--) {
            String row = "";
            for (int j = 0; j < 7; j++) {
                for (int c = 8; c > 0; c--) {
                    var piece = board.getPiece(
                            new ChessPosition(isBlack ? r : (9 - r),
                                    isBlack ? c : (9 - c)));
                    if (piece != null && piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        row += SET_TEXT_COLOR_BLUE;
                    } else {
                        row += SET_TEXT_COLOR_RED;
                    }
                    if ((r + c) % 2 == 0) {
                        row += SET_BG_COLOR_WHITE;
                        row += SET_TEXT_COLOR_BLACK;
                    } else {
                        row += SET_BG_COLOR_BLACK;
                        row += SET_TEXT_COLOR_WHITE;

                    }
                    if (j == 0) {
                        row += isBlack ? (9 - r) : r;
                        row += coordMap.get(9 - c);
                    } else {
                        row += " ";
                    }
                    if (piece != null && piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        row += SET_TEXT_COLOR_BLUE;
                    } else {
                        row += SET_TEXT_COLOR_RED;
                    }
                    if (piece == null) {
                        row += emptySquareString[j];
                    } else {
                        row += getPieceRowHelper(piece.getPieceType(), j);
                    }
                }
                row += "\n";
            }
            formattedBoard += row;
        }
        System.out.println(formattedBoard);
    }

    private String getPieceRowHelper(ChessPiece.PieceType type, int j) {
        var emptySquareString = new String[]{"                      ", "                       ", "                       ",
                "                       ", "                       ", "                       ",
                "                       ", "                       "};
        var pawnStrings = new String[]{
                "                      ", "         (PP)          ", "         /  \\          ",
                "      __/    \\__       ", "     {_        _}      ", "     __}      {__      ", "    {____________}     "
        };
        var bishopStrings = new String[]{
                "        (BB)          ", "          )(           ", "         /  \\          ",
                "      (OOOOOOOO)       ", "        /    \\         ", "     .. )    ( ..      ", "   (______________)    "
        };
        var rookStrings = new String[]{
                "  R  R  R  R  R  R    ", "  {!__!__!__!__!__!}   ", "    { |   |    | }     ",
                "    {   |    |   }     ", "    { |    |   | }     ", "    {   |    |   }     ", "  {{{{{{{{{}}}}}}}}}   "
        };
        var knightStrings = new String[]{
                "  / N N N N N N N\\    ", "  / / N N N   (0)  \\   ", " / / / N   ______  .\\  ",
                "  / / /   /      \\_/   ", "    |      \\_          ", "    |         \\___     ", "  /________________\\   "
        };
        var queenStrings = new String[]{
                "      ~(*QQ*)~        ", "         )##(          ", "        /Q**Q\\         ",
                "     (Q*QQQQQQ*Q)      ", "        \\Q**Q/         ", "     ..  )##(  ..      ", "   (*Q*Q*Q*Q*Q*Q*Q)    "
        };
        var kingStrings = new String[]{
                "      K══╬╬══K        ", "         K║║K          ", "        ╔═╬╬═╗         ",
                "     (║║║║║║║║║║)      ", "       K╬╬╬╬╬╬K        ", "     .. )╬╬╬╬( ..      ", "   (╬╬╬╬╬╬╬╬╬╬╬╬╬╬)    "
        };
        return switch (type) {
            case PAWN -> pawnStrings[j];
            case BISHOP -> bishopStrings[j];
            case KNIGHT -> knightStrings[j];
            case ROOK -> rookStrings[j];
            case QUEEN -> queenStrings[j];
            case KING -> kingStrings[j];
            case null, default -> emptySquareString[j];
        };
    }
}
