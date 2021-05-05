import java.util.ArrayList;
import java.util.Random;
//import java.util.Scanner;  // Import the Scanner class
import java.io.*;

public class World
{
	private double evaluation;
	private String[][] board = null;
	private int rows = 7;
	private int columns = 5;
	private int myColor = 0; // 0 is white, 1 is black
	public ArrayList<String> availableMoves = null;
	private int rookBlocks = 3;	// rook can move towards <rookBlocks> blocks in any vertical or horizontal direction
	private int nTurns = 0;
	private int nBranches = 0;
	private int noPrize = 9;
	private int myScore = 0;
	private int opScore = 0;
	// private variable for algorithm to choose
	private int algo = 4; 
	// 0 is minimax, 1 is minimax with a-b, 2 is forward pruning, 
	// 3 is minimax with singular extensions, 4 is Monte Carlo Tree Search
	private boolean premadeMoves = false; // choose some premade moves at the start of game
	private String checker; // check if moves are repeated
	private double endTime = 6000; // maximum time allowed per move
	private int algoDepth = 5; // depth for minimax

	// Getters-Setters

	// set my and opponent's score
	public void setScores(int whiteScore, int blackScore) {
		if(myColor==0) {
			myScore=whiteScore;
			opScore=blackScore;
		}
		else {
			myScore=blackScore;
			opScore=whiteScore;
		}
	}
	public int getMyScore() {
		return myScore;
	}
	public int getOpScore() {
		return opScore;
	}
	public int getAlgo() {
		return algo;
	}
	public String getChecker() {
		return checker;
	}
	public void setChecker(String checker) {
		this.checker = checker;
	}
	public void setAlgo(int algo) {
		this.algo = algo;
	}
	public double getEvaluation() {
		return evaluation;
	}
	public int getRows() {
		return rows;
	}
	public void setRows(int rows) {
		this.rows = rows;
	}
	public int getColumns() {
		return columns;
	}
	public void setColumns(int columns) {
		this.columns = columns;
	}
	public int getRookBlocks() {
		return rookBlocks;
	}
	public void setRookBlocks(int rookBlocks) {
		this.rookBlocks = rookBlocks;
	}
	public int getnTurns() {
		return nTurns;
	}
	public void setnTurns(int nTurns) {
		this.nTurns = nTurns;
	}
	public int getnBranches() {
		return nBranches;
	}
	public void setnBranches(int nBranches) {
		this.nBranches = nBranches;
	}
	public int getNoPrize() {
		return noPrize;
	}
	public void setNoPrize(int noPrize) {
		this.noPrize = noPrize;
	}
	public ArrayList<String> getAvailableMoves() {
		return availableMoves;
	}
	public void setAvailableMoves(ArrayList<String> availableMoves) {
		this.availableMoves = availableMoves;
	}
	public String[][] getBoard() {
		return board;
	}
	public void setBoard(String[][] board) {
		this.board = board;
	}
	public void setEvaluation(double evaluation) {
		this.evaluation = evaluation;
	}
	public int getMyColor() {
		return myColor;
	}
	public void setMyColor(int myColor)
	{
		this.myColor = myColor;
	}
	public void setMyScore(int myScore) {
		this.myScore = myScore;
	}
	public void setOpScore(int opScore) {
		this.opScore = opScore;
	}

	// Constructor #1, called by client, initializes board
	public World()
	{
		board = new String[rows][columns];
		/* represent the board

		BP|BR|BK|BR|BP
		BP|BP|BP|BP|BP
		--|--|--|--|--
		P |P |P |P |P 
		--|--|--|--|--
		WP|WP|WP|WP|WP
		WP|WR|WK|WR|WP
		 */

		// initialization of the board
		for(int i=0; i<rows; i++)
			for(int j=0; j<columns; j++)
				board[i][j] = " ";

		// setting the black player's chess parts

		// black pawns
		for(int j=0; j<columns; j++)
			board[1][j] = "BP";

		board[0][0] = "BP";
		board[0][columns-1] = "BP";

		// black rooks
		board[0][1] = "BR";
		board[0][columns-2] = "BR";

		// black king
		board[0][columns/2] = "BK";

		// setting the white player's chess parts

		// white pawns
		for(int j=0; j<columns; j++)
			board[rows-2][j] = "WP";

		board[rows-1][0] = "WP";
		board[rows-1][columns-1] = "WP";

		// white rooks
		board[rows-1][1] = "WR";
		board[rows-1][columns-2] = "WR";

		// white king
		board[rows-1][columns/2] = "WK";

		// setting the prizes
		for(int j=0; j<columns; j++)
			board[rows/2][j] = "P";

		availableMoves = new ArrayList<String>();
	}

