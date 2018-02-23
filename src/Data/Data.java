package Data;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import Site.Node;
import Site.TreeSite;
public class Data {
	private HashMap<Integer,DataCateAttribute> cateattrs;
	private HashMap<Integer,DataNumAttribute> numattrs;
	private int total; 
	private int[][] dentries;
	private int[][] bentries;
	private int dim;
	private int[] starts;
	private int[] ends;
	private int bdim;
	private HashMap<Integer,Node> datanodes;
	private HashMap<Integer,TreeSite> DataSet;
	private int[] cells;
	public static void main(String[] args) throws IOException
	{
		Data data = new Data("adult.dat","adult.domain");
	}
	public Data()
	{
		
	}
	public Data(String datafile,String domainfile) throws IOException
	{
		loadDomain(domainfile);
		dataDiscretization(datafile);
		binarization();
		System.out.println(total);
	}
	public void loadDomain(String domainfile) throws IOException
	{
		cateattrs = new HashMap<Integer,DataCateAttribute>();
		numattrs = new HashMap<Integer,DataNumAttribute>();
		BufferedReader domain = new BufferedReader(new FileReader(domainfile));
		int num = Integer.parseInt(domain.readLine());
		dim = num;
		cells = new int[dim];
		starts = new int[dim];
		ends = new int[dim];
		for(int i=1;i<=num;i++)
		{
			String[] tokens = domain.readLine().split("\\s+");
			if(tokens[0].equals("C"))
			{
				double start = Double.parseDouble(tokens[1]);
				double end = Double.parseDouble(tokens[2]);
				int d = Integer.parseInt(tokens[3]);
				cells[i-1] = d;
				DataNumAttribute numattr = new DataNumAttribute(d,start,end);
				numattrs.put(i, numattr);
				if(i==1)
				{
					starts[0] = 0;
					ends[0] = starts[0]+(int)Math.ceil(Math.log(d)/Math.log(2))-1;
					
					//System.out.println("a"+i+":"+"start"+starts[0]);
					//System.out.println("a"+i+":"+"end"+ends[0]);
				}
				else
				{
					starts[i-1]=ends[i-2]+1;
					ends[i-1] = starts[i-1]+(int)Math.ceil(Math.log(d)/Math.log(2))-1;
					bdim = ends[i-1]+1;
					//System.out.println("a"+i+":"+"start"+starts[i-1]);
					//System.out.println("a"+i+":"+"end"+ends[i-1]);
				}
			}
			else
			{
				int d = tokens.length-1;
				cells[i-1] = d;
				DataCateAttribute cateattr = new DataCateAttribute(d);
				for(int t=1;t<=d;t++)
				{
					cateattr.put(tokens[t], t-1);
				}
				cateattrs.put(i, cateattr);
				if(i==1)
				{
					starts[0] = 0;
					ends[0] = starts[0]+(int)Math.ceil(Math.log(d)/Math.log(2))-1;
					//System.out.println("a"+i+":"+"start"+starts[0]);
					//System.out.println("a"+i+":"+"end"+ends[0]);
				}
				else
				{
					starts[i-1]=ends[i-2]+1;
					ends[i-1] = starts[i-1]+(int)Math.ceil(Math.log(d)/Math.log(2))-1;
					bdim = ends[i-1]+1;
					//System.out.println("a"+i+":"+"start"+starts[i-1]);
					//System.out.println("a"+i+":"+"end"+ends[i-1]);
				}
			}
		}
		//System.out.println("4:"+cateattrs.get(4).getD());
		dim=num;
		domain.close();
	}
	public void dataDiscretization(String datafile) throws NumberFormatException, IOException
	{
		BufferedReader datar = new BufferedReader(new FileReader(datafile));	
		total = Integer.parseInt(datar.readLine());
		dentries = new int[total][dim];
		for(int i=0;i<total;i++)
		{
			String[] s = datar.readLine().split("\\s+");
			//System.out.println(i);
			for(int t=0;t<dim;t++)
			{
				if(cateattrs.get(t+1)!=null)
				{
					dentries[i][t] = cateattrs.get(t+1).get(s[t]);
				}
				else
				{
					double step = numattrs.get(t+1).getStep();
					int d=numattrs.get(t+1).getD();
					double ss = numattrs.get(t+1).getStart();
					int m=0;
					for(;m<d;m++)
					{
						//System.out.println(i);
						if(Double.parseDouble(s[t])<=(ss+(m+1)*step))
						{
							dentries[i][t] = m;
							break;
						}
					}
					if(m==d)
					{
						System.out.println("errors");
					}
				}
			}
		}
		datar.close();
	}
	public void writeDiscretizationFile() throws IOException
	{
		FileWriter writer = new FileWriter("discretion1.csv");
		FileWriter writerbin = new FileWriter("bin1.csv");
		for(int i=0;i<total;i++)
		{
			writer.write(i+":");
			for(int m=0;m<dim;m++)
			{
				writer.write(dentries[i][m]+",");
			}
			writer.write("\n");
			writerbin.write(i+":");
			for(int n=0;n<bdim;n++)
			{
				writerbin.write(bentries[i][n]+",");
			}
			writerbin.write("\n");
		}
		writer.close();
		writerbin.close();
	}
	public void writeDFile(String DFileName) throws IOException    //csv格式写入
	{
		FileWriter writer = new FileWriter(DFileName);
		for(int i=0;i<total;i++)
		{
			for(int m=0;m<dim-1;m++)
			{
				writer.write(dentries[i][m]+",");
			}
			writer.write(dentries[i][dim-1]+"\n");
		}
		writer.close();
	}
	public void writeBFile(String BFileName) throws IOException
	{
		FileWriter writerbin = new FileWriter(BFileName);
		for(int i=0;i<total;i++)
		{
			for(int n=0;n<bdim;n++)
			{
				writerbin.write(bentries[i][n]+",");
			}
			writerbin.write("\n");
			//writerbin.write(dentries[i][dim-1]+"\n");
		}
		writerbin.close();
	}
	public void binarization()
	{
		System.out.println(bdim);
		bentries = new int[total][bdim];
		//高位在左边;
		for(int i=0;i<total;i++)
		{
			for (int d = 0; d < dim; d++){
				int tmp = dentries[i][d];
				for (int p = ends[d]; p>= starts[d]; p--){
					bentries[i][p] = tmp % 2;
					tmp = tmp >> 1;
				}
			}
		}
	}
	public HashMap<Integer,TreeSite> divideSites()
	{
		DataSet=new HashMap<Integer,TreeSite>();
		int sid=1;
		int i=1;
		while(i<=bdim)
		{
			HashMap<Integer,Node> nodes = new HashMap<Integer,Node>();
			for(int p=i;(p<=i+4)&&(p<=bdim);p++)
			{
				nodes.put(p,datanodes.get(p));
			}
			TreeSite site = new TreeSite(sid,nodes);
			DataSet.put(sid,site);
			sid++;
			i=i+5;
		}
		return DataSet;
	}
	public HashMap<Integer, TreeSite> divideSites(HashMap<Integer,HashSet<Integer>> sets)
	{
		DataSet=new HashMap<Integer,TreeSite>();
		for(Integer p:sets.keySet())
		{
			HashMap<Integer,Node> nodes = new HashMap<Integer,Node>();
			for(Integer q:sets.get(p))
				nodes.put(q,datanodes.get(q));
			TreeSite site = new TreeSite(p,nodes);	
			DataSet.put(p,site);
		}
		return DataSet;
	}
	public HashMap<Integer,Node> fillDataVec()
	{
		datanodes = new HashMap<Integer,Node>();
		for(int i=0;i<bdim;i++)
		{
			int[] vdata = new int[total];
			for(int t= 0;t<total;t++)
			{
				vdata[t] = bentries[t][i];
			}
			Node node=new Node(i+1,vdata);
			datanodes.put(i+1,node);
		}
		return datanodes;
	}
	public int getDim()
	{
		return dim;
	}
	public int getBDim()
	{
		return bdim;
	}
	public int[] getStarts()
	{
		return starts;
	}
	public int[] getEnds()
	{
		return ends;
	}
	public int[] getCells()
	{
		return cells;
	}
	public int getdatasize()
	{
		return total;
	}
	public int[][] getDentries()
	{
		return dentries;
	}
	public int[][] getBentries()
	{
		return bentries;
	}
	public void writeallI(String filename) throws IOException
	{
		FileWriter writer = new FileWriter(filename);
		java.text.DecimalFormat   df   =new   java.text.DecimalFormat("#0.00000000"); 
		for(int m=1;m<=bdim;m++)
		{
			for(int n=m+1;n<=bdim;n++)
			{
				int n00=0;
				int n01=0;
				int n10=0;
				int n11=0;
				for(int i = 0;i<total;i++)
				{
					if(bentries[i][m-1]==0&&bentries[i][n-1]==0)
						n00++;
					else if(bentries[i][m-1]==0&&bentries[i][n-1]==1)
						n01++;
					else if(bentries[i][m-1]==1&&bentries[i][n-1]==0)
						n10++;
					else
						n11++;
				}
				double p00=(1.0*n00)/total;
				double p01=(1.0*n01)/total;
				double p10=(1.0*n10)/total;
				double p11=(1.0*n11)/total;
				double pA0=p00+p01;
				double pA1=p10+p11;
				double pB0=p00+p10;
				double pB1=p01+p11;
				//System.out.println(n00+","+n01+","+n10+","+n11+",");
				//System.out.println(p00+","+p01+","+p10+","+p11+",");
				double entropy=(p00*Math.log(p00/(pA0*pB0))+p01*Math.log(p01/(pA0*pB1))+p10*Math.log(p10/(pA1*pB0))+p11*Math.log(p11/(pA1*pB1)))/Math.log(2);
				double entropy1=(p01*Math.log(p01/(pA0*pB1))+p10*Math.log(p10/(pA1*pB0))+p11*Math.log(p11/(pA1*pB1)))/Math.log(2);
				double entropy2=(p00*Math.log(p00/(pA0*pB0))+p10*Math.log(p10/(pA1*pB0))+p11*Math.log(p11/(pA1*pB1)))/Math.log(2);
				double entropy3=(p00*Math.log(p00/(pA0*pB0))+p01*Math.log(p01/(pA0*pB1))+p11*Math.log(p11/(pA1*pB1)))/Math.log(2);
				double entropy4=(p00*Math.log(p00/(pA0*pB0))+p01*Math.log(p01/(pA0*pB1))+p10*Math.log(p10/(pA1*pB0)))/Math.log(2);
				entropy = (p00==0)?entropy1:((p01==0)?entropy2:((p10==0?entropy3:((p11==0)?entropy4:entropy))));
				//double entropy =  (p00*Math.log(p00/(pA0*pB0))+p01*Math.log(p01/(pA0*pB1))+p10*Math.log(p10/(pA1*pB0))+p11*Math.log(p11/(pA1*pB1)))/Math.log(2);
				if(m<10)
					writer.write(m+"     ");
				else
					writer.write(m+"    ");
				if(n<10)
					writer.write(n+"     ");
				else
					writer.write(n+"    ");
				writer.write(df.format(entropy)+"\n");
				//writer.write("the mutual information between  "+m+","+n+"is "+entropy+"\n");
			}
		}
		writer.close();
	}
	public void writeallF(String filename) throws IOException
	{
		FileWriter writer = new FileWriter(filename);
		java.text.DecimalFormat   df   =new   java.text.DecimalFormat("#0.000000000000000"); 
		for(int m=1;m<=bdim;m++)
		{
			for(int n=m+1;n<=bdim;n++)
			{
				int n00=0;
				int n01=0;
				int n10=0;
				int n11=0;
				for(int i = 0;i<total;i++)
				{
					if(bentries[i][m-1]==0&&bentries[i][n-1]==0)
						n00++;
					else if(bentries[i][m-1]==0&&bentries[i][n-1]==1)
						n01++;
					else if(bentries[i][m-1]==1&&bentries[i][n-1]==0)
						n10++;
					else
						n11++;
				}
				double ans = 0.0;
				HashMap<Integer, Integer> states = new HashMap<Integer, Integer>();
				states.put(0, 0);
				int ceil = (total+1)/2;
				HashMap<Integer, Integer> newStates;
				int a,b;
				{
					a = n00;
					b = n10;
					newStates = new HashMap<Integer, Integer>();
					for (int M : states.keySet()){
						int N = states.get(M);
						int newM = Math.min(M + a, ceil);
						int newN = Math.min(N + b, ceil);
						
						if (newStates.get(newM) == null || newStates.get(newM) < N){
							newStates.put(newM, N);
						}
						
						if (newStates.get(M) == null || newStates.get(M) < newN){
							newStates.put(M, newN);
						}
					}
					
					states = newStates;
				}
				
				{
					a = n01;
					b = n11;
					newStates = new HashMap<Integer, Integer>();
					for (int M : states.keySet()){
						int N = states.get(M);
						int newM = Math.min(M + a, ceil);
						int newN = Math.min(N + b, ceil);
						
						if (newStates.get(newM) == null || newStates.get(newM) < N){
							newStates.put(newM, N);
						}
						
						if (newStates.get(M) == null || newStates.get(M) < newN){
							newStates.put(M, newN);
						}
					}
					states = newStates;
				}
				ans = -total;
				for (int M : states.keySet()){
					int N = states.get(M);
					double score = M + N - total;
					if (score > ans) ans = score;
				}
				if(m<10)
					writer.write(m+"     ");
				else
					writer.write(m+"    ");
				if(n<10)
					writer.write(n+"     ");
				else
					writer.write(n+"    ");
				writer.write(df.format(ans)+"\n");
				//writer.write("the mutual information between  "+m+","+n+"is "+entropy+"\n");
			}
		}
		writer.close();
	}
	public void writemar2(String mar2file) throws IOException
	{
		FileWriter writer = new FileWriter(mar2file);
		for(int m=0;m<bdim;m++)
		{
			for(int n=m+1;n<bdim;n++)
			{
				int n00=0,n01=0,n10=0,n11=0;
				for(int i=0;i<total;i++)
				{
					if(bentries[i][m]==0&&bentries[i][n]==0)
						n00++;
					else if(bentries[i][m]==0&&bentries[i][n]==1)
						n01++;
					else if(bentries[i][m]==1&&bentries[i][n]==0)
						n10++;
					else
						n11++;
				}
				writer.write(m+","+n+","+n00+","+n01+","+n10+","+n11+"\n");
			}
		}
		writer.close();
	}
	public void writemar3(String mar3file) throws IOException
	{
		FileWriter writer = new FileWriter(mar3file);
		for(int m=0;m<bdim;m++)
		{
			for(int n=m+1;n<bdim;n++)
			{
				for(int p = n+1;p<bdim;p++)
				{
					int n000=0,n001=0,n010=0,n011=0,n100=0,n101=0,n110=0,n111=0;
					for(int i=0;i<total;i++)
					{
						if(bentries[i][m]==0&&bentries[i][n]==0&&bentries[i][p]==0)
							n000++;
						else if(bentries[i][m]==0&&bentries[i][n]==0&&bentries[i][p]==1)
							n001++;
						else if(bentries[i][m]==0&&bentries[i][n]==1&&bentries[i][p]==0)
							n010++;
						else if(bentries[i][m]==0&&bentries[i][n]==1&&bentries[i][p]==1)
							n011++;
						else if(bentries[i][m]==1&&bentries[i][n]==0&&bentries[i][p]==0)
							n100++;
						else if(bentries[i][m]==1&&bentries[i][n]==0&&bentries[i][p]==1)
							n101++;
						else if(bentries[i][m]==1&&bentries[i][n]==1&&bentries[i][p]==0)
							n110++;
						else
							n111++;
					}
					writer.write(m+","+n+","+p+","+n000+","+n001+","+n010+","+n011+","+n100+","+n101+","+n110+","+n111+"\n");
				}
			}
		}
		writer.close();
	}
	public void writemar4(String mar4file) throws IOException
	{
		FileWriter writer = new FileWriter(mar4file);
		for(int m=0;m<bdim;m++)
		{
			for(int n=m+1;n<bdim;n++)
			{
				for(int p=n+1;p<bdim;p++)
				{
					for(int q=p+1;q<bdim;q++)
					{
						int n0000=0,n0001=0,n0010=0,n0011=0,n0100=0,n0101=0,n0110=0,n0111=0,n1000=0,n1001=0,n1010=0,n1011=0,n1100=0,n1101=0,n1110=0,n1111=0;
						for(int i=0;i<total;i++)
						{
							if(bentries[i][m]==0&&bentries[i][n]==0&&bentries[i][p]==0&&bentries[i][q]==0)
								n0000++;
							else if(bentries[i][m]==0&&bentries[i][n]==0&&bentries[i][p]==0&&bentries[i][q]==1)
								n0001++;
							else if(bentries[i][m]==0&&bentries[i][n]==0&&bentries[i][p]==1&&bentries[i][q]==0)
								n0010++;
							else if(bentries[i][m]==0&&bentries[i][n]==0&&bentries[i][p]==1&&bentries[i][q]==1)
								n0011++;
							else if(bentries[i][m]==0&&bentries[i][n]==1&&bentries[i][p]==0&&bentries[i][q]==0)
								n0100++;
							else if(bentries[i][m]==0&&bentries[i][n]==1&&bentries[i][p]==0&&bentries[i][q]==1)
								n0101++;
							else if(bentries[i][m]==0&&bentries[i][n]==1&&bentries[i][p]==1&&bentries[i][q]==0)
								n0110++;
							else if(bentries[i][m]==0&&bentries[i][n]==1&&bentries[i][p]==1&&bentries[i][q]==1)
								n0111++;
							else if(bentries[i][m]==1&&bentries[i][n]==0&&bentries[i][p]==0&&bentries[i][q]==0)
								n1000++;
							else if(bentries[i][m]==1&&bentries[i][n]==0&&bentries[i][p]==0&&bentries[i][q]==1)
								n1001++;
							else if(bentries[i][m]==1&&bentries[i][n]==0&&bentries[i][p]==1&&bentries[i][q]==0)
								n1010++;
							else if(bentries[i][m]==1&&bentries[i][n]==0&&bentries[i][p]==1&&bentries[i][q]==1)
								n1011++;
							else if(bentries[i][m]==1&&bentries[i][n]==1&&bentries[i][p]==0&&bentries[i][q]==0)
								n1100++;
							else if(bentries[i][m]==1&&bentries[i][n]==1&&bentries[i][p]==0&&bentries[i][q]==1)
								n1101++;
							else if(bentries[i][m]==1&&bentries[i][n]==1&&bentries[i][p]==1&&bentries[i][q]==0)
								n1110++;
							else
								n1111++;
						}
						writer.write(m+","+n+","+p+","+q+","+n0000+","+n0001+","+n0010+","+n0011+","+n0100+","+n0101+","+n0110+","+n0111+","+n1000+","+n1001+","+n1010+","+n1011+","+n1100+","+n1101+","+n1110+","+n1111+"\n");
					}
				}
			}
		}
		writer.close();
	}
	public int getTotal()
	{
		return total;
	}
}
