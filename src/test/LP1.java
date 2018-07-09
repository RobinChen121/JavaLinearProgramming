package test;

/**
 * @author chen
 * 一个简单的线性规划
 */



import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;


public class LP1 {

	public static void main(String[] args) {
		try {
			IloCplex cplex = new IloCplex(); // creat a model
			
			double[] lb = {0.0, 0.0, 0.0};
			double[] ub = {40.0, Double.MAX_VALUE, Double.MAX_VALUE};
			IloNumVar[] x = cplex.numVarArray(3, lb, ub);
			
			double[] objvals = {1.0, 2.0, 3.0};
			cplex.addMaximize(cplex.scalProd(x, objvals));
			
			double[] coeff1 = {-1.0, 1.0, 1.0};
			double[] coeff2 = {1.0, -3.0, 1.0};
			
			cplex.addLe(cplex.scalProd(x, coeff1), 20.0);
			cplex.addLe(cplex.scalProd(x, coeff2), 30.0);
			
			if (cplex.solve()) {
				cplex.output().println("Solution status = " + cplex.getStatus());
				cplex.output().println("Solution value = " + cplex.getObjValue());
				double[] val = cplex.getValues(x);
				for (int j = 0; j < val.length; j++)
					cplex.output().println("x" + (j+1) + "  = " + val[j]);
			}
			cplex.end();
			
		} catch (IloException e) {
			System.err.println("Concert exception caught: " + e);
		}
	}
}
