package RoboticsPD;

class PDController 
{
	final float P_CONTROL = 65;
	final float D_CONTROL = 500;
	float TARGET_POWER = 360;
	float SPEED_CAP = 500;
	float turn, leftSpeed, rightSpeed;
	float lastErr = 0;
	float der = 0;
	float sensorData;
	
	public void run(float[] sensorData, float whiteData) 
	{
		float redData = sensorData[0];
		float err = redData*100 - whiteData;
		float der = err - lastErr;
		lastErr = err;
		turn = P_CONTROL * err + D_CONTROL * der;
		if(err < 0)
			turn *= Math.abs(err*1.5); // black
		else
			turn *= Math.abs(err/3); // white
		
		//System.out.println("err " + err);
		//System.out.println("turn " + turn);
		
		//System.out.println(leftSpeed);
		
		if(leftSpeed > SPEED_CAP)
			leftSpeed = SPEED_CAP;
		if(leftSpeed < -SPEED_CAP)
			leftSpeed = SPEED_CAP;
		if(rightSpeed > SPEED_CAP)
			rightSpeed = SPEED_CAP;
		if(rightSpeed < -SPEED_CAP)
			rightSpeed = -SPEED_CAP;
		
		leftSpeed = TARGET_POWER + turn;
		rightSpeed = TARGET_POWER - turn;
		
		Robot.drive(leftSpeed, rightSpeed*0.6f);
		//test
	}
}