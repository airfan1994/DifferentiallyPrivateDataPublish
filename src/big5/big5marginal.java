package big5;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import Data.Data;
import Data.DataGenerator;
import Records.MutualInfo;
import Records.Pair;
import Records.Query;
import Site.GlobalTree;
import Site.Node;
import Site.TreeSite;

public class big5marginal {
	private Data data;
	private HashMap<Integer,Node> datanodes;
	private HashMap<Integer,TreeSite> DataSet;
	private MutualInfo mutualinfo;
	private int MaxidNode;
	private GlobalTree globaltree;
	private  double epsilon;
	private HashMap<Pair,Query> mutualquery;
	private int max_iter;
	public static void main(String[] args) throws IOException
	{
		/*Data data = new Data("big5.dat","big5.domain");
		//data.writeBFile("big5.bin");
		data.writemar2("big5.mar2");
		data.writemar3("big5.mar3");
		data.writemar4("big5.mar4");*/
		
		
		big5marginal main = new big5marginal();
		main.epsilon = Double.parseDouble(args[0]);
		//main.epsilon = 0.4;
		//main.epsilon = Integer.parseInt(args[1]);
		main.max_iter=100;
		main.loaddata("big5.dat","big5.domain","big5d",19636);
		//Data datatest = new Data("big5rawtest.dat","big5rawtest.domain"); 
		//datatest.writeDFile("big5d.test");
		
	}
	public void loaddata(String datafile,String domainfile,String trainfile,int samplesize) throws IOException
	{
		for(int x=0;x<20;x++){
			System.out.println("times:"+x);
		data = new Data(datafile,domainfile);
		//epsilon = 0.5;
		datanodes=data.fillDataVec();
		HashSet<Integer> set1 = new HashSet<Integer>();
		HashSet<Integer> set2 = new HashSet<Integer>();
		for(int i=1;i<=87;i++)
		{
			set1.add(i);
		}
		for(int i=88;i<=174;i++)
		{
			set2.add(i);
		}
		HashMap<Integer,HashSet<Integer>> group1 = new HashMap<Integer,HashSet<Integer>>();
		group1.put(1, set1);
		group1.put(2, set2);
		DataSet = data.divideSites(group1);
		MaxidNode = data.getBDim();
		//data.writeDiscretizationFile();
		mutualinfo = new MutualInfo();
		mutualquery = new HashMap<Pair,Query>();
		globaltree = new GlobalTree(DataSet,mutualinfo,MaxidNode,4,epsilon,mutualquery,max_iter);
		globaltree.setall();
		DataGenerator datagen = new DataGenerator(samplesize,globaltree,data,(trainfile+x+".train"));
		//datagen.generatedata("a.dat");
		datagen.generateprocessor();
		datagen.calmar2distance("big5.mar2", "big5.mard2");
		datagen.calmar3distance("big5.mar3", "big5.mard3");
		//datagen.calmar4distance("big5.mar4", "big5.mard4");
		}
	}
}
