package tak.util;

import java.awt.Color;
import java.util.ArrayList;

import tak.com.Piece;
import tak.window.TakTakSingleplayerWindow;

public class PlayerAI {

	public static ArrayList<OrderedPair> getAllValidMoves(int row, int column) {

		Piece p = TakTakSingleplayerWindow.board[row][column];

		ArrayList<OrderedPair> moves = new ArrayList<OrderedPair>();

		if (p.getTopPiece().isKing()) {
			if (TakTakSingleplayerWindow.canPieceMoveToLocation(p.getTopPiece(), row + 1, column)) {
				moves.add(new OrderedPair(row + 1, column));
			}
			if (TakTakSingleplayerWindow.canPieceMoveToLocation(p.getTopPiece(), row + 2, column)) {
				moves.add(new OrderedPair(row + 2, column));
			}
			if (TakTakSingleplayerWindow.canPieceMoveToLocation(p.getTopPiece(), row + 1, column + 1)) {
				moves.add(new OrderedPair(row + 1, column + 1));
			}
			if (TakTakSingleplayerWindow.canPieceMoveToLocation(p.getTopPiece(), row + 2, column + 2)) {
				moves.add(new OrderedPair(row + 2, column + 2));
			}
			if (TakTakSingleplayerWindow.canPieceMoveToLocation(p.getTopPiece(), row + 1, column - 1)) {
				moves.add(new OrderedPair(row + 1, column - 1));
			}
			if (TakTakSingleplayerWindow.canPieceMoveToLocation(p.getTopPiece(), row + 2, column - 2)) {
				moves.add(new OrderedPair(row + 2, column - 2));
			}
			if (TakTakSingleplayerWindow.canPieceMoveToLocation(p.getTopPiece(), row - 1, column)) {
				moves.add(new OrderedPair(row - 1, column));
			}
			if (TakTakSingleplayerWindow.canPieceMoveToLocation(p.getTopPiece(), row - 2, column)) {
				moves.add(new OrderedPair(row - 2, column));
			}
			if (TakTakSingleplayerWindow.canPieceMoveToLocation(p.getTopPiece(), row - 1, column + 1)) {
				moves.add(new OrderedPair(row - 1, column + 1));
			}
			if (TakTakSingleplayerWindow.canPieceMoveToLocation(p.getTopPiece(), row - 2, column + 2)) {
				moves.add(new OrderedPair(row - 2, column + 2));
			}
			if (TakTakSingleplayerWindow.canPieceMoveToLocation(p.getTopPiece(), row - 1, column - 1)) {
				moves.add(new OrderedPair(row - 1, column - 1));
			}
			if (TakTakSingleplayerWindow.canPieceMoveToLocation(p.getTopPiece(), row - 2, column - 2)) {
				moves.add(new OrderedPair(row - 2, column - 2));
			}
			if (TakTakSingleplayerWindow.canPieceMoveToLocation(p.getTopPiece(), row, column - 1)) {
				moves.add(new OrderedPair(row, column - 1));
			}
			if (TakTakSingleplayerWindow.canPieceMoveToLocation(p.getTopPiece(), row, column - 2)) {
				moves.add(new OrderedPair(row, column - 2));
			}
			if (TakTakSingleplayerWindow.canPieceMoveToLocation(p.getTopPiece(), row, column + 1)) {
				moves.add(new OrderedPair(row, column + 1));
			}
			if (TakTakSingleplayerWindow.canPieceMoveToLocation(p.getTopPiece(), row, column + 2)) {
				moves.add(new OrderedPair(row, column + 2));
			}
		}

		if (!p.getTopPiece().isKing()) {
			if (TakTakSingleplayerWindow.canPieceMoveToLocation(p.getTopPiece(), row + 1, column)) {
				moves.add(new OrderedPair(row + 1, column));
			}
			if (TakTakSingleplayerWindow.canPieceMoveToLocation(p.getTopPiece(), row + 1, column + 1)) {
				moves.add(new OrderedPair(row + 1, column + 1));
			}
			if (TakTakSingleplayerWindow.canPieceMoveToLocation(p.getTopPiece(), row + 1, column - 1)) {
				moves.add(new OrderedPair(row + 1, column - 1));
			}
		}

		return moves;
	}

	public static OrderedPair getPieceToMove() {
		OrderedPair biggestStack = null;

		for (int zRow = 0; zRow < TakTakSingleplayerWindow.ROWS; zRow++) {
			for (int zColumn = 0; zColumn < TakTakSingleplayerWindow.COLUMNS; zColumn++) {
				if (TakTakSingleplayerWindow.board[zRow][zColumn] != null
						&& TakTakSingleplayerWindow.board[zRow][zColumn].getTopPiece().getBackgroundColor() == Color.black) {
					ArrayList<OrderedPair> moves = getAllValidMoves(zRow, zColumn);

					if (!moves.isEmpty()) {
						for (OrderedPair temp : moves) {
							if (TakTakSingleplayerWindow.board[temp.getX()][temp.getY()] != null
									&& TakTakSingleplayerWindow.board[temp.getX()][temp.getY()].getWholeStack()
											.size() > moves.size()) {
								biggestStack = new OrderedPair(temp.getX(), temp.getY());
							}
						}
					}
				}
			}
		}

		if (biggestStack == null) {
			int column = TakTakSingleplayerWindow.rand.nextInt(TakTakSingleplayerWindow.COLUMNS);
			int row = TakTakSingleplayerWindow.rand.nextInt(TakTakSingleplayerWindow.ROWS);

			while (TakTakSingleplayerWindow.board[row][column] == null
					|| TakTakSingleplayerWindow.board[row][column] != null && getAllValidMoves(row, column).size() == 0
							&& TakTakSingleplayerWindow.board[row][column].getTopPiece()
									.getBackgroundColor() != Color.black) {
				column = TakTakSingleplayerWindow.rand.nextInt(TakTakSingleplayerWindow.COLUMNS);
				row = TakTakSingleplayerWindow.rand.nextInt(TakTakSingleplayerWindow.ROWS);
			}
			biggestStack = new OrderedPair(row, column);
		}

		if (TakTakSingleplayerWindow.board[biggestStack.getX()][biggestStack.getY()].getTopPiece().getBackgroundColor() == Color.white) {
			biggestStack = getPieceToMove();
		}
		return biggestStack;
	}
	
	public static void makeMove() {
		if (TakTakSingleplayerWindow.numBlackPiecesOnBoard <= 0) {
			TakTakSingleplayerWindow.chooseWinner();
			return;
		}
			
		OrderedPair op = getPieceToMove();
		ArrayList<OrderedPair> moves = getAllValidMoves(op.getX(), op.getY());
		
		if (moves != null && !moves.isEmpty())
			TakTakSingleplayerWindow.movePieceToLocation(op, moves.get(TakTakSingleplayerWindow.rand.nextInt(moves.size())));
		else {
			while(moves.isEmpty() || moves == null) {
				op = getPieceToMove();
				moves = getAllValidMoves(op.getX(), op.getY());
			}
			TakTakSingleplayerWindow.movePieceToLocation(op, moves.get(TakTakSingleplayerWindow.rand.nextInt(moves.size())));
		}
	}

}
