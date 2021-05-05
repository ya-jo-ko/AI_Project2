import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MiniMaxAlgo {
	private int pruning; // 0 is minimax, 1 is a-b, 2 is forward, 3 is singular extensions
	private double evaluation;
	private int depth; // depth+1 number of depths, 0, 1, 2 etc.
	private String move;
	private double bonus=0;
	private double pruneDepth = 0.6;
	private int singularEx = 2;// select 2 moves to expand further
	private int singExStartDepth = 4; //for first 4 iterations run mini max
	private double endTime;
	public World initialWorld;

	// Constructor 1, pruning not specified
	public MiniMaxAlgo(double evaluation, int depth, double endTime) {
		this.evaluation = evaluation;
		this.depth = depth;
		this.move = null;
		this.pruning = 0;
		this.endTime = System.currentTimeMillis() + endTime;
	}
	// Constructor 2, if a-b pruning is specified
	public MiniMaxAlgo(double evaluation, int depth, int pruning, double endTime) {
		this.evaluation = evaluation;
		this.depth = depth;
		this.move = null;
		this.pruning = pruning;
		this.endTime = System.currentTimeMillis() + endTime;
	}

	// Get-Set
	public int isPruning() {
		return pruning;
	}
	public void setPruning(int pruning) {
		this.pruning = pruning;
	}
	public double getEvaluation() {
		return evaluation;
	}
	public void setEvaluation(double evaluation) {
		this.evaluation = evaluation;
	}
	public int getDepth() {
		return depth;
	}
	public void setDepth(int depth) {
		this.depth = depth;
	}
	public String getMove() {
		return move;
	}
	public void setMove(String move) {
		this.move = move;
	}

	// Functions
	public String runMiniMax(World world) {
		this.initialWorld = world;
		SearchNode current = new SearchNode(world, null);
		Move nextMove;
		//for a-b pruning
		if(this.pruning==1) {
			nextMove = alpha_beta_search(current, depth, false, false);
		}
		else if(this.pruning==2){
			nextMove = forwardPruning(current, depth, true, false, false);
		}
		else if(this.pruning==3){
			nextMove = singularExtensions(current, depth, true, false, false);
		}
		else {
			sortMovesList(current, current.getWorld().getAvailableMoves());
			nextMove = minimaxAlgo(current, true, this.depth,false,false);
		}
		setEvaluation(nextMove.getEvaluation());
		world.setEvaluation(nextMove.getEvaluation());
		return nextMove.getMove();
	}


	// plain minimax algorithm
	public Move minimaxAlgo(SearchNode currentNode, boolean maxPlayer, int depth, boolean prizeWorth, boolean prizeWorthOp) {
		if(timeOut(this.endTime)) {
			depth = 0;
		}
		Move result = new Move(null,0);
		ArrayList<String> availableMoves = currentNode.getWorld().getAvailableMoves();
		boolean terminal = currentNode.isTerminal();
		// Evaluate Node
		if ((depth == 0)||terminal) {
			if(!maxPlayer) {
				currentNode.getWorld().setMyColor(currentNode.getWorld().getMyColor() ^ (int) 1);
			}
			if(terminal) {
				terminalAction(currentNode, result);
			}
			else {
				double evaluation = evaluatePosition(currentNode, prizeWorth, prizeWorthOp);
				result.setEvaluation(evaluation);
				System.out.println("This is the new evaluation: "+evaluation);
			}
			bonus = 0;
			return result;
		}
		// Recursion with a possible move
		// Maximize
		if (maxPlayer) {
			double maxEva = -100000;
			for (int i=0;i<availableMoves.size(); i++) {
				String move = availableMoves.get(i);
				int x1 = Integer.parseInt(Character.toString(move.charAt(0)));
				int y1 = Integer.parseInt(Character.toString(move.charAt(1)));
				int x2 = Integer.parseInt(Character.toString(move.charAt(2)));
				int y2 = Integer.parseInt(Character.toString(move.charAt(3)));
				if(pawnCanAscend(currentNode.getWorld(),x1, y1, x2, y2)) {
					bonus++;
				}
				World world2 = new World(currentNode.getWorld());
				if(canTakeKing(world2, x2, y2)) {
					result.setMove(move);
					return result;
				}
				String[][] CurrentB=world2.getBoard();
				if (CurrentB[x2][y2].equals("P")) {
					prizeWorth=true;
				}
				else {
					prizeWorth=false;
				}
				world2.makeMove(x1, y1, x2, y2, world2.getNoPrize(), 0);
				world2.setMyColor(currentNode.getWorld().getMyColor() ^ (int) 1);
				if(world2.getMyColor() == 0)		// I am the white player
					world2.whiteMoves();
				else					// I am the black player
					world2.blackMoves();
				SearchNode child = new SearchNode(world2, currentNode);
				child.getState().setMove(new Move(move,0));
				double eva = minimaxAlgo(child, false, depth-1,prizeWorth,prizeWorthOp).getEvaluation();
				if(maxEva<eva) {
					maxEva = eva;
					if(depth==this.depth) {
						result.setMove(move);
						System.out.println("Max player: "+result.getMove()+" with eval: "+eva);
					}
				}
			}
			result.setEvaluation(maxEva);
			return result;
		}
		// Minimize
		else {
			double minEva = 100000;
			for (int i=0;i<availableMoves.size(); i++) {
				String move = availableMoves.get(i);
				System.out.println("Min player: "+move);
				int x1 = Integer.parseInt(Character.toString(move.charAt(0)));
				int y1 = Integer.parseInt(Character.toString(move.charAt(1)));
				int x2 = Integer.parseInt(Character.toString(move.charAt(2)));
				int y2 = Integer.parseInt(Character.toString(move.charAt(3)));
				if(pawnCanAscend(currentNode.getWorld(),x1, y1, x2, y2)) {
					bonus--;
				}
				World world2 = new World(currentNode.getWorld());
				if(canTakeKing(world2, x2, y2)) {
					result.setMove(move);
					return result;
				}
				String[][] CurrentB=world2.getBoard();
				if (CurrentB[x2][y2].equals("P")) {
					prizeWorthOp=true;
				}
				else {
					prizeWorthOp=false;
				}
				world2.makeMove(x1, y1, x2, y2, world2.getNoPrize(), 0);
				world2.setMyColor(currentNode.getWorld().getMyColor() ^ (int) 1);
				if(world2.getMyColor() == 0)		// I am the white player
					world2.whiteMoves();
				else					// I am the black player
					world2.blackMoves();
				SearchNode child = new SearchNode(world2, currentNode);
				double eva = minimaxAlgo(child, true, depth-1,prizeWorth,prizeWorthOp).getEvaluation(); //not sure what prizeWorth we should pass here. checkare to. isws thelei allagh mesa sto minimizer gt alliws tha nai idio me tou maximizer
				minEva = Math.min(minEva, eva);
			}
			result.setEvaluation(minEva);
			return result;
		}
	}



	public Move alpha_beta_search(SearchNode current, int depth,boolean prizeWorth, boolean prizeWorthOp) {
		double minus_Inf= -100000;
		double inf = 100000;
		Move move = a_b_pruning_Max(current, depth, minus_Inf, inf,prizeWorth,prizeWorthOp);
		return move;
	}

	public Move a_b_pruning_Max(SearchNode currentNode, int depth, double alpha, double beta,boolean prizeWorth, boolean prizeWorthOp) {
		if(timeOut(this.endTime)) {
			depth = 0;
		}
		Move result = new Move(null,0);
		ArrayList<String> availableMoves = currentNode.getWorld().getAvailableMoves();
		boolean terminal = currentNode.isTerminal();
		// Evaluate Node
		if ((depth == 0)||terminal) {
			if(terminal) {
				terminalAction(currentNode, result);
			}
			else {
				double evaluation = evaluatePosition(currentNode, prizeWorth, prizeWorthOp);
				result.setEvaluation(evaluation);
				System.out.println("This is the new evaluation: "+evaluation);
			}
			bonus = 0;
			return result;
		}
		sortMovesList(currentNode, availableMoves);
		//availableMoves = sortMoves(currentNode.getWorld());
		//prune based on b, re-evaluate a
		double maxEva = -100000;
		for (int i=0;i<availableMoves.size(); i++) {
			//get move
			String move = availableMoves.get(i);
			int x1 = Integer.parseInt(Character.toString(move.charAt(0)));
			int y1 = Integer.parseInt(Character.toString(move.charAt(1)));
			int x2 = Integer.parseInt(Character.toString(move.charAt(2)));
			int y2 = Integer.parseInt(Character.toString(move.charAt(3)));
			//create new instance of board
			World world2 = new World(currentNode.getWorld());
			// check if can take king
			if(canTakeKing(world2, x2, y2)) {
				result.setMove(move);
				return result;
			}
			//for present evaluation
			String[][] CurrentB = world2.getBoard();
			if (CurrentB[x2][y2].equals("P")) {
				prizeWorth=true;
			}
			//make move in new world
			world2.makeMove(x1, y1, x2, y2, world2.getNoPrize(), 0);
			//change color for next iteration
			world2.setMyColor(currentNode.getWorld().getMyColor() ^ (int) 1);
			//re-evaluate available moves
			if(world2.getMyColor() == 0)		// I am the white player
				world2.whiteMoves();
			else					// I am the black player
				world2.blackMoves();
			//create child node
			SearchNode child = new SearchNode(world2, currentNode);
			double eva = a_b_pruning_Min(child, depth-1, alpha, beta, prizeWorth, prizeWorthOp).getEvaluation();
			if(maxEva<eva) {
				maxEva = eva;
				System.out.println("ALLAKSA KAINOURIO MAX (ab)");
				if(depth==this.depth) {
					result.setMove(move);
					System.out.println("Max player: "+result.getMove()+" with eval: "+eva);
				}
			}
			if(beta<=maxEva) {
				result.setEvaluation(maxEva);
				return result;
			}
			alpha = Math.max(alpha, maxEva);
		}
		result.setEvaluation(maxEva);
		return result;
	}	


	public Move a_b_pruning_Min(SearchNode currentNode, int depth, double alpha, double beta, boolean prizeWorth, boolean prizeWorthOp) {
		if(timeOut(this.endTime)) {
			depth = 0;
		}
		Move result = new Move(null,0);
		ArrayList<String> availableMoves = currentNode.getWorld().getAvailableMoves();
		boolean terminal = currentNode.isTerminal();
		// Evaluate Node
		if ((depth == 0)||terminal) {
			if(terminal) {
				terminalAction(currentNode, result);
			}
			else {
				double evaluation = evaluatePosition(currentNode, prizeWorth, prizeWorthOp);
				result.setEvaluation(evaluation);
				System.out.println("This is the new evaluation: "+evaluation);
			}
			bonus = 0;
			return result;
		}

		sortMovesList(currentNode, availableMoves);
		//availableMoves = sortMoves(currentNode.getWorld());
		//prune based on b, re-evaluate a
		double minEva = 100000;
		for (int i=0;i<availableMoves.size(); i++) {
			String move = availableMoves.get(i);
			System.out.println("a-b, Min player: "+move);
			int x1 = Integer.parseInt(Character.toString(move.charAt(0)));
			int y1 = Integer.parseInt(Character.toString(move.charAt(1)));
			int x2 = Integer.parseInt(Character.toString(move.charAt(2)));
			int y2 = Integer.parseInt(Character.toString(move.charAt(3)));
			World world2 = new World(currentNode.getWorld());
			// check if can take king
			if(canTakeKing(world2, x2, y2)) {
				result.setMove(move);
				return result;
			}
			// for present evaluation
			String[][] CurrentB = world2.getBoard();
			if (CurrentB[x2][y2].equals("P")) {
				prizeWorthOp=true;
			}
			world2.makeMove(x1, y1, x2, y2, world2.getNoPrize(), 0);
			world2.setMyColor(currentNode.getWorld().getMyColor() ^ (int) 1);
			if(world2.getMyColor() == 0)		// I am the white player
				world2.whiteMoves();
			else					// I am the black player
				world2.blackMoves();
			SearchNode child = new SearchNode(world2, currentNode);
			double eva = a_b_pruning_Max(child, depth-1, alpha, beta, prizeWorth, prizeWorthOp).getEvaluation();
			minEva = Math.min(minEva, eva);
			if(alpha>=minEva) {
				result.setEvaluation(minEva);
				return result;
			}
			beta = Math.min(beta, minEva);
		}
		result.setEvaluation(minEva);
		return result;
	}




	// Forward Pruning, code same with minimax. Difference is some moves with low eval are pruned
	public Move forwardPruning(SearchNode currentNode, int depth, boolean maxPlayer, boolean prizeWorth, boolean prizeWorthOp) {
		if(timeOut(this.endTime)) {
			depth = 0;
		}
		Move result = new Move(null,0);
		ArrayList<String> availableMoves = currentNode.getWorld().getAvailableMoves();
		boolean terminal = currentNode.isTerminal();
		// Evaluate Node, like minimax
		if ((depth == 0)||terminal) {
			if(!maxPlayer) {
				currentNode.getWorld().setMyColor(currentNode.getWorld().getMyColor() ^ (int) 1);
			}
			if(terminal) {
				terminalAction(currentNode, result);
			}
			else {
				double evaluation = evaluatePosition(currentNode, prizeWorth, prizeWorthOp);
				result.setEvaluation(evaluation);
				System.out.println("This is the new evaluation: "+evaluation);
			}
			bonus = 0;
			return result;
		}
		// sort based on each evaluation, don't start close to root
		if(depth<this.depth-2) {
			expandNode(currentNode);
			Collections.sort(currentNode.getChildArray());
			//Collections.sort(currentNode.getChildArray(), Collections.reverseOrder());
			// cut some moves
			pruneMoves(currentNode, availableMoves);
		}
		// Recursion with a possible move
		// Maximize
		if (maxPlayer) {
			double maxEva = -100000;
			for (int i=0;i<availableMoves.size(); i++) {
				String move = availableMoves.get(i);
				int x1 = Integer.parseInt(Character.toString(move.charAt(0)));
				int y1 = Integer.parseInt(Character.toString(move.charAt(1)));
				int x2 = Integer.parseInt(Character.toString(move.charAt(2)));
				int y2 = Integer.parseInt(Character.toString(move.charAt(3)));
				if(pawnCanAscend(currentNode.getWorld(),x1, y1, x2, y2)) {
					bonus++;
				}
				World world2 = new World(currentNode.getWorld());
				if(canTakeKing(world2, x2, y2)) {
					result.setMove(move);
					return result;
				}
				String[][] CurrentB=world2.getBoard();
				if (CurrentB[x2][y2].equals("P")) {
					prizeWorth=true;
				}
				else {
					prizeWorth=false;
				}
				world2.makeMove(x1, y1, x2, y2, world2.getNoPrize(), 0);
				world2.setMyColor(currentNode.getWorld().getMyColor() ^ (int) 1);
				if(world2.getMyColor() == 0)		// I am the white player
					world2.whiteMoves();
				else					// I am the black player
					world2.blackMoves();
				SearchNode child = new SearchNode(world2, currentNode);
				child.getState().setMove(new Move(move,0));
				double eva = minimaxAlgo(child, false, depth-1,prizeWorth,prizeWorthOp).getEvaluation();
				if(maxEva<eva) {
					maxEva = eva;
					if(depth==this.depth) {
						result.setMove(move);
						System.out.println("Max player: "+result.getMove()+" with eval: "+eva);
					}
				}
			}
			result.setEvaluation(maxEva);
			return result;
		}
		// Minimize
		else {
			double minEva = 100000;
			for (int i=0;i<availableMoves.size(); i++) {
				String move = availableMoves.get(i);
				System.out.println("Min player: "+move);
				int x1 = Integer.parseInt(Character.toString(move.charAt(0)));
				int y1 = Integer.parseInt(Character.toString(move.charAt(1)));
				int x2 = Integer.parseInt(Character.toString(move.charAt(2)));
				int y2 = Integer.parseInt(Character.toString(move.charAt(3)));
				if(pawnCanAscend(currentNode.getWorld(),x1, y1, x2, y2)) {
					bonus--;
				}
				World world2 = new World(currentNode.getWorld());
				if(canTakeKing(world2, x2, y2)) {
					result.setMove(move);
					return result;
				}
				String[][] CurrentB=world2.getBoard();
				if (CurrentB[x2][y2].equals("P")) {
					prizeWorthOp=true;
				}
				else {
					prizeWorthOp=false;
				}
				world2.makeMove(x1, y1, x2, y2, world2.getNoPrize(), 0);
				world2.setMyColor(currentNode.getWorld().getMyColor() ^ (int) 1);
				if(world2.getMyColor() == 0)		// I am the white player
					world2.whiteMoves();
				else					// I am the black player
					world2.blackMoves();
				SearchNode child = new SearchNode(world2, currentNode);
				double eva = minimaxAlgo(child, true, depth-1,prizeWorth,prizeWorthOp).getEvaluation(); //not sure what prizeWorth we should pass here. checkare to. isws thelei allagh mesa sto minimizer gt alliws tha nai idio me tou maximizer
				minEva = Math.min(minEva, eva);
			}
			result.setEvaluation(minEva);
			return result;
		}
	}


	public Move singularExtensions(SearchNode currentNode, int depth, boolean maxPlayer, boolean prizeWorth, boolean prizeWorthOp) {
		if(timeOut(this.endTime)) {
			depth = 0;
		}
		Move result = new Move(null,0);
		ArrayList<String> availableMoves = currentNode.getWorld().getAvailableMoves();
		boolean terminal = currentNode.isTerminal();
		// Evaluate Node, like minimax
		if ((depth == 0)||terminal) {
			if(!maxPlayer) {
				currentNode.getWorld().setMyColor(currentNode.getWorld().getMyColor() ^ (int) 1);
			}
			if(terminal) {
				terminalAction(currentNode, result);
			}
			else {
				double evaluation = evaluatePosition(currentNode, prizeWorth, prizeWorthOp);
				result.setEvaluation(evaluation);
				System.out.println("This is the new evaluation: "+evaluation);
			}
			bonus = 0;
			return result;
		}
		// minimax for depth 4, then singular extension on the 2 best moves
		if(depth<this.depth-singExStartDepth) {
			expandNode(currentNode);
			Collections.sort(currentNode.getChildArray());
			//Collections.sort(currentNode.getChildArray(), Collections.reverseOrder());
			// cut all moves but 2
			singularExtend(currentNode, availableMoves);
		}
		// Recursion with a possible move
		// Maximize
		if (maxPlayer) {
			double maxEva = -100000;
			for (int i=0;i<availableMoves.size(); i++) {
				String move = availableMoves.get(i);
				int x1 = Integer.parseInt(Character.toString(move.charAt(0)));
				int y1 = Integer.parseInt(Character.toString(move.charAt(1)));
				int x2 = Integer.parseInt(Character.toString(move.charAt(2)));
				int y2 = Integer.parseInt(Character.toString(move.charAt(3)));
				if(pawnCanAscend(currentNode.getWorld(),x1, y1, x2, y2)) {
					bonus++;
				}
				World world2 = new World(currentNode.getWorld());
				if(canTakeKing(world2, x2, y2)) {
					result.setMove(move);
					return result;
				}
				String[][] CurrentB=world2.getBoard();
				if (CurrentB[x2][y2].equals("P")) {
					prizeWorth=true;
				}
				else {
					prizeWorth=false;
				}
				world2.makeMove(x1, y1, x2, y2, world2.getNoPrize(), 0);
				world2.setMyColor(currentNode.getWorld().getMyColor() ^ (int) 1);
				if(world2.getMyColor() == 0)		// I am the white player
					world2.whiteMoves();
				else					// I am the black player
					world2.blackMoves();
				SearchNode child = new SearchNode(world2, currentNode);
				child.getState().setMove(new Move(move,0));
				double eva = minimaxAlgo(child, false, depth-1,prizeWorth,prizeWorthOp).getEvaluation();
				if(maxEva<eva) {
					maxEva = eva;
					if(depth==this.depth) {
						result.setMove(move);
						System.out.println("Max player: "+result.getMove()+" with eval: "+eva);
					}
				}
			}
			result.setEvaluation(maxEva);
			return result;
		}
		// Minimize
		else {
			double minEva = 100000;
			for (int i=0;i<availableMoves.size(); i++) {
				String move = availableMoves.get(i);
				System.out.println("Min player: "+move);
				int x1 = Integer.parseInt(Character.toString(move.charAt(0)));
				int y1 = Integer.parseInt(Character.toString(move.charAt(1)));
				int x2 = Integer.parseInt(Character.toString(move.charAt(2)));
				int y2 = Integer.parseInt(Character.toString(move.charAt(3)));
				if(pawnCanAscend(currentNode.getWorld(),x1, y1, x2, y2)) {
					bonus--;
				}
				World world2 = new World(currentNode.getWorld());
				if(canTakeKing(world2, x2, y2)) {
					result.setMove(move);
					return result;
				}
				String[][] CurrentB=world2.getBoard();
				if (CurrentB[x2][y2].equals("P")) {
					prizeWorthOp=true;
				}
				else {
					prizeWorthOp=false;
				}
				world2.makeMove(x1, y1, x2, y2, world2.getNoPrize(), 0);
				world2.setMyColor(currentNode.getWorld().getMyColor() ^ (int) 1);
				if(world2.getMyColor() == 0)		// I am the white player
					world2.whiteMoves();
				else					// I am the black player
					world2.blackMoves();
				SearchNode child = new SearchNode(world2, currentNode);
				double eva = minimaxAlgo(child, true, depth-1,prizeWorth,prizeWorthOp).getEvaluation(); //not sure what prizeWorth we should pass here. checkare to. isws thelei allagh mesa sto minimizer gt alliws tha nai idio me tou maximizer
				minEva = Math.min(minEva, eva);
			}
			result.setEvaluation(minEva);
			return result;
		}
	}

	public void pruneMoves(SearchNode current, ArrayList<String> availableMoves ) {
		int sizeL = current.getChildArray().size();
		ArrayList<String> availableMoves2 = new ArrayList<String>();
		long limit = Math.round(sizeL*pruneDepth);
		int cnt=0;
		for(int i=0;i<sizeL-(int) limit;i++) {
			if(current.getChildArray().size()-(int) limit>0) {
				current.getChildArray().remove((int) limit);
			}
			if(!(cnt<current.getChildArray().size())) {
				continue;
			}
			for(int j=0;j<availableMoves.size();j++) {
				if(availableMoves.get(j).equals(current.getChildArray().get(cnt).getState().getMove().getMove())) {
					String temp = availableMoves.get(j);
					availableMoves2.add(i, temp);
					cnt++;
				}
			}
		}
		availableMoves.clear();
		for(int h=0;h<availableMoves2.size();h++) {
			availableMoves.add(availableMoves2.get(h));
		}
	}

	public void singularExtend(SearchNode current, ArrayList<String> availableMoves ) {
		int sizeL = current.getChildArray().size();
		ArrayList<String> availableMoves2 = new ArrayList<String>();
		int limit = singularEx;
		int cnt=0;
		for(int i=0;i<sizeL-(int) limit;i++) {
			if(current.getChildArray().size()-(int) limit>0) {
				current.getChildArray().remove((int) limit);
			}
			if(!(cnt<current.getChildArray().size())) {
				continue;
			}
			for(int j=0;j<availableMoves.size();j++) {
				if(availableMoves.get(j).equals(current.getChildArray().get(cnt).getState().getMove().getMove())) {
					String temp = availableMoves.get(j);
					availableMoves2.add(i, temp);
					cnt++;
				}
			}
		}
		availableMoves.clear();
		for(int h=0;h<availableMoves2.size();h++) {
			availableMoves.add(availableMoves2.get(h));
		}
	}

	private void expandNode(SearchNode current) {
		if(current.getState().getBoard().getMyColor()==0) {
			current.getState().getBoard().whiteMoves();
		}
		else {
			current.getState().getBoard().blackMoves();
		}
		List<State> nextStates = current.getState().getAllPossibleStates();
		for(int i=0;i<nextStates.size();i++) {
			SearchNode child = new SearchNode(nextStates.get(i), current);
			current.childArray.add(child);
			Evaluation e = new Evaluation(nextStates.get(i));
			child.setValue(e.pieceEvaluation());
		}
	}

	// Evaluation Function
	public double evaluatePosition(SearchNode currentNode, boolean prizeWorth, boolean prizeWorthOp) {
		State position = new State(currentNode.getWorld());
		Evaluation evaluation = new Evaluation(position);

		// check if gift is at stake
		if(prizeWorth) {
			return evaluation.pieceEvaluation()+90+100*bonus;
		}
		else if(prizeWorthOp) {
			return evaluation.pieceEvaluation()-90+100*bonus;			
		}
		else {
			return evaluation.pieceEvaluation()+100*bonus;
		}
	}

	public void sortMovesList(SearchNode currentNode, ArrayList<String> availableMoves) {
		expandNode(currentNode);
		Collections.sort(currentNode.getChildArray());//sort in descending order
		//Collections.sort(currentNode.getChildArray(), Collections.reverseOrder());
		// cut all moves but 2
		for(int i=0;i<currentNode.getChildArray().size();i++) {
			for(int j=i;j<availableMoves.size();j++) {
				if(currentNode.getChildArray().get(i).getState().getMove().getMove().equals(availableMoves.get(j))) {
					String temp = availableMoves.get(j);
					availableMoves.remove(j);
					availableMoves.add(i, temp);
				}
			}	
		}
	}
	// Stuff for evaluation
	// action when a terminal state is reached
	public void terminalAction(SearchNode currentNode, Move result) {
		int whoWon = currentNode.getWhoWon();
		if(whoWon==1) {
			result.setEvaluation(Integer.MAX_VALUE);
		}
		else if(whoWon==-1) {
			result.setEvaluation(Integer.MIN_VALUE);
		}
		else {
			if(currentNode.getWorld().getMyScore()>currentNode.getWorld().getOpScore()) {
				result.setEvaluation(Integer.MAX_VALUE);
			}
			else if(currentNode.getWorld().getMyScore()<currentNode.getWorld().getOpScore()) {
				result.setEvaluation(Integer.MIN_VALUE);
			}
			else {
				result.setEvaluation(0);//0 evaluation for draw
			}
		}
	}

	// check if you can take opponent's king
	public boolean canTakeKing(World world, int x2, int y2) {
		String[][] board = world.getBoard();
		if(world.getMyColor()==0) {
			if(board[x2][y2].equals("BK")) {
				return true;
			}
		}
		else {
			if(board[x2][y2].equals("WK")) {
				return true;
			}
		}
		return false;
	}

	// check if your pawn can reach the other side of board
	public boolean pawnCanAscend(World world,int i1, int j1, int i2, int j2) {
		int rows = world.getRows();
		String[][] board = world.getBoard();
		String piece = board[i1][j1];
		if(world.getMyColor()==0) {
			if(!piece.equals("WP")) {
				return false;
			}
			if(i2==0&&i1==1) {
				return true;
			}
		}
		else {
			if(!piece.equals("BP")) {
				return false;
			}
			if(i2==rows-1&&i1==rows-2) {
				return true;
			}
		}
		return false;
	}

	// sort available moves based on points gaining
	public ArrayList<String> sortMoves(World world){
		ArrayList<String> availableMoves = world.getAvailableMoves();
		for (int i=0;i<availableMoves.size(); i++) {
			String move = availableMoves.get(i);
			String[][] CurrentA=world.getBoard();
			int x1 = Integer.parseInt(Character.toString(move.charAt(0)));
			int y1 = Integer.parseInt(Character.toString(move.charAt(1)));
			int x3 = Integer.parseInt(Character.toString(move.charAt(2)));
			int y3 = Integer.parseInt(Character.toString(move.charAt(3)));
			if(world.getMyColor()==0) {
				if ((CurrentA[x1][y1].equals("WP") && CurrentA[x3][y3].equals("BR")) || CurrentA[x3][y3].equals("BK")) {
					availableMoves.remove(i);
					availableMoves.add(0, move);
				}
			}
			else {
				if ((CurrentA[x1][y1].equals("BP") && CurrentA[x3][y3].equals("WR")) || CurrentA[x3][y3].equals("WK")) {
					availableMoves.remove(i);
					availableMoves.add(0, move);
				}
			}
		}
		return availableMoves;
	}

	// action to take when time will run out, not used
	public String timeOutAction() {
		this.depth = 1;
		return runMiniMax(this.initialWorld);
	}

	// check for time
	public boolean timeOut(double endTime) {
		if(System.currentTimeMillis() < endTime) {
			return false;
		}
		return true;
	}
}
