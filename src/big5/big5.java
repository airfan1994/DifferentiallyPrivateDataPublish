package big5;
/**
 * 数据集4 总数21574 80% 17259 20% 4135
 */
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import Corex.Corex;
import Data.Data;
import Data.DataGenerator;
import Records.MutualInfo;
import Records.Pair;
import Records.Query;
import Site.GlobalTree;
import Site.Groups;
import Site.Node;
import Site.TreeSite;


public class big5 {
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
		big5 main = new big5();
		main.epsilon = Double.parseDouble(args[0]);
		//main.epsilon = 0.4;
		//main.epsilon = Integer.parseInt(args[1]);
		
		main.max_iter=100;
		main.loaddata("big5train.dat","big5train.domain","big5d",15611);
		Data datatest = new Data("big5test.dat","big5test.domain"); 
		datatest.writeDFile("big5d.test");
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
		}
	}
}
