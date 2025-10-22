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
        history = new ArrayList<ChessBoard>();
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
        if (startPiece.getPieceType() == ChessPiece.PieceType.KING) {
            possibleMoves.addAll(allCastleMoves(startPiece.getTeamColor()));
        }
        if (startPiece.getPieceType() == ChessPiece.PieceType.PAWN) {
            possibleMoves.addAll(enPassant(startPosition));
        }
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
                if (currentPiece != null && currentPiece.getTeamColor() == otherTeam(teamColor)) {
                    var potentialMoves = currentPiece.pieceMoves(board, new ChessPosition(row, col));
                    for (var move : potentialMoves) {
                        if (move.getEndPosition().getColumn() == kingPos.getColumn() && move.getEndPosition().getRow() == kingPos.getRow()) {
                            return true;
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
            if (((startingPiece.getTeamColor() == TeamColor.WHITE && isWhitesTurn)
                    || (startingPiece.getTeamColor() == TeamColor.BLACK && !isWhitesTurn)) && validMoves(move.getStartPosition()).contains(move)) {
                if (startingPiece.getPieceType() == ChessPiece.PieceType.PAWN && !enPassant(move.getStartPosition()).isEmpty()) {
                    for (var ep : enPassant(move.getStartPosition())) {
                        if (ep.getEndPosition().equals(move.getEndPosition())) {
                            var pawnBehindVector = (startingPiece.getTeamColor() == TeamColor.BLACK) ? 1 : -1;
                            gameboard.addPiece(
                                    new ChessPosition(move.getEndPosition().getRow() + pawnBehindVector, move.getEndPosition().getColumn()), null);
                        }
                    }
                }

                history.add(gameboard.copy());
                //check if we need to move rook after king moves for a castle
                if (startingPiece.getPieceType() == ChessPiece.PieceType.KING) {
                    if (move.getStartPosition().getColumn() - move.getEndPosition().getColumn() == -2) {
                        gameboard.movePiece(new ChessMove(new ChessPosition(move.getStartPosition().getRow(), 8),
                                new ChessPosition(move.getStartPosition().getRow(), 6), null));
                    } else if (move.getStartPosition().getColumn() - move.getEndPosition().getColumn() == 2) {
                        gameboard.movePiece(new ChessMove(new ChessPosition(move.getStartPosition().getRow(), 1),
                                new ChessPosition(move.getStartPosition().getRow(), 4), null));
                    }
                }
                gameboard.movePiece(move);
                isWhitesTurn = !isWhitesTurn;
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
        return new ChessPosition(0, 0);
    }

    private Collection<ChessMove> allCastleMoves(TeamColor teamColor) {
        var possibleCastles = new HashSet<ChessMove>();
        if (getKingPosition(gameboard, teamColor).equals(new ChessPosition(1, 5))
                || getKingPosition(gameboard, teamColor).equals(new ChessPosition(8, 5))) {
            if (!evaluateBoardForCheck(gameboard, teamColor)) {
                possibleCastles.addAll(possibleCastlingMovesRight(teamColor));
                possibleCastles.addAll(possibleCastlingMovesLeft(teamColor));
            }
        }

        return possibleCastles;
    }

    private Collection<ChessMove> possibleCastlingMovesRight(TeamColor teamColor) {
        var row = (teamColor == TeamColor.WHITE) ? 1 : 8;
        var possibleCastles = new HashSet<ChessMove>();
        // Check if king is in his starting square
        if (gameboard.getPiece(new ChessPosition(row, 5)) != null) {
            if (gameboard.getPiece(new ChessPosition(row, 5)).getPieceType() != ChessPiece.PieceType.KING) {
                return possibleCastles;
            }
        }
        // First check that the adjacent spaces are empty
        if (gameboard.getPiece(new ChessPosition(row, 6)) != null || gameboard.getPiece(new ChessPosition(row, 7)) != null) {
            return possibleCastles;
        }
        // Check if the king will have to move through check
        var potentialBoard = gameboard.copy();
        potentialBoard.movePiece(new ChessMove(new ChessPosition(row, 5), new ChessPosition(row, 6), null));
        if (evaluateBoardForCheck(potentialBoard, teamColor)) {
            return possibleCastles;
        }
        potentialBoard.movePiece(new ChessMove(new ChessPosition(row, 6), new ChessPosition(row, 7), null));
        if (evaluateBoardForCheck(potentialBoard, teamColor)) {
            return possibleCastles;
        }
        // go through board and see if king or rook have moved

        for (var board : history) {
            var whereKingShouldBe = board.getPiece(new ChessPosition(row, 5));
            var whereRookShouldBe = board.getPiece(new ChessPosition(row, 8));
            if (whereKingShouldBe == null || whereRookShouldBe == null) {
                return possibleCastles;
            }
            if (whereKingShouldBe.getPieceType() != ChessPiece.PieceType.KING || whereKingShouldBe.getTeamColor() != teamColor) {
                return possibleCastles;
            }
            if (whereRookShouldBe.getPieceType() != ChessPiece.PieceType.ROOK || whereRookShouldBe.getTeamColor() != teamColor) {
                return possibleCastles;
            }

        }


        possibleCastles.add(new ChessMove(new ChessPosition(row, 5), new ChessPosition(row, 7), null));
        return possibleCastles;
    }

    private Collection<ChessMove> possibleCastlingMovesLeft(TeamColor teamColor) {
        var row = (teamColor == TeamColor.WHITE) ? 1 : 8;
        var possibleCastles = new HashSet<ChessMove>();
        // Check if king is in his starting square
        if (gameboard.getPiece(new ChessPosition(row, 5)) != null) {
            if (gameboard.getPiece(new ChessPosition(row, 5)).getPieceType() != ChessPiece.PieceType.KING) {
                return possibleCastles;
            }
        }
        // First check that the adjacent spaces are empty
        if (gameboard.getPiece(new ChessPosition(row, 4)) != null || gameboard.getPiece(new ChessPosition(row, 3)) != null) {
            return possibleCastles;
        }
        // Check if the king will have to move through check
        var potentialBoard = gameboard.copy();
        potentialBoard.movePiece(new ChessMove(new ChessPosition(row, 5), new ChessPosition(row, 4), null));
        if (evaluateBoardForCheck(potentialBoard, teamColor)) {
            return possibleCastles;
        }
        potentialBoard.movePiece(new ChessMove(new ChessPosition(row, 4), new ChessPosition(row, 3), null));
        if (evaluateBoardForCheck(potentialBoard, teamColor)) {
            return possibleCastles;
        }
        // go through board and see if king or rook have moved
        for (var board : history) {
            var whereKingShouldBe = board.getPiece(new ChessPosition(row, 5));
            var whereRookShouldBe = board.getPiece(new ChessPosition(row, 1));
            if (whereKingShouldBe == null || whereRookShouldBe == null) {
                return possibleCastles;
            }
            if (whereKingShouldBe.getPieceType() != ChessPiece.PieceType.KING || whereKingShouldBe.getTeamColor() != teamColor) {
                return possibleCastles;
            }
            if (whereRookShouldBe.getPieceType() != ChessPiece.PieceType.ROOK || whereRookShouldBe.getTeamColor() != teamColor) {
                return possibleCastles;
            }
        }

        possibleCastles.add(new ChessMove(new ChessPosition(row, 5), new ChessPosition(row, 3), null));
        return possibleCastles;
    }


    private Collection<ChessMove> enPassant(ChessPosition startPosition) {
        var possibleEnPassant = new HashSet<ChessMove>();
        var pawn = gameboard.getPiece(startPosition);
        if (!history.isEmpty()) {
            var lastturnboard = history.getLast();
            var enemyColor = otherTeam(pawn.getTeamColor());
            var directionVector = (pawn.getTeamColor() == TeamColor.WHITE) ? 1 : -1;
            if (startPosition.getRow() == 4 || startPosition.getRow() == 5) {
                if (startPosition.getColumn() < 8) {
                    var potentialLastMovedPawn = lastturnboard.getPiece(
                            new ChessPosition(startPosition.getRow() + 2 * directionVector, startPosition.getColumn() + 1));
                    if (potentialLastMovedPawn != null && potentialLastMovedPawn.getPieceType() == ChessPiece.PieceType.PAWN
                            && potentialLastMovedPawn.getTeamColor() == enemyColor) {
                        var movedPawn = gameboard.getPiece(new ChessPosition(startPosition.getRow(), startPosition.getColumn() + 1));
                        if (movedPawn != null && movedPawn.getPieceType() == ChessPiece.PieceType.PAWN && movedPawn.getTeamColor() == enemyColor) {
                            possibleEnPassant.add(new ChessMove(startPosition,
                                    new ChessPosition(startPosition.getRow() + directionVector, startPosition.getColumn() + 1), null));
                        }
                    }

                }

                if (startPosition.getColumn() > 1) {
                    var potentialLastMovedPawn = lastturnboard.getPiece(
                            new ChessPosition(startPosition.getRow() + 2 * directionVector, startPosition.getColumn() - 1));
                    if (potentialLastMovedPawn != null && potentialLastMovedPawn.getPieceType() == ChessPiece.PieceType.PAWN
                            && potentialLastMovedPawn.getTeamColor() == enemyColor) {
                        var movedPawn = gameboard.getPiece(new ChessPosition(startPosition.getRow(), startPosition.getColumn() - 1));
                        if (movedPawn != null && movedPawn.getPieceType() == ChessPiece.PieceType.PAWN && movedPawn.getTeamColor() == enemyColor) {
                            possibleEnPassant.add(new ChessMove(startPosition, new ChessPosition(startPosition.getRow() + directionVector,
                                    startPosition.getColumn() - 1), null));
                        }
                    }
                }
            }


        }
        return possibleEnPassant;
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
        s += "\n";
        s += (isWhitesTurn) ? "White's turn" : "Black's turn";
        return s;
    }
}
