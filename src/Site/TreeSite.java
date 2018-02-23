package Site;

import java.util.HashMap;
import java.util.HashSet;

public class TreeSite {
	private int siteid;
	private HashMap<Integer,HashMap<Integer,TreeNode>> treenodes;
	private HashMap<Integer,Node> floornodes;
	private int maxfloorid;
	private TreeNode RootNode;
	public TreeSite()
	{
		
	}
	public TreeSite(HashMap<Integer,HashMap<Integer,TreeNode>> treenodes,int siteid)
	{
		this.siteid=siteid;
		this.treenodes=treenodes;
		//System.out.println("init again");
	}
	public TreeSite(int siteid, HashMap<Integer,Node> nodes)
	{
		this.siteid=siteid;
		this.floornodes=nodes;
		treenodes = new HashMap<Integer,HashMap<Integer,TreeNode>>();
		HashMap<Integer,TreeNode> tn = new HashMap<Integer,TreeNode>();
		for(Integer p:floornodes.keySet())
		{
			TreeNode tnode = new TreeNode(p,floornodes.get(p));
			tn.put(p, tnode);
		}
		treenodes.put(0, tn);
		maxfloorid =0;
	}
	public HashMap<Integer,Node> getFloorNodes()
	{
		return floornodes;
	}
	public void addHiddenLayer(HashMap<Integer,Node> hiddennodes,Groups gr,int maxnodeid)
	{
		HashMap<Integer,HashSet<Integer>> layergroup=gr.getGroup();
		int id=1;
		for(Integer p:layergroup.keySet())       //group分组组号从1开始
		{                                                 
			int pid = maxnodeid+id;
			id++;
			TreeNode tnode = new TreeNode(pid,hiddennodes.get(p));
			hiddennodes.get(p).setNodeid(pid);
			hiddennodes.get(p).setSiteid(siteid);
			tnode.setChild(layergroup.get(p));
			for(Integer q:layergroup.get(p))
			{
				/*alpha[p-1][id] = 1;
				NodeMap.put(q, id);
				int[] tvec = Layer.get(q).getVec();
				for(int i=0;i<n_sample;i++){
					matrix[i][id] = tvec[i];
				}
				id++;*/
				treenodes.get(maxfloorid).get(q).setParent(pid);
			}
			if(treenodes.get(maxfloorid+1)==null)
			{
				HashMap<Integer,TreeNode> hn= new HashMap<Integer,TreeNode>();
				hn.put(pid, tnode);
				treenodes.put(maxfloorid+1, hn);
			}
			else
			{
				treenodes.get(maxfloorid+1).put(pid, tnode);
			}
		}
		maxfloorid++;
	}
	public void addRootLayer()
	{
		
	}
	public HashMap<Integer,Node> gettmpLayer()
	{
		HashMap<Integer,Node> lay = new HashMap<Integer,Node>();
		for(Integer p:treenodes.get(maxfloorid).keySet())
		{
			lay.put(p, treenodes.get(maxfloorid).get(p).getNode());
		}
		return lay;
	}
	public void setfloorNodeRoot()
	{
		if(treenodes.get(maxfloorid).size()!=1)
		{
			System.out.println("error");
			System.exit(-1);
		}
		else
		{
			for(int t:treenodes.get(maxfloorid).keySet())
			{
				treenodes.get(maxfloorid).get(t).setNodeRoot();
			}
		}
	}
	public int getMaxFloorID()
	{
		return maxfloorid;
	}
	public HashMap<Integer,TreeNode> getFloorNode(int i)
	{
		return treenodes.get(i);
	}
	public HashMap<Integer,HashMap<Integer,TreeNode>> getAllNodes()
	{
		return treenodes;
	}
}
