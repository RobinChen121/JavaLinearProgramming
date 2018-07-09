package deterministic.others;

import java.util.Arrays;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

/** 
* @author chen zhen 
* @version 创建时间：2017年12月25日 下午10:31:56 
* @value 类说明 : 需求确定时，构建线性模型
*/


/* total acres of land: 500
 * minimum requirement: wheat >= 200 tons
 * minimum requirement: corn >= 240 tons
 * planning cost: wheat 150/ton, corn 230/ton, sugar 260/ton
 * selling price: wheat 170/ton, corn 150/ton
 * selling price: sugar 36/ton if yields <= 6000, 10/ton for excess  if yields > 6000
 * yield rate: 2.5, 3, 20 respectively
 * purchasing price: wheat 170*1.4/ton, corn 150*1.4/ton
 * 
 * decision variable: 
 * x1: acres of land devoted to wheat
 * x2: acres of land devoted to corn
 * x3: acres of land devoted to sugar
 * y1: tons of purchased wheat
 * y2: tons of purchased corn
 * w1: tons of selling wheat
 * w2: tons of selling corn
 * w3: tons of selling sugar at price 36
 * w4: tons of selling sugar at price 10
 * 
 * build model:
 * objective: max 170*w1 + 150 *w2 + 36*w3 + 10*w4 - 170*1.4*y1 - 150*1.4*y2 - 150*x1 - 230 *x2 - 260*x3
 * 
 * constraints:
 * x1 + x2 + x3 <= 500
 * 2.5*x1 + y1 - w1>= 200
 * 3*x2 + y2 -w2 >= 240
 * w3 + w4 <= 20*x3
 * w3 <= 6000
 * x1, x2, x3, y1, y2, w1, w2, w3, w4 >= 0
 * 
 */


public class DeterministicDemand {
	
	static void normalYield(){
		try {
			IloCplex cplex = new IloCplex();
			IloNumVar[] x = cplex.numVarArray(9, 0, Double.MAX_VALUE);
			
			IloLinearNumExpr objective = cplex.linearNumExpr();
			objective.addTerm(-150, x[0]);
			objective.addTerm(-230, x[1]);
			objective.addTerm(-260, x[2]);
			objective.addTerm(-170*1.4, x[3]);
			objective.addTerm(-150*1.4, x[4]);
			objective.addTerm(170, x[5]);
			objective.addTerm(150, x[6]);
			objective.addTerm(36, x[7]);
			objective.addTerm(10, x[8]);
			cplex.addMaximize(objective);
			
			cplex.addLe(cplex.sum(x[0], x[1], x[2]), 500);
			double[] coeff2 = {2.5, 0, 0, 1, 0, -1, 0, 0, 0};
			cplex.addGe(cplex.scalProd(x, coeff2), 200);
			double[] coeff3 = {0, 3, 0, 0, 1, 0, -1, 0, 0};
			cplex.addGe(cplex.scalProd(x, coeff3), 240);
			double[] coeff4 = {0, 0, -20, 0, 0, 0, 0, 1, 1};
			cplex.addLe(cplex.scalProd(x, coeff4), 0);
			cplex.addLe(x[7], 6000);
			
			if(cplex.solve()) {
				cplex.output().println("Solution status normal yield = " + cplex.getStatus());
				cplex.output().println("Solution value = " + cplex.getObjValue());
				double[] val = cplex.getValues(x);
				System.out.println("parameter values:" + Arrays.toString(val));
			}
		}
		catch (IloException e) {
			System.err.println("Concert exception caught: " + e);
		}
	}
	
	static void highYield(){
		try {
			IloCplex cplex = new IloCplex();
			IloNumVar[] x = cplex.numVarArray(9, 0, Double.MAX_VALUE);
			
			IloLinearNumExpr objective = cplex.linearNumExpr();
			objective.addTerm(-150, x[0]);
			objective.addTerm(-230, x[1]);
			objective.addTerm(-260, x[2]);
			objective.addTerm(-170*1.4, x[3]);
			objective.addTerm(-150*1.4, x[4]);
			objective.addTerm(170, x[5]);
			objective.addTerm(150, x[6]);
			objective.addTerm(36, x[7]);
			objective.addTerm(10, x[8]);
			cplex.addMaximize(objective);
			
			cplex.addLe(cplex.sum(x[0], x[1], x[2]), 500);
			double[] coeff2 = {2.5*1.2, 0, 0, 1, 0, -1, 0, 0, 0};
			cplex.addGe(cplex.scalProd(x, coeff2), 200);
			double[] coeff3 = {0, 3*1.2, 0, 0, 1, 0, -1, 0, 0};
			cplex.addGe(cplex.scalProd(x, coeff3), 240);
			double[] coeff4 = {0, 0, -20*1.2, 0, 0, 0, 0, 1, 1};
			cplex.addLe(cplex.scalProd(x, coeff4), 0);
			cplex.addLe(x[7], 6000);
			
			if(cplex.solve()) {
				cplex.output().println("Solution status high yield = " + cplex.getStatus());
				cplex.output().println("Solution value = " + cplex.getObjValue());
				double[] val = cplex.getValues(x);
				System.out.println("parameter values:" + Arrays.toString(val));
			}
		}
		catch (IloException e) {
			System.err.println("Concert exception caught: " + e);
		}
	}
	
	static void lowYield(){
		try {
			IloCplex cplex = new IloCplex();
			IloNumVar[] x = cplex.numVarArray(9, 0, Double.MAX_VALUE);
			
			IloLinearNumExpr objective = cplex.linearNumExpr();
			objective.addTerm(-150, x[0]);
			objective.addTerm(-230, x[1]);
			objective.addTerm(-260, x[2]);
			objective.addTerm(-170*1.4, x[3]);
			objective.addTerm(-150*1.4, x[4]);
			objective.addTerm(170, x[5]);
			objective.addTerm(150, x[6]);
			objective.addTerm(36, x[7]);
			objective.addTerm(10, x[8]);
			cplex.addMaximize(objective);
			
			cplex.addLe(cplex.sum(x[0], x[1], x[2]), 500);
			double[] coeff2 = {2.5*0.8, 0, 0, 1, 0, -1, 0, 0, 0};
			cplex.addGe(cplex.scalProd(x, coeff2), 200);
			double[] coeff3 = {0, 3*0.8, 0, 0, 1, 0, -1, 0, 0};
			cplex.addGe(cplex.scalProd(x, coeff3), 240);
			double[] coeff4 = {0, 0, -20*0.8, 0, 0, 0, 0, 1, 1};
			cplex.addLe(cplex.scalProd(x, coeff4), 0);
			cplex.addLe(x[7], 6000);
			
			if(cplex.solve()) {
				cplex.output().println("Solution status low yield = " + cplex.getStatus());
				cplex.output().println("Solution value = " + cplex.getObjValue());
				double[] val = cplex.getValues(x);
				System.out.println("parameter values:" + Arrays.toString(val));
			}
		}
		catch (IloException e) {
			System.err.println("Concert exception caught: " + e);
		}
	}
	
	public static void main(String[] args) {
		normalYield();
		highYield();
		lowYield();

	}
}
