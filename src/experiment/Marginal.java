package experiment;

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

public class Marginal {
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
		//Data data = new Data("adult.dat","adult.domain");
		//data.writeBFile("adult.bin");
		/*data.writemar2("adult.mar2");
		data.writemar3("adult.mar3");
		data.writemar4("adult.mar4");*/
		Marginal main = new Marginal();
		main.epsilon = Double.parseDouble(args[0]);
		//main.epsilon = 0.4;
		//main.epsilon = Integer.parseInt(args[1]);
		main.max_iter=100;
		main.loaddata("adult.dat","adult.domain","adultd",45222);
		//Data datatest = new Data("adultrawtest.dat","adultrawtest.domain"); 
		//datatest.writeDFile("adultd.test");
		
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
		for(int i=1;i<=26;i++)
		{
			set1.add(i);
		}
		for(int i=27;i<=52;i++)
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
		datagen.calmar2distance("adult.mar2", "adult.mard2");
		datagen.calmar3distance("adult.mar3", "adult.mard3");
		datagen.calmar4distance("adult.mar4", "adult.mard4");
		}
		//datagen.printmatrix();
		/*TreeSite site = DataSet.get(1);
		HashSet<Node> hsn  = site.getNodes();
		HashMap<Integer,Node> Layer = new HashMap<Integer,Node>();
		for(Node p:hsn)
		{
			Layer.put(p.getNodeid(), p);
		}
		Groups gr = new Groups(Layer,4,mutualinfo);
		gr.setDroups();
		Corex corex = new Corex(gr,2);
		corex.fit(0);*/
	}
}
