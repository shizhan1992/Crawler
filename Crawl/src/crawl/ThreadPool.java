package crawl;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy;

public class ThreadPool extends ThreadPoolExecutor{
	public boolean hasFinish = false; 
	private boolean isClear = false;
	public ThreadPool(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
		// TODO Auto-generated constructor stub
	}

	public ThreadPool(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
			RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
				threadFactory, handler);
		// TODO Auto-generated constructor stub
	}

	public ThreadPool(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
				threadFactory);
		// TODO Auto-generated constructor stub
	}

	public ThreadPool(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		// TODO Auto-generated constructor stub
	}

	
    protected void afterExecute(Runnable r, Throwable t) {
        // TODO Auto-generated method stub
        super.afterExecute(r, t);    
        synchronized(AllStock.tt){
        //System.out.println("自动调用了....afterEx 此时getActiveCount()值:"+this.getActiveCount()); 
        if(this.getActiveCount() == 1)//已执行完任务之后的最后一个线程
        {
//        	AllStock.tt.hasFinish=true;
        	AllStock.tt.notifyAll(); 
        }
      }
    }
     
     public void isEndTask() { 
    	//System.out.println("------------------------------------------------");
          synchronized(AllStock.tt){      
//            while (AllStock.tt.hasFinish==false) { 
               //System.out.println("等待线程池所有任务结束: wait...");          
          try { 
        	  AllStock.tt.wait(); 
          } 
          catch (InterruptedException e) { 
          e.printStackTrace(); 
          }
//          AllStock.tt.hasFinish = false;
          //System.out.println("线程池释放！！！");
         }
//      }
  }
     protected void afterExecute1() {
    	 try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        synchronized(AllStock.ss){
        	for(int i=0; i<AllStock.stocknumber; i++){
    			if(!MongoDB.topicSet.get(AllStock.codes[i]).isEmpty()||!MongoDB.publisherSet.get(AllStock.codes[i]).isEmpty()
    					||!MongoDB.commentSet.get(AllStock.codes[i]).isEmpty()){
    				try {
    					Thread.sleep(100);
    				} catch (InterruptedException e) {
    					e.printStackTrace();
    				}
    				i--;
    				continue;
    			};
    		}
        	this.isClear=true;
        	this.notifyAll(); 
         }
      }
     
      public void isClear() { 
           synchronized(this){      
             while (this.isClear==false) { 
            	 try {
            		 afterExecute1();
            		 this.wait(); 
            	 }catch (InterruptedException e) { 
            		 e.printStackTrace(); 
            	 }
             }
           }
      }
}