	//Constructor #2, called by algorithm functions, makes world copy
	public World(World world) {
		this.algo = world.algo;
		this.availableMoves = new ArrayList<String>();
		this.board = newBoard(world.board);
		this.columns = world.columns;
		this.evaluation = world.evaluation;
		this.myColor = world.myColor;
		this.nBranches = world.nBranches;
		this.noPrize = world.noPrize;
		this.nTurns = world.nTurns;
		this.rookBlocks = world.rookBlocks;
		this.rows = world.rows;
		this.myScore = world.myScore;
		this.opScore = world.opScore;
	}

	//Constructor #3, called to make World instance to pass as argument in algorithms
	public World(int algo,  String[][] board, ArrayList<String> availableMoves, int rows, int columns, int myColor, int rookBlocks, int nTurns, int nBranches, int noPrize, int myScore, int opScore) {
		if(algo==0) {
			setAlgo(0);
		}
		else if (algo==1){
			setAlgo(1);
		}
		else if(algo==2){
			setAlgo(2);
		}
		setBoard(board);
		setAvailableMoves(availableMoves);
		setColumns(columns);
		setRows(rows);
		setMyColor(myColor);
		setRookBlocks(rookBlocks);
		setnTurns(nTurns);
		setnBranches(nBranches);
		setNoPrize(noPrize);
		setMyScore(myScore);
		setOpScore(opScore);
	}

	// Function to decide next move
	public String selectAction()
	{
		availableMoves = new ArrayList<String>();
		if(myColor == 0)		// I am the white player
			this.whiteMoves();
		else					// I am the black player
			this.blackMoves();
		// keeping track of the branch factor
		nTurns++;
		nBranches += availableMoves.size();
		// Stalemate flag!
		try {
			stalemateFlag();
		} catch (IOException e){
			e.printStackTrace();
		}
		String newMove = "";
		// select algorithm
		// Minimax
		if(algo==0) {
			System.out.println("Running minimax with no extensions");
			if(nTurns<4&&premadeMoves) {
				if(myColor==0) {
					newMove = standardOpeningWhite2(nTurns).getMove();
				}
				else {
					newMove = standardOpeningBlack2(nTurns).getMove();
				}
			}
			else {
				MiniMaxAlgo minimax;
				World world_arg = new World(algo, board, availableMoves, rows, columns, myColor, rookBlocks, nTurns, nBranches, noPrize, myScore, opScore);
				minimax = new MiniMaxAlgo(0, algoDepth, endTime);
				newMove = minimax.runMiniMax(world_arg);
				if (nTurns%2!=0) {
					if(!(newMove.equals(checker))) {
						checker=newMove;}
					else {
						if(opScore>=myScore) {
							minimax = new MiniMaxAlgo(0, 1, endTime);
							newMove = minimax.runMiniMax(world_arg);
						} 
					}
				}
			}
		}
		// 1: Minimax with alpha beta pruning, 
		// 2: Minimax with forward pruning, 
		// 3: Minimax with singular extensions 
		else if(algo==1||algo==2||algo==3){
			if(algo==1) {
				System.out.println("Running minimax with alpha beta pruning");
			}
			else if(algo==2) {
				System.out.println("Running minimax with forward pruning");
			}
			else {
				System.out.println("Running minimax with forward pruning");
			}
			if(nTurns<4&&premadeMoves) {
				if(myColor==0) {
					newMove = standardOpeningWhite2(nTurns).getMove();
				}
				else {
					newMove = standardOpeningBlack2(nTurns).getMove();
				}
			}
			else {
				MiniMaxAlgo minimax;
				World world_arg = new World(algo, board, availableMoves, rows, columns, myColor, rookBlocks, nTurns, nBranches, noPrize, myScore, opScore);
				minimax = new MiniMaxAlgo(0, algoDepth, algo, endTime);
				newMove = minimax.runMiniMax(world_arg);
				if (nTurns%2!=0) {                  
					if(!(newMove.equals(checker))) {
						checker=newMove;}
					else {
						if(opScore>=myScore) {
							minimax = new MiniMaxAlgo(0, 1, algo, endTime);
							newMove = minimax.runMiniMax(world_arg);
						}
					}

				}
			}
		}
		// Monte Carlo Tree Search
		else if(algo==4) {
			System.out.println("Running Monte Carlo Tree Search");
			if(nTurns<4&&premadeMoves) {
				if(myColor==0) {
					newMove = standardOpeningWhite2(nTurns).getMove();
				}
				else {
					newMove = standardOpeningBlack2(nTurns).getMove();
				}
			}
			else {
				MonteCarloTreeSearch mcts = new MonteCarloTreeSearch(endTime, 0);
				World world_arg = new World(algo, board, availableMoves, rows, columns, myColor, rookBlocks, nTurns, nBranches, noPrize, myScore, opScore);
				newMove = mcts.runMonteCarlo(world_arg);
				if (nTurns%2!=0) {                  
					if(!(newMove.equals(checker))) {
						checker=newMove;}
					else {
						if(opScore>=myScore) {
							mcts = new MonteCarloTreeSearch(500, 0);
							newMove = mcts.runMonteCarlo(world_arg);
						}
					}

				}
			}
		}
		//printBoard();  // print the board to check that it's the same
		return newMove;
	}


