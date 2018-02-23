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

public class m1 {
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
		m1 main = new m1();
		main.loaddata("adultrawtrain.dat","adultrawtrain.domain","adultd",36177);
		main.epsilon =0.0;
		//main.epsilon = 0.4;
		//main.epsilon = Integer.parseInt(args[1]);
		main.max_iter=100;
	}
	public void loaddata(String datafile,String domainfile,String trainfile,int samplesize) throws IOException
	{

		data = new Data(datafile,domainfile);
		datanodes=data.fillDataVec();
		HashSet<Integer> set1 = new HashSet<Integer>();
		HashSet<Integer> set2 = new HashSet<Integer>();
		set1.add(1);
		for(int i=27;i<=40;i++)
		{
			set2.add(i);
		}
		HashMap<Integer,HashSet<Integer>> group1 = new HashMap<Integer,HashSet<Integer>>();
		group1.put(1, set1);
		group1.put(2, set2);
		DataSet = data.divideSites(group1);
		MaxidNode = 40;
		mutualinfo = new MutualInfo();
		mutualquery = new HashMap<Pair,Query>();
		globaltree = new GlobalTree(DataSet,mutualinfo,MaxidNode,4,epsilon,mutualquery,max_iter);
		globaltree.sete();
	}
}
