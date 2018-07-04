package RoboticsPD;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;
class colorSample 
{
	private static EV3ColorSensor colorSensor = new EV3ColorSensor(SensorPort.S2);
	SampleProvider redProvider, rgbProvider;
	float[] redSample, rgbSample;
	public colorSample()
	{
		redProvider = colorSensor.getRedMode();
		redSample = new float[redProvider.sampleSize()];
		
		rgbProvider = colorSensor.getRGBMode();
		rgbSample = new float[rgbProvider.sampleSize()];
	}
	
	public float[] redSample()
	{
		redProvider.fetchSample(redSample, 0);
		return redSample;
	}
	
	public float[] rgbSample()
	{
		rgbProvider.fetchSample(rgbSample, 0);
		return rgbSample;
	//Test
	}
}