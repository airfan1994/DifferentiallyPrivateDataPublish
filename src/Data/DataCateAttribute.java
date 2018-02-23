package Data;

import java.util.HashMap;

public class DataCateAttribute{
	private int  d;
	private HashMap<String,Integer> attrs;
	public DataCateAttribute()
	{
		
	}
	public DataCateAttribute(int  d,HashMap<String,Integer> attrs)
	{
		this.d=d;
		this.attrs=attrs;
	}
	public DataCateAttribute(int  d)
	{
		this.d=d;
		attrs = new HashMap<String,Integer>();
	}
	public void put(String s,int t)
	{
		attrs.put(s, t);
	}
	public int get(String s)
	{
		return attrs.get(s);
	}
	public int getD()
	{
		return d;
	}
}
