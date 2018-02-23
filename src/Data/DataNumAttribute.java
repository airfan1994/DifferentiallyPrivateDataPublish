package Data;

public class DataNumAttribute{
private double step;
private int d;
private double start;
private double end;
	public DataNumAttribute()
	{
		
	}
	public DataNumAttribute(double step,int d,double start,double end)
	{
		this.step=step;
		this.d=d;
		this.start=start;
		this.end=end;
	}
	public DataNumAttribute(int d,double start,double end)
	{
		this.step=(end-start)/d;
		this.d=d;
		this.start=start;
		this.end=end;
	}
	public int getD()
	{
		return d;
	}
	public double getStep()
	{
		return step;
	}
	public double getStart() 
	{
		return start;
	}
}
