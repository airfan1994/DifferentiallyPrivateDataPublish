package Data;

public class Marginal{
private int[][] dataA;
private int[][] dataB;
private int sizeA;
private int sizeB;
private int dim;
private int[] cells;
private double mrg1;
private double mrg2;
	public Marginal()
	{
		
	}
	public Marginal(int[][] dataA,int[][] dataB,int size1,int size2,int dim)
	{
		float s=0.8f;
		
	}
	public Marginal(int[][] vec,int size,int dim,Data data)
	{
		this.cells = data.getCells();
		sizeA = size;
		dataA = vec;
		sizeB = data.getdatasize();
		dataB = data.getDentries();
		dim = data.getDim();
		
	}
	public Marginal(int[][] vec,int size,int dim,String dataname)
	{
		
	}
	public double cal1Marg()
	{
		double sum = 0.0;
		for(int i=0;i<dim;i++)
			sum+=getP1(i);
		mrg1 = sum;
		return mrg1;
	}
	public double cal2Marg()
	{
		double sum = 0.0;
		for(int m=0;m<dim-1;m++)
			for(int n=m+1;n<dim;n++)
				sum+=getP2(m,n);
		mrg2  = sum;
		return sum;
	}
	public double getP1(int i) 
	{
		int d = cells[i];
		double sum = 0.0;
		int[]  tmprecord1 = new int[d];
		int[]  tmprecord2 = new int[d];
		for(int t=0;t<sizeA;t++)
		{
			tmprecord1[dataA[t][i]]++;
		}
		for(int t=0;t<sizeB;t++)
		{
			tmprecord2[dataB[t][i]]++;
		}
		for(int t=0;t<d;t++)
		{
			sum+=Math.abs(tmprecord1[t]-tmprecord2[t]);
		}
		return sum;
	}
	public double getP2(int m,int n) 
	{
		double sum=0.0;
		int dm = cells[m];
		int dn = cells[n];
		//for()
		return sum;
	}
}
