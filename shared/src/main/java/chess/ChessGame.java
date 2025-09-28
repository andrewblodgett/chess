package chess;

import java.util.ArrayList;
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
    private ArrayList<ChessBoard> history;

    public ChessGame() {
        gameboard = new ChessBoard();
        gameboard.resetBoard();
        isWhitesTurn = true;
        history.add(gameboard);
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
        BLACK,
    }

    /**
     * A utility method to easily get the other team.
     *
     * @param color
     * @return the other team's color
     */
    private TeamColor otherTeam(TeamColor color) {
        if (color == TeamColor.WHITE) {
            return TeamColor.BLACK;
        } else {
            return TeamColor.WHITE;
        }
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
        var validMoves = new HashSet<ChessMove>();
        var possibleMoves = startPiece.pieceMoves(gameboard, startPosition);
        for (var potentialMove : possibleMoves) {
            var potentialBoard = gameboard.copy();
            potentialBoard.movePiece(potentialMove);
            if (!evaluateBoardForCheck(potentialBoard, startPiece.getTeamColor())) {
                validMoves.add(potentialMove);
            }
        }
        return validMoves;
    }

    private Collection<ChessMove> allValidMovesForTeam(TeamColor color) {
        var allMoves = new HashSet<ChessMove>();
        for (var row = 1; row < 9; row++) {
            for (var col = 1; col < 9; col++) {
                var currentPiece = gameboard.getPiece(new ChessPosition(row, col));
                    if (currentPiece != null) {
                        if (currentPiece.getTeamColor() == color) {
                            allMoves.addAll(validMoves(new ChessPosition(row, col)));
                        }
                    }
                }
            }
        return allMoves;
    }

    /**
     * Evaluates a board to see if a team is in check.
     *
     * @param board
     * @param teamColor
     * @return true if the team is in check.
     */
    private boolean evaluateBoardForCheck(ChessBoard board, TeamColor teamColor) {
        var kingPos = getKingPosition(board, teamColor);

        for (var row = 1; row < 9; row++) {
            for (var col = 1; col < 9; col++) {
                var currentPiece = board.getPiece(new ChessPosition(row, col));
                if (currentPiece != null) {
                    if (currentPiece.getTeamColor() == otherTeam(teamColor)) {
                        var potentialMoves = currentPiece.pieceMoves(board, new ChessPosition(row, col));
                        for (var move : potentialMoves) {
                            if (move.getEndPosition().getColumn() == kingPos.getColumn() && move.getEndPosition().getRow() == kingPos.getRow()) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        try {
            var startingPiece = gameboard.getPiece(move.getStartPosition());
            if (((startingPiece.getTeamColor() == TeamColor.WHITE && isWhitesTurn) || (startingPiece.getTeamColor() == TeamColor.BLACK && !isWhitesTurn)) && validMoves(move.getStartPosition()).contains(move)){
                gameboard.movePiece(move);
                isWhitesTurn = !isWhitesTurn;

                history.add(gameboard);
            } else {
                throw new InvalidMoveException(move.toString() + " is an invalid move.");
            }
        } catch (Exception e) {
            throw new InvalidMoveException(move.toString() + " is an invalid move because " + e.toString());
        }
    }

    private ChessPosition getKingPosition(ChessBoard board, TeamColor teamColor) {
        for (var row = 1; row < 9; row++) {
            for (var col = 1; col < 9; col++) {
                var pieceInQuestion = board.getPiece(new ChessPosition(row, col));
                if (pieceInQuestion != null) {
                    if (pieceInQuestion.getPieceType() == ChessPiece.PieceType.KING && pieceInQuestion.getTeamColor() == teamColor) {
                        return new ChessPosition(row, col);
                    }
                }
            }
        }
        return new ChessPosition(0,0);
    }

    private boolean canWhiteKingCastleRight() {
        for (var board : history) {
            // go through board and see if king or rook have moved
            if (!((board.getPiece(new ChessPosition(1,5)).getPieceType() != ChessPiece.PieceType.KING && board.getPiece(new ChessPosition(1,5)).getTeamColor() != TeamColor.WHITE) &&(board.getPiece(new ChessPosition(1,8)).getPieceType() != ChessPiece.PieceType.ROOK && board.getPiece(new ChessPosition(1,8)).getTeamColor() != TeamColor.WHITE))) {
                return false;
            }
        }
        return true;
    }
    private boolean canWhiteKingCastleLeft() {
        for (var board : history) {
            // go through board and see if king or rook have moved
            if (!((board.getPiece(new ChessPosition(1,5)).getPieceType() != ChessPiece.PieceType.KING && board.getPiece(new ChessPosition(1,5)).getTeamColor() != TeamColor.WHITE) &&(board.getPiece(new ChessPosition(1,1)).getPieceType() != ChessPiece.PieceType.ROOK && board.getPiece(new ChessPosition(1,1)).getTeamColor() != TeamColor.WHITE))) {
                return false;
            }
        }
        return true;
    }


    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return evaluateBoardForCheck(gameboard, teamColor);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return allValidMovesForTeam(teamColor).isEmpty();
        }
        return false;

    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return allValidMovesForTeam(teamColor).isEmpty();
        }
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameboard = board.copy();
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameboard.copy();
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
        int result = Boolean.hashCode(isWhitesTurn);
        result = 31 * result + Objects.hashCode(gameboard);
        return result;
    }

    @Override
    public String toString() {
        var s = gameboard.toString();
        s+="\n";
        s+= (isWhitesTurn) ? "White's turn" : "Black's turn";
        return s;
    }
}