	// Other useful functions

	// stalemate flag
	public void stalemateFlag() throws IOException {
		if(isStalemate()) {
			//Scanner myObj = new Scanner(System.in);  // stop program, expect user input
			try {
				System.out.println("There is no available move");
				//myObj.nextLine();  // there is no possible action to be taken, so game should stop
			}finally {
				//myObj.close();
			}
		}
	}

	// make copy board
	public String[][] newBoard(String[][] board){
		String[][] newBoard = new String[this.getRows()][this.getColumns()];
		for(int i=0;i<this.getRows(); i++) {
			for(int j=0;j<this.getColumns();j++) {
				newBoard[i][j] = board[i][j];
			}
		}
		return newBoard;
	}

	// print the whole board
	public void printBoard() {
		for (int i=0;i<rows;i++) {
			for(int j=0;j<columns;j++) {
				System.out.print(board[i][j]+" ");
			}
			System.out.println();
		}
	}

	// Premade Starting Moves
	public Move standardOpeningWhite1(int moveNumber){
		if(moveNumber==1) {
			Move nextMove = new Move("5141");
			return nextMove;
		}
		else{
			Move nextMove = new Move("5343");
			return nextMove;
		}
	}
	public Move standardOpeningWhite2(int moveNumber){
		if(moveNumber==1) {
			Move nextMove = new Move("5242");
			return nextMove;
		}
		else if(moveNumber==2) {
			Move nextMove = new Move("5040");
			return nextMove;
		}
		else {
			Move nextMove = new Move("5444");
			return nextMove;
		}
	}

	public Move standardOpeningBlack2(int moveNumber){
		if(moveNumber==1) {
			Move nextMove = new Move("1222");
			return nextMove;
		}
		else if(moveNumber==2) {
			Move nextMove = new Move("1020");
			return nextMove;
		}
		else {
			Move nextMove = new Move("1424");
			return nextMove;
		}
	}



