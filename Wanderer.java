package RoboticsPD;

import lejos.utility.Stopwatch;
//test
public class Wanderer 
{
	final static float baseSpeed = 200;
	static Stopwatch stopwatch = new Stopwatch();
	
	public static void run(float[] sensorData, float whiteData)
	{		
		float greenData = sensorData[1];
		if(greenData*100 < whiteData)		
			Robot.drive(baseSpeed, baseSpeed);
		else
		{
			goBack();
			turnAround();
		}
	}
	
	public static void stop()
	{
		Robot.drive(0, 0);
	}
	
	public static void goBack()
	{	
		stopwatch.reset();
		while(stopwatch.elapsed() < 1000)
			Robot.drive(-baseSpeed, -baseSpeed);
	}
	
	public static void turnAround()
	{
		stopwatch.reset();
		while(stopwatch.elapsed() < 300)
		{
			Robot.drive(-baseSpeed*2, baseSpeed*2);
		}
	}

}
