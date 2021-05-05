import java.util.LinkedList;
import java.util.List;

public class Evaluation {

	State position;
	double mobilityPenalty = 0.1;
	double kingIsSafe = 400;

	public Evaluation(State position) {
		this.position = position;
	}


	//	public double main_evaluation(State pos) {
	//		
	//	}

	public double pieceEvaluation() {
		String[][] board = this.position.getBoard().getBoard();
		int kings=0;
		int rooks=0;
		int pawns=0;
		int isolatedPawns=0;
		int doubledPawns=0;
		int blockedPawns=0;
		double blockedRooks=0;
		double mobility=0;
		double kingSafety=0;
		String my_color;
		if(position.getBoard().getMyColor()==0) {
			my_color = "W";
		}
		else {
			my_color = "B";
		}
		for(int i=0;i<position.getBoard().getRows();i++) {
			for(int j=0;j<position.getBoard().getColumns();j++) {
				String firstLetter = Character.toString(board[i][j].charAt(0));
				if (firstLetter.equals(" ") || firstLetter.equals("P")) {
					continue;
				}
				else if(firstLetter.equals(my_color)) {
					// check the kind of my chess part
					String secondLetter = Character.toString(board[i][j].charAt(1));

					if(secondLetter.equals("P"))	// it is a pawn
					{
						pawns++;
						if(isIsolatedPawn(i, j)) {
							isolatedPawns++;
						}
						if(isDoubled(i, j)) {
							doubledPawns++;
						}
						if(isBlocked(i, j)) {
							blockedPawns++;
						}
					}
					else if(secondLetter.equals("R"))	// it is a rook
					{
						rooks++;
						blockedRooks+=rookIsBlocked(i, j);
						mobility += evaluateMobility(i, j);
					}
					else // it is the king
					{
						kings++;
						if(notSafeKing()) {
							kingSafety += kingIsSafe;
						}
					}
				}
				else {
					// check the kind of opponent's chess part
					String secondLetter = Character.toString(board[i][j].charAt(1));
					if(secondLetter.equals("P"))	// it is a pawn
					{
						pawns--;
						if(isIsolatedPawn(i, j)) {
							isolatedPawns--;
						}
						if(isDoubled(i, j)) {
							doubledPawns--;
						}
						if(isBlocked(i, j)) {
							blockedPawns--;
						}
					}
					else if(secondLetter.equals("R"))	// it is a rook
					{
						rooks--;
						blockedRooks-=rookIsBlocked(i, j);
						mobility -= evaluateMobility(i, j);
					}
					else // it is the king
					{
						kings--;
						if(notSafeKing()) {
							kingSafety -= kingIsSafe;
						}
					}
				}
			}
		}
		double finalEval = 1800*kings+500*rooks+100*pawns-10*(isolatedPawns+doubledPawns+blockedPawns)-30*blockedRooks-10*mobility-kingSafety;

		return finalEval;
	}

	public boolean isIsolatedPawn(int x, int y) {
		String[][] board = position.getBoard().getBoard();
		int columns = position.getBoard().getColumns();
		int rows = position.getBoard().getRows();
		if(x==0||x==rows-1) {
			return false;
		}
		if(position.getBoard().getMyColor()==0) {
			//if on edge of board
			if(y==0) {
				if(board[x+1][y+1].equals("WP")) {
					return false;
				}
			}
			else if(y==columns-1) {
				if(board[x+1][y-1].equals("WP")) {
					return false;
				}
			}
			else if(board[x+1][y-1].equals("WP")||board[x+1][y+1].equals("WP")) {
				return false;
			}
		}
		else {
			//if on edge of board
			if(y==0) {
				if(board[x-1][y+1].equals("BP")) {
					return false;
				}
			}
			else if(y==columns-1) {
				if(board[x-1][y-1].equals("BP")) {
					return false;
				}
			}
			else if(board[x-1][y-1].equals("BP")||board[x-1][y+1].equals("BP")) {
				return false;
			}
		}
		return true;
	}


