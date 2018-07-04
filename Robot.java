package RoboticsPD;

import lejos.hardware.motor.*;
import lejos.hardware.port.SensorPort;

public class Robot 
{	
	public static void drive(float l, float r) 
	{
		// B-> to left C-> to right

		Motor.B.setSpeed(Math.abs(l));
		Motor.C.setSpeed(Math.abs(r));
		if (l > 0) {
			Motor.B.forward();
		} 
		else if (l < 0) 
		{
			Motor.B.backward();
		} 
		else 
		{
			Motor.B.stop(true);
		}

		if (r > 0) 
		{
			Motor.C.forward();
		} 
		else if (r < 0) 
		{
			Motor.C.backward();
		} 
		else 
		{
			Motor.C.stop(true);
		}
	}
	
	public static float[] getData()
	{
		colorSample sample = new colorSample();
		float[] array = sample.rgbSample();
		//System.out.println(array[0]);
		
		return array;
	}
	
	public static float getUltraDistance()
	{
		UltraSample ultraSample = new UltraSample();
		float distance = ultraSample.getDistance();
		
		return distance;
	}	
}
