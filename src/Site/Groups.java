package Site;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import Records.MutualInfo;
import Records.Pair;
import Records.Query;
import Site.Node;

public class Groups {
	private int k;
	private HashMap<Integer,HashSet<Integer>> group;
	private HashMap<Integer,Node> Layer;
	private MutualInfo mutualinfo;
	private int samplesize;
	private double epsilon;
	private HashMap<Pair,Query> mutualquery;
	private double epsilongroup;
	private double sens;
	private int type=1;
	private int num;
	public Groups()
	{
		
	}
	public Groups(HashMap<Integer,Node> Layer,int k,MutualInfo mutual,double epsilon,HashMap<Pair,Query> mutualquery,int type)
	{
		this.k=k;
		this.Layer = Layer;
		this.mutualinfo = mutual;
		this.epsilon = epsilon;
		this.mutualquery = mutualquery;
		this.type = type;//1:I;2,F
	}
	public HashMap<Integer,HashSet<Integer>> setDroups() throws IOException
	{
		HashSet<Integer> S = new HashSet<Integer>();    //grouped
		HashSet<Integer> T = new HashSet<Integer>(Layer.keySet());   //to be grouped
		HashSet<Integer> TmpSet;
		Iterator<Integer> s3=T.iterator();
		int h3=s3.next();
		num = Layer.get(h3).getSize();
		if(type==1)
		{sens = Math.log(num)/(num*1.0)+(num-1)*Math.log(num*1.0/(num-1))/(num*1.0);
		sens = sens/Math.log(2);}
		else{sens = 1.0/(num*1.0);}
		group = new HashMap<Integer,HashSet<Integer>>();
		int total = T.size();
		int zheng = total/k;
		int yu = total-zheng*k;
		epsilongroup = epsilon/(yu-1+zheng*(k-1));
		//System.out.println("total"+total);
		//System.out.println("k"+k);
		//System.out.println("zheng"+zheng);
		for(int i=1;i<=zheng;i++)
		{
			System.out.println("group"+i);
			TmpSet = new HashSet<Integer>();
			Iterator<Integer> s=T.iterator();
			int h=s.next();
			T.remove(h);
			S.add(h);
			TmpSet.add(h);
			System.out.println("the first node:"+h);
			/*double max=0.0;
			int maxid=0;
			
			for(Integer p:T)
			{
				double en = calEntropy(p, h);
				if(en>max)
				{
					max=en;
					maxid=p;
				}
			}
			*/
			HashMap<Integer,Double> candidates = new HashMap<Integer,Double>();
			for(Integer p:T)
			{
				if(type==1)
					candidates.put(p, calEntropy(p,h));
				else
					candidates.put(p, calF(p,h));
			}
			int maxid = Expo(candidates);
			if(maxid==0)
			{
				System.out.println("error");
			}
			T.remove(maxid);
			S.add(maxid);
			TmpSet.add(maxid);
			System.out.println("the second node:"+maxid);
			while(TmpSet.size()<k){
				double max1=-11111110.0;
				int maxid1=0;
				for(Integer p:T)
				{
					double mi;
					if(type==1)
						mi = calMI(p,TmpSet);
					else
						mi = calMF(p,TmpSet);
					if(mi>max1)
					{
						max1=mi;
						maxid1 = p;
					}
				}
				T.remove(maxid1);
				S.add(maxid1);
				TmpSet.add(maxid1);
				System.out.println("node added:"+maxid1);
				//System.out.println("node added"+maxid1);
				//System.out.println(TmpSet.size());
			}
			group.put(i, TmpSet);
		}
		if(yu!=0)
		{
			group.put(zheng+1, T);
		}
		return group;
	}
	public double calEntropy(int A,int B) throws IOException
	{
		Pair pair = new Pair(A,B);
		if(mutualinfo.get(pair)!=null)
		{
			return mutualinfo.get(pair);
		}
		else
		{
			//FileWriter writer = new FileWriter("process.txt",true);
			//writer.write("calculate entropy for A:"+A+",B:"+B+"\n");
			Node nodeA= Layer.get(A);
			Node nodeB= Layer.get(B);
			int n00=0;
			int n01=0;
			int n10=0;
			int n11=0;
			//System.out.println("A:"+A);
			//System.out.println("B:"+B);
			int[] vecA=nodeA.getVec();
			int[] vecB=nodeB.getVec();
			samplesize  = vecA.length;
			for(int i=0;i<samplesize;i++)
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
			/*Random ran1 = new Random();
			Random ran2 = new Random();
			Random ran3 = new Random();
			Random ran4 = new Random();
			n00+= LaplaceDist(ran1,2.0/epsilon);
			n01+= LaplaceDist(ran2,2.0/epsilon);
			n10+= LaplaceDist(ran3,2.0/epsilon);
			n11+= LaplaceDist(ran4,2.0/epsilon);*/
			n00=(n00>=0?n00:0);
			n01=(n01>=0?n01:0);
			n10=(n10>=0?n10:0);
			n11=(n11>=0?n11:0);
			Query query = new Query(n00,n01,n10,n11,A,B);
			double p00=(1.0*n00+1.0)/(nodeA.getSize()+4.0);
			double p01=(1.0*n01+1.0)/(nodeA.getSize()+4.0);
			double p10=(1.0*n10+1.0)/(nodeA.getSize()+4.0);
			double p11=(1.0*n11+1.0)/(nodeA.getSize()+4.0);
			double pA0=p00+p01;
			double pA1=p10+p11;
			double pB0=p00+p10;
			double pB1=p01+p11;
			double entropy =  (p00*Math.log(p00/(pA0*pB0))+p01*Math.log(p01/(pA0*pB1))+p10*Math.log(p10/(pA1*pB0))+p11*Math.log(p11/(pA1*pB1)))/Math.log(2);
			mutualinfo.put(pair,entropy);
			//System.out.println(entropy);
			mutualquery.put(pair, query);
			//writer.close();
			return entropy;
		}
	}
	public double calMI(Integer p,HashSet<Integer> S) throws IOException
	{
		double max = 0.0;
		for(Integer s:S)
		{
			if(s==0)
			{
				System.out.println("error!");
			}
			double en = calEntropy(p,s);
			if(en>max)
				max=en;
		}
		return max;
	}
	public double calMF(Integer p,HashSet<Integer> S)
	{
		double max = -11111110.0;
		for(Integer s:S)
		{
			if(s==0)
			{
				System.out.println("error!");
			}
			double en = calF(p,s);
			if(en>max||max==-11111110.0)
				max=en;
		}
		return max;
	}
	public double calF(int A,int B)
	{
		
		Pair pair = new Pair(A,B);
		if(mutualinfo.get(pair)!=null)
		{
			return mutualinfo.get(pair);
		}
		else
		{
		double ans = 0.0;
		HashMap<Integer, Integer> states = new HashMap<Integer, Integer>();
		states.put(0, 0);
		int ceil = (num+1)/2;
		
		Node nodeA= Layer.get(A);
		Node nodeB= Layer.get(B);
		int n00=0;
		int n01=0;
		int n10=0;
		int n11=0;
		//System.out.println("A:"+A);
		//System.out.println("B:"+B);
		int[] vecA=nodeA.getVec();
		int[] vecB=nodeB.getVec();
		samplesize  = vecA.length;
		for(int i=0;i<samplesize;i++)
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
		Query query = new Query(n00,n01,n10,n11,A,B);
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
		ans = -num;
		for (int M : states.keySet()){
			int N = states.get(M);
			double score = M + N - num;
			if (score > ans) ans = score;
		}
		
		double p00=(1.0*n00+1.0)/(nodeA.getSize()+4.0);
		double p01=(1.0*n01+1.0)/(nodeA.getSize()+4.0);
		double p10=(1.0*n10+1.0)/(nodeA.getSize()+4.0);
		double p11=(1.0*n11+1.0)/(nodeA.getSize()+4.0);
		double pA0=p00+p01;
		double pA1=p10+p11;
		double pB0=p00+p10;
		double pB1=p01+p11;
		double entropy =  (p00*Math.log(p00/(pA0*pB0))+p01*Math.log(p01/(pA0*pB1))+p10*Math.log(p10/(pA1*pB0))+p11*Math.log(p11/(pA1*pB1)))/Math.log(2);
		mutualinfo.put(pair,entropy);
		
		//mutualinfo.put(pair,ans);
		//System.out.println(entropy);
		mutualquery.put(pair, query);
		//System.out.println("ans:"+ans);
		return ans;}
	}
	public HashMap<Integer,Node> getLayer() 
	{
		return Layer;
	}
	public HashMap<Integer,HashSet<Integer>> getGroup()
	{
		return group;
	}
	public int getSampleSize()
	{
		return samplesize;
	}
	public int Expo(HashMap<Integer,Double> candidates)
	{
		//if(type==1)
		//{
			if(epsilongroup<1e-6)
			{
				double max = 0.0;
				int maxpos = 0;
				for(Integer p:candidates.keySet())
				{
					if(candidates.get(p)>max||max==0.0)
					{
						max = candidates.get(p);
						maxpos = p;
					}
				}
				return maxpos;
			}
			else
			{
				double sum = 0;
				HashMap<Integer,Double> ecandi= new HashMap<Integer,Double>();
				for(Integer p:candidates.keySet())
				{
					double tmp = Math.exp(epsilongroup*candidates.get(p)/(2*sens));
					ecandi.put(p, tmp);
					sum+=tmp;
				}
				double rantmp = sum*Math.random();
				double sumtmp = 0.0;
				for(Integer p:ecandi.keySet())
				{
					sumtmp+=ecandi.get(p);
					if(sumtmp>=rantmp)
						return p;
				}
			}
			System.out.println("expo error!");
			return 0;
		//}
		/*else
		{
			if(epsilongroup<1e-6)
			{
				double max = 0.0;
				int maxpos = 0;
				for(Integer p:candidates.keySet())
				{
					if(candidates.get(p)>max||max==0.0)
					{
						max = candidates.get(p);
						maxpos = p;
					}
				}
				return maxpos;
			}
			else
			{
				double sum = 0;
				HashMap<Integer,Double> ecandi= new HashMap<Integer,Double>();
				for(Integer p:candidates.keySet())
				{
					double tmp = Math.exp(epsilongroup*candidates.get(p)/(2*sens));
					ecandi.put(p, tmp);
					sum+=tmp;
				}
				double rantmp = sum*Math.random();
				double sumtmp = 0.0;
				for(Integer p:ecandi.keySet())
				{
					sumtmp+=ecandi.get(p);
					if(sumtmp>=rantmp)
						return p;
				}
			}
			System.out.println("expo error!");
			return 0;
		}*/
	}
	public static double LaplaceDist(Random rng, double lambda){
		double U = rng.nextDouble()-0.5;
		return -lambda*Math.signum(U)*Math.log(1-2*Math.abs(U));
	}
}
