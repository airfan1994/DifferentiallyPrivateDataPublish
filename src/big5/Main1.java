package big5;

import java.io.IOException;

import Data.Data;

public class Main1 {
	public static void main(String[] args) throws IOException
	{
		Data datatrain = new Data("big5train.dat","big5train.domain"); 
		datatrain.writeDFile("big5d.train");
		Data datatest = new Data("big5test.dat","big5test.domain"); 
		datatest.writeDFile("big5d.test");
	}
}