	// check if a king is out of the game
	public int checkKings() {
		boolean check_p = false;
		boolean check_op = false;
		String color;
		String opColor;
		if(myColor==0) {
			color = "W";
			opColor = "B";
		}
		else {
			color = "B";
			opColor = "W";
		}
		for (int i=0;i<rows;i++) {
			for(int j=0;j<columns;j++) {
				if(Character.toString(board[i][j].charAt(0)).equals(color)) {
					if(Character.toString(board[i][j].charAt(1)).equals("K")) {
						check_p=true;
					}
				}
				else if(Character.toString(board[i][j].charAt(0)).equals(opColor)) {
					if(Character.toString(board[i][j].charAt(1)).equals("K")) {
						check_op=true;
					}
				}
			}
		}
		if(check_p&&check_op) {
			return 0;
		}
		else if(check_p&&!check_op) {
			return 1;
		}
		else if(check_op&&!check_p){
			return -1;
		}
		else {
			//Scanner myObj = new Scanner(System.in);  // Create a Scanner object
			try {
				System.out.println("No king is left on board");
				//myObj.nextLine();  // Read user input
				//System.exit(0);
				return 0;
			} finally {
				//myObj.close();
			}
		}
	}

	//check if there is a stalemate
	public boolean isStalemate() {
		if(availableMoves.size()!=0) {
			return false;
		}
		return true;
	}

