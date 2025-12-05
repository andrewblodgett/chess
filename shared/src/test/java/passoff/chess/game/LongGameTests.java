package passoff.chess.game;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LongGameTests {

    @Test
    @DisplayName("Fool's Mate Checkmate")
    public void foolsMate() throws InvalidMoveException {
        var game = new ChessGame();

        // Print Initial Board
        System.out.println("--- Initial Board ---");
        System.out.println(game.getBoard());

        // Move 1: White Pawn f2 -> f3
        game.makeMove(new ChessMove(new ChessPosition(2, 6), new ChessPosition(3, 6), null));
        System.out.println("--- After White f3 ---");
        System.out.println(game.getBoard());

        // Move 2: Black Pawn e7 -> e5
        game.makeMove(new ChessMove(new ChessPosition(7, 5), new ChessPosition(5, 5), null));
        System.out.println("--- After Black e5 ---");
        System.out.println(game.getBoard());

        // Move 3: White Pawn g2 -> g4
        game.makeMove(new ChessMove(new ChessPosition(2, 7), new ChessPosition(4, 7), null));
        System.out.println("--- After White g4 ---");
        System.out.println(game.getBoard());

        // Move 4: Black Queen d8 -> h4 (CHECKMATE)
        game.makeMove(new ChessMove(new ChessPosition(8, 4), new ChessPosition(4, 8), null));
        System.out.println("--- After Black Qh4# ---");
        System.out.println(game.getBoard());

        // Assertions for White (The victim of Fool's Mate)
        Assertions.assertTrue(game.isInCheck(ChessGame.TeamColor.WHITE), "White should be in Check");
        Assertions.assertTrue(game.isInCheckmate(ChessGame.TeamColor.WHITE), "White should be in Checkmate");
        Assertions.assertFalse(game.isInStalemate(ChessGame.TeamColor.WHITE), "White should not be in Stalemate");

        // Assertions for Black ( The winner)
        Assertions.assertFalse(game.isInCheck(ChessGame.TeamColor.BLACK), "Black should not be in Check");
        Assertions.assertFalse(game.isInCheckmate(ChessGame.TeamColor.BLACK), "Black should not be in Checkmate");
        Assertions.assertFalse(game.isInStalemate(ChessGame.TeamColor.BLACK), "Black should not be in Stalemate");
    }

    @Test
    @DisplayName("Longer Game: Blackburne Shilling Gambit (Black Wins)")
    public void blackburneShillingMate() throws InvalidMoveException {
        var game = new ChessGame();

        // 1. White: e4
        game.makeMove(new ChessMove(new ChessPosition(2, 5), new ChessPosition(4, 5), null));
        // 1. Black: e5
        game.makeMove(new ChessMove(new ChessPosition(7, 5), new ChessPosition(5, 5), null));
        /*
                |r|n|b|q|k|b|n|r|
                |p|p|p|p| |p|p|p|
                | | | | | | | | |
                | | | | |p| | | |
                | | | | |P| | | |
                | | | | | | | | |
                |P|P|P|P| |P|P|P|
                |R|N|B|Q|K|B|N|R|
         */

        // 2. White: Nf3
        game.makeMove(new ChessMove(new ChessPosition(1, 7), new ChessPosition(3, 6), null));
        // 2. Black: Nc6
        game.makeMove(new ChessMove(new ChessPosition(8, 2), new ChessPosition(6, 3), null));
        /*
                |r| |b|q|k|b|n|r|
                |p|p|p|p| |p|p|p|
                | | |n| | | | | |
                | | | | |p| | | |
                | | | | |P| | | |
                | | | | | |N| | |
                |P|P|P|P| |P|P|P|
                |R|N|B|Q|K|B| |R|
         */

        // 3. White: Bc4
        game.makeMove(new ChessMove(new ChessPosition(1, 6), new ChessPosition(4, 3), null));
        // 3. Black: Nd4 (Black sets the trap)
        game.makeMove(new ChessMove(new ChessPosition(6, 3), new ChessPosition(4, 4), null));
        /*
                |r| |b|q|k|b|n|r|
                |p|p|p|p| |p|p|p|
                | | | | | | | | |
                | | | | |p| | | |
                | | |B|n|P| | | |
                | | | | | |N| | |
                |P|P|P|P| |P|P|P|
                |R|N|B|Q|K| | |R|
         */

        // 4. White: Nxe5? (White falls for it, taking the pawn)
        game.makeMove(new ChessMove(new ChessPosition(3, 6), new ChessPosition(5, 5), null));
        // 4. Black: Qg5 (Attacking the Knight and g2)
        game.makeMove(new ChessMove(new ChessPosition(8, 4), new ChessPosition(5, 7), null));
        /*
                |r| |b| |k|b|n|r|
                |p|p|p|p| |p|p|p|
                | | | | | | | | |
                | | | | |N| |q| |
                | | |B|n| | | | |
                | | | | | | | | |
                |P|P|P|P| |P|P|P|
                |R|N|B|Q|K| | |R|
         */

        // 5. White: Nxf7? (Forking Rook and Queen, but it's too late)
        game.makeMove(new ChessMove(new ChessPosition(5, 5), new ChessPosition(7, 6), null));
        // 5. Black: Qxg2
        game.makeMove(new ChessMove(new ChessPosition(5, 7), new ChessPosition(2, 7), null));
        /*
                |r| |b| |k|b|n|r|
                |p|p|p|p| |N|p|p|
                | | | | | | | | |
                | | | | | | | | |
                | | |B|n| | | | |
                | | | | | | | | |
                |P|P|P|P| |P|q|P|
                |R|N|B|Q|K| | |R|
         */

        // 6. White: Rf1 (Saving the Rook)
        game.makeMove(new ChessMove(new ChessPosition(1, 8), new ChessPosition(1, 6), null));
        // 6. Black: Qxe4+ (Check)
        game.makeMove(new ChessMove(new ChessPosition(2, 7), new ChessPosition(4, 5), null));
        /*
                |r| |b| |k|b|n|r|
                |p|p|p|p| |N|p|p|
                | | | | | | | | |
                | | | | | | | | |
                | | |B|n|q| | | |
                | | | | | | | | |
                |P|P|P|P| |P| |P|
                |R|N|B|Q|K|R| | |
         */

        // 7. White: Be2 (Blocking the check)
        game.makeMove(new ChessMove(new ChessPosition(4, 3), new ChessPosition(2, 5), null));
        /*
                |r| |b| |k|b|n|r|
                |p|p|p|p| |N|p|p|
                | | | | | | | | |
                | | | | | | | | |
                | | | |n|q| | | |
                | | | | | | | | |
                |P|P|P|P|B|P| |P|
                |R|N|B|Q|K|R| | |
         */

        // 7. Black: Nf3# (Smothered Mate - The White King is trapped by his own pieces)
        game.makeMove(new ChessMove(new ChessPosition(4, 4), new ChessPosition(3, 6), null));
        /*
                |r| |b| |k|b|n|r|
                |p|p|p|p| |N|p|p|
                | | | | | | | | |
                | | | | | | | | |
                | | | | |q| | | |
                | | | | | |n| | |
                |P|P|P|P|B|P| |P|
                |R|N|B|Q|K|R| | |
         */

        // Validate Game State
        Assertions.assertTrue(game.isInCheck(ChessGame.TeamColor.WHITE), GameStatusTests.MISSING_WHITE_CHECK);
        Assertions.assertFalse(game.isInCheck(ChessGame.TeamColor.BLACK), GameStatusTests.INCORRECT_BLACK_CHECK);

        Assertions.assertTrue(game.isInCheckmate(ChessGame.TeamColor.WHITE), GameStatusTests.MISSING_WHITE_CHECKMATE);
        Assertions.assertFalse(game.isInCheckmate(ChessGame.TeamColor.BLACK), GameStatusTests.INCORRECT_BLACK_CHECKMATE);

        Assertions.assertFalse(game.isInStalemate(ChessGame.TeamColor.WHITE), GameStatusTests.INCORRECT_WHITE_STALEMATE);
        Assertions.assertFalse(game.isInStalemate(ChessGame.TeamColor.BLACK), GameStatusTests.INCORRECT_BLACK_STALEMATE);
    }

    @Test
    @DisplayName("Polish Immortal: Glucksberg vs Najdorf (1929)")
    public void polishImmortal() throws InvalidMoveException {
        var game = new ChessGame();

        // 1. d4 f5 (Dutch Defense)
        game.makeMove(new ChessMove(new ChessPosition(2, 4), new ChessPosition(4, 4), null));
        game.makeMove(new ChessMove(new ChessPosition(7, 6), new ChessPosition(5, 6), null));

        // 2. c4 Nf6
        game.makeMove(new ChessMove(new ChessPosition(2, 3), new ChessPosition(4, 3), null));
        game.makeMove(new ChessMove(new ChessPosition(8, 7), new ChessPosition(6, 6), null));

        // 3. Nc3 e6
        game.makeMove(new ChessMove(new ChessPosition(1, 2), new ChessPosition(3, 3), null));
        game.makeMove(new ChessMove(new ChessPosition(7, 5), new ChessPosition(6, 5), null));

        // 4. Nf3 d5
        game.makeMove(new ChessMove(new ChessPosition(1, 7), new ChessPosition(3, 6), null));
        game.makeMove(new ChessMove(new ChessPosition(7, 4), new ChessPosition(5, 4), null));

        // 5. e3 c6
        game.makeMove(new ChessMove(new ChessPosition(2, 5), new ChessPosition(3, 5), null));
        game.makeMove(new ChessMove(new ChessPosition(7, 3), new ChessPosition(6, 3), null));

        // 6. Bd3 Bd6
        game.makeMove(new ChessMove(new ChessPosition(1, 6), new ChessPosition(3, 4), null));
        game.makeMove(new ChessMove(new ChessPosition(8, 6), new ChessPosition(6, 4), null));

        // 7. O-O O-O
        // Note: Assuming standard King-move castling logic (King moves 2 squares)
        game.makeMove(new ChessMove(new ChessPosition(1, 5), new ChessPosition(1, 7), null));
        game.makeMove(new ChessMove(new ChessPosition(8, 5), new ChessPosition(8, 7), null));

        // 8. Ne2 Nbd7
        game.makeMove(new ChessMove(new ChessPosition(3, 3), new ChessPosition(2, 5), null));
        game.makeMove(new ChessMove(new ChessPosition(8, 2), new ChessPosition(7, 4), null));

        // 9. Ng5 Bxh2+
        game.makeMove(new ChessMove(new ChessPosition(3, 6), new ChessPosition(5, 7), null));
        game.makeMove(new ChessMove(new ChessPosition(6, 4), new ChessPosition(2, 8), null));

        // 10. Kh1 Ng4
        game.makeMove(new ChessMove(new ChessPosition(1, 7), new ChessPosition(1, 8), null));
        game.makeMove(new ChessMove(new ChessPosition(6, 6), new ChessPosition(4, 7), null));

        // 11. f4 Qe8
        game.makeMove(new ChessMove(new ChessPosition(2, 6), new ChessPosition(4, 6), null));
        game.makeMove(new ChessMove(new ChessPosition(8, 4), new ChessPosition(8, 5), null));

        // 12. g3 Qh5
        game.makeMove(new ChessMove(new ChessPosition(2, 7), new ChessPosition(3, 7), null));
        game.makeMove(new ChessMove(new ChessPosition(8, 5), new ChessPosition(5, 8), null));

        // 13. Kg2 Bg1 (Bishop sacrifices itself to clear the line)
        game.makeMove(new ChessMove(new ChessPosition(1, 8), new ChessPosition(2, 7), null));
        game.makeMove(new ChessMove(new ChessPosition(2, 8), new ChessPosition(1, 7), null));

        // 14. Nxg1 Qh2+
        game.makeMove(new ChessMove(new ChessPosition(2, 5), new ChessPosition(1, 7), null));
        game.makeMove(new ChessMove(new ChessPosition(5, 8), new ChessPosition(2, 8), null));

        // 15. Kf3 e5 (Black threatens mate, White must take)
        game.makeMove(new ChessMove(new ChessPosition(2, 7), new ChessPosition(3, 6), null));
        game.makeMove(new ChessMove(new ChessPosition(6, 5), new ChessPosition(5, 5), null));

        // 16. dxe5 Ndxe5+
        game.makeMove(new ChessMove(new ChessPosition(4, 4), new ChessPosition(5, 5), null));
        game.makeMove(new ChessMove(new ChessPosition(7, 4), new ChessPosition(5, 5), null));

        // 17. fxe5 Nxe5+
        game.makeMove(new ChessMove(new ChessPosition(4, 6), new ChessPosition(5, 5), null));
        game.makeMove(new ChessMove(new ChessPosition(4, 7), new ChessPosition(5, 5), null));

        // 18. Kf4 Ng6+
        game.makeMove(new ChessMove(new ChessPosition(3, 6), new ChessPosition(4, 6), null));
        game.makeMove(new ChessMove(new ChessPosition(5, 5), new ChessPosition(6, 7), null));

        // 19. Kf3 f4 (Threatening checkmate with Ne5# or Bg4#)
        game.makeMove(new ChessMove(new ChessPosition(4, 6), new ChessPosition(3, 6), null));
        game.makeMove(new ChessMove(new ChessPosition(5, 6), new ChessPosition(4, 6), null));

        // 20. exf4 Bg4+!! (The brilliancy)
        game.makeMove(new ChessMove(new ChessPosition(3, 5), new ChessPosition(4, 6), null));
        game.makeMove(new ChessMove(new ChessPosition(8, 3), new ChessPosition(4, 7), null));

        // 21. Kxg4 Ne5+! (The final sacrifice)
        game.makeMove(new ChessMove(new ChessPosition(3, 6), new ChessPosition(4, 7), null));
        game.makeMove(new ChessMove(new ChessPosition(6, 7), new ChessPosition(5, 5), null));

        // 22. fxe5 h5# (Pawn checkmate)
        game.makeMove(new ChessMove(new ChessPosition(4, 6), new ChessPosition(5, 5), null));
        game.makeMove(new ChessMove(new ChessPosition(7, 8), new ChessPosition(5, 8), null));

        // Assertions
        Assertions.assertTrue(game.isInCheck(ChessGame.TeamColor.WHITE), "White should be in check");
        Assertions.assertTrue(game.isInCheckmate(ChessGame.TeamColor.WHITE), "White should be in checkmate");
        Assertions.assertFalse(game.isInCheck(ChessGame.TeamColor.BLACK), "Black should not be in check");
        Assertions.assertFalse(game.isInStalemate(ChessGame.TeamColor.WHITE), "White should not be in stalemate");
        Assertions.assertFalse(game.isInStalemate(ChessGame.TeamColor.BLACK), "Black should not be in stalemate");
    }
}