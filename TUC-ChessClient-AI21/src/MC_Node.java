import java.util.*;
public class MC_Node {
	private State state;
	private MC_Node parent;
	public List<MC_Node> childArray = new LinkedList<MC_Node>();
	private int whoWon = 0; //0 is draw, 1 is me, -1 is opponent
	
	//    public MC_Node(State state, MC_Node parent) {
	//    	this.parent = parent;
	//    	this.state = state;
	//    	childArray = new LinkedList<MC_Node>();
	//    }
	
	public MC_Node() {
		
	}
	public MC_Node(MC_Node node) {
		this.state = node.state;
		this.parent = node.parent;
		this.childArray = node.childArray;
	}
	
	// setters and getters
	public int getWhoWon() {
		return whoWon;
	}
	public void setWhoWon(int whoWon) {
		this.whoWon = whoWon;
	}
	public State getState() {
		return state;
	}
	public void setState(State state) {
		this.state = state;
	}
	public MC_Node getParent() {
		return parent;
	}
	public void setParent(MC_Node parent) {
		this.parent = parent;
	}
	public List<MC_Node> getChildArray() {
		return childArray;
	}
	public void setChildArray(List<MC_Node> childArray) {
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
//	public boolean stateIsStalemate(MC_Node node) {
//		
//	}
	public MC_Node getChildWithMaxScore(){
		double maxWin = Integer.MIN_VALUE;
		MC_Node result = new MC_Node();
		for(int i=0;i<childArray.size();i++) {
			MC_Node child = childArray.get(i);
			if(maxWin<child.getState().getWinScore()) {
				maxWin = child.getState().getWinScore();
				result = child;
			}
		}
		return result;
	}
}