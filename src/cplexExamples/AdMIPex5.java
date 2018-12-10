/* --------------------------------------------------------------------------
 * File: AdMIPex5.java
 * Version 12.7.1  
 * --------------------------------------------------------------------------
 * Licensed Materials - Property of IBM
 * 5725-A06 5725-A29 5724-Y48 5724-Y49 5724-Y54 5724-Y55 5655-Y21
 * Copyright IBM Corporation 2001, 2017. All Rights Reserved.
 *
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with
 * IBM Corp.
 * --------------------------------------------------------------------------
 *
 * AdMIPex5.java -- Solving noswot by adding cuts in a cut callback
 *
 * Examples AdMIPex4.java and AdMIPex5.java both solve the MIPLIB
 * 3.0 model noswot.mps by adding user cuts.  AdMIPex4.java adds
 * these cuts to the cut table before the branch-and-cut process
 * begins, while AdMIPex5.java adds them through the user cut callback
 * during the branch-and-cut process. 
 */
package cplexExamples;
import ilog.concert.*;
import ilog.cplex.*;


public class AdMIPex5 {

   // Use a user cut callback when the added constraints strengthen the
   // formulation.  Use a lazy constraint callback when the added constraints
   // remove part of the feasible region.  Use a cut callback when you
   // are not certain.
	
	// Add the following valid cuts for the noswot model via cut callback:
	//
	//  cut1: X21 - X22 <= 0
	//  cut2: X22 - X23 <= 0
	//  cut3: X23 - X24 <= 0
	//  cut4: 2.08 X11 + 2.98 X21 + 3.47 X31 + 2.24 X41 + 2.08 X51
	//      + 0.25 W11 + 0.25 W21 + 0.25 W31 + 0.25 W41 + 0.25 W51
	//        <= 20.25
	//  cut5: 2.08 X12 + 2.98 X22 + 3.47 X32 + 2.24 X42 + 2.08 X52
	//      + 0.25 W12 + 0.25 W22 + 0.25 W32 + 0.25 W42 + 0.25 W52
	//        <= 20.25
	//  cut6: 2.08 X13 + 2.98 X23 + 3.4722 X33 + 2.24 X43 + 2.08 X53
	//      + 0.25 W13 + 0.25 W23 + 0.25 W33 + 0.25 W43 + 0.25 W53
	//        <= 20.25
	//  cut7: 2.08 X14 + 2.98 X24 + 3.47 X34 + 2.24 X44 + 2.08 X54
	//      + 0.25 W14 + 0.25 W24 + 0.25 W34 + 0.25 W44 + 0.25 W54
	//        <= 20.25
	//  cut8: 2.08 X15 + 2.98 X25 + 3.47 X35 + 2.24 X45 + 2.08 X55
	//      + 0.25 W15 + 0.25 W25 + 0.25 W35 + 0.25 W45 + 0.25 W55
	//        <= 16.25

   public static class Callback extends IloCplex.UserCutCallback {
      double     eps = 1.0e-6;
      IloRange[] cut;
      Callback(IloRange[] cuts) { cut = cuts; }
    
      public void main() throws IloException {
         int num = cut.length;
         for (int i = 0; i < num; ++i) {
            IloRange thecut = cut[i];
            if ( thecut != null ) {
               double val = getValue(thecut.getExpr());
               if ( thecut.getLB() > val+eps || val-eps > thecut.getUB() ) {
                  add(thecut, IloCplex.CutManagement.UseCutForce); // add cuts when the cuts are not followed by present candidates
                  cut[i] = null;
               }
            }
         }
      }
   }


