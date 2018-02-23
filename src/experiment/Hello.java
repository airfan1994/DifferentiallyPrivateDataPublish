package experiment;

import java.io.IOException;

import Data.Data;
import Records.MutualInfo;
import Records.Pair;

public class Hello {
	public static void main(String[] args) throws IOException
	{
		/*Pair a = new Pair(1,2);
		MutualInfo mutualinfo = new MutualInfo();
		mutualinfo.put(a, 0.01);
		Pair b = new Pair(2,1);
		System.out.println(mutualinfo.get(b));
		System.out.println(a.equals(b));*/
		Data datatrain = new Data("adultrawtrain.dat","adultrawtrain.domain"); 
		datatrain.writeDFile("adultd.train");
		Data datatest = new Data("adultrawtest.dat","adultrawtest.domain"); 
		datatest.writeDFile("adultd.test");
	}
}
