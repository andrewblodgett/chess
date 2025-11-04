import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPosition;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Client {
    static void main() {
        var board = new ChessBoard();
        board.resetBoard();
        new Client().displayBoard(board);

    }

    public void repl() {
        System.out.println("Welcome to 240 chess. Type help to get started.");
        while (true) {
            System.out.printf("%s > ", WHITE_KING);
            var scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            var arguments = line.split(" ");
            if (!parseCommand(arguments)) {
                break;
            }
        }
    }

    private boolean parseCommand(String[] command) {
        switch (command[0].toLowerCase()) {
            case "logout":
                return false;
            case null, default:
                displayHelp();
        }
        return true;
    }

    private void displayHelp() {
        System.out.println("""
                register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                login <USERNAME> <PASSWORD> - to play chess
                quit - playing chess
                help - with possible commands
                """);
    }

    private void displayBoard(ChessBoard board) {
        var WHITE_SQUARE_STRINGS = new String[]{"▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓", "▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓", "▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓", "▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓", "▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓", "▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓", "▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓", "▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓"};
        var EMPTY_SQUARE_STRING = new String[]{"                        ", "                        ", "                        ", "                        ", "                        ", "                        ", "                        ", "                        "};
        var PAWN_STRINGS = new String[]{
                "                        ",
                "          (PP)          ",
                "          /  \\          ",
                "       __/    \\__       ",
                "      {_        _}      ",
                "      __}      {__      ",
                "     {____________}     "
        };
        var BISHOP_STRINGS = new String[]{
                "          (BB)          ",
                "           )(           ",
                "          /  \\          ",
                "       (OOOOOOOO)       ",
                "         /    \\         ",
                "      .. )    ( ..      ",
                "    (______________)    "
        };
        var ROOK_STRINGS = new String[]{
                "    R  R  R  R  R  R    ",
                "   {!__!__!__!__!__!}   ",
                "     { |   |    | }     ",
                "     {   |    |   }     ",
                "     { |    |   | }     ",
                "     {   |    |   }     ",
                "   {{{{{{{{{}}}}}}}}}   "
        };
        var KNIGHT_STRINGS = new String[]{
                "    / N N N N N N N\\    ",
                "   / / N N N   (0)  \\   ",
                "  / / / N   ______  .\\  ",
                "   / / /   /      \\_/   ",
                "     |      \\_          ",
                "     |         \\___     ",
                "   /________________\\   "
        };
        var QUEEN_STRINGS = new String[]{
                "        ~(*QQ*)~        ",
                "          )##(          ",
                "         /Q**Q\\         ",
                "      (Q*QQQQQQ*Q)      ",
                "         \\Q**Q/         ",
                "      ..  )##(  ..      ",
                "    (*Q*Q*Q*Q*Q*Q*Q)    "
        };
        var KING_STRINGS = new String[]{
                "        K══╬╬══K        ",
                "          K║║K          ",
                "         ╔═╬╬═╗         ",
                "      (║║║║║║║║║║)      ",
                "        K╬╬╬╬╬╬K        ",
                "      .. )╬╬╬╬( ..      ",
                "    (╬╬╬╬╬╬╬╬╬╬╬╬╬╬)    "
        };
        String formattedBoard = SET_TEXT_BOLD;
        for (int r = 1; r < 9; r++) {
            String row = "";
            for (int j = 0; j < 7; j++) {
                for (int c = 1; c < 9; c++) {
                    var piece = board.getPiece(new ChessPosition(r, c));
                    if (piece != null && piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        row += SET_TEXT_COLOR_WHITE;
                    } else {
                        row += SET_TEXT_COLOR_BLACK;
                    }
                    if ((r + c) % 2 == 0) {
                        row += SET_BG_COLOR_DARK_GREY;
                    } else {
                        row += SET_BG_COLOR_LIGHT_GREY;
                    }
                    if (piece == null) {
                        row += EMPTY_SQUARE_STRING[j];
                    } else {
                        switch (piece.getPieceType()) {
                            case PAWN -> row += PAWN_STRINGS[j];
                            case BISHOP -> row += BISHOP_STRINGS[j];
                            case KNIGHT -> row += KNIGHT_STRINGS[j];
                            case ROOK -> row += ROOK_STRINGS[j];
                            case QUEEN -> row += QUEEN_STRINGS[j];
                            case KING -> row += KING_STRINGS[j];
                            case null, default -> row += EMPTY_SQUARE_STRING[j];
                        }
                    }

                }
                row += "\n";
            }
            formattedBoard += row;
        }
        System.out.println(formattedBoard);
    }
}
