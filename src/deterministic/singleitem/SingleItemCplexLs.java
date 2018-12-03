package deterministic.singleitem;

import java.util.Arrays;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

/**
 * @author chen
 * 单产品强资金约束批量问题的cplex表达
 *
 */
public class SingleItemCplexLs {

	public static void main(String[] args) {
		try {
			IloCplex cplex = new IloCplex();
			
			// parameters
			double[] D = {10,62,12,130,154,129,88,52,124,160,238,41}; 
			double[] s = {54,54,54,54,54,54,54,54,54,54,54,54};
			double[] h = {0.4,0.4,0.4,0.4,0.4,0.4,0.4,0.4,0.4,0.4,0.4,0.4};
			double[] v = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
			int T = D.length;
			double iniInventory = 0.0;
			
			// decision variables
			IloIntVar[] x = cplex.boolVarArray(T);
			IloNumVar[] stock = cplex.numVarArray(T, 0.0, Double.MAX_VALUE); // 最好用这种形式
			
			// objective
//			IloNumExpr[] setupCost = new IloNumExpr[T];
//			IloNumExpr[] holdCost = new IloNumExpr[T];
//			IloNumExpr[] variCost = new IloNumExpr[T];
//			IloNumExpr totalCost = null;
//			IloNumExpr[] Q = new IloNumExpr[T];
//			for (int i = 0; i < T; i++) {
//				setupCost[i] = cplex.prod(s[i], x[i]); 
//				holdCost[i] = cplex.prod(h[i], stock[i]);
//				if (i == 0) {
//					Q[i] = cplex.sum(stock[0], D[0]-iniInventory);
//				}
//				else {
//					Q[i] = cplex.sum(D[i], cplex.diff(stock[i], stock[i-1]));
//					
//				}
//				variCost[i] = cplex.prod(v[i], Q[i]);
//				if (i == 0)
//					totalCost = cplex.sum(setupCost[i], holdCost[i], variCost[i]);
//				else {
//					totalCost = cplex.sum(totalCost, cplex.sum(setupCost[i], holdCost[i], variCost[i]));
//				}
//			}
			
			IloLinearNumExpr setupCost = cplex.linearNumExpr();
			IloLinearNumExpr holdCost = cplex.linearNumExpr();
			IloNumExpr[] variCost = new IloNumExpr[T];
			IloNumExpr totalCost = null;
			IloNumExpr[] Q = new IloNumExpr[T]; // ordering quantity
			setupCost.addTerms(s, x);
			holdCost.addTerms(h, stock);
			for (int i = 0; i < T; i++) {
				if (i == 0) {
					Q[i] = cplex.sum(stock[0], D[0]-iniInventory);
				}
				else {
					Q[i] = cplex.sum(D[i], cplex.diff(stock[i], stock[i-1]));
				}
				variCost[i] = cplex.prod(v[i], Q[i]);
				if (i == 0)
					totalCost = cplex.sum(0.0, variCost[i]);
				else {
					totalCost = cplex.sum(totalCost, variCost[i]);
				}
			}
			totalCost = cplex.sum(totalCost, setupCost, holdCost);
			
			cplex.addMinimize(totalCost);
			//cplex.addMinimize(cplex.sum(cplex.scalProd(s, x), cplex.scalProd(h, stock)));
			
			//constraints
			IloNumExpr Q2 = cplex.sum(stock[0], D[0]-iniInventory);
			IloNumExpr temp = cplex.prod(500000.00, x[0]);
			cplex.addLe(Q2, temp);
			
			
			for (int i = 1; i < T; i++) {
				Q2 = cplex.sum(D[i], cplex.diff(stock[i], stock[i-1]));
				temp = cplex.prod(500000.00, x[i]);
				cplex.addLe(Q2, temp);
			}
			
			if (cplex.solve()) {
				cplex.output().println("Solution status = " + cplex.getStatus());
				cplex.output().println("Solution value = " + cplex.getObjValue());
				double[] valx = cplex.getValues(x);
				double[] valI = cplex.getValues(stock);
				double[] orderQ = new double[T];
				for (int i = 0; i < T; i++)
					orderQ[i] = cplex.getValue(Q[i]);
				System.out.println("x = ");
				System.out.println(Arrays.toString(valx));
				System.out.println("I = ");
				System.out.println(Arrays.toString(valI));
				System.out.println("Q = ");
				System.out.println(Arrays.toString(orderQ));
			}
			cplex.end();
		} catch (IloException e) {
			System.err.println("Concert exception caught: " + e);
		}

	}

}
