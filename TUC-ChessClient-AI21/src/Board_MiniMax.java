import java.util.ArrayList;

public class Board_MiniMax {
	public double evaluation;
	public String[][] board = null;
	public int rows = 7;
	public int columns = 5;
	public int myColor = 0;
	public ArrayList<String> availableMoves = null;
	public int rookBlocks = 3;		// rook can move towards <rookBlocks> blocks in any vertical or horizontal direction
	public int nTurns = 0;
	public int nBranches = 0;
	public int noPrize = 9;
	
	//Make a new world, copy of original
	public Board_MiniMax(String[][] board, ArrayList<String> availableMoves, int rows, int columns, int myColor, int rookBlocks, int nTurns, int nBranches, int noPrize, double evaluation) {
		this.board = board;
		this.columns = columns;
		this.evaluation = evaluation;
		this.myColor = myColor;
		this.nBranches = nBranches;
		this.noPrize = noPrize;
		this.nTurns = nTurns;
		this.rookBlocks = rookBlocks;
		this.rows = rows;
		this.availableMoves = new ArrayList<String>();
		if(myColor == 0)		// I am the white player
			this.whiteMoves();
		else					// I am the black player
			this.blackMoves();
	}
	
	
	public void makeMiniMaxMove(String[][] board, int x1, int y1, int x2, int y2)
	{
		String chesspart = Character.toString(board[x1][y1].charAt(1));
		
		boolean pawnLastRow = false;
		System.out.println("MakeMove: "+x1+y1+x2+y2);
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
	}
	
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
}
