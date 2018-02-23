package Site;

import java.util.HashSet;

public class GlobalNode {
	private int id;
	private Node node;
	private int parent;
	private HashSet<Integer> childs;
	private HashSet<Integer> globalchilds;
	public GlobalNode(){
		
	}
	public GlobalNode(int id,Node node)
	{
		this.id = id;
		this.node = node;
	}
	public GlobalNode(TreeNode tn)
	{
		id = tn.getId();
		node = tn.getNode();
		childs = tn.getChild();
		globalchilds = null;
	}
	public void setLeaf()
	{
		globalchilds = null;
	}
	public void setParent(int parent)
	{
		this.parent = parent;
	}
	public void setChild(HashSet<Integer> childset)
	{
		this.childs = childset;
	}
	public Node getNode()
	{
		return node;
	}
	public void setNodeRoot()
	{
		parent=-1;
	}
	public HashSet<Integer> getChild()
	{
		return childs;
	}
	public HashSet<Integer> getGlobalChild()
	{
		return globalchilds;
	}
	public void setGlobalChild(HashSet<Integer> globalchildset)
	{
		this.globalchilds = globalchildset;
	}
}
