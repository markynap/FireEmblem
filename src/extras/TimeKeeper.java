package extras;


public class TimeKeeper {

	public long currentTime, gameTime;
	
	public TimeKeeper() {
		gameTime = System.currentTimeMillis();
	}
	/** Sets the current Session Time equal to the systems current milisecond count */
	public void startSession() {
		currentTime = System.currentTimeMillis();
	}
	
	/** Number of seconds that this game has been running */
	public double getGameTime() {
		return (System.currentTimeMillis() - gameTime)/1000.0;
	}
	/** Returns the number of miliseconds that have elapsed since startSession() was last invoked */
	public double sessionAt() {
		return ((double)(System.currentTimeMillis() - currentTime));
	}
	/** True if the number of miliseconds that have elapsed since startSession() was invoked 
	 *  is greater than or equal to the desired amount of time
	 * @param desiredMiliSeconds - desired amount of miliseconds to have been elapsed
	 * @return
	 */
	public boolean sessionAtDesiredTime(double desiredMiliSeconds) {
		return (sessionAt() >= desiredMiliSeconds);
	}
	
	public static void threadWait(long miliseconds) {
		long current = System.currentTimeMillis();
		long end = current + miliseconds;
		
		while (current < end) {
			current = System.currentTimeMillis();
		}
		
	}
	
}
