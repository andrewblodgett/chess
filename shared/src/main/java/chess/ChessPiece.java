package chess;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece implements Serializable {

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
        } else {
            return piece.getTeamColor() == pieceColor;
        }
    }

    private boolean canCapture(ChessPiece piece) {
        if (piece == null) {
            return false;
        } else {
            return !isSameColor(piece);
        }
    }

    private void kingAndKnightHelperUtil(ChessBoard board, ChessPosition pos, HashSet<ChessMove> validMoves, int row, int col, int[] p) {
        var newPos = new ChessPosition(row + p[0], col + p[1]);
        if (newPos.getRow() >= 1 && newPos.getRow() <= 8 && newPos.getColumn() >= 1 && newPos.getColumn() <= 8) {
            if (board.getPiece(newPos) == null) {
                validMoves.add(new ChessMove(pos, newPos, null));
            } else if (canCapture(board.getPiece(newPos))) {
                validMoves.add(new ChessMove(pos, newPos, null));
            }
        }

    }

    private void moveUtility(ChessPosition pos, ChessBoard board, HashSet<ChessMove> validMoves, int rowIncrement, int colIncrement) {
        var row = pos.getRow();
        var col = pos.getColumn();
        while (true) {
            row = row + rowIncrement;
            col = col + colIncrement;
            if (row > 8 || col > 8 || row < 1 || col < 1 || isSameColor(board.getPiece(new ChessPosition(row, col)))) {
                break;
            }
            validMoves.add(new ChessMove(pos, new ChessPosition(row, col), null));
            if (canCapture(board.getPiece(new ChessPosition(row, col)))) {
                break;
            }
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
            int[][] increments = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
            for (int[] increment : increments) {
                moveUtility(pos, board, validMoves, increment[0], increment[1]);
            }
        }
        if (type == PieceType.ROOK || type == PieceType.QUEEN) {
            int[][] increments = {{1, 0}, {0, -1}, {-1, 0}, {0, 1}};
            for (int[] increment : increments) {
                moveUtility(pos, board, validMoves, increment[0], increment[1]);
            }
        } else if (type == PieceType.KING) {
            int[][] kingPossibilities = {{1, 1}, {1, 0}, {1, -1}, {0, 1}, {0, -1}, {-1, 1}, {-1, 0}, {-1, -1}};
            for (var p : kingPossibilities) {
                try {
                    kingAndKnightHelperUtil(board, pos, validMoves, row, col, p);
                } catch (Exception e) {
                }
            }
        } else if (type == PieceType.KNIGHT) {
            int[][] knightPossibilities = {{2, 1}, {1, 2}, {2, -1}, {-1, 2}, {-2, -1}, {-1, -2}, {-2, 1}, {1, -2}};
            for (var p : knightPossibilities) {
                try {
                    if (col + p[1] > 0 && row + p[0] > 0) {
                        kingAndKnightHelperUtil(board, pos, validMoves, row, col, p);
                    }
                } catch (Exception e) {
                }
            }
        } else if (type == PieceType.PAWN) {
            var directionMultiplier = 1;
            var startingRow = 2;
            var promoRow = 8;
            if (pieceColor == ChessGame.TeamColor.BLACK) {
                directionMultiplier = -1;
                startingRow = 7;
                promoRow = 1;
            }
            try {
                if (canCapture(board.getPiece(new ChessPosition(row + directionMultiplier, col + 1)))) {
                    validMoves.add(new ChessMove(pos, new ChessPosition(row + directionMultiplier, col + 1), null));
                }
            } catch (ArrayIndexOutOfBoundsException e) {
            }
            try {
                if (canCapture(board.getPiece(new ChessPosition(row + directionMultiplier, col - 1)))) {
                    validMoves.add(new ChessMove(pos, new ChessPosition(row + directionMultiplier, col - 1), null));
                }
            } catch (ArrayIndexOutOfBoundsException e) {
            }
            if (board.getPiece(new ChessPosition(row + directionMultiplier, col)) == null) {
                validMoves.add(new ChessMove(pos, new ChessPosition(row + directionMultiplier, col), null));
                if (row == startingRow) {
                    if (board.getPiece(new ChessPosition(row + 2 * directionMultiplier, col)) == null) {
                        validMoves.add(new ChessMove(pos, new ChessPosition(row + 2 * directionMultiplier, col), null));
                    }
                }
            }
            var movesToRemove = new HashSet<ChessMove>();
            var movesToAdd = new HashSet<ChessMove>();
            for (var m : validMoves) {
                if (m.getEndPosition().getRow() == promoRow) {
                    movesToRemove.add(m);
                    movesToAdd.add(new ChessMove(m, PieceType.QUEEN));
                    movesToAdd.add(new ChessMove(m, PieceType.ROOK));
                    movesToAdd.add(new ChessMove(m, PieceType.BISHOP));
                    movesToAdd.add(new ChessMove(m, PieceType.KNIGHT));
                }
            }
            validMoves.removeAll(movesToRemove);
            validMoves.addAll(movesToAdd);
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

    @Override
    public String toString() {
        var s = "";
        if (type == PieceType.PAWN) {
            s = "p";
        } else if (type == PieceType.KNIGHT) {
            s = "n";
        } else if (type == PieceType.BISHOP) {
            s = "b";
        } else if (type == PieceType.ROOK) {
            s = "r";
        } else if (type == PieceType.QUEEN) {
            s = "q";
        } else if (type == PieceType.KING) {
            s = "k";
        }
        if (pieceColor == ChessGame.TeamColor.WHITE) {
            s = s.toUpperCase();
        }
        return s;
    }
}
