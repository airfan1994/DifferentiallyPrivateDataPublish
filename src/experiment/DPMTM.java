package experiment;

import java.io.IOException;

import Data.Data;

public class DPMTM {
	public static void main(String[] args) throws IOException
	{
		DPMTM main = new DPMTM(); 
		Data data = new Data("adultrawtrain.dat","adultrawtrain.domain");
		data.writeallF("F.txt");
	}
}
