package passoff.chess.game;

import chess.ChessGame;
import chess.ChessPiece;
import org.junit.jupiter.params.ParameterizedTest;

import chess.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import passoff.chess.TestUtilities;

import static org.junit.jupiter.api.Assertions.*;

public class MorePromotionTests {


    private ChessGame game;

    @BeforeEach
    public void setup() {
        game = new ChessGame();
    }

    // 1. HAPPY PATH: Test all valid promotion types for White (Moving forward)
    @ParameterizedTest
    @EnumSource(value = ChessPiece.PieceType.class, names = {"QUEEN", "BISHOP", "ROOK", "KNIGHT"})
    @DisplayName("White Valid Promotions (Push)")
    public void testWhitePromotionSuccess(ChessPiece.PieceType promoType) throws InvalidMoveException {
        // Setup: White pawn ready to promote at 7,5. Empty square at 8,5.
        game.setBoard(TestUtilities.loadBoard("""
                | | | | | | | | |
                | | | | |P| | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                """));

        ChessPosition start = new ChessPosition(7, 5);
        ChessPosition end = new ChessPosition(8, 5);
        ChessMove move = new ChessMove(start, end, promoType);

        game.makeMove(move);

        ChessPiece resultingPiece = game.getBoard().getPiece(end);

        assertAll("White Promotion Integrity",
                () -> assertNull(game.getBoard().getPiece(start), "Start position should be empty"),
                () -> assertNotNull(resultingPiece, "End position should have a piece"),
                () -> assertEquals(ChessGame.TeamColor.WHITE, resultingPiece.getTeamColor(), "Piece should be White"),
                () -> assertEquals(promoType, resultingPiece.getPieceType(), "Pawn should have promoted to " + promoType)
        );
    }

    // 2. HAPPY PATH: Test all valid promotion types for Black (Capturing)
    @ParameterizedTest
    @EnumSource(value = ChessPiece.PieceType.class, names = {"QUEEN", "BISHOP", "ROOK", "KNIGHT"})
    @DisplayName("Black Valid Promotions (Capture)")
    public void testBlackPromotionCapture(ChessPiece.PieceType promoType) throws InvalidMoveException {
        // Setup: Black pawn at 2,2; White rook at 1,3 (capture target)
        game.setBoard(TestUtilities.loadBoard("""
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | |p| | | | | | |
                | | |R| | | | | |
                """));

        game.setTeamTurn(ChessGame.TeamColor.BLACK);

        ChessPosition start = new ChessPosition(2, 2);
        ChessPosition end = new ChessPosition(1, 3);
        ChessMove move = new ChessMove(start, end, promoType);

        game.makeMove(move);

        ChessPiece resultingPiece = game.getBoard().getPiece(end);

        assertAll("Black Capture Promotion Integrity",
                () -> assertNull(game.getBoard().getPiece(start), "Start position should be empty"),
                () -> assertEquals(ChessGame.TeamColor.BLACK, resultingPiece.getTeamColor(), "Piece should be Black"),
                () -> assertEquals(promoType, resultingPiece.getPieceType(), "Pawn should have promoted to " + promoType)
        );
    }

    // 3. NEGATIVE TEST: Attempting to promote to King or Pawn
    @ParameterizedTest
    @EnumSource(value = ChessPiece.PieceType.class, names = {"KING", "PAWN"})
    @DisplayName("Invalid Promotion Types (King/Pawn)")
    public void testInvalidPromotionTypes(ChessPiece.PieceType invalidType) {
        game.setBoard(TestUtilities.loadBoard("""
                | | | | | | | | |
                | | |P| | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                |K| | | | | | |k|
                """));

        ChessMove move = new ChessMove(new ChessPosition(7, 3), new ChessPosition(8, 3), invalidType);

        // Depending on your implementation, this might throw checkMove validity
        // OR simply be excluded from validMoves().
        // This test assumes your engine throws InvalidMoveException for illegal moves.
        assertThrows(InvalidMoveException.class, () -> game.makeMove(move),
                "Should throw exception when promoting to " + invalidType);
    }

    // 4. NEGATIVE TEST: Promoting when not on the last rank
    @Test
    @DisplayName("Cannot Promote Mid-Board")
    public void testPrematurePromotionMetadata() {
        game.setBoard(TestUtilities.loadBoard("""
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | |P| | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                |K| | | | | | |k|
                """));

        // Moving from rank 5 to 6, but requesting a Queen promotion
        ChessMove move = new ChessMove(new ChessPosition(5, 4), new ChessPosition(6, 4), ChessPiece.PieceType.QUEEN);

        // This implies the move is invalid because a promotion type was provided
        // for a non-promotion move, OR it should move but IGNORE the promotion.
        // Usually, strict validation rejects this.
        assertThrows(InvalidMoveException.class, () -> game.makeMove(move),
                "Should not allow promotion parameters on non-final ranks");
    }

    // 5. GAME STATE TEST: Promotion causing Checkmate
    @Test
    @DisplayName("Promotion leads to Checkmate")
    public void testPromotionCausesCheckmate() throws InvalidMoveException {
        // White Pawn at 7,7. Black King constrained at 8,8.
        // Promotion to Rook or Queen should mate. Promotion to Bishop is stalemate/check.
        game.setBoard(TestUtilities.loadBoard("""
                | | | | | | |k| |
                | | | | |P| | | |
                | | | | | | |K| |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                """));

        ChessMove move = new ChessMove(new ChessPosition(7, 5), new ChessPosition(8, 5), ChessPiece.PieceType.ROOK);
        game.makeMove(move);

        assertTrue(game.isInCheckmate(ChessGame.TeamColor.BLACK),
                "Promoting to Rook should checkmate the Black King");
    }
}

