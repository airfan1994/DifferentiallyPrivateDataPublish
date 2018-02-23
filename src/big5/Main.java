package big5;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

import Data.Data;

public class Main {
	public static void main(String[] args) throws IOException
	{
	/*	Data data = new Data("big5.dat","big5.domain");
	    data.writeBFile("big5.bin");
	    System.out.println(data.getBDim());*/
	    
	    FileWriter wr = new FileWriter("big6.dat");
		 BufferedReader domain = new BufferedReader(new FileReader("big5.dat"));
		 int num = Integer.parseInt(domain.readLine());
		 for(int i=0;i<num;i++)
		 {
			 String s = domain.readLine();
			 String[] tokens = s.split("\\s+");
			 int a=Integer.parseInt(tokens[3]);
			 if(a==1||a==2)
				 wr.write(s+"\n");
		 }
		 domain.close();
		 wr.close();
	}
}
