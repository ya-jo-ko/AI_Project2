
public class Move {
	private String move;
	private double evaluation;

	public Move(String move, double evaluation) {
		this.move = move;
		this.evaluation = evaluation;
	}
	public Move(String move) {
		this.move = move;
	}

	public String getMove() {
		return move;
	}

	public void setMove(String move) {
		this.move = move;
	}

	public double getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(double evaluation) {
		this.evaluation = evaluation;
	}
	
	
	//Useful Functions
	

}
