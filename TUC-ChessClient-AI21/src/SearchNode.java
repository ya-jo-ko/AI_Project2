import java.util.LinkedList;
import java.util.List;

public class SearchNode implements Comparable<SearchNode>{
	private double value;
	private State state;
	private SearchNode parent;
	public List<SearchNode> childArray = new LinkedList<SearchNode>();
	private int whoWon = 0; //0 is draw, 1 is me, -1 is opponent

	public SearchNode(State state, SearchNode parent) {
		this.state = state;
		this.parent = parent;
	}

	public SearchNode(World world, SearchNode parent) {
		this.state = new State(world);
		this.parent = parent;
	}

	public SearchNode(SearchNode node) {
		this.state = node.state;
		this.parent = node.parent;
		this.childArray = node.childArray;
	}

	
	
	public int getWhoWon() {
		return whoWon;
	}

	public void setWhoWon(int whoWon) {
		this.whoWon = whoWon;
	}

	public void setParent(SearchNode parent) {
		this.parent = parent;
	}

	public World getWorld() {
		return state.getBoard();
	}


	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public List<SearchNode> getChildArray() {
		return childArray;
	}

	public void setChildArray(List<SearchNode> childArray) {
		this.childArray = childArray;
	}

	//other functions
	public boolean isTerminal() {
		boolean result = false;
		int kings = this.state.getBoard().checkKings();
		if(kings==1) {
			setWhoWon(1);
			result = true;
			//System.out.println("I WON?");
		}
		else if(kings==-1) {
			setWhoWon(-1);
			result = true;
			//System.out.println("I LOST?");
		}
		else {
			if(state.getBoard().isStalemate()) {
				result = true;
				System.out.println("Stalemate!");
			}
			setWhoWon(0);
		}
		return result;
	}

	//  public String getReachedBy() {
	//    return reachedBy;
	//  }


	public double getValue() {
		return value;
	}

	public void setValue(double value2) {
		value = value2;
	}

	//  public int getSideToPlay() {
	//    return sideToPlay;
	//  }

	//  public boolean isTerminal() {
	//    return isTerminalNode;
	//  }

	public SearchNode getParent() {
		return parent;
	}

	//  public int getId() {
	//    return id;
	//  }

	public String toString() {
		String res = "";

		//res += "ID: " + getId();
		res += "\nType of node: " + getClass();
		//res += "\nReached by: " + getReachedBy();
		// res += "\nSide to play: " + getSideToPlay();
		res += "\nNode value: " + value;
		res += "\nBoard:\n";
		res += getWorld().toString();

		return res;
	}
	
	@Override
	public int compareTo(SearchNode other) {
		if(this.value==other.getValue()) {
			return 0;
		}
		else if(this.value<other.value) {
			return 1;
		}
		else {
			return -1;
		}
	}
	
//	public int compare(SearchNode node1, SearchNode node2) {
//		if (node1.value == node2.value)
//            return 0;
//        else if (node1.value < node2.value)
//            return 1;
//        else
//            return -1;
//	}
}