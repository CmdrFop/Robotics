package RoboticsPD;

import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
//test1
public class UltraSample 
{
	public static EV3UltrasonicSensor sensor = new EV3UltrasonicSensor(SensorPort.S3);
	SampleProvider distancer = sensor.getDistanceMode();
    float[] readings;
    float distance;
    
	public UltraSample()
	{
		readings = new float[distancer.sampleSize()];
	}
	
	public float getDistance()
	{
		distancer.fetchSample(readings, 0);
		distance = (float) readings[0];
		return distance;
	}
}
