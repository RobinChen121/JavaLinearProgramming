package test;

import ilog.concert.IloException;
import ilog.concert.IloLPMatrix;
import ilog.concert.IloModeler;
import ilog.concert.IloNumVar;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;

/**
* @author Zhen Chen
* @date: 2018年12月7日 下午5:58:27  
* @email: 15011074486@163.com,
* @licence: MIT licence. 
*
* @Description:  test callback for a seft defined model
*/

public class CallBackTest {
	
	   // Implement the callback as an extension of class
	   // IloCplex.ContinuousCallback by overloading method main().  In the
	   // implementation use protected methods of class IloCplex.ContinuousCallback
	   // and its super classes, such as getNiterations64(), isFeasible(),
	   // getObjValue(), and getInfeasibility() used in this example.

	static class MyCallback extends IloCplex.ContinuousCallback {
		public void main() throws IloException {
			System.out.print("Iteration " + getNiterations64() + ": ");
			if ( isFeasible() )
				System.out.println("Objective = " + getObjValue());
			else
				System.out.println("Infeasibility measure = " + getInfeasibility());
		}
	}
	   
	   // Use a user cut callback when the added constraints strengthen the
	   // formulation.  Use a lazy constraint callback when the added constraints
	   // remove part of the feasible region.  Use a cut callback when you
	   // are not certain. This is a control call back
	public static class Callback extends IloCplex.UserCutCallback {
		double     eps = 1.0e-6;
		IloRange[] cut;
		Callback(IloRange[] cuts) { cut = cuts; }

		public void main() throws IloException {
			int num = cut.length;
			for (int i = 0; i < num; ++i) {
				IloRange thecut = cut[i];
				if ( thecut != null ) {
					double val = getValue(thecut.getExpr()); // value of the cut expression
					if (  val + eps < thecut.getLB() || val - eps > thecut.getUB() ) { // conditions for cutting
						add(thecut, IloCplex.CutManagement.UseCutForce);
						cut[i] = null;
					}
				}
			}
		}
	}
	   
	public static IloRange[] makeCuts(IloModeler m) {
		
		
		
		IloRange[] cut = new IloRange[8];
		return cut;
	}
	
	
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
			
			IloRange con1 = cplex.range(-Double.MAX_VALUE, cplex.scalProd(x, coeff1), 20);
			IloRange con2 = cplex.range(-Double.MAX_VALUE, cplex.scalProd(x, coeff2), 30);
			
			IloLPMatrix lp = cplex.addLPMatrix();
			lp.addRow(con1);
			lp.addRow(con2);
			
			//cplex.addLe(cplex.scalProd(x, coeff1), 20.0);
			//cplex.addLe(cplex.scalProd(x, coeff2), 30.0);
			
			// turn off presolve to prevent it from completely solving the model
			// before entering the actual LP optimizer
			cplex.setParam(IloCplex.Param.Preprocessing.Presolve, false);

			// turn off logging
			//cplex.setOut(null);

			// create and instruct cplex to use callback
			//cplex.use(new MyCallback());
			
			// need to use traditional branch-and-cut to allow for control callbacks
			cplex.setParam(IloCplex.Param.MIP.Strategy.Search, IloCplex.MIPSearch.Traditional);
			cplex.setParam(IloCplex.Param.MIP.Interval, 1000); // frequency of logging information
			
			cplex.exportModel("test.mps");
			//cplex.importModel("test.mps");
	        // IloLPMatrix lp = (IloLPMatrix)cplex.LPMatrixIterator().next();
			
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


