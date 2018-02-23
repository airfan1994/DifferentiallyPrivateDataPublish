package Site;

import java.io.FileWriter;
import java.io.FilterWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import Corex.Corex;
import Records.MutualInfo;
import Records.Pair;
import Records.Query;

public class GlobalTree {
	private HashMap<Integer,TreeSite> DataSet;
	private MutualInfo mutualinfo;
	private HashMap<Pair,Query> mutualquery;
	private int MaxidNode;
	private int k ;
	private HashMap<Integer,HashSet<Integer>> matches;      //只在refine内部用，最大树什么的就用不着了;
	private int tol;
	private double[][] ConnectionMatrix;
	private HashMap<Integer,Integer> NodeMap;
	private HashMap<Integer,Integer> NodeMapS;
	private int nodenumber;
	private HashSet<Pair> hpairs;
	private HashMap<Integer,Integer> nodeceng;
	private int Root;
	private HashMap<Integer,HashMap<Integer,GlobalNode>> globaltree;
	private int ceng;
	private HashMap<Integer,Integer> PSrelationship;
	private HashMap<Integer,Query> PSQuery;
	private int R0;
	private int R1;
	private double epsilon;
	private double epsilonleft;
	private double epsilon1;
	private double epsilon2;
	private double epsilon3;
	private HashMap<Integer,HashSet<Integer>> gu;
	private int max_iter;
	public GlobalTree()
	{
		
	}
	public GlobalTree(HashMap<Integer,TreeSite> DataSet,MutualInfo mutualinfo,int MaxidNode,int k,double epsilon,HashMap<Pair,Query> mutualquery,int max_it)
	{
		this.DataSet = DataSet;
		this.mutualinfo = mutualinfo;
		this.MaxidNode = MaxidNode;
		this.mutualquery = mutualquery;
		this.k=k;
		tol = DataSet.size();
		this.epsilon = epsilon;
		this.epsilon1 = epsilon/3.0;
		this.epsilon2 = epsilon/3.0;
		this.epsilon3 = epsilon/3.0;
		this.epsilonleft = 0.0;
		this.max_iter=max_it;
	}
	public void setall() throws IOException
	{
		System.out.println("Starting constructing local trees for every site");
		setLocalTrees();
		System.out.println("Local trees for every site constructed.");
		writelocaltree();
		System.out.println("Starting Matching local trees");
		TreeMatches();
		System.out.println("Local trees matched.");
		System.out.println("Refinement starts");
		refine();
		System.out.println("Refinement dinished.");
		System.out.println("Starting building ConnectionMatrix");
		setConnectionMatrix();
		System.out.println("ConnectionMatrix builded.");
		System.out.println("Starting getting the globaltree");
		setDirection();
		System.out.println("Global tree builded.");
	}
	public void sete() throws IOException
	{
		FileWriter wwwr = new FileWriter("adulte0407");
		TreeSite site;
		HashMap<Integer,Node> Layer;
		Layer = DataSet.get(2).getFloorNodes();
		Groups gr = new Groups(Layer,3,mutualinfo,0.0,mutualquery,1);
		for(int i=27;i<=40;i++)
		{
			wwwr.write(1+"             "+i+"                "+calEntropy(this.getNodeById(1).getNode(),this.getNodeById(i).getNode(),0.0)+"\n");
		}
		gr.setDroups();
		Corex corex = new Corex(gr,2,max_iter);
		corex.fit(0);
		DataSet.get(2).addHiddenLayer(corex.getHiddenNodes(),gr,MaxidNode);
		wwwr.write("\n\n");
		wwwr.write(1+"             "+41+"                "+calEntropy(this.getNodeById(1).getNode(),this.getNodeById(41).getNode(),0.0)+"\n");
		wwwr.write(1+"             "+42+"                "+calEntropy(this.getNodeById(1).getNode(),this.getNodeById(42).getNode(),0.0)+"\n");
		wwwr.write(1+"             "+43+"                "+calEntropy(this.getNodeById(1).getNode(),this.getNodeById(43).getNode(),0.0)+"\n");
		wwwr.write(1+"             "+44+"                "+calEntropy(this.getNodeById(1).getNode(),this.getNodeById(44).getNode(),0.0)+"\n");
		wwwr.write(1+"             "+45+"                "+calEntropy(this.getNodeById(1).getNode(),this.getNodeById(45).getNode(),0.0)+"\n");
		wwwr.close();
	}
	public void setLocalTrees() throws IOException
	{
		TreeSite site;
		HashMap<Integer,Node> Layer;
		for(Integer p:DataSet.keySet())
		{
			site= DataSet.get(p);
			Layer = site.getFloorNodes();
			int count  = 0;
			int size = Layer.size();
			while(size>k)
			{
				size = size/k;
				count++;
			}
			while(Layer.size()>k){
				Groups gr = new Groups(Layer,k,mutualinfo,epsilon1/(count*1.0),mutualquery,1);
				gr.setDroups();
				Corex corex = new Corex(gr,2,max_iter);
				corex.fit(0);
				site.addHiddenLayer(corex.getHiddenNodes(),gr,MaxidNode);
				MaxidNode += corex.getNHidden();
				Layer = site.gettmpLayer();
			}
			if(Layer.size()==1)
			{
				site.setfloorNodeRoot();
			}
			else
			{
				Groups gr = new Groups(Layer,k,mutualinfo,epsilon1/(count*1.0),mutualquery,1);
				gr.setDroups();
				Corex corex = new Corex(gr,2,max_iter);
				corex.fit(0);
				site.addHiddenLayer(corex.getHiddenNodes(),gr,MaxidNode);
				site.setfloorNodeRoot();
				MaxidNode += corex.getNHidden();
			}
		}
	}
	public void writelocaltree() throws IOException
	{
		FileWriter writer = new FileWriter("processlocaltree.txt",true);
		for(Integer p:DataSet.keySet())
		{
			writer.write("start writing local tree for site "+p+":\n");
			for(int i=DataSet.get(p).getMaxFloorID();i>=0;i--)
			{
				writer.write("floor "+i+":\n");
				for(Integer q:DataSet.get(p).getFloorNode(i).keySet())
				{
					if(i!=0){
						writer.write(q+"(");
						int a=0;
					for(Integer m:DataSet.get(p).getFloorNode(i).get(q).getChild())
					{	writer.write(m+",");a++;}
					
					writer.write("),");
					for(int t=25;t>=(3*a+2);t--)
						writer.write(" ");
					}
				}
				writer.write("\n");
			}
			writer.write("\n");
		}
		writer.write("\n\n\n");
		writer.close();
	}
	public void TreeMatches() throws IOException
	{
		//FileWriter writer = new FileWriter("processmatch.txt",true);
		//FileWriter writer1 = new FileWriter("processregu.txt",true);
		//writer.write("starting writing matches for sites\n");
		matches = new HashMap<Integer,HashSet<Integer>>();
		double epsilonsite = epsilon2*2.0/(tol*(tol-1)); 
		gu = new HashMap<Integer,HashSet<Integer>>();
		for(int m=1;m<tol;m++)
			for(int n=m+1;n<=tol;n++)
			{
				int fA = DataSet.get(m).getMaxFloorID()-1;
				int fB = DataSet.get(n).getMaxFloorID()-1;
				double epsilons2s;
				if(fA>fB)
				{
					epsilons2s = epsilonsite/(fA*1.0); 
				}
				else
				{
					epsilons2s = epsilonsite/(fB*1.0); 
				}
				double epsilonp2 = 0.0;
				int times = 0;
				HashMap<Integer,HashSet<Integer>> fanweiA = new HashMap<Integer,HashSet<Integer>>();//每一层都有几堆点;
				HashMap<Integer,HashSet<Integer>> fanweiB = new HashMap<Integer,HashSet<Integer>>();
				HashMap<Integer,Integer> S = new HashMap<Integer,Integer>();//匹配的只加入一次 ,匹配点集
				HashSet<Integer> V ;//每次范围剩余点集，有配对的就remove,最后剩孤点；
				HashSet<Integer> T ; //全局剩点，每层；
				HashSet<Integer> tA = new HashSet<Integer>();
				HashSet<Integer> tB = new HashSet<Integer>();
				for(Integer p:DataSet.get(m).getFloorNode(fA).keySet())
				{
					tA.add(p);
				}
				for(Integer p:DataSet.get(n).getFloorNode(fB).keySet())
				{
					tB.add(p);
				}
				fanweiA.put(1, tA);
				fanweiB.put(1, tB);
				//times = tA.size()*tB.size();
				int id=2;
				while(true)
				{
				//	if(fa)
					times=0;
					for(Integer p:fanweiA.keySet())
					{
						times+=fanweiA.get(p).size()*fanweiB.get(p).size();
					}
					epsilonp2 = epsilons2s/times;
					S = new HashMap<Integer,Integer>();
					T = new HashSet<Integer>();
					for(Integer p:fanweiA.keySet())    //就对这一层运算；
					{
						V= new HashSet<Integer>();
						for(Integer y:fanweiA.get(p))
						{
							V.add(y);
						}
						for(Integer y:fanweiB.get(p))
						{
							V.add(y);
						}
						HashSet<Integer> ttA = fanweiA.get(p);
						HashSet<Integer> ttB = fanweiB.get(p);
						//if(ttA.size()>ttB.size())
						//{
						//在范围内部以q为基准穷举配对
							for(Integer q:ttA)
							{
								int maxid = 0;
								double max=0.0;
								for(Integer x:ttB)
								{
									Node NodeA = DataSet.get(m).getFloorNode(fA).get(q).getNode();
									Node NodeB = DataSet.get(n).getFloorNode(fB).get(x).getNode();
									double en = calEntropy(NodeA,NodeB,epsilonp2);
									if(en>max||max==0.0)
									{
										max=en;
										maxid = x;
										
									}
									if(en<=0)
									{
										System.out.println("sError");
									}
								}
								if(maxid==0)
								{
									System.out.println("vError");
								}
								S.put(q, maxid);
								if(matches.get(q)!=null)
								{
									matches.get(q).add(maxid);
								}
								else
								{
									HashSet<Integer> ksets = new HashSet<Integer>();
									ksets.add(maxid);
									matches.put(q, ksets);
								}
								//writer.write(q+" matches "+maxid+"\n");
								V.remove(q);
								V.remove(maxid);
							}
							for(Integer z:V)
							{
								T.add(z);
							}
						//}
						/*else
						{
							for(Integer q:ttB)
							{
								int maxid = 0;
								double max=0.0;
								for(Integer x:ttA)
								{
									Node NodeB = DataSet.get(m).getFloorNode(fA).get(q).getNode();
									Node NodeA = DataSet.get(m).getFloorNode(fA).get(q).getNode();
									double en = calEntropy(NodeA,NodeB);
									if(calEntropy(NodeA,NodeB)>max)
									{
										max=en;
										maxid = x;
									}
								}
								S.put(q, maxid);
								V.remove(q);
								V.remove(maxid);
							}
							for(Integer z:V)
							{
								T.add(z);
							}
						}*/
					}
					fanweiA = new HashMap<Integer,HashSet<Integer>>();
					fanweiB = new HashMap<Integer,HashSet<Integer>>();
					if(fA!=1&&fB!=1)
					{
						for(Integer p:S.keySet())
						{
							System.out.println("p:"+p);
							System.out.println("p match:"+S.get(p));
							HashSet<Integer> childsetA= DataSet.get(m).getFloorNode(fA).get(p).getChild();
							fanweiA.put(id,childsetA);
							HashSet<Integer> childsetB= DataSet.get(n).getFloorNode(fB).get(S.get(p)).getChild();
							//一定记住加的是孤点的子节点
							if(T.size()!=0){
								for(Integer q:T)
								{
									for(Integer d:DataSet.get(n).getFloorNode(fB).get(q).getChild())
										childsetB.add(d);
								}
							}
							fanweiB.put(id,childsetB);
							id++;
						}
						fA--;
						fB--;
					}
					else if(fA==1&&fB!=1)
					{
						for(Integer p:S.keySet())
						{
							//HashSet<Integer> childsetA= DataSet.get(m).getFloorNode(fA).get(p).getChild();
							//fanweiA,fanweiB在这里加的每一个点都必须是子节点，本能是本层节点;除非本层不下降
							System.out.println("p:"+p);
							System.out.println("p match:"+S.get(p));
							HashSet<Integer> childsetA = new HashSet<Integer>();
							childsetA.add(p);
							fanweiA.put(id,childsetA);
							HashSet<Integer> childsetB= DataSet.get(n).getFloorNode(fB).get(S.get(p)).getChild();
							//一定记住加的是孤点的子节点
							
							if(T.size()!=0){
								for(Integer q:T)
								{
									for(Integer d:DataSet.get(n).getFloorNode(fB).get(q).getChild())
										childsetB.add(d);
								}
							}
							fanweiB.put(id,childsetB);
							id++;
						}
						fB--;
					}
					else if(fA!=1&&fB==1)
					{
						for(Integer p:S.keySet())
						{
							//HashSet<Integer> childsetA= DataSet.get(m).getFloorNode(fA).get(p).getChild();
							System.out.println("p:"+p);
							System.out.println("p match:"+S.get(p));
							HashSet<Integer> childsetA= DataSet.get(m).getFloorNode(fA).get(p).getChild();
							fanweiA.put(id,childsetA);
							HashSet<Integer> childsetB= new HashSet<Integer>();
							childsetB.add(S.get(p));//很对的
							if(T.size()!=0){
								for(Integer q:T)
								{
									//for(Integer d:DataSet.get(n).getFloorNode(fB).get(q).getChild())
										childsetB.add(q);
								}
							}
							fanweiB.put(id,childsetB);
							id++;
						}
						fA--;
					}
					else
					{
						HashSet<Integer> hgu = new HashSet<Integer>(T);
						gu.put(n, T);
						/*if(T.size()!=0){
							for(Integer q:T)
							{
								//for(Integer d:DataSet.get(n).getFloorNode(fB).get(q).getChild())
								for(Integer d:DataSet.get(n).getFloorNode(fB).get(q).getChild())
									writer1.write(d+"\n");
							}
						}*/
						break;
					}
				}/*
				for(Integer k:S.keySet())
				{
					if(matches.get(k)!=null)
					{
						matches.get(k).add(S.get(k));
					}
					else
					{
						HashSet<Integer> ksets = new HashSet<Integer>();
						ksets.add(S.get(k));
						matches.put(k, ksets);
					}
					
				}*/
			}
		//writer.close();
		//writer1.close();
	}
	public void refine() throws IOException
	{
		//FileWriter writer = new FileWriter("processrefine.txt",true);
		//FileWriter writer = new FileWriter("processrematch.txt",true);
		HashMap<Pair,Integer> matchlabel = new HashMap<Pair,Integer>();
		int N = 0;
		for(Integer p:matches.keySet())
		{
			for(Integer q:matches.get(p))
			{
				Pair pair  = new Pair(p,q);
				matchlabel.put(pair, 0);
				N++;
			}
		}
		int M=0;
		for(Integer p:gu.keySet())
		{
			M+=gu.get(p).size();
		}
		double epsilonp3 = epsilon3/(N*4.0*k+M*1.0*k);
		for(Integer p:gu.keySet())
		{
			for(Integer q:gu.get(p))
			{
				for(Integer c:DataSet.get(p).getFloorNode(1).get(q).getChild())
				{
					calEntropy(DataSet.get(p).getFloorNode(0).get(c).getNode(),DataSet.get(p).getFloorNode(1).get(q).getNode(),epsilonp3);
				}
			}
		}
		for(int m=1;m<tol;m++)
			for(int n=m+1;n<=tol;n++)
			{
				HashMap<Integer,TreeNode> hmA = DataSet.get(m).getFloorNode(1);
				HashMap<Integer,TreeNode> hmB = DataSet.get(n).getFloorNode(1);
				for(Integer p:hmA.keySet())
				{
					for(Integer q:hmB.keySet())
					{
						Pair pair = new Pair(p,q);
						if(matchlabel.get(pair)!=null)
						{
							//writer.write(p+","+q+"\n");
							if(matchlabel.get(pair)==0){
							HashSet<Integer> childSetA = DataSet.get(m).getFloorNode(1).get(p).getChild();
							HashSet<Integer> childSetB = DataSet.get(n).getFloorNode(1).get(q).getChild();
							HashSet<Integer> rechildSetA = new HashSet<Integer>();
							HashSet<Integer> rechildSetB = new HashSet<Integer>();
							for(Integer x:childSetA)
							{
								//childSetA.remove(x);
								/*Node nodeM = DataSet.get(m).getFloorNode(0).get(x).getNode();
								Node PnodeA = DataSet.get(m).getFloorNode(1).get(p).getNode();
								Node PnodeB = DataSet.get(n).getFloorNode(1).get(q).getNode();*/
								TreeNode TnodeM = getNodeById(x);
								TreeNode TPnodeA = getNodeById(p);
								TreeNode TPnodeB = getNodeById(q);
								Node nodeM = TnodeM.getNode();
								Node PnodeA = TPnodeA.getNode();
								Node PnodeB = TPnodeB.getNode();
								double en1 = calEntropy(nodeM,PnodeA,epsilonp3);
								double en2 = calEntropy(nodeM,PnodeB,epsilonp3);
								if(en1>en2)
								{
									TnodeM.setParent(p);
									rechildSetA.add(x);
									//writer.write(x+","+p+"\n");
								}
								else
								{
									TnodeM.setParent(q);
									rechildSetB.add(x);
									//writer.write(x+","+q+"\n");
								}
							}
							for(Integer y:childSetB)
							{
								//childSetB.remove(y);
								/*Node nodeN = DataSet.get(n).getFloorNode(0).get(y).getNode();
								Node PnodeA = DataSet.get(m).getFloorNode(1).get(p).getNode();
								Node PnodeB = DataSet.get(n).getFloorNode(1).get(q).getNode();*/
								TreeNode TnodeN = getNodeById(y);
								TreeNode TPnodeA = getNodeById(p);
								TreeNode TPnodeB = getNodeById(q);
								Node nodeN = TnodeN.getNode();
								Node PnodeA = TPnodeA.getNode();
								Node PnodeB = TPnodeB.getNode();
								double en1 = calEntropy(nodeN,PnodeA,epsilonp3);
								double en2 = calEntropy(nodeN,PnodeB,epsilonp3);
								if(en1>en2)
								{
									TnodeN.setParent(p);
									rechildSetA.add(y);
									//writer.write(y+","+p+"\n");
								}
								else
								{
									TnodeN.setParent(q);
									rechildSetB.add(y);
									//writer.write(y+","+p+"\n");
								}
							}
							DataSet.get(m).getFloorNode(1).get(p).setChild(rechildSetA);
							DataSet.get(n).getFloorNode(1).get(q).setChild(rechildSetB);
							matchlabel.put(pair, 1);
							}
						}
					}
				}
			}
		//writer.write("\n\n\n");
		//writer.close();
	}
	public double calEntropy(Node nodeA,Node nodeB,double epsl) throws IOException
	{
		int A = nodeA.getNodeid();
		int B = nodeB.getNodeid();
		Pair pair = new Pair(A,B);
		if(mutualinfo.get(pair)!=null)
		{
			return mutualinfo.get(pair);
		}
		else
		{
			//FileWriter writer = new FileWriter("process.txt",true);
			//writer.write("calculate entropy for A:"+A+",B:"+B+"\n");
			int n00=0;
			int n01=0;
			int n10=0;
			int n11=0;
			int[] vecA=nodeA.getVec();
			int[] vecB=nodeB.getVec();
			//samplesize  = vecA.length;
			//for(int i=0;i<samplesize;i++)
			for(int i=0;i<vecA.length;i++)
			{
				if(vecA[i]==0&&vecB[i]==0)
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
			}
			//writer.write(n00+","+n01+","+n10+","+n11+"\n");
			Random ran1 = new Random();
			Random ran2 = new Random();
			Random ran3 = new Random();
			Random ran4 = new Random();
			if(epsl>1e-6){     					//隐私参数为0时认为不加入噪声；
			n00+= LaplaceDist(ran1,2.0/epsl);
			n01+= LaplaceDist(ran2,2.0/epsl);
			n10+= LaplaceDist(ran3,2.0/epsl);
			n11+= LaplaceDist(ran4,2.0/epsl);}
			n00=(n00>=0?n00:0);
			n01=(n01>=0?n01:0);
			n10=(n10>=0?n10:0);
			n11=(n11>=0?n11:0);
			//writer.write(n00+","+n01+","+n10+","+n11+"\n");
			int total = n00+n01+n10+n11;
			double p00=(1.0*n00+1.0)/(total+4.0);
			double p01=(1.0*n01+1.0)/(total+4.0);
			double p10=(1.0*n10+1.0)/(total+4.0);
			double p11=(1.0*n11+1.0)/(total+4.0);
			double pA0=p00+p01;
			double pA1=p10+p11;
			double pB0=p00+p10;
			double pB1=p01+p11;
			double entropy =  (p00*Math.log(p00/(pA0*pB0))+p01*Math.log(p01/(pA0*pB1))+p10*Math.log(p10/(pA1*pB0))+p11*Math.log(p11/(pA1*pB1)))/Math.log(2);
			mutualinfo.put(pair,entropy);
			Query query = new Query(n00,n01,n10,n11,A,B);
			mutualquery.put(pair,query);
			//System.out.println(entropy);
			//writer.write("entropy:"+entropy+"\n\n");
			//writer.close();
			return entropy;
		}
	}
	public void setConnectionMatrix()
	{
		System.out.println("total multiinfo");
		for(Pair pair: mutualinfo.getInfo().keySet())
		{
			System.out.println("pair:   "+pair.idA+"   ,      "+pair.idB);
		}
		System.out.println("total multiinfo");
		NodeMap = new HashMap<Integer,Integer>();
		NodeMapS = new HashMap<Integer,Integer>();
		hpairs = new HashSet<Pair>();
		nodenumber = 0;
		HashSet<Integer> S = new HashSet<Integer>();
		HashSet<Integer> V = new HashSet<Integer>();
		//因为只涉及到了最下面一层隐变量；
		for(Integer p:DataSet.keySet())
		{
			for(Integer q:DataSet.get(p).getFloorNode(1).keySet())
			{
				nodenumber++;
				NodeMap.put(q, nodenumber);
				NodeMapS.put(nodenumber, q);
				V.add(nodenumber);
			}
		}
		ConnectionMatrix = new double[nodenumber+1][nodenumber+1];
		for(int m =1 ;m<=nodenumber;m++)
		{
			for(int n=1;n<=nodenumber;n++)
			{
				ConnectionMatrix[m][n] = -100.0;
				int rm = NodeMapS.get(m);
				int rn = NodeMapS.get(n);
				Pair pair = new Pair(rm,rn);
				if(mutualinfo.get(pair)!=null)
				{
					double s = mutualinfo.get(pair);
					ConnectionMatrix[m][n] = s;
					ConnectionMatrix[n][m] = s;
				}
				else
				{
					ConnectionMatrix[m][n] = -100.0;
					ConnectionMatrix[n][m] = -100.0;
				}
			}
		}
		/*for(Integer p:matches.keySet())
		{
			for(Integer q:matches.get(p))
			{
				//matches包含各个层匹配的所有点，这样能保证当前点在第一层；
				System.out.println("get pair:"+"       "+p+"       "+q);
				if(NodeMap.get(p)!=null&&NodeMap.get(q)!=null){
					Pair pair = new Pair(p,q);
					System.out.println("got pair:"+"       "+p+"       "+q);
					double en = mutualinfo.get(pair);
					ConnectionMatrix[NodeMap.get(p)-1][NodeMap.get(q)-1] = en; 
					ConnectionMatrix[NodeMap.get(q)-1][NodeMap.get(p)-1] = en; 
				}
			}
		}*/
		//for()
		System.out.println("got matrix");
		System.out.println("start building the maximum tree");
		//1是最大生成树的起始点
		S.add(1);
		V.remove(1);
		while(V.size()!=0)
		{
			double max=0.0;
			int idA = 0;
			int idB = 0;
			for(Integer p:S)
			{
				for(int i=1;i<=nodenumber;i++)
				{
					if(!S.contains(i)){
						if(ConnectionMatrix[p][i]>=max)
						{
							max = ConnectionMatrix[p][i];
							idA = p;
							idB = i;
						}
					}
				}
			}
			S.add(idB);
			V.remove(idB);
			Pair pair = new Pair(NodeMapS.get(idA),NodeMapS.get(idB));
			hpairs.add(pair);
		}
		System.out.println("got the maximum tree");
	}
	public void setDirection() throws IOException
	{
		//形成树是用隐节点来做的，不用先显节点
		PSrelationship = new HashMap<Integer,Integer>();
		PSQuery = new HashMap<Integer,Query>();
		globaltree = new HashMap<Integer,HashMap<Integer,GlobalNode>>();
		HashSet<Integer> V = new HashSet<Integer>();
		ceng = 0;
		nodeceng = new HashMap<Integer,Integer>();
		for(Integer p:NodeMap.keySet())
		{
			V.add(p);
			nodeceng.put(p, -1);
		}
		Iterator<Pair> ir = hpairs.iterator();
		Root  =  ir.next().idA;
		setR();
		PSrelationship.put(Root, -1);
		nodeceng.put(Root,0);
		GlobalNode gn = new GlobalNode(getNodeById(Root));
		gn.setNodeRoot();
		HashMap<Integer,GlobalNode> hmg = new HashMap<Integer,GlobalNode>();
		hmg.put(Root, gn);
		System.out.println("Root:"+Root);
		globaltree.put(0, hmg);
		HashSet<Integer> S = new HashSet<Integer>();  //tmp ceng;
		S.add(Root);
		V.remove(Root);
		//HashSet<Pair> T = new HashSet<Pair>(hpairs);
		HashSet<Integer> X = new HashSet<Integer>();
		X.add(Root);
		System.out.println("start getting the global tree");
		while(V.size()!=0)
		{
			HashSet<Integer> U = new HashSet<Integer>(); 
			ceng++;
			for(Integer p:S)
			{
				HashSet<Integer> childsetp = new HashSet<Integer>();
				for(Pair q:hpairs)
				{
					if(q.idA==p&&(!X.contains(q.idB)))
					{
						//T.remove(q);
						V.remove(q.idB);
						U.add(q.idB);
						X.add(q.idB);
						childsetp.add(q.idB);
						nodeceng.put(q.idB, ceng);
						PSrelationship.put(q.idB, p);
						setPSQuery(q.idB,p);
						GlobalNode gn1 = new GlobalNode(getNodeById(q.idB));
						gn1.setParent(p);
						if(globaltree.get(ceng)!=null)
							globaltree.get(ceng).put(q.idB, gn1);
						else
						{
							HashMap<Integer,GlobalNode> hig = new HashMap<Integer,GlobalNode>();
							hig.put(q.idB,gn1);
							globaltree.put(ceng, hig);
						}
					}
					else if(q.idB==p&&(!X.contains(q.idA)))
					{
						//T.remove(q);
						V.remove(q.idA);
						U.add(q.idA);
						X.add(q.idA);
						childsetp.add(q.idA);
						nodeceng.put(q.idA, ceng);
						PSrelationship.put(q.idA, p);
						setPSQuery(q.idA,p);
						GlobalNode gn1 = new GlobalNode(getNodeById(q.idA));
						gn1.setParent(p);
						if(globaltree.get(ceng)!=null)
							globaltree.get(ceng).put(q.idA, gn1);
						else
						{
							HashMap<Integer,GlobalNode> hig = new HashMap<Integer,GlobalNode>();
							hig.put(q.idA,gn1);
							globaltree.put(ceng, hig);
						}
					}
				}
				globaltree.get(ceng-1).get(p).setGlobalChild(childsetp);
			}
			S=U;
		}
		for(Integer p:DataSet.keySet())
		{
			for(Integer q:DataSet.get(p).getFloorNode(0).keySet())
			{
				PSrelationship.put(q, DataSet.get(p).getFloorNode(0).get(q).getParentId());
				setPSQuery(DataSet.get(p).getFloorNode(0).get(q).getNode(),this.getNodeById(DataSet.get(p).getFloorNode(0).get(q).getParentId()).getNode());
			}
		}
		/*
		for(Integer p:DataSet.keySet())
		{
			for(Integer q:DataSet.get(p).getFloorNode(1).keySet())
			{
				for(Integer c:DataSet.get(p).getFloorNode(1).get(q).getChild())
				{
					
					setPSQuery(DataSet.get(p).getFloorNode(0).get(q).getNode(),this.getNodeById(DataSet.get(p).getFloorNode(0).get(q).getParentId()).getNode());
				}
			}
		}*/
		/*System.out.println("got the global tree");
		FileWriter writer = new FileWriter("processglobaltree.txt",true);
		for(int i = 0;i<=ceng;i++)
		{
			writer.write("floor "+i+":");
			for(Integer p:globaltree.get(i).keySet())
			{
				int parent = PSrelationship.get(p);
				writer.write(p+"("+parent+")    ");
				for(Integer c:DataSet.keySet())
				{
					if(DataSet.get(c).getFloorNode(1).keySet().contains(p)){
						writer.write("[");
						int a=0;
						for(Integer q:DataSet.get(c).getFloorNode(1).get(p).getChild())
						{
							writer.write(q+",");
							a++;
						}
						//System.out.println("a:"+(3*a+2));
						writer.write("]     ");
						for(int t=30;t>=(3*a+2);t--)
							writer.write(" ");
					}
				}
			}
			writer.write("\n");
		}
		writer.write("\n\n\n");
		writer.close();*/
	}
	public TreeNode getNodeById(int nodeid)
	{
		for(Integer p:DataSet.keySet()){
			for(Integer q:DataSet.get(p).getAllNodes().keySet())
			{
				if(DataSet.get(p).getAllNodes().get(q).keySet().contains(nodeid))
					return DataSet.get(p).getAllNodes().get(q).get(nodeid);
			}
		}
		return null;
	}
	public void setPSQuery(int id,int parentid)
	{
			/*Node nodeA = this.getNodeById(id).getNode();
			Node nodeB = this.getNodeById(parentid).getNode();
			int n00=0;
			int n01=0;
			int n10=0;
			int n11=0;
			int[] vecA=nodeA.getVec();
			int[] vecB=nodeB.getVec();
			//samplesize  = vecA.length;
			//for(int i=0;i<samplesize;i++)
			for(int i=0;i<vecA.length;i++)
			{
				if(vecA[i]==0&&vecB[i]==0)
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
			}
			Query query = new Query(n00+1,n01+1,n10+1,n11+1);
			PSQuery.put(id, query);*/
			Pair pair = new Pair(id,parentid);
			PSQuery.put(id, mutualquery.get(pair));
	}
	public void setPSQuery(Node nodeA,Node nodeB)
	{
		int id = nodeA.getNodeid();
		int parentid = nodeB.getNodeid();
		/*int n00=0;
		int n01=0;
		int n10=0;
		int n11=0;
		int[] vecA=nodeA.getVec();
		int[] vecB=nodeB.getVec();
		//samplesize  = vecA.length;
		//for(int i=0;i<samplesize;i++)
		for(int i=0;i<vecA.length;i++)
		{
			if(vecA[i]==0&&vecB[i]==0)
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
		}
		Random ran1 = new Random();
		Random ran2 = new Random();
		Random ran3 = new Random();
		Random ran4 = new Random();
		n00+= LaplaceDist(ran1,2.0/epsilon);
		n01+= LaplaceDist(ran2,2.0/epsilon);
		n10+= LaplaceDist(ran3,2.0/epsilon);
		n11+= LaplaceDist(ran4,2.0/epsilon);
		n00=n00>=0?n00:0;
		n01=n01>=0?n01:0;
		n10=n10>=0?n10:0;
		n11=n11>=0?n11:0;
		Query query = new Query(n00+1,n01+1,n10+1,n11+1);*/
		Pair pair = new Pair(id,parentid);
		PSQuery.put(id, mutualquery.get(pair));
	}
	public HashMap<Integer,Query> getPSQuery()
	{
		return PSQuery;
	}
	public HashMap<Integer,Integer> getPSrelationship()
	{
		return PSrelationship;
	}
	public HashMap<Integer,HashMap<Integer,GlobalNode>> getGlobalTree() 
	{
		return globaltree;
	}
	public int getCeng() 
	{
		return ceng;
	}
	public HashMap<Integer,HashSet<Integer>> getgensets()
	{
		HashMap<Integer,HashSet<Integer>> s = new HashMap<Integer,HashSet<Integer>>();
		HashSet<Integer> s1 = new HashSet<Integer>();
		HashSet<Integer> s2 = new HashSet<Integer>();
		for(Integer p:DataSet.keySet())
		{
			for(Integer q:DataSet.get(p).getFloorNode(0).keySet())
			{
				s1.add(q);
			}
			for(Integer m:DataSet.get(p).getFloorNode(1).keySet())
			{
				s2.add(m);
			}
		}
		s.put(0, s1);
		s.put(1, s2);
		return s;
	}
	public void setR()
	{
		int a=0;
		int b=0;
		Node node= getNodeById(Root).getNode();
		int[] vec=node.getVec();
		for(int i=0;i<vec.length;i++)
		{
			if(vec[i]==0)
			{
				a++;
			}
			else
			{
				b++;
			}
		}
		R0=a+1;
		R1=b+1;
	}
	public int getR0()
	{
		return R0;
	}
	public int getR1()
	{
		return R1;
	}
	public static double LaplaceDist(Random rng, double scale){
		double U = rng.nextDouble()-0.5;
		return -scale*Math.signum(U)*Math.log(1-2*Math.abs(U));
	}
	public HashMap<Pair,Query> getmutualquery()
	{
		return mutualquery;
	}
}

