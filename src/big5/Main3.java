package big5;

import java.io.IOException;

import Data.Data;

public class Main3 {
	public static void main(String[] args) throws IOException
	{
		Data data = new Data("big5train.dat","big5train.domain");
		data.writeDFile("big5dd.dat");
	}
}
