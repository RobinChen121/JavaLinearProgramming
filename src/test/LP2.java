package test;

import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class LP2 {

	public static void main(String[] args) {
		try {
			IloCplex cplex = new IloCplex();

			double[] lb = {0.0, -Double.MAX_VALUE, -Double.MAX_VALUE};
			double[] ub = {40.0, Double.MAX_VALUE, Double.MAX_VALUE};
			IloNumVar[] x = cplex.numVarArray(3, lb, ub);
			
			double[] objvars = {1.0, 2.0, 3.0};
			cplex.addMaximize(cplex.scalProd(x, objvars));
			// cplex.add(cplex.maximize(cplex.scalProd(x, objvars)));
			
			double[] coe1 = {-1.0, 1.0, 1.0};
			double[] coe2 = {1.0, -3.0, 1.0};
			cplex.addLe(cplex.scalProd(x, coe1), 20.0);
			cplex.addLe(cplex.scalProd(x, coe2), 30.0);
			
			if (cplex.solve()) {
				cplex.output().println("Solution status = " + cplex.getStatus());
				cplex.output().println("Solution value = " + cplex.getObjValue());
				double[] val = cplex.getValues(x);
				for (int j = 0; j < val.length; j++)
					cplex.output().println("x" + (j+1) + "  = " + val[j]);
			}
			cplex.end();
			
		} catch (IloException e){
			System.err.println("Concert exception caught: " + e);
		}

	}

}
