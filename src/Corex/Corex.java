package Corex;

import java.util.HashMap;
import java.util.HashSet;

import Site.Groups;
import Site.Node;
import Site.TreeNode;


public class Corex {
	private Groups group;
	private int[][]	matrix;
	private int times;
	private int n_sample;
	private int n_visible;
	private int dim_visible;
	private int dim_hidden; 
	private int n_events;
    private int n_hidden = 2; 
    private int balance = 0;
    private HashMap<Integer,Node> Layer;
    private HashMap<Integer,Integer> NodeMap;
    private double[][][] log_p_y_given_x_unnorm;
    private double[][][] p_y_given_x;
    private int[][] alpha;
    private double[][] logz;
    private double[][] p_x;
    private double[] entropy;
    private double[][] pseudo_counts;
    private double[][] log_p_y;
    private double[][][] log_marg;
    private double[][][] vec;
    private int max_iter=100;
    private double[][] smis;
    private int[][] X_event;
   // private double[][][] smist;
    private double[][] mis;
    private double[][] alpha_req;
    private double[][][] ji1;
    private double[] tcs;
    private HashMap<Integer,Double> tc_history;
    private HashMap<Integer,Node> hiddenNodes; 
    //private max_iter = 2;
	public Corex()
	{
		
	}
	public Corex(Groups group,int dim_hidden,int max_it)
	{
		this.group=group;
		this.dim_hidden=dim_hidden;
		n_sample = group.getSampleSize();
		this.max_iter=max_it;
	}
	public Corex(int[][] matrix,int[][] alpha,int dim_hidden,int n_hidden,int n_sample,int n_visible,int max_it)
	{
		this.matrix = matrix;
		this.alpha = alpha;
		this.dim_hidden=dim_hidden;
		this.n_hidden = n_hidden;
		this.n_sample = n_sample;
		this.n_visible=n_visible;
		this.max_iter=max_it;
	}
	public static void main(String[] args)
	{
		int[][] aaa = {{1,1,1,0,0},{0,0,0,1,0},{0,1,0,1,0},{1,0,0,1,1},{1,1,1,1,1},{1,1,0,1,1}};
		int[][] bbb = {{1,0,1,0,1},{0,1,0,1,0}};
		Corex corex = new Corex(aaa,bbb,2,2,6,5,90);
		corex.fit(2);
	}
	public void fit(int type)
	{
		init(type);
		times=0;
		for(;times<max_iter;times++)
		{
			cal_p_y();
			cal_log_marg();
			//calculate_mis();
			calculate_latent();
			update_tc();
			if(judgeconvergence()==true)
				break;
		}
		sort_and_output();
	}
	public void init(int type)
	{
		dim_visible = 2;
		dim_hidden = 2;
		n_hidden = 2;
		if(type==0){
		Layer = group.getLayer();
		NodeMap = new HashMap<Integer,Integer>();
		n_visible = Layer.size();
		HashMap<Integer,HashSet<Integer>> layergroup=group.getGroup();
		n_hidden = layergroup.size();
		alpha = new int[n_hidden][n_visible]; 
		matrix = new int[n_sample][n_visible];
		
		
		
		int id=0;
		for(Integer p:layergroup.keySet())       //group分组组号从1开始
		{
			//System.out.println("size:"+layergroup.get(p).size());
			for(Integer q:layergroup.get(p))
			{
				alpha[p-1][id] = 1;
				NodeMap.put(q, id);
				int[] tvec = Layer.get(q).getVec();
				for(int i=0;i<n_sample;i++){
					matrix[i][id] = tvec[i];
				}
				id++;
			}
		}
		}
		//else
		//{
			
		//}
		n_events = n_visible * dim_visible;
		//System.out.println(n_events);
		log_p_y_given_x_unnorm = new double[n_hidden][n_sample][dim_hidden];
		double yinzi = -Math.log(dim_hidden);
		logz = new double[n_hidden][n_sample];
		for(int x=0;x<n_hidden;x++)
		{
			for(int y=0;y<n_sample;y++)
			{
				double sum=0;
				for(int z=0;z<dim_hidden;z++)
				{
					log_p_y_given_x_unnorm[x][y][z] = yinzi*(Math.random()+0.5);
					sum+=log_p_y_given_x_unnorm[x][y][z];
				}
				logz[x][y] = Math.exp(sum);
			}
		}
		if(type==2)
		{
			n_hidden = 2;
			double ran[][][]=new double[][][]{
					{{0.5,0.6},{0.7,0.8},{0.88,0.9},{0.52,0.62},{0.72,0.82},{0.92,0.54}},
					{{0.56,0.66},{0.76,0.86},{0.96,0.58},{0.68,0.78},{0.88,0.98},{0.6,0.7}}
			};
			for(int x=0;x<n_hidden;x++)
			{
				for(int y=0;y<n_sample;y++)
				{
					double sum=0;
					for(int z=0;z<dim_hidden;z++)
					{
						log_p_y_given_x_unnorm[x][y][z] = yinzi*(ran[x][y][z]);
						sum+=Math.exp(log_p_y_given_x_unnorm[x][y][z]);
					}
					logz[x][y] = Math.log(sum);
				}
			}
			/*int array[][][] = new int[][][]{                    // 创建并初始化数组  
	                { { 1, 2, 3 }, { 4, 5, 6 } },  
	                { { 7, 8, 9 }, { 10, 11, 12 } },  
	                { { 13, 14, 15 }, { 16, 17, 18 } }  
	            };*/
			//self.n_hidden, self.n_samples, self.dim_hidden)    -> 2 6 2
			//ran = np.array([[[0.5,0.6],[0.7,0.8],[0.88,0.9],[0.52,0.62],[0.72,0.82],[0.92,0.54]],[[0.56,0.66],[0.76,0.86],[0.96,0.58],[0.68,0.78],[0.88,0.98],[0.6,0.7]]], dtype = float)
		}
		//printMatrix(log_p_y_given_x_unnorm,n_hidden, n_sample, dim_hidden);
		//printMatrix(logz,n_hidden, n_sample);
		p_y_given_x = new double[n_hidden][n_sample][dim_hidden];
		for(int x=0;x<n_hidden;x++)
		{
			for(int y=0;y<n_sample;y++)
			{
				for(int z=0;z<dim_hidden;z++)
				{
					p_y_given_x[x][y][z] = Math.exp(log_p_y_given_x_unnorm[x][y][z]-logz[x][y]);
				}
			}
		}
		//printMatrix(p_y_given_x,n_hidden, n_sample, dim_hidden);
		X_event = new int[n_events][n_sample];
		//System.out.println(n_events);
		//System.out.println(n_sample);
		//System.out.println(n_visible);
		p_x = new double[n_visible][dim_visible];
		for(int n=0;n<n_visible;n++)
		{
			for(int m=0;m<n_sample;m++)
			{
				X_event[dim_visible*n+matrix[m][n]][m]=1;
				p_x[n][matrix[m][n]]++;
			}
		}
		
		int[] ppx = new int[n_visible];
		for(int m=0;m<n_visible;m++)
		{
			for(int n=0;n<dim_visible;n++)
			{
				ppx[m]+=p_x[m][n];
			}
		}
		entropy = new double[n_visible];
		for(int m=0;m<n_visible;m++)
		{
			double sum=0.0;
			for(int n=0;n<dim_visible;n++)
			{
				p_x[m][n]=p_x[m][n]/ppx[m];
				sum+=-p_x[m][n]*Math.log(p_x[m][n]);
			}
			entropy[m] = sum;
		}
		//printMatrix(p_x,n_visible,dim_visible);
		//printMatrix(entropy,n_visible);
	    pseudo_counts = new double[n_hidden][dim_hidden];
	    log_p_y =new double[n_hidden][dim_hidden];
	    log_marg = new double[n_hidden][n_events][dim_hidden];
	    tc_history = new HashMap<Integer,Double>();
		// = -Math.log(dim_hidden)*;
	}
	public void cal_p_y()
	{
		/*
		 pseudo_counts= 0.001 + sum(p_y_given_x,2); %calcu the p(yj),用的公式8的后半部分
		 p_y_given_x = new double[n_hidden][n_sample][dim_hidden];
         pseudo_counts=reshape(pseudo_counts,n_hidden,dim_hidden);
         for m=1:n_hidden
           pseudo_logy = sum(pseudo_counts,2);
           for n=1:dim_hidden
                log_p_y(m,n)=log(pseudo_counts(m,n)/pseudo_logy(m));
           end
         end
		 */
		double[] pseudo_logy = new double[n_hidden];
		for(int m=0;m<n_hidden;m++)
		{
			double sums = 0.0;
			for(int p=0;p<dim_hidden;p++)
			{
				double sum=0.0;
				for(int n=0;n<n_sample;n++)
				{
					sum+=p_y_given_x[m][n][p];
				}
				pseudo_counts[m][p] = 0.001+sum;
				sums+=0.001+sum;
			}
			pseudo_logy[m] = sums;
		}
		for(int m=0;m<n_hidden;m++)
		{
			for(int n=0;n<dim_hidden;n++)
			{
				 log_p_y[m][n]=Math.log(pseudo_counts[m][n]/pseudo_logy[m]);
			}
		}
		//printMatrix(log_p_y,n_hidden,dim_hidden);
	}
	public void cal_log_marg()
	{
		/*
		ji = zeros(n_events,n_hidden,dim_hidden);
        for m=1:n_events
           for n=1:n_hidden
              for q=1:dim_hidden
                  sums=0;
                  for p=1:n_samples
                      aa=X_event(m,p)*p_y_given_x(n,p,q); 
                      sums=sums+aa;
                  end
                  ji(m,n,q)=sums;
              end
           end
        end
        psuedo_ji = zeros(n_hidden,n_events,dim_hidden);
        for m=1:n_events
           for n=1:n_hidden
              for q=1:dim_hidden
                 psuedo_ji(n,m,q) = ji(m,n,q)+0.001;
              end
           end
        end
        swss=sum(psuedo_ji,3);
        log_marg = zeros(n_hidden,n_events,dim_hidden); 
        log_sum_pseudo = log(sum(psuedo_ji,3));
        for m=1:n_hidden
           for n=1:n_events 
              for q=1:dim_hidden
                 log_marg(m,n,q) = log(psuedo_ji(m,n,q))-log_sum_pseudo(m,n)-log_p_y(m,q);  
              end
           end
        end
		 */
		double[][][] ji = new double[n_events][n_hidden][dim_hidden];
		double[][][] psuedo_ji = new double[n_hidden][n_events][dim_hidden];
		double[][] log_sum_pseudo = new double[n_hidden][n_events];
		for(int m=0;m<n_events;m++)
		{
			for(int n=0;n<n_hidden;n++)
			{
				 double swss=0.0;
		         for(int p=0;p<dim_hidden;p++)
		         {
		        	 double sums=0.0;
		        	 for(int q=0;q<n_sample;q++)
		        	 {
		        		 sums+=X_event[m][q]*p_y_given_x[n][q][p]; 
		        		 //p_y_given_x = new double[n_hidden][n_sample][dim_hidden];
		        		 //X_event = new int[n_events][n_sample];
		        		 
		        	 }
		        	 ji[m][n][p]=sums;
		        	 psuedo_ji[n][m][p] = ji[m][n][p]+0.001;
		        	 swss+=psuedo_ji[n][m][p];
		         }
		         log_sum_pseudo[n][m] = Math.log(swss);
			}
		}
		vec = new double[n_hidden][n_events][dim_hidden];
		smis = new double[n_hidden][n_events];
		for(int m=0;m<n_hidden;m++)
		{
			for(int n=0;n<n_events;n++)
			{
				double sum=0.0;
				for(int q=0;q<dim_hidden;q++)
				{
					log_marg[m][n][q] = Math.log(psuedo_ji[m][n][q])- log_sum_pseudo[m][n]-log_p_y[m][q];
					vec[m][n][q]= Math.exp(log_marg[m][n][q]+log_p_y[m][q]);
					sum+=log_marg[m][n][q]*vec[m][n][q];
				}
				smis[m][n]=sum;
			}
		}
		//printMatrix(log_marg,n_hidden,n_events,dim_hidden);
	}
	public void calculate_latent()
	{
		alpha_req = new double[n_hidden][n_visible*dim_visible];
		for(int m=0;m<n_hidden;m++)
		{
			for(int n=0;n<n_visible;n++)
			{
				for(int q=0;q<dim_visible;q++)
				{
					alpha_req[m][n*dim_visible+q]=alpha[m][n];
				}
			}
		}
		
		ji1 = new double[n_hidden][n_visible*dim_visible][dim_hidden];
		for(int m=0;m<n_hidden;m++)
		{
			for(int n=0;n<n_events;n++)
			{
				for(int q=0;q<dim_hidden;q++)
				{
					ji1[m][n][q] = log_marg[m][n][q]*alpha_req[m][n];
				}
			}
		}
		for(int m=0;m<n_sample;m++)
		{
			for(int n=0;n<n_hidden;n++)
			{
				double esum=0.0;
				for(int q=0;q<dim_hidden;q++)
				{
					double ssum=0.0;
					for(int p=0;p<n_events;p++)
					{
						ssum+=X_event[p][m]*ji1[n][p][q]; 
					}
					log_p_y_given_x_unnorm[n][m][q]=log_p_y[n][q]*(1-balance)+ssum;
					//log_p_y_given_x_unnorm = new double[n_hidden][n_sample][dim_hidden];
					//log_p_y =new double[n_hidden][dim_hidden];
					esum += Math.exp(log_p_y_given_x_unnorm[n][m][q]);
				}
				logz[n][m] = Math.log(esum);
				//n_hidden,n_sample
			}
		}
		for(int m=0;m<n_hidden;m++)
		{
			for(int n=0;n<n_sample;n++)
			{
				for(int q=0;q<dim_hidden;q++)
				{
					p_y_given_x[m][n][q] = Math.exp(log_p_y_given_x_unnorm[m][n][q]-logz[m][n]); 
				}
			}
		}
		//printMatrix(p_y_given_x,n_hidden,n_sample,dim_hidden);
		//printMatrix(logz,n_hidden,n_sample);
		/*
		 * alpha_req = zeros(n_hidden,n_visible*dim_visible);
        for m=1:n_hidden
           for n=1:n_visible
              for q=1:dim_visible
                  alpha_req(m,(n-1)*dim_visible+q)=alpha(m,n);
              end
           end
        end
        log_p_y_given_x_unnorm = new double[n_hidden][n_sample][dim_hidden];
        ji1 = zeros(n_hidden,n_visible*dim_visible,dim_hidden);
        for m=1:n_hidden
            for n=1:n_visible*dim_visible
                for q=1:dim_hidden
                    ji1(m,n,q) = log_marg(m,n,q)*alpha_req(m,n);
                end
            end
        end
        ji3 = zeros(n_hidden,n_samples,dim_hidden);
        for m=1:n_samples
            for n=1:n_hidden
                for q=1:dim_hidden
                    ssum=0;
                    for p=1:n_visible*dim_visible
                        kk = X_event(p,m)*ji1(n,p,q);
                        ssum=ssum+kk;
                    end
                    %ji2(m,n,q) = ssum;
                    ji3(n,m,q)=log_p_y(n,q)*(1-balance)+ssum;
                end
            end
        end
        log_p_y_given_x_unnorm=ji3;
        logz=log(sum(exp(log_p_y_given_x_unnorm),3));
        logz=reshape(logz,n_hidden,n_samples)
        p_y_given_x = ones(n_hidden, n_samples, dim_hidden);
        for m=1:n_hidden
            for n=1:n_samples
                for q=1:dim_hidden
                         p_y_given_x(m,n,q) = exp(log_p_y_given_x_unnorm(m,n,q)-logz(m,n)); 
                end
            end
        end
		 */
	}
	public void calculate_mis()
	{
		//smist = new double[n_hidden][n_visible][dim_visible];
		mis = new double[n_hidden][n_visible];
		for(int m=0;m<n_hidden;m++)
		{
			for(int n=0;n<n_visible;n++)
			{
				double sum=0.0;
				for(int q=0;q<dim_visible;q++)
				{
					//smist[m][n][q]=smis[m][n*dim_visible-dim_visible+q]*p_x[n][q];
					sum+=smis[m][n*dim_visible+q]*p_x[n][q];
				}
				mis[m][n]=sum/entropy[n];
			}
		}
		//printMatrix(mis,n_hidden,n_visible);
		/*
		 * vec = zeros(n_hidden,n_events,dim_hidden);
            for m=1:n_hidden
               for n=1:n_events
                  for q=1:dim_hidden
                     
                  end
               end
            end
            vec = new double[n_hidden][n_events][dim_hidden];
            log_marg = new double[n_hidden][n_events][dim_hidden];
            smis = sum(vec.*log_marg, 3);
            smist = ones(n_hidden, n_visible,dim_visible);
            for m=1:n_hidden
               for n=1:n_visible
                  for q=1:dim_visible
                     smist(m,n,q)=smis(m,n*dim_visible-dim_visible+q); 
                  end
               end
            end
            %smist
            
            for m=1:n_hidden
                for n=1:n_visible
                   for q=1:dim_visible
                       smist(m,n,q)=smist(m,n,q)*p_x(n,q);
                   end
                end
            end
            mis = sum(smist,3);
            for m=1:n_hidden
                for n=1:n_visible
                   mis(m,n)=mis(m,n)/entropy_x(n);
                end
            end
		 */
		
		
	}
	public void update_tc()
	{
		/*
		 for m=1:n_hidden
           sssum=0;
           for n=1:n_samples
               sssum=sssum+logz(m,n);
           end
           tcs(m)=sssum/n_samples;
        end
		 */
		tcs =new double[n_hidden];
		double sum = 0.0;
		for(int m=0;m<n_hidden;m++)
		{
			double ssum=0.0;
			for(int n=0;n<n_sample;n++)
			{
				ssum+=logz[m][n];
			}
			tcs[m] = ssum/n_sample;
			sum+=tcs[m];
		}
		//printMatrix(tcs,n_hidden);
		//System.out.println(sum);
		tc_history.put(times, sum);
	}
	public boolean judgeconvergence()
	{
		if(times<10)
			return false;
		else{
			double dist =0.0;
			double s1=0.0;
			double s2=0.0;
			for(int i=0;i<5;i++)
			{
				s1+=tc_history.get(times-i);
				s2+=tc_history.get(times-5-i);
			}
			dist = s1-s2;
			if(Math.abs(dist)<1e-5)
				return true;
			else
				return false;
		}
	}
	public void printMatrix(int[] mat,int a)
	{
			for(int x=0;x<a;x++)
			{
					System.out.print(mat[x]+",");
			}
	}
	public void printMatrix(double[] mat,int a)
	{
			for(int x=0;x<a;x++)
			{
					System.out.print(mat[x]+",");
			}
	}
	public void printMatrix(int[][] mat,int a,int b)
	{
			for(int x=0;x<a;x++)
			{
				for(int y=0;y<b;y++)
				{
					System.out.print(mat[x][y]+",");
				}
				System.out.println();
			}
	}
	public void printMatrix(int[][][] mat,int a,int b,int c)
	{
		for(int x=0;x<a;x++)
		{
			System.out.println("line:"+x);
			for(int y=0;y<b;y++)
			{
				for(int z=0;z<c;z++)
				{
					System.out.print(mat[x][y][z]+",");
				}
				System.out.println();
			}
		}
	}
	public void printMatrix(double[][] mat,int a,int b)
	{
			for(int x=0;x<a;x++)
			{
				for(int y=0;y<b;y++)
				{
					System.out.print(mat[x][y]+",");
				}
				System.out.println();
			}
	}
	public void printMatrix(double[][][] mat,int a,int b,int c)
	{
		for(int x=0;x<a;x++)
		{
			System.out.println("line:"+x);
			for(int y=0;y<b;y++)
			{
				for(int z=0;z<c;z++)
				{
					System.out.print(mat[x][y][z]+",");
				}
				System.out.println();
			}
		}
	}
	public void sort_and_output()
	{
		hiddenNodes = new HashMap<Integer,Node>();
		//return np.argmax(p_y_given_x, axis=2).T
		//p_y_given_x = new double[n_hidden][n_sample][dim_hidden];
		for(int i=0;i<n_hidden;i++)
		{
			int[] hvec = new int[n_sample];
			for(int t=0;t<n_sample;t++)
			{
				double max=0.0;
				int maxid = 0;
				for(int m=0;m<dim_hidden;m++)
				{
					if(p_y_given_x[i][t][m]>max)
					{
						max = p_y_given_x[i][t][m];
						maxid = m;
					}
				}
				hvec[t] = maxid;
			}
			
			Node node  = new Node(hvec);
			hiddenNodes.put(i+1, node);
		}
	}
	public HashMap<Integer,Node> getHiddenNodes()
	{
		return hiddenNodes;
	}
	public int getNHidden()
	{
		return n_hidden;
	}
}
