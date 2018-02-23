package Site;

public class Node {
	private int nodeid;
	private int[] vec;
	private int siteid;
	public Node(){
		
	}
	public Node(int[]vec){
		this.vec=vec;
	}
	public Node(int id,int[]vec){
		nodeid=id;
		this.vec=vec;
	}
	public int getSize()
	{
		return vec.length;
	}
	public int[] getVec()
	{
		return vec;
	}
	public int getNodeid()
	{
		return nodeid;
	}
	public void setNodeid(int nodeid)
	{
		this.nodeid=nodeid;
	}
	public void setSiteid(int siteid)
	{
		this.siteid = siteid;
	}
}
