import java.util.*;
public class State {
	private World board;
	private int playerNo;
	private int visitCount;
	private double winScore;
	private Move move;//reached by this move

	// copy constructor, getters, and setters
	public State(World board) {
		this.board = board;
		//this.playerNo = playerNo;
		this.visitCount = 0;
		//this.winScore = winScore;
	}
	
	//Getters - Setters
	public void setMove(Move move) {
		this.move = move;
	}
	public Move getMove() {
		return move;
	}
	public World getBoard() {
		return board;
	}
	public void setBoard(World board) {
		this.board = board;
	}
	public int getPlayerNo() {
		return playerNo;
	}
	public void setPlayerNo(int playerNo) {
		this.playerNo = playerNo;
	}
	public int getVisitCount() {
		return visitCount;
	}
	public void setVisitCount(int visitCount) {
		this.visitCount = visitCount;
	}
	public double getWinScore() {
		return winScore;
	}
	public void setWinScore(double winScore) {
		this.winScore = winScore;
	}

	public List<State> getAllPossibleStates() {
		List<State> nextStates = new LinkedList<State>();
		// constructs a list of all possible states from current state
		for(int i=0;i<this.board.getAvailableMoves().size();i++) {
			String move = this.board.getAvailableMoves().get(i);
			int x1 = Integer.parseInt(Character.toString(move.charAt(0)));
			int y1 = Integer.parseInt(Character.toString(move.charAt(1)));
			int x2 = Integer.parseInt(Character.toString(move.charAt(2)));
			int y2 = Integer.parseInt(Character.toString(move.charAt(3)));
			World world2 = new World(this.board);
			world2.makeMove(x1, y1, x2, y2, world2.getNoPrize(), 0);
			world2.setMyColor(this.board.getMyColor() ^ (int) 1);
			State nextState = new State(world2);
			Move madeMove = new Move(move);
			nextState.setMove(madeMove);
			nextStates.add(nextState);
		}
		return nextStates;
	}
	public void randomPlay() {
		/* get a list of all possible positions on the board and 
           play a random move */
	}
}