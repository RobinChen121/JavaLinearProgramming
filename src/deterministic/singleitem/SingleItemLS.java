package deterministic.singleitem;



import java.util.Arrays;

/**
 * @author chen
 * WW 算法
 *
 */
public class SingleItemLS {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int T=12; //周期
		double[] D={10,62,12,130,154,129,88,52,124,160,238,41}; 
		double[] s={54,54,54,54,54,54,54,54,54,54,54,54};
		double[] h={0.4,0.4,0.4,0.4,0.4,0.4,0.4,0.4,0.4,0.4,0.4,0.4};
		double[] v = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
		
		double[][] cost = new double[T][T];//任意两个周期间的成本，从一个周期启动生产，库存持续到另一个周期
		double[] OptCost = new double[T]; //记录从第一期到第T期的最优总成本
		int[] Order = new int[T];  //从第一期到第T期的最优生产序列，用0-1表示，0表示该期不生产，1表示该期启动生产	
		double[] I = new double[T]; //记录每阶段的库存

		for (int i = 0; i < T; i++)
			for (int j = 0; j < T; j++)
				cost[i][j]=Double.MAX_VALUE;  //初试化成本


		//计算两个周期内的成本，以及最优总成本序列
		for (int i = 0; i < T; i++)
		{
			if(i > 0)  //记录从第1期到第i-1期的最优总成本
			{
				double[] p = new double[T];
				for (int j=0;j<T;j++)
					p[j]=cost[j][i-1];
				OptCost[i-1]= Arrays.stream(p).min().getAsDouble();
			}

			for (int j = i; j < T; j++)
			{
				double sum=0;
				for (int k = i; k < j + 1; k++)
					sum += D[k];
				double hSum = 0; double tempSum = sum;
				for (int k = i; k < j + 1; k++)
				{	
					hSum = hSum + h[i]*(sum - D[k]);
					sum = sum - D[k];
				}
				if (i>0)
					cost[i][j] = OptCost[i-1] + hSum + s[i] + v[i] * tempSum;//得到第i期到第j期的最优总成本
				else
					cost[i][j] = hSum + s[i]+ v[i] * tempSum;
			}
		}
		double[] p = new double[T];
		for (int j = 0; j < T; j++)
				p[j] = cost[j][T-1];
		OptCost[T-1] = Arrays.stream(p).min().getAsDouble();

		//求最优生产序列，从后向前推
		int i=T-1;
		while (i>=0)
		{
			if (OptCost[i] == cost[i][i])
			{
				Order[i] = 1;
				i = i-1;
			}
			else
			{
				Order[i] = 0;
				int index = i;
				for (int k = 0; k < i; k++)
				{
					if (OptCost[i] == cost[k][i])
					{
						index = k;
						Order[index] = 1;
						break;
					}
				}
				Order[index] = 1;
				for (int k = index + 1; k < i; k++)
					Order[k] = 0;
				i = index - 1;
			}
		}

		//根据最优生产序列得到每个阶段的库存，从前向后推
		i=0;
		int index = 0;
		while (index < T-1)
		{		
			for (int j = i + 1; j < T; j++)
			{
				if (Order[j] == 1)
				{
					index = j;
					break;
				}
				if (j == T-1 && Order[j] == 0)
				{
					index = T;
				}
			}
			double sum = 0;
			for (int k = i; k < index; k++)
				sum += D[k];
			for (int k = i; k < index; k++)
			{
				sum = sum - D[k];
				I[k] = sum;
			}
			i = index;
		}
		
		System.out.println("最优生产序列:");
		System.out.println(Arrays.toString(Order));
		System.out.println("从第1期到各期的最优总成本:");
		System.out.println(Arrays.toString(OptCost));
		System.out.println("最优生产时的各阶段库存水平位:");
		System.out.println(Arrays.toString(I));
	}
}
