package Records;

public class Query {
	public int p00;
	public int p01;
	public int p10;
	public int p11;
	public int qian;
	public int hou;
	public Query(int p00,int p01,int p10,int p11,int qian,int hou)
	{
		this.p00=p00;
		this.p01=p01;
		this.p10=p10;
		this.p11=p11;
		this.qian = qian;
		this.hou = hou;
	}
	public Query(Query query)
	{
		p00=query.p00;
		p01=query.p01;
		p10=query.p10;
		p11=query.p11;
		qian=query.qian;
		hou=query.hou;
	}
}