	boolean isDoubled(int x, int y) {
		String[][] board = position.getBoard().getBoard();
		int rows = position.getBoard().getRows();
		if(position.getBoard().getMyColor()==0) {
			for (int i=0; i<rows;i++) {
				if(board[i][y].equals("WP")&&i!=x) {
					return true;
				}
			}
		}
		else {
			for (int i=0; i<rows;i++) {
				if(board[i][y].equals("BP")&&i!=x) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isBlocked(int x, int y) {
		String[][] board = position.getBoard().getBoard();
		int rows = position.getBoard().getRows();
		int columns = position.getBoard().getColumns();
		if(position.getBoard().getMyColor()==0&&x==0) {
			return false;
		}
		if(position.getBoard().getMyColor()==1&&x==rows-1) {
			return false;
		}
		if(((position.getBoard().getMyColor()==0)&&(!board[x-1][y].equals(" ")||!board[x-1][y].equals("P")))||((!board[x+1][y].equals(" ")||!board[x+1][y].equals("P"))&&(position.getBoard().getMyColor()==1))) {
			if(position.getBoard().getMyColor()==0) {
				//if on edge of board
				if(y==0) {
					if(!(Character.toString(board[x-1][y+1].charAt(0)).equals("B"))) {
						return true;
					}
				}
				else if(y==columns-1) {
					if(!(Character.toString(board[x-1][y-1].charAt(0)).equals("B"))) {
						return true;
					}
				}
				else if(!(Character.toString(board[x-1][y-1].charAt(0)).equals("B"))||!(Character.toString(board[x-1][y+1].charAt(0)).equals("B"))) {
					return true;
				}
			}
			else {
				//if on edge of board
				if(y==0) {
					if(!(Character.toString(board[x+1][y+1].charAt(0)).equals("W"))) {
						return true;
					}
				}
				else if(y==columns-1) {
					if(!(Character.toString(board[x+1][y-1].charAt(0)).equals("W"))) {
						return true;
					}
				}
				else if(!(Character.toString(board[x+1][y-1].charAt(0)).equals("W"))||!(Character.toString(board[x+1][y+1].charAt(0)).equals("W"))) {
					return true;
				}
			}
		}
		return false;
	}

	public double rookIsBlocked(int i, int j) {
		String[][] board = position.getBoard().getBoard();
		int rows = position.getBoard().getRows();
		int columns = position.getBoard().getColumns();
		String firstLetter;
		int counter=0;
		//for white
		if(position.getBoard().getMyColor()==0) {
			// check if it can move upwards
			if(!((i-1) < 0)) {
				firstLetter = Character.toString(board[i-1][j].charAt(0));
				if(!firstLetter.equals("W")) {
					counter++;
				}
			}
			// check if it can move downwards
			if(!((i+1) == rows)) {
				firstLetter = Character.toString(board[i+1][j].charAt(0));
				if(!firstLetter.equals("W")){
					counter++;
				}
			}
			// check if it can move on the left
			if(!((j-1) < 0)) {
				firstLetter = Character.toString(board[i][j-1].charAt(0));
				if(!firstLetter.equals("W")){
					counter++;
				}
			}
			// check of it can move on the right
			if(!((j+1) == columns)) {
				firstLetter = Character.toString(board[i][j+1].charAt(0));
				if(!firstLetter.equals("W")){
					counter++;
				}
			}
		}
		else {
			// check if it can move upwards
			if(!((i-1) < 0)) {
				firstLetter = Character.toString(board[i-1][j].charAt(0));
				if(!firstLetter.equals("B")) {
					counter++;
				}
			}
			// check if it can move downwards
			if(!((i+1) == rows)) {
				firstLetter = Character.toString(board[i+1][j].charAt(0));
				if(!firstLetter.equals("B")){
					counter++;
				}
			}
			// check if it can move on the left
			if(!((j-1) < 0)) {
				firstLetter = Character.toString(board[i][j-1].charAt(0));
				if(!firstLetter.equals("B")){
					counter++;
				}
			}
			// check of it can move on the right
			if(!((j+1) == columns)) {
				firstLetter = Character.toString(board[i][j+1].charAt(0));
				if(!firstLetter.equals("B")){
					counter++;
				}
			}
		}
		return counter/4;
	}

	public boolean notSafeKing() {
		String[][] board = position.getBoard().getBoard();
		List<State> nextStates = new LinkedList<State>();
		nextStates = position.getAllPossibleStates();
		for(int i=0;i<nextStates.size();i++) {
			int x2 = Integer.parseInt(Character.toString(nextStates.get(i).getMove().getMove().charAt(2)));
			int y2 = Integer.parseInt(Character.toString(nextStates.get(i).getMove().getMove().charAt(3)));
			if(position.getBoard().getMyColor()==0) {
				if(board[x2][y2].equals("WK")) {
					return true;
				}
			}
			else {
				if(board[x2][y2].equals("BK")) {
					return true;
				}
			}
		}
		return false;
	}
	
	
	public double evaluateMobility(int i, int j) {
		double result = 0;
		if(position.getBoard().getMyColor()==0) {
			if((i==6&&j==1)||(i==6&&j==3)) {
				result += mobilityPenalty;
			}
		}
		else {
			if((i==0&&j==1)||(i==0&&j==3)) {
				result += mobilityPenalty;
			}
		}
		return result;
	}

	public double evaluatePoints() {
		return position.getBoard().getMyScore()-position.getBoard().getOpScore();
	}
}
