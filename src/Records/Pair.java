package Records;

public class Pair {
public int idA;
public int idB;
	public Pair(int A,int B)
	{
		idA=A;
		idB=B;
	}
	@Override
	public boolean equals(Object Bths) {
        if (Bths == null)
            return false;
        
        Pair B = (Pair) Bths;
        if(((idA==B.idA)&&(idB==B.idB))||((idB==B.idA)&&(idA==B.idB)))
		{
			return true;
		}
		else
		{
			return false;
		}
    }
	@Override  
    public int hashCode() {  
        // TODO Auto-generated method stub  
        return idA+idB;  
    } 
}
