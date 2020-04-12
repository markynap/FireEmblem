package extras;


public class TimeKeeper {

	public long currentTime, gameTime, tempTime;
	
	public TimeKeeper() {
		gameTime = System.currentTimeMillis();
	}
	public void startSession() {
		currentTime = System.currentTimeMillis();
	}

	public void startNewSession() {
		tempTime = System.currentTimeMillis();
	}
	public void resetSessions() {
		currentTime = 0;
		tempTime = 0;
	}
	public void resetNewestSession() {
		tempTime = 0;
	}
	public double getGameTime() {
		return ((double)(System.currentTimeMillis() - gameTime)/1000);
	}
	
	public double sessionAt() {
		return ((double)(System.currentTimeMillis() - currentTime)/1000);
	}
	public boolean sessionAtDesiredTime(double desiredSeconds) {
		return (sessionAt() >= desiredSeconds);
	}
	
	public double newSessionAt() {
		return (double)((System.currentTimeMillis() - tempTime)/1000);
	}
	public boolean newSessionAtDesiredTime(double desiredSeconds) {
		return (newSessionAt() >= desiredSeconds);
	}
	
}
