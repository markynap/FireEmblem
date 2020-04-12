package extras;

public class PooledThread extends Thread{
	
	private static IDAssigner threadID = new IDAssigner(1);
	private ThreadPool pool;
	
	public PooledThread(ThreadPool pool) {
		super(pool, "PooledThread-" + threadID.next());
		this.pool = pool;
	}
	
	public void run() {
		if (interrupted())
			notify();
		while (!isInterrupted()) {
			Runnable task = null;
			try {
				task = pool.getTask();
			} catch (InterruptedException e) {
				System.err.println("Thread was interrupted!");
			}
			if (task == null) return;
			
			try {
				task.run();
			} catch (Throwable t) {
				pool.uncaughtException(this, t);
			}
			
		}
	}
	public void stopTask() {
		if (isInterrupted()) return;
		this.interrupt();
		
	}
}