	// set available moves for each case of my color
	public void whiteMoves()
	{
		String firstLetter = "";
		String secondLetter = "";
		String move = "";

		for(int i=0; i<rows; i++)
		{
			for(int j=0; j<columns; j++)
			{
				firstLetter = Character.toString(board[i][j].charAt(0));

				// if it there is not a white chess part in this position then keep on searching
				if(firstLetter.equals("B") || firstLetter.equals(" ") || firstLetter.equals("P"))
					continue;

				// check the kind of the white chess part
				secondLetter = Character.toString(board[i][j].charAt(1));

				if(secondLetter.equals("P"))	// it is a pawn
				{

					// check if it can move one vertical position ahead
					firstLetter = Character.toString(board[i-1][j].charAt(0));

					if(firstLetter.equals(" ") || firstLetter.equals("P"))
					{
						move = Integer.toString(i) + Integer.toString(j) + 
								Integer.toString(i-1) + Integer.toString(j);

						availableMoves.add(move);
					}

					// check if it can move crosswise to the left
					if(j!=0 && i!=0)
					{
						firstLetter = Character.toString(board[i-1][j-1].charAt(0));						
						if(!(firstLetter.equals("W") || firstLetter.equals(" ") || firstLetter.equals("P"))) {
							move = Integer.toString(i) + Integer.toString(j) + 
									Integer.toString(i-1) + Integer.toString(j-1);

							availableMoves.add(move);
						}											
					}

					// check if it can move crosswise to the right
					if(j!=columns-1 && i!=0)
					{
						firstLetter = Character.toString(board[i-1][j+1].charAt(0));
						if(!(firstLetter.equals("W") || firstLetter.equals(" ") || firstLetter.equals("P"))) {

							move = Integer.toString(i) + Integer.toString(j) + 
									Integer.toString(i-1) + Integer.toString(j+1);							
							availableMoves.add(move);
						}
					}
				}
				else if(secondLetter.equals("R"))	// it is a rook
				{
					// check if it can move upwards
					for(int k=0; k<rookBlocks; k++)
					{
						if((i-(k+1)) < 0)
							break;

						firstLetter = Character.toString(board[i-(k+1)][j].charAt(0));

						if(firstLetter.equals("W"))
							break;

						move = Integer.toString(i) + Integer.toString(j) + 
								Integer.toString(i-(k+1)) + Integer.toString(j);

						availableMoves.add(move);

						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("B") || firstLetter.equals("P"))
							break;
					}

					// check if it can move downwards
					for(int k=0; k<rookBlocks; k++)
					{
						if((i+(k+1)) == rows)
							break;

						firstLetter = Character.toString(board[i+(k+1)][j].charAt(0));

						if(firstLetter.equals("W"))
							break;

						move = Integer.toString(i) + Integer.toString(j) + 
								Integer.toString(i+(k+1)) + Integer.toString(j);

						availableMoves.add(move);

						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("B") || firstLetter.equals("P"))
							break;
					}

					// check if it can move on the left
					for(int k=0; k<rookBlocks; k++)
					{
						if((j-(k+1)) < 0)
							break;

						firstLetter = Character.toString(board[i][j-(k+1)].charAt(0));

						if(firstLetter.equals("W"))
							break;

						move = Integer.toString(i) + Integer.toString(j) + 
								Integer.toString(i) + Integer.toString(j-(k+1));

						availableMoves.add(move);

						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("B") || firstLetter.equals("P"))
							break;
					}

					// check of it can move on the right
					for(int k=0; k<rookBlocks; k++)
					{
						if((j+(k+1)) == columns)
							break;

						firstLetter = Character.toString(board[i][j+(k+1)].charAt(0));

						if(firstLetter.equals("W"))
							break;

						move = Integer.toString(i) + Integer.toString(j) + 
								Integer.toString(i) + Integer.toString(j+(k+1));

						availableMoves.add(move);

						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("B") || firstLetter.equals("P"))
							break;
					}
				}
				else // it is the king
				{
					// check if it can move upwards
					if((i-1) >= 0)
					{
						firstLetter = Character.toString(board[i-1][j].charAt(0));

						if(!firstLetter.equals("W"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
									Integer.toString(i-1) + Integer.toString(j);

							availableMoves.add(move);	
						}
					}

					// check if it can move downwards
					if((i+1) < rows)
					{
						firstLetter = Character.toString(board[i+1][j].charAt(0));

						if(!firstLetter.equals("W"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
									Integer.toString(i+1) + Integer.toString(j);

							availableMoves.add(move);	
						}
					}

					// check if it can move on the left
					if((j-1) >= 0)
					{
						firstLetter = Character.toString(board[i][j-1].charAt(0));

						if(!firstLetter.equals("W"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
									Integer.toString(i) + Integer.toString(j-1);

							availableMoves.add(move);	
						}
					}

					// check if it can move on the right
					if((j+1) < columns)
					{
						firstLetter = Character.toString(board[i][j+1].charAt(0));

						if(!firstLetter.equals("W"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
									Integer.toString(i) + Integer.toString(j+1);

							availableMoves.add(move);	
						}
					}
				}			
			}	
		}
	}

	public void blackMoves()
	{
		String firstLetter = "";
		String secondLetter = "";
		String move = "";

		for(int i=0; i<rows; i++)
		{
			for(int j=0; j<columns; j++)
			{
				firstLetter = Character.toString(board[i][j].charAt(0));

				// if it there is not a black chess part in this position then keep on searching
				if(firstLetter.equals("W") || firstLetter.equals(" ") || firstLetter.equals("P"))
					continue;

				// check the kind of the white chess part
				secondLetter = Character.toString(board[i][j].charAt(1));

				if(secondLetter.equals("P"))	// it is a pawn
				{

					// check if it can move one vertical position ahead
					firstLetter = Character.toString(board[i+1][j].charAt(0));

					if(firstLetter.equals(" ") || firstLetter.equals("P"))
					{
						move = Integer.toString(i) + Integer.toString(j) + 
								Integer.toString(i+1) + Integer.toString(j);

						availableMoves.add(move);
					}

					// check if it can move crosswise to the left
					if(j!=0 && i!=rows-1)
					{
						firstLetter = Character.toString(board[i+1][j-1].charAt(0));

						if(!(firstLetter.equals("B") || firstLetter.equals(" ") || firstLetter.equals("P"))) {
							move = Integer.toString(i) + Integer.toString(j) + 
									Integer.toString(i+1) + Integer.toString(j-1);

							availableMoves.add(move);
						}																	
					}

					// check if it can move crosswise to the right
					if(j!=columns-1 && i!=rows-1)
					{
						firstLetter = Character.toString(board[i+1][j+1].charAt(0));

						if(!(firstLetter.equals("B") || firstLetter.equals(" ") || firstLetter.equals("P"))) {
							move = Integer.toString(i) + Integer.toString(j) + 
									Integer.toString(i+1) + Integer.toString(j+1);

							availableMoves.add(move);
						}



					}
				}
				else if(secondLetter.equals("R"))	// it is a rook
				{
					// check if it can move upwards
					for(int k=0; k<rookBlocks; k++)
					{
						if((i-(k+1)) < 0)
							break;

						firstLetter = Character.toString(board[i-(k+1)][j].charAt(0));

						if(firstLetter.equals("B"))
							break;

						move = Integer.toString(i) + Integer.toString(j) + 
								Integer.toString(i-(k+1)) + Integer.toString(j);

						availableMoves.add(move);

						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("W") || firstLetter.equals("P"))
							break;
					}

					// check if it can move downwards
					for(int k=0; k<rookBlocks; k++)
					{
						if((i+(k+1)) == rows)
							break;

						firstLetter = Character.toString(board[i+(k+1)][j].charAt(0));

						if(firstLetter.equals("B"))
							break;

						move = Integer.toString(i) + Integer.toString(j) + 
								Integer.toString(i+(k+1)) + Integer.toString(j);

						availableMoves.add(move);

						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("W") || firstLetter.equals("P"))
							break;
					}

					// check if it can move on the left
					for(int k=0; k<rookBlocks; k++)
					{
						if((j-(k+1)) < 0)
							break;

						firstLetter = Character.toString(board[i][j-(k+1)].charAt(0));

						if(firstLetter.equals("B"))
							break;

						move = Integer.toString(i) + Integer.toString(j) + 
								Integer.toString(i) + Integer.toString(j-(k+1));

						availableMoves.add(move);

						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("W") || firstLetter.equals("P"))
							break;
					}

					// check of it can move on the right
					for(int k=0; k<rookBlocks; k++)
					{
						if((j+(k+1)) == columns)
							break;

						firstLetter = Character.toString(board[i][j+(k+1)].charAt(0));

						if(firstLetter.equals("B"))
							break;

						move = Integer.toString(i) + Integer.toString(j) + 
								Integer.toString(i) + Integer.toString(j+(k+1));

						availableMoves.add(move);

						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("W") || firstLetter.equals("P"))
							break;
					}
				}
				else // it is the king
				{
					// check if it can move upwards
					if((i-1) >= 0)
					{
						firstLetter = Character.toString(board[i-1][j].charAt(0));

						if(!firstLetter.equals("B"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
									Integer.toString(i-1) + Integer.toString(j);

							availableMoves.add(move);	
						}
					}

					// check if it can move downwards
					if((i+1) < rows)
					{
						firstLetter = Character.toString(board[i+1][j].charAt(0));

						if(!firstLetter.equals("B"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
									Integer.toString(i+1) + Integer.toString(j);

							availableMoves.add(move);	
						}
					}

					// check if it can move on the left
					if((j-1) >= 0)
					{
						firstLetter = Character.toString(board[i][j-1].charAt(0));

						if(!firstLetter.equals("B"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
									Integer.toString(i) + Integer.toString(j-1);

							availableMoves.add(move);	
						}
					}

					// check if it can move on the right
					if((j+1) < columns)
					{
						firstLetter = Character.toString(board[i][j+1].charAt(0));

						if(!firstLetter.equals("B"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
									Integer.toString(i) + Integer.toString(j+1);

							availableMoves.add(move);	
						}
					}
				}			
			}	
		}
	}

	public String selectRandomAction()
	{		
		Random ran = new Random();
		int x = ran.nextInt(availableMoves.size());

		return availableMoves.get(x);
	}

	public double getAvgBFactor()
	{
		return nBranches / (double) nTurns;
	}

	public void makeMove(int x1, int y1, int x2, int y2, int prizeX, int prizeY)
	{
		String chesspart = Character.toString(board[x1][y1].charAt(1));

		boolean pawnLastRow = false;

		// check if it is a move that has made a move to the last line
		if(chesspart.equals("P"))
			if( (x1==rows-2 && x2==rows-1) || (x1==1 && x2==0) )
			{
				board[x2][y2] = " ";	// in a case an opponent's chess part has just been captured
				board[x1][y1] = " ";
				pawnLastRow = true;
			}

		// otherwise
		if(!pawnLastRow)
		{
			board[x2][y2] = board[x1][y1];
			board[x1][y1] = " ";
		}

		// check if a prize has been added in the game
		if(prizeX != noPrize)
			board[prizeX][prizeY] = "P";
	}

}