   public static IloRange[] makeCuts(IloModeler m, IloLPMatrix lp)
                                                 throws IloException {
      IloNumVar x11 = null, x12 = null, x13 = null, x14 = null, x15 = null;
      IloNumVar x21 = null, x22 = null, x23 = null, x24 = null, x25 = null;
      IloNumVar x31 = null, x32 = null, x33 = null, x34 = null, x35 = null;
      IloNumVar x41 = null, x42 = null, x43 = null, x44 = null, x45 = null;
      IloNumVar x51 = null, x52 = null, x53 = null, x54 = null, x55 = null;
      IloNumVar w11 = null, w12 = null, w13 = null, w14 = null, w15 = null;
      IloNumVar w21 = null, w22 = null, w23 = null, w24 = null, w25 = null;
      IloNumVar w31 = null, w32 = null, w33 = null, w34 = null, w35 = null;
      IloNumVar w41 = null, w42 = null, w43 = null, w44 = null, w45 = null;
      IloNumVar w51 = null, w52 = null, w53 = null, w54 = null, w55 = null;
    
      IloNumVar[] vars = lp.getNumVars();
      int         num  = vars.length;
    
      for (int i = 0; i < num; ++i) {
         if      ( vars[i].getName().equals("X11") ) x11 = vars[i];
         else if ( vars[i].getName().equals("X12") ) x12 = vars[i];
         else if ( vars[i].getName().equals("X13") ) x13 = vars[i];
         else if ( vars[i].getName().equals("X14") ) x14 = vars[i];
         else if ( vars[i].getName().equals("X15") ) x15 = vars[i];
         else if ( vars[i].getName().equals("X21") ) x21 = vars[i];
         else if ( vars[i].getName().equals("X22") ) x22 = vars[i];
         else if ( vars[i].getName().equals("X23") ) x23 = vars[i];
         else if ( vars[i].getName().equals("X24") ) x24 = vars[i];
         else if ( vars[i].getName().equals("X25") ) x25 = vars[i];
         else if ( vars[i].getName().equals("X31") ) x31 = vars[i];
         else if ( vars[i].getName().equals("X32") ) x32 = vars[i];
         else if ( vars[i].getName().equals("X33") ) x33 = vars[i];
         else if ( vars[i].getName().equals("X34") ) x34 = vars[i];
         else if ( vars[i].getName().equals("X35") ) x35 = vars[i];
         else if ( vars[i].getName().equals("X41") ) x41 = vars[i];
         else if ( vars[i].getName().equals("X42") ) x42 = vars[i];
         else if ( vars[i].getName().equals("X43") ) x43 = vars[i];
         else if ( vars[i].getName().equals("X44") ) x44 = vars[i];
         else if ( vars[i].getName().equals("X45") ) x45 = vars[i];
         else if ( vars[i].getName().equals("X51") ) x51 = vars[i];
         else if ( vars[i].getName().equals("X52") ) x52 = vars[i];
         else if ( vars[i].getName().equals("X53") ) x53 = vars[i];
         else if ( vars[i].getName().equals("X54") ) x54 = vars[i];
         else if ( vars[i].getName().equals("X55") ) x55 = vars[i];
         else if ( vars[i].getName().equals("W11") ) w11 = vars[i];
         else if ( vars[i].getName().equals("W12") ) w12 = vars[i];
         else if ( vars[i].getName().equals("W13") ) w13 = vars[i];
         else if ( vars[i].getName().equals("W14") ) w14 = vars[i];
         else if ( vars[i].getName().equals("W15") ) w15 = vars[i];
         else if ( vars[i].getName().equals("W21") ) w21 = vars[i];
         else if ( vars[i].getName().equals("W22") ) w22 = vars[i];
         else if ( vars[i].getName().equals("W23") ) w23 = vars[i];
         else if ( vars[i].getName().equals("W24") ) w24 = vars[i];
         else if ( vars[i].getName().equals("W25") ) w25 = vars[i];
         else if ( vars[i].getName().equals("W31") ) w31 = vars[i];
         else if ( vars[i].getName().equals("W32") ) w32 = vars[i];
         else if ( vars[i].getName().equals("W33") ) w33 = vars[i];
         else if ( vars[i].getName().equals("W34") ) w34 = vars[i];
         else if ( vars[i].getName().equals("W35") ) w35 = vars[i];
         else if ( vars[i].getName().equals("W41") ) w41 = vars[i];
         else if ( vars[i].getName().equals("W42") ) w42 = vars[i];
         else if ( vars[i].getName().equals("W43") ) w43 = vars[i];
         else if ( vars[i].getName().equals("W44") ) w44 = vars[i];
         else if ( vars[i].getName().equals("W45") ) w45 = vars[i];
         else if ( vars[i].getName().equals("W51") ) w51 = vars[i];
         else if ( vars[i].getName().equals("W52") ) w52 = vars[i];
         else if ( vars[i].getName().equals("W53") ) w53 = vars[i];
         else if ( vars[i].getName().equals("W54") ) w54 = vars[i];
         else if ( vars[i].getName().equals("W55") ) w55 = vars[i];
      }
    
      IloRange[] cut = new IloRange[8];
    
      cut[0] = m.le(m.diff(x21, x22), 0.0);
      cut[1] = m.le(m.diff(x22, x23), 0.0);
      cut[2] = m.le(m.diff(x23, x24), 0.0);
      cut[3] = m.le(m.sum(m.sum(m.prod(2.08, x11),
                                m.prod(2.98, x21),
                                m.prod(3.47, x31),
                                m.prod(2.24, x41),
                                m.prod(2.08, x51)),
                          m.sum(m.prod(0.25, w11),
                                m.prod(0.25, w21),
                                m.prod(0.25, w31),
                                m.prod(0.25, w41),
                                m.prod(0.25, w51))), 20.25);
      cut[4] = m.le(m.sum(m.sum(m.prod(2.08, x12),
                                m.prod(2.98, x22),
                                m.prod(3.47, x32),
                                m.prod(2.24, x42),
                                m.prod(2.08, x52)),
                          m.sum(m.prod(0.25, w12),
                                m.prod(0.25, w22),
                                m.prod(0.25, w32),
                                m.prod(0.25, w42),
                                m.prod(0.25, w52))), 20.25);
      cut[5] = m.le(m.sum(m.sum(m.prod(2.08, x13),
                                m.prod(2.98, x23),
                                m.prod(3.47, x33),
                                m.prod(2.24, x43),
                                m.prod(2.08, x53)),
                          m.sum(m.prod(0.25, w13),
                                m.prod(0.25, w23),
                                m.prod(0.25, w33),
                                m.prod(0.25, w43),
                                m.prod(0.25, w53))), 20.25);
      cut[6] = m.le(m.sum(m.sum(m.prod(2.08, x14),
                                m.prod(2.98, x24),
                                m.prod(3.47, x34),
                                m.prod(2.24, x44),
                                m.prod(2.08, x54)),
                          m.sum(m.prod(0.25, w14),
                                m.prod(0.25, w24),
                                m.prod(0.25, w34),
                                m.prod(0.25, w44),
                                m.prod(0.25, w54))), 20.25);
      cut[7] = m.le(m.sum(m.sum(m.prod(2.08, x15),
                                m.prod(2.98, x25),
                                m.prod(3.47, x35),
                                m.prod(2.24, x45),
                                m.prod(2.08, x55)),
                          m.sum(m.prod(0.25, w15),
                                m.prod(0.25, w25),
                                m.prod(0.25, w35),
                                m.prod(0.25, w45),
                                m.prod(0.25, w55))), 16.25);
    
      return cut;
   }

   public static void main(String[] args) {
      try {
         IloCplex cplex = new IloCplex();
       
         cplex.importModel("noswot.mps");
         IloLPMatrix lp = (IloLPMatrix)cplex.LPMatrixIterator().next();
         IloNumVar[] vars = ((IloLPMatrix)cplex.LPMatrixIterator().next()).getNumVars();
         
         cplex.use(new Callback(makeCuts(cplex, lp)));
       
         cplex.setParam(IloCplex.Param.MIP.Interval, 1000); // frequency of logging information
         cplex.setParam(IloCplex.Param.MIP.Strategy.Search, IloCplex.MIPSearch.Traditional);
         cplex.setParam(IloCplex.Param.MIP.Display, 3); // default value is 2
         if ( cplex.solve() ) {
            System.out.println("Solution status = " + cplex.getStatus());
            System.out.println("Solution value  = " + cplex.getObjValue());
         }
         cplex.end();
      }
      catch (IloException e) {
         System.err.println("Concert exception caught: " + e);
      }
   }
}
