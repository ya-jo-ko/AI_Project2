import java.util.*;

public class MonteCarloTreeSearch {
	static final int WIN_SCORE = 10;
	int level;
	private double end;

	//Constructor
	public MonteCarloTreeSearch(double end, int level){
		this.end = System.currentTimeMillis()+end;
		this.level = level;
	}
	
	// Functions
	public String runMonteCarlo(World world) {
		State state = new State(world);
		MC_Node current = new MC_Node();
		current.setState(state);
		current.setChildArray(new LinkedList<MC_Node>());
		initialExpand(current);
		current.setParent(null);
		Move nextMove;
		nextMove = findNextMove(current);
		world.setEvaluation(nextMove.getEvaluation());
		return nextMove.getMove();
	}

	public Move findNextMove(MC_Node rootNode) {
		while (System.currentTimeMillis() < end) {
			MC_Node promisingNode = selectPromisingNode(rootNode);
			System.out.println(promisingNode.isTerminal());
			System.out.println("Test for expand");
			rootNode.getState().getBoard().printBoard();
			expandNode(promisingNode); //calculate available moves for node to expand
			MC_Node nodeToExplore = new MC_Node();
			if (!promisingNode.isTerminal()) {
				nodeToExplore = selectRandomChild(promisingNode);
			}
			else {
				nodeToExplore = promisingNode;
			}


			int playoutResult = simulateRandomPlayout(nodeToExplore);
			backPropogation(nodeToExplore, playoutResult);
		}

		MC_Node winnerNode = rootNode.getChildWithMaxScore();
		Move move = winnerNode.getState().getMove();
		
		return move;
	}

	
	private void backPropogation(MC_Node nodeToExplore, int playerNo) {
		MC_Node tempNode =  nodeToExplore;
	    while (tempNode != null) {
	        tempNode.getState().setVisitCount(tempNode.getState().getVisitCount()+1);
	        if (playerNo==1) {
	            tempNode.getState().setWinScore(tempNode.getState().getWinScore()+WIN_SCORE);
	        }
	        tempNode = tempNode.getParent();
	    }
	}
	
	
	

	private int simulateRandomPlayout(MC_Node node) {
		MC_Node tempNode = node;
	    //State tempState = tempNode.getState();
	    int boardStatus = 0; // 0 no one won, 1 I won , -1 I lost
	    if (tempNode.isTerminal()) {
	    	if(tempNode.getWhoWon()==-1) {
		        tempNode.getParent().getState().setWinScore(Integer.MIN_VALUE);
		        //boardStatus=-1;
	    	}
	    	else if(tempNode.getWhoWon()==1){
		        //tempNode.getParent().getState().setWinScore(Integer.MAX_VALUE);
		        boardStatus=1;
	    	}
	        return boardStatus;
	    }
	    
	    
	    while (!tempNode.isTerminal()) {
	    	expandNode(tempNode);
	    	tempNode = selectRandomChild(tempNode);
	    	if (tempNode.isTerminal()) {
		    	if(tempNode.getWhoWon()==-1) {
			        tempNode.getParent().getState().setWinScore(Integer.MIN_VALUE);
			        //boardStatus=-1;
		    	}
		    	else if(tempNode.getWhoWon()==1){
			        //tempNode.getParent().getState().setWinScore(Integer.MAX_VALUE);
			        boardStatus=1;
		    	}
		    }
	    }
	    return boardStatus;
	}


	private void expandNode(MC_Node current) {
		if(current.getState().getBoard().getMyColor()==0) {
			current.getState().getBoard().whiteMoves();
		}
		else {
			current.getState().getBoard().blackMoves();
		}
		List<State> nextStates = current.getState().getAllPossibleStates();
//		if(nextStates.size()==0) {
//			Scanner myObj = new Scanner(System.in);  // Create a Scanner object
//		    System.out.println("there is no next state");
//		    myObj.nextLine();  // Read user input			
//		}
		for(int i=0;i<nextStates.size();i++) {
			MC_Node child = new MC_Node();
			child.setParent(current);
			child.setState(nextStates.get(i));
			current.childArray.add(child);
		}
	}
	
	private void initialExpand(MC_Node current) {
		List<State> nextStates = current.getState().getAllPossibleStates();
		for(int i=0;i<nextStates.size();i++) {
			MC_Node child = new MC_Node();
			child.setParent(current);
			child.setState(nextStates.get(i));
			current.childArray.add(child);
		}
	}
	

	private MC_Node selectRandomChild(MC_Node parent) {
		Random ran = new Random();
		//System.out.println(parent.getChildArray().size());
		int x = ran.nextInt(parent.getChildArray().size());
		return parent.getChildArray().get(x);
	}
	

	private MC_Node selectPromisingNode(MC_Node rootNode) {
		MC_Node node = rootNode;
		while (node.getChildArray().size() != 0) {
			node = UCT.findBestNodeWithUCT(node);
		}
		return node;
	}

}