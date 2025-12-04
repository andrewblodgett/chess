package ui;

import chess.*;
import client.ServerFacade;
import client.ServerMessageObserver;
import websocket.messages.ServerMessage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ChessClient implements ServerMessageObserver {

    private final ServerFacade facade;
    private String authToken;
    private State state;
    private ChessGame currentGame;
    private ChessGame.TeamColor color;
    private int gameID;

    private final Map<Integer, String> coordMap = Map.of(1, "A", 2, "B",
            3, "C",
            4, "D",
            5, "E",
            6, "F",
            7, "G",
            8, "H");

    enum State {
        LOGGED_OUT("Logged out"),
        LOGGED_IN("Logged in"),
        IN_GAME("In a game"),
        OBSERVING("Watching a game");

        private final String description;

        State(String description) {
            this.description = description;
        }

        public String toString() {
            return this.description;
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
            } else if (state == State.OBSERVING) {
                if (!parseObserverCommand(arguments)) {
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
            case NOTIFICATION:
                System.out.println(msg.getMessage());
                break;
            case ERROR:
                System.out.println(msg.getMessage());
                break;
            case LOAD_GAME:
                currentGame = msg.getGame();
                displayBoard(msg.getGame().getBoard(), color != null ? color : ChessGame.TeamColor.WHITE, new HashSet<>());
                break;
        }
    }

    private boolean parseBasicCommand(String[] command) {
        switch (command[0].toLowerCase()) {
            case "register", "r":
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
            case "help", "h":
                displayHelp();
                break;
            case "quit", "q":
                try {
                    facade.logout(authToken);
                } catch (Exception e) {

                }
                return false;
            case "list", "l":
                try {
                    System.out.println(facade.listGames(authToken));
                } catch (Exception e) {
                    System.out.println("Unable to fetch games. Are you logged in?");
                }
                break;
            case "join", "j":
                try {
                    var color = command[2].equalsIgnoreCase("white") ? ChessGame.TeamColor.WHITE :
                            ChessGame.TeamColor.BLACK;
                    gameID = Integer.parseInt(command[1]);
                    facade.joinGame(authToken, gameID, color);
                    state = State.IN_GAME;
                    this.color = color;
                } catch (Exception e) {
                    System.out.println("Unable to join game. Verify that you have the proper game ID and player color");
                }
                break;
            case "observe", "o":
                try {
                    gameID = Integer.parseInt(command[1]);
                    facade.observeGame(authToken, gameID);
                    state = State.OBSERVING;
                } catch (Exception e) {
                    System.out.println(("Unable to observe game. Verify that you have the proper game ID"));
                }
                break;
            case "create", "c":
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
            case "redraw", "r":
                displayBoard(currentGame.getBoard(), color, new HashSet<>());
                break;
            case "leave", "l":
                try {
                    facade.leaveGame(authToken, gameID);
                    state = State.LOGGED_IN;
                } catch (Exception e) {
                    System.out.println("For some reason that didn't work");
                }
                break;
            case "move", "m":
                try {
                    var move = parseMove(command[1], command[2], "");
                    facade.makeMove(authToken, gameID, move);
                } catch (Exception e) {
                    System.out.println("That move didn't work, either because it wasn't formatted correctly or it wasn't legal.");
                }
                break;
            case "resign":
                break;
            case "highlight", "h":
                try {
                    displayBoard(currentGame.getBoard(), color, getSquaresToHighlight(parseChessCoordinate(command[1])));
                } catch (Exception e) {
                    System.out.println(e);
                    throw new RuntimeException(e);
                }
                break;
            default:
                System.out.println("You may have a typo in your command. type help to see a list of all commands.");

        }
        return true;
    }

    private boolean parseObserverCommand(String[] command) {
        switch (command[0].toLowerCase()) {
            case "help":
                displayHelp();
                break;
            case "redraw", "r":
                displayBoard(currentGame.getBoard(), color, new HashSet<>());
                break;
            case "leave", "l":
                try {
                    facade.leaveGame(authToken, gameID);
                    state = State.LOGGED_IN;
                } catch (Exception e) {
                    System.out.println("For some reason that didn't work");
                }
                break;
            case "highlight", "h":
                try {
                    displayBoard(currentGame.getBoard(), ChessGame.TeamColor.WHITE, getSquaresToHighlight(parseChessCoordinate(command[1])));
                } catch (Exception e) {
                    System.out.println(e);
                    throw new RuntimeException(e);
                }
                break;
            default:
                System.out.println("You may have a typo in your command. type help to see a list of all commands.");

        }
        return true;
    }

    private ChessMove parseMove(String start, String end, String promotion) throws Exception {
        return new ChessMove(parseChessCoordinate(start), parseChessCoordinate(end), parsePromotionPiece(promotion));
    }


    private ChessPosition parseChessCoordinate(String coord) throws Exception {
        if (coord.length() != 2) {
            throw new Exception("Invalid coordinate");
        }
        int col = Character.getNumericValue(coord.toUpperCase().charAt(0)) - 9;
        var row = Integer.parseInt(coord.substring(1, 2));
        return new ChessPosition(row, col);
    }

    private ChessPiece.PieceType parsePromotionPiece(String promotion) {
        return null; // TODO
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
                        highlight <PIECE> - all legal moves
                        leave - the game
                        move <START> <END> {PROMOTION TYPE (optional)} - make a move on your turn
                        resign - admit defeat
                        help - with possible commands
                        """);
                break;
            case OBSERVING:
                System.out.println("""
                        redraw - draw the most updated board
                        highlight <PIECE> - all legal moves
                        leave - the game
                        help - with possible commands
                        """);
                break;
        }

    }

    private Collection<ChessPosition> getSquaresToHighlight(ChessPosition start) {
        var squares = new HashSet<ChessPosition>();
        squares.add(start);
        for (var move : currentGame.validMoves(start)) {
            squares.add(move.getEndPosition());
        }
        return squares;
    }

    private void displayBoard(ChessBoard board, ChessGame.TeamColor teamColor, Collection<ChessPosition> highlights) {
        var emptySquareString = new String[]{"                      ", "                       ", "                       ",
                "                       ", "                       ", "                       ",
                "                       ", "                       "};

        String formattedBoard = SET_TEXT_BOLD + "\n";
        boolean isWhite = teamColor.equals(ChessGame.TeamColor.WHITE);
        for (int r = 8; r > 0; r--) {
            String row = "";
            for (int j = 0; j < 7; j++) {
                for (int c = 1; c <= 8; c++) {
                    var square = new ChessPosition(isWhite ? r : (9 - r),
                            isWhite ? c : (9 - c));
                    var piece = board.getPiece(square);
                    if (highlights.contains(square)) {
                        row += SET_BG_HIGHLIGHT_COLOR;
                        row += SET_TEXT_COLOR_DARK_GREY;
                    } else if ((r + c + 1) % 2 == 0) {
                        row += SET_BG_COLOR_WHITE;
                        row += SET_TEXT_COLOR_BLACK;
                    } else {
                        row += SET_BG_COLOR_BLACK;
                        row += SET_TEXT_COLOR_WHITE;

                    }
                    if (j == 0) {
                        row += !isWhite ? coordMap.get(9 - c) : coordMap.get(c);
                        row += !isWhite ? (9 - r) : r;
                    } else {
                        row += " ";
                    }
                    if (piece != null && piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
//                        row += SET_TEXT_COLOR_BLUE;
                        row += SET_TEXT_COLOR_NICE_GREEN;
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
                "      __/    \\__       ", "     (_        _)      ", "     __)      (__      ", "    (____________)     "
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
