public class UCT {
    public static double uctValue(int totalVisit, double nodeWinScore, int nodeVisit) {
        if (nodeVisit == 0) {
            return Integer.MAX_VALUE;//inf uct
        }
        return ((double) nodeWinScore / (double) nodeVisit) 
          + 1.41 * Math.sqrt(Math.log(totalVisit) / (double) nodeVisit);
    }

    public static MC_Node findBestNodeWithUCT(MC_Node node) {
        int parentVisit = node.getState().getVisitCount();
        double uct = Integer.MIN_VALUE;
        MC_Node result = new MC_Node();
        for(int i=0;i<node.getChildArray().size();i++) {
        	State s = node.getChildArray().get(i).getState();
        	double temp = uctValue(parentVisit, s.getWinScore(), s.getVisitCount());
        	if(temp>uct) {
        		uct = temp;
        		result = node.getChildArray().get(i);
        	}
        }
        return result;
    }
}