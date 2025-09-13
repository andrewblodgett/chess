package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */

public class ChessGame {

    private boolean isWhitesTurn;
    private ChessBoard gameboard;

    public ChessGame() {
        gameboard = new ChessBoard();
        gameboard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return (isWhitesTurn) ? TeamColor.WHITE : TeamColor.BLACK;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        isWhitesTurn = (team == TeamColor.WHITE);
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        var startPiece = gameboard.getPiece(startPosition);
        if (startPiece == null) {
            return new HashSet<>();
        }
        if ((startPiece.getTeamColor() == TeamColor.WHITE && isWhitesTurn) || (startPiece.getTeamColor()==TeamColor.BLACK && !isWhitesTurn)) {
            return startPiece.pieceMoves(gameboard, startPosition);
        }
        return new HashSet<>();
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        var startPiece = gameboard.getPiece(move.getStartPosition());
        if (validMoves(move.getStartPosition()).contains(move)) {
            gameboard.addPiece(move.getEndPosition(),new ChessPiece(startPiece.getTeamColor(), (move.getPromotionPiece() == null) ? startPiece.getPieceType() : move.getPromotionPiece()));
            gameboard.addPiece(move.getStartPosition(),null);
            isWhitesTurn = !isWhitesTurn;
        } else {
            throw new InvalidMoveException("That move is not allowed");
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        for (var row = 1; row < 8; row++) {
            for (var col = 1; col < 8; col++) {
                return false;
            }
        }
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameboard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameboard;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return isWhitesTurn == chessGame.isWhitesTurn && Objects.equals(gameboard, chessGame.gameboard);
    }

    @Override
    public int hashCode() {
        return 71 * (isWhitesTurn ? 2 : 1) * gameboard.hashCode();
    }
}
