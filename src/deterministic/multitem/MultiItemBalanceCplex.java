package deterministic.multitem;



import java.util.Arrays;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

/**
 * @author chen
 * 多产品强资金约束批量问题的cplex表达
 *
 */
public class MultiItemBalanceCplex {

	public static void main(String[] args) {
		try {
			IloCplex cplex = new IloCplex();
			// parameters
			double[][] price = {{27.0, 23.0, 31.0, 27.0, 34.0, 36.0, 40.0, 28.0, 27.0, 23.0, 31.0, 27.0, 34.0, 36.0, 40.0, 28.0, 27.0, 23.0, 31.0, 27.0, 34.0, 36.0, 40.0, 28.0, 27.0, 23.0, 31.0, 27.0, 34.0, 36.0, 40.0, 28.0, 27.0, 23.0, 31.0, 27.0, 34.0, 36.0, 40.0, 28.0, 27.0, 23.0, 31.0, 27.0, 34.0, 36.0, 40.0, 28.0, 27.0, 23.0, 31.0, 27.0},
					{30.0, 22.0, 42.0, 14.0, 15.0, 12.0, 12.0, 40, 30.0, 22.0, 42.0, 14.0, 15.0, 12.0, 12.0, 40, 30.0, 22.0, 42.0, 14.0, 15.0, 12.0, 12.0, 40, 30.0, 22.0, 42.0, 14.0, 15.0, 12.0, 12.0, 40, 30.0, 22.0, 42.0, 14.0, 15.0, 12.0, 12.0, 40, 30.0, 22.0, 42.0, 14.0, 15.0, 12.0, 12.0, 40, 30.0, 22.0, 42.0, 14.0}};
			double[][] demand = {{9.0, 12.0, 9.0, 25.0, 9.0, 20.0, 20.0, 25.0, 9.0, 12.0, 9.0, 25.0, 9.0, 20.0, 20.0, 25.0,9.0, 12.0, 9.0, 25.0, 9.0, 20.0, 20.0, 25.0, 9.0, 12.0, 9.0, 25.0, 9.0, 20.0, 20.0, 25.0, 9.0, 12.0, 9.0, 25.0, 9.0, 20.0, 20.0, 25.0, 9.0, 12.0, 9.0, 25.0, 9.0, 20.0, 20.0, 25.0, 9.0, 12.0, 9.0, 25.0},
					{13.0, 19.0, 18.0, 17.0, 10.0, 8.0, 16.0, 16.0, 13.0, 19.0, 18.0, 17.0, 10.0, 8.0, 16.0, 16.0, 13.0, 19.0, 18.0, 17.0, 10.0, 8.0, 16.0, 16.0, 13.0, 19.0, 18.0, 17.0, 10.0, 8.0, 16.0, 16.0, 13.0, 19.0, 18.0, 17.0, 10.0, 8.0, 16.0, 16.0, 13.0, 19.0, 18.0, 17.0, 10.0, 8.0, 16.0, 16.0, 13.0, 19.0, 18.0, 17.0}};
			int T = price[0].length;
			int N = price.length;
			double iniBalance = 300.00;
			double[] iniInventory = new double[2];
			double[][] s = new double[2][T];
			double[][] v = new double[2][T];
			double[][] h = new double[2][T];
			for (int i = 0; i < N; i++)
				for (int j = 0; j < T; j++) {
					if (i == 0) {
						s[i][j] = 20.0; v[i][j] = 10.0;
						h[i][j] = 2.0;
					}
					else {
						s[i][j] = 10.0; v[i][j] = 12.0;
						h[i][j] = 1.0;
					}	
				}
			int L = 2; double alpha = 0.9; 
			double fixPay = 20.0; double minBalance = -10.0;
			double M = 10000.0;

			// decision variables
			IloIntVar[][] x = new IloIntVar[2][T];
			IloNumVar[][] stock = new IloNumVar[2][T];
			IloNumVar[][] w = new IloNumVar[2][T];
			for (int i = 0; i < N; i++) {
				x[i] = cplex.boolVarArray(T);
				stock[i] = cplex.numVarArray(T, 0, Double.MAX_VALUE);
				for (int t = 0; t < T; t++)
					w[i][t] = cplex.numVar(0, demand[i][t]);
			}
			
			// objective function
			IloNumExpr[] revenue = new IloNumExpr[T];
			IloNumExpr[] variProdCost = new IloNumExpr[T];
			IloNumExpr[] balance = new IloNumExpr[T];
			IloLinearNumExpr[] setupCost = new IloLinearNumExpr[T];
			IloLinearNumExpr[] holdCost = new IloLinearNumExpr[T];
			IloNumExpr[][] Q = new IloNumExpr[N][T];
			for (int t = 0; t < T; t++) {
				setupCost[t] = cplex.linearNumExpr();
				holdCost[t] = cplex.linearNumExpr();
				for (int i = 0; i < 2; i++) {
					setupCost[t].addTerm(s[i][t], x[i][t]);
					holdCost[t].addTerm(h[i][t], stock[i][t]);
					IloNumExpr satDemandPay = t >= L  ? cplex.diff(demand[i][t-L], w[i][t-L]) : null;
					IloNumExpr satDemandInventory = cplex.diff(demand[i][t], w[i][t]);
					if (t >= L ) 
						revenue[t] = i == 0 ? cplex.prod(price[i][t-L], satDemandPay)
								: cplex.sum(revenue[t], cplex.prod(price[i][t-L], satDemandPay));
					if (t == T - 1) {
						for (int k = T - L; k < T; k++) {
							IloNumExpr satDemandDiscount = cplex.diff(demand[i][k], w[i][k]);
							revenue[t] = cplex.sum(revenue[t], cplex.prod(price[i][k]/Math.pow(1 + alpha, T + L - k + 1), satDemandDiscount));
						}
					}
					
					Q[i][t] = t == 0 ? cplex.sum(cplex.sum(satDemandInventory, iniInventory[i]), stock[i][t])
					                 : cplex.sum(satDemandInventory, cplex.diff(stock[i][t], stock[i][t-1]));
					variProdCost[t] = i == 0 ? cplex.prod(v[i][t], Q[i][t])
				                 : cplex.sum(variProdCost[t], cplex.prod(v[i][t], Q[i][t]));
				}
				
				IloNumExpr costs = cplex.sum(setupCost[t], holdCost[t], variProdCost[t]);
				if (t == 0)
						balance[t] = t < L ? cplex.diff(iniBalance, costs)
								:cplex.diff(cplex.sum(iniBalance, revenue[t]),costs);
				else 
					balance[t] = t < L ? cplex.diff(balance[t-1], costs)
							           : cplex.diff(cplex.sum(balance[t-1], revenue[t]), costs);
				balance[t] = cplex.diff(balance[t], fixPay);
			}
			cplex.addMaximize(cplex.diff(balance[T-1], iniBalance));
			
			// constraints
			for (int t = 0; t < T; t++) {
				for(int i =0; i < N; i++) {
					cplex.addLe(Q[i][t], cplex.prod(M, x[i][t]));
					cplex.addLe(w[i][t], demand[i][t]);
				}
				cplex.addGe(balance[t], minBalance);
			}
			
			// output results
			if (cplex.solve()) {
				System.out.println("Solution status: " + cplex.getStatus());
				System.out.println("Solution value: " + cplex.getObjValue());
				double[][] valx = new double[N][T];
				double[][] valy = new double[N][T];
				double[][] valw = new double[N][T];
				double[] B = new double[T];
				double[] cashIn = new double[T];
				double[] sCost = new double[T];
				double[] pCost = new double[T];
				double[] hCost = new double[T];
				
				for (int t = 0; t < T; t++) {
					B[t] = cplex.getValue(balance[t]);
					cashIn[t] = t > L -1 ? cplex.getValue(revenue[t]) : 0;
					sCost[t] = cplex.getValue(setupCost[t]);
					pCost[t] = cplex.getValue(variProdCost[t]);
					hCost[t] = cplex.getValue(holdCost[t]);
					for (int i = 0; i <N; i++) {
						valx[i][t] = cplex.getValue(x[i][t]);
						valy[i][t] = cplex.getValue(Q[i][t]);
						valw[i][t] = cplex.getValue(w[i][t]);
					}	
				}
				System.out.println("cash balance: ");
				System.out.println(Arrays.toString(B));
				System.out.println("x: ");
				System.out.println(Arrays.deepToString(valx));
				System.out.println("y: ");
				System.out.println(Arrays.deepToString(valy));
				System.out.println("w: ");
				System.out.println(Arrays.deepToString(valw));
				System.out.println("revenue: ");
				System.out.println(Arrays.toString(cashIn));
				System.out.println("set up cost: ");
				System.out.println(Arrays.toString(sCost));
				System.out.println("vari prod cost: ");
				System.out.println(Arrays.toString(pCost));
				System.out.println("hold cost: ");
				System.out.println(Arrays.toString(hCost));
			}
					
		} catch (IloException e) {
			System.err.println("Concert exception caught: " + e);
		}	
	}
}
