package tak.util;

import java.awt.Color;
import java.util.ArrayList;

import tak.com.Piece;
import tak.window.TakTakSingleplayerWindow;

public class PlayerAI {

    public static ArrayList<OrderedPair> getAllValidMoves(int row, int column) {

        Piece p = TakTakSingleplayerWindow.board[row][column];

        ArrayList<OrderedPair> moves = new ArrayList<>();

        if (TakTakSingleplayerWindow.canPieceMoveToLocation(p.getTopPiece(), row + 1, column)) {
            moves.add(new OrderedPair(row + 1, column));
        }
        if (TakTakSingleplayerWindow.canPieceMoveToLocation(p.getTopPiece(), row + 1, column + 1)) {
            moves.add(new OrderedPair(row + 1, column + 1));
        }
        if (TakTakSingleplayerWindow.canPieceMoveToLocation(p.getTopPiece(), row + 1, column - 1)) {
            moves.add(new OrderedPair(row + 1, column - 1));

        }

        return moves;
    }

    public static ArrayList<OrderedPair> findMovements() {
        OrderedPair blacktomove;
        OrderedPair placetomoveto;

        for (int zRow = 0; zRow < TakTakSingleplayerWindow.ROWS; zRow++) {
            for (int zColumn = 0; zColumn < TakTakSingleplayerWindow.COLUMNS; zColumn++) {
                if (TakTakSingleplayerWindow.board[zRow][zColumn] != null
                        && TakTakSingleplayerWindow.board[zRow][zColumn].getTopPiece().getBackgroundColor() == Color.black) {
                    ArrayList<OrderedPair> moves = getAllValidMoves(zRow, zColumn);
                    if (!moves.isEmpty()) {
                        for (OrderedPair temp : moves) {
                            if (TakTakSingleplayerWindow.board[temp.getX()][temp.getY()] != null
                                    && TakTakSingleplayerWindow.board[temp.getX()][temp.getY()].getTopPiece().getBackgroundColor() == Color.WHITE) {
                                //Capturing a piece
                                blacktomove = new OrderedPair(zRow, zColumn);
                                placetomoveto = new OrderedPair(temp.getX(), temp.getY());
                                ArrayList<OrderedPair> retpack = new ArrayList<>();
                                retpack.add(blacktomove);
                                retpack.add(placetomoveto);
                                return (retpack);
                            } else if (zRow == 4) {
                                //Scoring a piece
                                blacktomove = new OrderedPair(zRow, zColumn);
                                placetomoveto = new OrderedPair(temp.getX(), temp.getY());
                                ArrayList<OrderedPair> retpack = new ArrayList<>();
                                retpack.add(blacktomove);
                                retpack.add(placetomoveto);
                                return (retpack);
                            }
                        }
                    }
                }
            }
        }

        {
            int column = (int) (Math.random() * TakTakSingleplayerWindow.COLUMNS);
            int row = (int) (Math.random() * TakTakSingleplayerWindow.ROWS);
            ArrayList<OrderedPair> moves;

            while (TakTakSingleplayerWindow.board[row][column] == null
                    || TakTakSingleplayerWindow.board[row][column] != null && getAllValidMoves(row, column).isEmpty()
                    || TakTakSingleplayerWindow.board[row][column] != null && TakTakSingleplayerWindow.board[row][column].getTopPiece().getBackgroundColor() != Color.black) {
                column = (int) (Math.random() * TakTakSingleplayerWindow.COLUMNS);
                row = (int) (Math.random() * TakTakSingleplayerWindow.ROWS);
            }
            moves = getAllValidMoves(row, column);
            blacktomove = new OrderedPair(row, column);
            placetomoveto = moves.get((int) (Math.random() * moves.size()));
            ArrayList<OrderedPair> retpack = new ArrayList<>();
            retpack.add(blacktomove);
            retpack.add(placetomoveto);
            return (retpack);
        }
    }

    public static void makeMove() {
        if (TakTakSingleplayerWindow.numBlackPiecesOnBoard <= 0) {
            TakTakSingleplayerWindow.chooseWinner();
            return;
        }
        ArrayList<OrderedPair> movings = new ArrayList<>(findMovements());
        OrderedPair moved = movings.get(0);
        OrderedPair movee = movings.get(1);

        TakTakSingleplayerWindow.movePieceToLocation(moved, movee);
    }
}
