package RoboticsPD;

import lejos.hardware.Button;

public class Main
{
	public final static float WHITE_SENSOR_DATA = 2.7f;
	
	public static void main(String[] args) 
	{
		Tasks tasks = new Tasks();
		PDController pd = new PDController();
		
		
		//tasks.grid(WHITE_SENSOR_DATA);
		//tasks.testRangeNew(WHITE_SENSOR_DATA);
		//tasks.testTurnCheck();
		
		while(true)
		{
			//tasks.playDre();
			//tasks.followLine(WHITE_SENSOR_DATA, pd);
			//tasks.seekOutObject();
			//tasks.playCena();
			//tasks.wander(WHITE_SENSOR_DATA);
			tasks.printColours();
			//tasks.grab();
			//tasks.ungrab();
			//tasks.readGyro();
			//tasks.lineObject(WHITE_SENSOR_DATA);
			//tasks.testRange(WHITE_SENSOR_DATA);
			if (Button.ESCAPE.isDown())
				break;
			//test1
		}
	}
}
