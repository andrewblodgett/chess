package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition pos) {
        var validMoves = new HashSet<ChessMove>();
        var row = pos.getRow();
        var col = pos.getColumn()-1;
        System.out.println(row + " " + col);

        if (type == PieceType.BISHOP) {
            while (true) {
                row = row +1;
                col = col+1;
                if (row > 8 || col > 8) {
                    break;
                }
                validMoves.add(new ChessMove(pos, new ChessPosition(row, col), null));
            }
            row = pos.getRow();
            col = pos.getColumn()-1;
            while (true) {
                row = row +1;
                col = col-1;
                if (row > 8 || col < 1) {
                    break;
                }
                validMoves.add(new ChessMove(pos, new ChessPosition(row, col), null));
            }
            row = pos.getRow();
            col = pos.getColumn()-1;
            while (true) {
                row = row-1;
                col = col+1;
                if (row < 1 || col > 8) {
                    break;
                }
                validMoves.add(new ChessMove(pos, new ChessPosition(row, col), null));
            }
            row = pos.getRow();
            col = pos.getColumn()-1;
            while (true) {
                row = row-1;
                col = col-1;
                if (row < 1 || col < 1) {
                    break;
                }
                validMoves.add(new ChessMove(pos, new ChessPosition(row, col), null));
            }

        }

        return validMoves;
    }
}
