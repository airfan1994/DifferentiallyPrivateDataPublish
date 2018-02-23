package Records;

import java.util.HashMap;

public class MutualInfo {
	private HashMap<Pair,Double> info;
	public MutualInfo()
	{
		info = new HashMap<Pair,Double>();
	}
	public void put(Pair pair,Double d)
	{
		info.put(pair, d);
	}
	public Double get(Pair pair)
	{
		return info.get(pair);
	}
	public HashMap<Pair,Double> getInfo()
	{
		return info;
	}
}
