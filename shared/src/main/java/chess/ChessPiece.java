package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

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


    private boolean isSameColor(ChessPiece piece) {
        if (piece == null) {
            return false;
        }
        else {
            return piece.getTeamColor() == pieceColor;
        }
    }

    private boolean canCapture(ChessPiece piece) {
        if (piece == null) {
            return false;
        }
        else {
            return !isSameColor(piece);
        }
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
        var col = pos.getColumn();

        if (type == PieceType.BISHOP || type == PieceType.QUEEN) {
            while (true) {
                row = row +1;
                col = col+1;
                if (row > 8 || col > 8 || isSameColor(board.getPiece(new ChessPosition(row, col)))) {
                    break;
                }
                validMoves.add(new ChessMove(pos, new ChessPosition(row, col), null));
                if (canCapture(board.getPiece(new ChessPosition(row, col)))) {
                    break;
                }
            }
            row = pos.getRow();
            col = pos.getColumn();
            while (true) {
                row = row +1;
                col = col-1;
                if (row > 8 || col < 1 || isSameColor(board.getPiece(new ChessPosition(row, col)))) {
                    break;
                }
                validMoves.add(new ChessMove(pos, new ChessPosition(row, col), null));
                if (canCapture(board.getPiece(new ChessPosition(row, col)))) {
                    break;
                }
            }
            row = pos.getRow();
            col = pos.getColumn();
            while (true) {
                row = row-1;
                col = col+1;
                if (row < 1 || col > 8 || isSameColor(board.getPiece(new ChessPosition(row, col)))) {
                    break;
                }
                validMoves.add(new ChessMove(pos, new ChessPosition(row, col), null));
                if (canCapture(board.getPiece(new ChessPosition(row, col)))) {
                    break;
                }
            }
            row = pos.getRow();
            col = pos.getColumn();
            while (true) {
                row = row-1;
                col = col-1;
                if (row < 1 || col < 1 || isSameColor(board.getPiece(new ChessPosition(row, col)))) {
                    break;
                }
                validMoves.add(new ChessMove(pos, new ChessPosition(row, col), null));
                if (canCapture(board.getPiece(new ChessPosition(row, col)))) {
                    break;
                }
            }

        }
        if (type == PieceType.ROOK || type == PieceType.QUEEN) {
            row = pos.getRow();
            col = pos.getColumn();
            var temprow = row;
            var tempcol = col;
            while (temprow < 8) {
                temprow++;
                if (isSameColor(board.getPiece(new ChessPosition(temprow, tempcol)))) {
                    break;
                }
                validMoves.add(new ChessMove(pos, new ChessPosition(temprow, tempcol), null));
                if (canCapture(board.getPiece(new ChessPosition(temprow, tempcol)))) {
                    break;
                }
            }
            temprow = row;
            while (temprow > 1) {
                temprow--;
                if (isSameColor(board.getPiece(new ChessPosition(temprow, tempcol)))) {
                    break;
                }
                validMoves.add(new ChessMove(pos, new ChessPosition(temprow, tempcol), null));
                if (canCapture(board.getPiece(new ChessPosition(temprow, tempcol)))) {
                    break;
                }
            }
            temprow = row;
            while (tempcol > 1) {
                tempcol--;
                if (isSameColor(board.getPiece(new ChessPosition(temprow, tempcol)))) {
                    break;
                }
                validMoves.add(new ChessMove(pos, new ChessPosition(temprow, tempcol), null));
                if (canCapture(board.getPiece(new ChessPosition(temprow, tempcol)))) {
                    break;
                }
            }
            tempcol = col;
            while (tempcol < 8) {
                tempcol++;
                if (isSameColor(board.getPiece(new ChessPosition(temprow, tempcol)))) {
                    break;
                }
                validMoves.add(new ChessMove(pos, new ChessPosition(temprow, tempcol), null));
                if (canCapture(board.getPiece(new ChessPosition(temprow, tempcol)))) {
                    break;
                }
            }
        }

        return validMoves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
