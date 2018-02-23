package Data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import Records.Query;
import Site.GlobalNode;
import Site.GlobalTree;

public class DataGenerator {
	private int samplesize;
	private HashMap<Integer,Integer> PSrelationship;
	private HashMap<Integer,Query> PSQuery;
	private HashMap<Integer,HashMap<Integer,GlobalNode>> globaltree;
	private int ceng;
	private int s1size;
	private int s2size;
	private int totalnumber;
	private int[][] synp;
	private int R0;
	private int R1;
	private HashMap<Integer,HashSet<Integer>> gensets;
	private HashMap<Integer,Integer> NodeMap;
	private int dim;
	private int bdim;
	private int[][] bentries;
	private int[][] dentries;
	private Data data;
	private int[] starts;
	private int[] ends;
	private String trainfile;
	public DataGenerator()
	{
		
	}
	public DataGenerator(int samplesize,HashMap<Integer,Integer> PSrelationship,HashMap<Integer,Query> PSQuery)
	{
		this.samplesize = samplesize;
		this.PSrelationship = PSrelationship;
		this.PSQuery = PSQuery;
	}
	public DataGenerator(int samplesize,GlobalTree global,Data data,String trainfile)
	{
		this.samplesize = samplesize;
		this.PSrelationship = global.getPSrelationship();
		this.PSQuery = global.getPSQuery();
		this.globaltree =global.getGlobalTree();
		gensets = global.getgensets();
		s1size = gensets.get(0).size();//实际e二进制变量
		s2size = gensets.get(1).size();//隐变量
		totalnumber = s1size+s2size;
		R0 = global.getR0();
		R1 = global.getR1();
		ceng = global.getCeng();
		this.data = data;
		this.trainfile = trainfile;
	}
	public void generateprocessor() throws IOException
	{
		this.generatedata();
		this.getBEntries();
		this.getDiscretionMatrix();
		this.print2file(dentries,samplesize, dim);
		//this.printmatrix(bentries,samplesize, bdim);
		//this.printmatrix(dentries,samplesize, dim);
	}
	/*A 是子，B是父
	 * if(vecA[i]==0&&vecB[i]==0)
			{
				n00++;
			}
			else if(vecA[i]==0&&vecB[i]==1)
			{
				n01++;
			}
			else if(vecA[i]==1&&vecB[i]==0)
			{
				n10++;
			}
			else
			{
				n11++;
			}
	 */
	public void generatedata() throws IOException
	{
		synp = new int[samplesize][totalnumber];
		for(int m=0;m<samplesize;m++)
		{
			for(int n=0;n<totalnumber;n++)
			{
				synp[m][n]=3;
			}
		}
		//FileWriter writer = new FileWriter(DataName);
		NodeMap = new HashMap<Integer,Integer>();
		int id = 0;
		for(Integer p:gensets.get(0))
		{
			NodeMap.put(p,p-1);
		}
		for(Integer p:gensets.get(1))
		{
			NodeMap.put(p,s1size+id);
			id++;
		}
		for(int i = 0;i<=ceng;i++)
		{
			for(Integer p:globaltree.get(i).keySet())
			{
				int parent = PSrelationship.get(p);
				if(parent==-1)
				{
					int rn = NodeMap.get(p);
					for(int t = 0;t<samplesize;t++)
					{
						int tmp = (int) (Math.random()*(R0+R1));
						if(tmp>R0)
							synp[t][rn] = 1;
						else
							synp[t][rn] = 0;
					}
				}
				else
				{
					int n00 = PSQuery.get(p).p00;
					int n01 = PSQuery.get(p).p01;
					int n10 = PSQuery.get(p).p10;
					int n11 = PSQuery.get(p).p11;
					if((p==PSQuery.get(p).qian)&&(parent==PSQuery.get(p).hou))
					{
						
					}
					else if((p==PSQuery.get(p).hou)&&(parent==PSQuery.get(p).qian))
					{
						n10=PSQuery.get(p).p01;
						n01=PSQuery.get(p).p10;
					}
					else
					{
						System.out.println("very serious error!");
					}
					int rparent = NodeMap.get(parent);
					int rn = NodeMap.get(p);
					for(int t = 0;t<samplesize;t++)
					{
						if(synp[t][rparent]==0)
						{
							int tmp = (int) (Math.random()*(n10+n00));
							if(tmp>=n00)
								synp[t][rn] = 1;
							else
								synp[t][rn] =0;
						}
						else if(synp[t][rparent]==1)
						{
							int tmp = (int) (Math.random()*(n01+n11));
							if(tmp>=n01)
								synp[t][rn] = 1;
							else
								synp[t][rn] =0;
						}
						else
						{
							System.out.println("serious error!");
						}
					}
				}
			}
		}
		for(int i=0;i<s1size;i++)
		{
			int parent = PSrelationship.get(i+1);
			int rparent = NodeMap.get(parent);
			int n00 = PSQuery.get(i+1).p00;
			int n01 = PSQuery.get(i+1).p01;
			int n10 = PSQuery.get(i+1).p10;
			int n11 = PSQuery.get(i+1).p11;
			if(((i+1)==PSQuery.get(i+1).qian)&&(parent==PSQuery.get(i+1).hou))
			{
				
			}
			else if(((i+1)==PSQuery.get(i+1).hou)&&(parent==PSQuery.get(i+1).qian))
			{
				n10=PSQuery.get(i+1).p01;
				n01=PSQuery.get(i+1).p10;
			}
			else
			{
				System.out.println("very serious error!");
			}
			for(int t = 0;t<samplesize;t++)
			{
				if(synp[t][rparent]==0)
				{
					int tmp = (int) (Math.random()*(n10+n00));
					if(tmp>=n00)
						synp[t][i] = 1;
					else
						synp[t][i] =0;
				}
				else if(synp[t][rparent]==1)
				{
					int tmp = (int) (Math.random()*(n01+n11));
					if(tmp>=n01)
						synp[t][i] = 1;
					else
						synp[t][i] =0;
				}
				else
				{
					System.out.println("serious error!");
				}
			}
		}
		//printmatrix();
	}
	public void printmatrix(int[][] matrix,int line,int width)
	{
		for(int m=0;m<line;m++)
		{
			for(int n=0;n<width;n++)
			{
				System.out.print(matrix[m][n]+",");
			}
			System.out.println("\n");
		}
	}
	public void printmatrix(double[][] matrix,int line,int width)
	{
		for(int m=0;m<line;m++)
		{
			for(int n=0;n<width;n++)
			{
				System.out.print(matrix[m][n]+",");
			}
			System.out.println("\n");
		}
	}
	public void getBEntries()
	{
		bdim = data.getBDim();
		if(bdim!=s1size)
		{
			System.out.println("generating error");
			System.exit(-1);
		}
		bentries = new int[samplesize][bdim];
		for(int i=0;i<samplesize;i++)
		{
			for(int t=0;t<bdim;t++)
			{
				bentries[i][t] = synp[i][t];
			}
		}
	}
	public void getDiscretionMatrix()
	{
		dim = data.getDim();
		dentries = new int[samplesize][dim];
		starts  = data.getStarts();
		ends = data.getEnds();
		for(int i=0;i<samplesize;i++)
		{
			for(int t=0;t<dim;t++)
			{
				int sum =0 ;
				int tmp=1;
				for(int p = ends[t];p>=starts[t];p--)
				{
					sum+=tmp*bentries[i][p];
					tmp=tmp<<1;
				}
				dentries[i][t] = sum;
			}
		}
	}
	public void print2file(int[][] matrix,int line,int width) throws IOException
	{
		FileWriter writercat = new FileWriter(trainfile);
		for(int m=0;m<line;m++)
		{
			for(int n=0;n<width-1;n++)
			{
				writercat.write(matrix[m][n]+",");
			}
			writercat.write(matrix[m][width-1]+"\n");
		}
		writercat.close();
	}
	public void print2file(double[][] matrix,int line,int width) throws IOException
	{
		java.text.DecimalFormat   df   =new   java.text.DecimalFormat("#.00"); 
		FileWriter writercat = new FileWriter(trainfile);
		for(int m=0;m<line;m++)
		{
			for(int n=0;n<width-1;n++)
			{
				writercat.write(df.format(matrix[m][n])+",");
			}
			writercat.write(df.format(matrix[m][width-1])+"\n");
		}
		writercat.close();
	}
	public void print2svmfile(int[][] matrix,int line,int width)
	{
		
	}
	public void print2svmfile(double[][] matrix,int line,int width)
	{
		
	}
	public void calmar2distance(String mar2file,String resultfile) throws IOException
	{
		FileWriter writer = new FileWriter(resultfile,true);
		BufferedReader mar2 = new BufferedReader(new FileReader(mar2file));
		int num = Integer.parseInt(mar2.readLine());
		double sum = 0.0;
		for(int i=0;i<num;i++)
		{
			//writer.write(m+","+n+","+n00+","+n01+","+n10+","+n11+"\n");
			String[] ss = mar2.readLine().split(",");
			int m = Integer.parseInt(ss[0]);
			int n = Integer.parseInt(ss[1]);
			int n00 = Integer.parseInt(ss[2]);
			int n01 = Integer.parseInt(ss[3]);
			int n10 = Integer.parseInt(ss[4]);
			int n11 = Integer.parseInt(ss[5]);
			int sn00=0,sn01=0,sn10=0,sn11=0;
			for(int p=0;p<samplesize;p++)
			{
				if(bentries[p][m]==0&&bentries[p][n]==0)
					sn00++;
				else if(bentries[p][m]==0&&bentries[p][n]==1)
					sn01++;
				else if(bentries[p][m]==1&&bentries[p][n]==0)
					sn10++;
				else
					sn11++;
			}
			sum+=Math.abs(n00-sn00)/(samplesize*1.0);
			sum+=Math.abs(n01-sn01)/(samplesize*1.0);
			sum+=Math.abs(n10-sn10)/(samplesize*1.0);
			sum+=Math.abs(n11-sn11)/(samplesize*1.0);
		}
		sum=sum/(2.0*num);
		writer.write(sum+"\n");
		mar2.close();
		writer.close();
	}
	public void calmar3distance(String mar3file,String resultfile) throws IOException
	{
		FileWriter writer = new FileWriter(resultfile,true);
		BufferedReader mar3 = new BufferedReader(new FileReader(mar3file));
		int num = Integer.parseInt(mar3.readLine());
		double sum = 0.0;
		for(int i=0;i<num;i++)
		{
			//writer.write(m+","+n+","+p+","+n000+","+n001+","+n010+","+n011+","+n100+","+n101+","+n110+","+n111+"\n");
			String[] ss = mar3.readLine().split(",");
			int m = Integer.parseInt(ss[0]);
			int n = Integer.parseInt(ss[1]);
			int p = Integer.parseInt(ss[2]);
			int n000 = Integer.parseInt(ss[3]);
			int n001 = Integer.parseInt(ss[4]);
			int n010 = Integer.parseInt(ss[5]);
			int n011 = Integer.parseInt(ss[6]);
			int n100 = Integer.parseInt(ss[7]);
			int n101 = Integer.parseInt(ss[8]);
			int n110 = Integer.parseInt(ss[9]);
			int n111 = Integer.parseInt(ss[10]);
			int sn000=0,sn001=0,sn010=0,sn011=0,sn100=0,sn101=0,sn110=0,sn111=0;
			for(int x=0;x<samplesize;x++)
			{
				if(bentries[x][m]==0&&bentries[x][n]==0&&bentries[x][p]==0)
					sn000++;
				else if(bentries[x][m]==0&&bentries[x][n]==0&&bentries[x][p]==1)
					sn001++;
				else if(bentries[x][m]==0&&bentries[x][n]==1&&bentries[x][p]==0)
					sn010++;
				else if(bentries[x][m]==0&&bentries[x][n]==1&&bentries[x][p]==1)
					sn011++;
				else if(bentries[x][m]==1&&bentries[x][n]==0&&bentries[x][p]==0)
					sn100++;
				else if(bentries[x][m]==1&&bentries[x][n]==0&&bentries[x][p]==1)
					sn101++;
				else if(bentries[x][m]==1&&bentries[x][n]==1&&bentries[x][p]==0)
					sn110++;
				else
					sn111++;
			}
			sum+=Math.abs(n000-sn000)/(samplesize*1.0);
			sum+=Math.abs(n001-sn001)/(samplesize*1.0);
			sum+=Math.abs(n010-sn010)/(samplesize*1.0);
			sum+=Math.abs(n011-sn011)/(samplesize*1.0);
			sum+=Math.abs(n100-sn100)/(samplesize*1.0);
			sum+=Math.abs(n101-sn101)/(samplesize*1.0);
			sum+=Math.abs(n110-sn110)/(samplesize*1.0);
			sum+=Math.abs(n111-sn111)/(samplesize*1.0);
		}
		sum=sum/(2.0*num);
		writer.write(sum+"\n");
		mar3.close();
		writer.close();
	}
	public void calmar4distance(String mar4file,String resultfile) throws IOException
	{
		FileWriter writer = new FileWriter(resultfile,true);
		BufferedReader mar4 = new BufferedReader(new FileReader(mar4file));
		int num = Integer.parseInt(mar4.readLine());
		double sum = 0.0;
		for(int i=0;i<num;i++)
		{
//writer.write(m+","+n+","+p+","+q+","+n0000+","+n0001+","+n0010+","+n0011+","+n0100+","+n0101+","+n0110+","+n0111+","+n1000+","+n1001+","+n1010+","+n1011+","+n1100+","+n1101+","+n1110+","+n1111+"\n");
			String[] ss = mar4.readLine().split(",");
			int m = Integer.parseInt(ss[0]);
			int n = Integer.parseInt(ss[1]);
			int p = Integer.parseInt(ss[2]);
			int q = Integer.parseInt(ss[3]);
			int n0000 = Integer.parseInt(ss[4]);
			int n0001 = Integer.parseInt(ss[5]);
			int n0010 = Integer.parseInt(ss[6]);
			int n0011 = Integer.parseInt(ss[7]);
			int n0100 = Integer.parseInt(ss[8]);
			int n0101 = Integer.parseInt(ss[9]);
			int n0110 = Integer.parseInt(ss[10]);
			int n0111 = Integer.parseInt(ss[11]);
			int n1000 = Integer.parseInt(ss[12]);
			int n1001 = Integer.parseInt(ss[13]);
			int n1010 = Integer.parseInt(ss[14]);
			int n1011 = Integer.parseInt(ss[15]);
			int n1100 = Integer.parseInt(ss[16]);
			int n1101 = Integer.parseInt(ss[17]);
			int n1110 = Integer.parseInt(ss[18]);
			int n1111 = Integer.parseInt(ss[19]);
			int sn0000=0,sn0001=0,sn0010=0,sn0011=0,sn0100=0,sn0101=0,sn0110=0,sn0111=0,sn1000=0,sn1001=0,sn1010=0,sn1011=0,sn1100=0,sn1101=0,sn1110=0,sn1111=0;
			for(int x=0;x<samplesize;x++)
			{
				if(bentries[x][m]==0&&bentries[x][n]==0&&bentries[x][p]==0&&bentries[x][q]==0)
					sn0000++;
				else if(bentries[x][m]==0&&bentries[x][n]==0&&bentries[x][p]==0&&bentries[x][q]==1)
					sn0001++;
				else if(bentries[x][m]==0&&bentries[x][n]==0&&bentries[x][p]==1&&bentries[x][q]==0)
					sn0010++;
				else if(bentries[x][m]==0&&bentries[x][n]==0&&bentries[x][p]==1&&bentries[x][q]==1)
					sn0011++;
				else if(bentries[x][m]==0&&bentries[x][n]==1&&bentries[x][p]==0&&bentries[x][q]==0)
					sn0100++;
				else if(bentries[x][m]==0&&bentries[x][n]==1&&bentries[x][p]==0&&bentries[x][q]==1)
					sn0101++;
				else if(bentries[x][m]==0&&bentries[x][n]==1&&bentries[x][p]==1&&bentries[x][q]==0)
					sn0110++;
				else if(bentries[x][m]==0&&bentries[x][n]==1&&bentries[x][p]==1&&bentries[x][q]==1)
					sn0111++;
				else if(bentries[x][m]==1&&bentries[x][n]==0&&bentries[x][p]==0&&bentries[x][q]==0)
					sn1000++;
				else if(bentries[x][m]==1&&bentries[x][n]==0&&bentries[x][p]==0&&bentries[x][q]==1)
					sn1001++;
				else if(bentries[x][m]==1&&bentries[x][n]==0&&bentries[x][p]==1&&bentries[x][q]==0)
					sn1010++;
				else if(bentries[x][m]==1&&bentries[x][n]==0&&bentries[x][p]==1&&bentries[x][q]==1)
					sn1011++;
				else if(bentries[x][m]==1&&bentries[x][n]==1&&bentries[x][p]==0&&bentries[x][q]==0)
					sn1100++;
				else if(bentries[x][m]==1&&bentries[x][n]==1&&bentries[x][p]==0&&bentries[x][q]==1)
					sn1101++;
				else if(bentries[x][m]==1&&bentries[x][n]==1&&bentries[x][p]==1&&bentries[x][q]==0)
					sn1110++;
				else
					sn1111++;
			}
			sum+=Math.abs(n0000-sn0000)/(samplesize*1.0);
			sum+=Math.abs(n0001-sn0001)/(samplesize*1.0);
			sum+=Math.abs(n0010-sn0010)/(samplesize*1.0);
			sum+=Math.abs(n0011-sn0011)/(samplesize*1.0);
			sum+=Math.abs(n0100-sn0100)/(samplesize*1.0);
			sum+=Math.abs(n0101-sn0101)/(samplesize*1.0);
			sum+=Math.abs(n0110-sn0110)/(samplesize*1.0);
			sum+=Math.abs(n0111-sn0111)/(samplesize*1.0);
			sum+=Math.abs(n1000-sn1000)/(samplesize*1.0);
			sum+=Math.abs(n1001-sn1001)/(samplesize*1.0);
			sum+=Math.abs(n1010-sn1010)/(samplesize*1.0);
			sum+=Math.abs(n1011-sn1011)/(samplesize*1.0);
			sum+=Math.abs(n1100-sn1100)/(samplesize*1.0);
			sum+=Math.abs(n1101-sn1101)/(samplesize*1.0);
			sum+=Math.abs(n1110-sn1110)/(samplesize*1.0);
			sum+=Math.abs(n1111-sn1111)/(samplesize*1.0);
		}
		sum=sum/(2.0*num);
		writer.write(sum+"\n");
		mar4.close();
		writer.close();
	}
	public void calmar2distance(String mar2file,String resultfile,int t) throws IOException
	{
		FileWriter writer = new FileWriter(resultfile,true);
		FileWriter writert = new FileWriter("adultsyn"+t+".mar2",true);
		BufferedReader mar2 = new BufferedReader(new FileReader(mar2file));
		int num = Integer.parseInt(mar2.readLine());
		double sum = 0.0;
		for(int i=0;i<num;i++)
		{
			//writer.write(m+","+n+","+n00+","+n01+","+n10+","+n11+"\n");
			String[] ss = mar2.readLine().split(",");
			int m = Integer.parseInt(ss[0]);
			int n = Integer.parseInt(ss[1]);
			int n00 = Integer.parseInt(ss[2]);
			int n01 = Integer.parseInt(ss[3]);
			int n10 = Integer.parseInt(ss[4]);
			int n11 = Integer.parseInt(ss[5]);
			int sn00=0,sn01=0,sn10=0,sn11=0;
			for(int p=0;p<samplesize;p++)
			{
				if(bentries[p][m]==0&&bentries[p][n]==0)
					sn00++;
				else if(bentries[p][m]==0&&bentries[p][n]==1)
					sn01++;
				else if(bentries[p][m]==1&&bentries[p][n]==0)
					sn10++;
				else
					sn11++;
			}
			writert.write(m+","+n+","+sn00+","+sn01+","+sn10+","+sn11+"\n");
			sum+=Math.abs(n00-sn00)/(samplesize*1.0);
			sum+=Math.abs(n01-sn01)/(samplesize*1.0);
			sum+=Math.abs(n10-sn10)/(samplesize*1.0);
			sum+=Math.abs(n11-sn11)/(samplesize*1.0);
		}
		sum=sum/(2.0*num);
		writer.write(sum+"\n");
		mar2.close();
		writer.close();
		writert.close();
	}
	public void calmar3distance(String mar3file,String resultfile,int t) throws IOException
	{
		FileWriter writer = new FileWriter(resultfile,true);
		BufferedReader mar3 = new BufferedReader(new FileReader(mar3file));
		FileWriter writert = new FileWriter("adultsyn"+t+".mar3",true);
		int num = Integer.parseInt(mar3.readLine());
		double sum = 0.0;
		for(int i=0;i<num;i++)
		{
			//writer.write(m+","+n+","+p+","+n000+","+n001+","+n010+","+n011+","+n100+","+n101+","+n110+","+n111+"\n");
			String[] ss = mar3.readLine().split(",");
			int m = Integer.parseInt(ss[0]);
			int n = Integer.parseInt(ss[1]);
			int p = Integer.parseInt(ss[2]);
			int n000 = Integer.parseInt(ss[3]);
			int n001 = Integer.parseInt(ss[4]);
			int n010 = Integer.parseInt(ss[5]);
			int n011 = Integer.parseInt(ss[6]);
			int n100 = Integer.parseInt(ss[7]);
			int n101 = Integer.parseInt(ss[8]);
			int n110 = Integer.parseInt(ss[9]);
			int n111 = Integer.parseInt(ss[10]);
			int sn000=0,sn001=0,sn010=0,sn011=0,sn100=0,sn101=0,sn110=0,sn111=0;
			for(int x=0;x<samplesize;x++)
			{
				if(bentries[x][m]==0&&bentries[x][n]==0&&bentries[x][p]==0)
					sn000++;
				else if(bentries[x][m]==0&&bentries[x][n]==0&&bentries[x][p]==1)
					sn001++;
				else if(bentries[x][m]==0&&bentries[x][n]==1&&bentries[x][p]==0)
					sn010++;
				else if(bentries[x][m]==0&&bentries[x][n]==1&&bentries[x][p]==1)
					sn011++;
				else if(bentries[x][m]==1&&bentries[x][n]==0&&bentries[x][p]==0)
					sn100++;
				else if(bentries[x][m]==1&&bentries[x][n]==0&&bentries[x][p]==1)
					sn101++;
				else if(bentries[x][m]==1&&bentries[x][n]==1&&bentries[x][p]==0)
					sn110++;
				else
					sn111++;
			}
			writert.write(m+","+n+","+p+","+sn000+","+sn001+","+sn010+","+sn011+","+sn100+","+sn101+","+sn110+","+sn111+"\n");
			sum+=Math.abs(n000-sn000)/(samplesize*1.0);
			sum+=Math.abs(n001-sn001)/(samplesize*1.0);
			sum+=Math.abs(n010-sn010)/(samplesize*1.0);
			sum+=Math.abs(n011-sn011)/(samplesize*1.0);
			sum+=Math.abs(n100-sn100)/(samplesize*1.0);
			sum+=Math.abs(n101-sn101)/(samplesize*1.0);
			sum+=Math.abs(n110-sn110)/(samplesize*1.0);
			sum+=Math.abs(n111-sn111)/(samplesize*1.0);
		}
		sum=sum/(2.0*num);
		writer.write(sum+"\n");
		mar3.close();
		writer.close();
		writert.close();
	}
	public void calmar4distance(String mar4file,String resultfile,int t) throws IOException
	{
		FileWriter writer = new FileWriter(resultfile,true);
		BufferedReader mar4 = new BufferedReader(new FileReader(mar4file));
		FileWriter writert = new FileWriter("adultsyn"+t+".mar4",true);
		int num = Integer.parseInt(mar4.readLine());
		double sum = 0.0;
		for(int i=0;i<num;i++)
		{
//writer.write(m+","+n+","+p+","+q+","+n0000+","+n0001+","+n0010+","+n0011+","+n0100+","+n0101+","+n0110+","+n0111+","+n1000+","+n1001+","+n1010+","+n1011+","+n1100+","+n1101+","+n1110+","+n1111+"\n");
			String[] ss = mar4.readLine().split(",");
			int m = Integer.parseInt(ss[0]);
			int n = Integer.parseInt(ss[1]);
			int p = Integer.parseInt(ss[2]);
			int q = Integer.parseInt(ss[3]);
			int n0000 = Integer.parseInt(ss[4]);
			int n0001 = Integer.parseInt(ss[5]);
			int n0010 = Integer.parseInt(ss[6]);
			int n0011 = Integer.parseInt(ss[7]);
			int n0100 = Integer.parseInt(ss[8]);
			int n0101 = Integer.parseInt(ss[9]);
			int n0110 = Integer.parseInt(ss[10]);
			int n0111 = Integer.parseInt(ss[11]);
			int n1000 = Integer.parseInt(ss[12]);
			int n1001 = Integer.parseInt(ss[13]);
			int n1010 = Integer.parseInt(ss[14]);
			int n1011 = Integer.parseInt(ss[15]);
			int n1100 = Integer.parseInt(ss[16]);
			int n1101 = Integer.parseInt(ss[17]);
			int n1110 = Integer.parseInt(ss[18]);
			int n1111 = Integer.parseInt(ss[19]);
			int sn0000=0,sn0001=0,sn0010=0,sn0011=0,sn0100=0,sn0101=0,sn0110=0,sn0111=0,sn1000=0,sn1001=0,sn1010=0,sn1011=0,sn1100=0,sn1101=0,sn1110=0,sn1111=0;
			for(int x=0;x<samplesize;x++)
			{
				if(bentries[x][m]==0&&bentries[x][n]==0&&bentries[x][p]==0&&bentries[x][q]==0)
					sn0000++;
				else if(bentries[x][m]==0&&bentries[x][n]==0&&bentries[x][p]==0&&bentries[x][q]==1)
					sn0001++;
				else if(bentries[x][m]==0&&bentries[x][n]==0&&bentries[x][p]==1&&bentries[x][q]==0)
					sn0010++;
				else if(bentries[x][m]==0&&bentries[x][n]==0&&bentries[x][p]==1&&bentries[x][q]==1)
					sn0011++;
				else if(bentries[x][m]==0&&bentries[x][n]==1&&bentries[x][p]==0&&bentries[x][q]==0)
					sn0100++;
				else if(bentries[x][m]==0&&bentries[x][n]==1&&bentries[x][p]==0&&bentries[x][q]==1)
					sn0101++;
				else if(bentries[x][m]==0&&bentries[x][n]==1&&bentries[x][p]==1&&bentries[x][q]==0)
					sn0110++;
				else if(bentries[x][m]==0&&bentries[x][n]==1&&bentries[x][p]==1&&bentries[x][q]==1)
					sn0111++;
				else if(bentries[x][m]==1&&bentries[x][n]==0&&bentries[x][p]==0&&bentries[x][q]==0)
					sn1000++;
				else if(bentries[x][m]==1&&bentries[x][n]==0&&bentries[x][p]==0&&bentries[x][q]==1)
					sn1001++;
				else if(bentries[x][m]==1&&bentries[x][n]==0&&bentries[x][p]==1&&bentries[x][q]==0)
					sn1010++;
				else if(bentries[x][m]==1&&bentries[x][n]==0&&bentries[x][p]==1&&bentries[x][q]==1)
					sn1011++;
				else if(bentries[x][m]==1&&bentries[x][n]==1&&bentries[x][p]==0&&bentries[x][q]==0)
					sn1100++;
				else if(bentries[x][m]==1&&bentries[x][n]==1&&bentries[x][p]==0&&bentries[x][q]==1)
					sn1101++;
				else if(bentries[x][m]==1&&bentries[x][n]==1&&bentries[x][p]==1&&bentries[x][q]==0)
					sn1110++;
				else
					sn1111++;
			}
			writert.write(m+","+n+","+p+","+q+","+sn0000+","+sn0001+","+sn0010+","+sn0011+","+sn0100+","+sn0101+","+sn0110+","+sn0111+","+sn1000+","+sn1001+","+sn1010+","+sn1011+","+sn1100+","+sn1101+","+sn1110+","+sn1111+"\n");
			sum+=Math.abs(n0000-sn0000)/(samplesize*1.0);
			sum+=Math.abs(n0001-sn0001)/(samplesize*1.0);
			sum+=Math.abs(n0010-sn0010)/(samplesize*1.0);
			sum+=Math.abs(n0011-sn0011)/(samplesize*1.0);
			sum+=Math.abs(n0100-sn0100)/(samplesize*1.0);
			sum+=Math.abs(n0101-sn0101)/(samplesize*1.0);
			sum+=Math.abs(n0110-sn0110)/(samplesize*1.0);
			sum+=Math.abs(n0111-sn0111)/(samplesize*1.0);
			sum+=Math.abs(n1000-sn1000)/(samplesize*1.0);
			sum+=Math.abs(n1001-sn1001)/(samplesize*1.0);
			sum+=Math.abs(n1010-sn1010)/(samplesize*1.0);
			sum+=Math.abs(n1011-sn1011)/(samplesize*1.0);
			sum+=Math.abs(n1100-sn1100)/(samplesize*1.0);
			sum+=Math.abs(n1101-sn1101)/(samplesize*1.0);
			sum+=Math.abs(n1110-sn1110)/(samplesize*1.0);
			sum+=Math.abs(n1111-sn1111)/(samplesize*1.0);
		}
		sum=sum/(2.0*num);
		writer.write(sum+"\n");
		mar4.close();
		writer.close();
		writert.close();
	}
}
