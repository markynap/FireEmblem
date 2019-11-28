package extras;

import java.util.List;
import java.util.LinkedList;

public class ThreadPool extends ThreadGroup{

	public static IDAssigner poolID = new IDAssigner(1);
	
	public boolean alive;
	private List<Runnable> taskList;
	private int id;
	
	public ThreadPool(int numThreads) {
		super("ThreadPool-" + poolID.next());
		this.id = poolID.getBaseID();
		setDaemon(true);
		taskList = new LinkedList<>();
		alive = true;
		for (int i = 0; i < numThreads; i++) {
			new PooledThread(this).start();;
		}
		
	}
	
	public synchronized void runTask(Runnable task) {
		if (!alive) throw new IllegalStateException("ThreadPool-" + id + " is dead");
		if (task != null) {
			taskList.add(task);
			notify();
		}
	}
	
	public synchronized void close() {
		if (!alive) return;
		alive = false;
		taskList.clear();
		interrupt();
	}
	
	public void join() {
		synchronized(this) {
			alive = false;
			notifyAll();
		}
		
		Thread[] threads = new Thread[activeCount()];
		int count = enumerate(threads);
		
		for (int i = 0; i < count; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				System.out.println("Went wrong in join method!");
			}
		}
		
	}
	

	public synchronized Runnable getTask() throws InterruptedException {
		while (taskList.size() == 0) {
			if (!alive) return null;
			wait();
		}
		return taskList.remove(0);
	}
	
}
