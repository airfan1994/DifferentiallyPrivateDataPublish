package Site;

import java.util.HashSet;

public class TreeNode {
	private int id;
	private Node node;
	private int parent;
	private HashSet<Integer> childs;
	public TreeNode(){
		
	}
	public TreeNode(int id,Node node)
	{
		this.id = id;
		this.node = node;
	}
	public void setLeaf()
	{
		childs = null;
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
	public int getId()
	{
		return id;
	}
	public int getParentId()
	{
		return parent;
	}
}
