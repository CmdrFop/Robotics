package RoboticsPD;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.Sounds;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;
import lejos.hardware.motor.*;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Stopwatch;

public class Tasks {
	static Stopwatch stopwatch = new Stopwatch();
	EV3GyroSensor gyro = new EV3GyroSensor(SensorPort.S1);
	SampleProvider gyroProvider = gyro.getAngleMode();

	void followLine(float whiteData, PDController pd) {
		float[] sensorData = Robot.getData();
		pd.run(sensorData, whiteData);
	}

	void wander(float whiteData) {
		testRangeNew(whiteData);
	}

	void evaluateAndAct(float[] sensorData, float whiteData) {
		System.out.println(sensorData[0] + "   " + sensorData[2]);
		if(sensorData[1]*100 > whiteData) {
			stopwatch.reset();
			while(stopwatch.elapsed() < 1000)
				Robot.drive(-200, -200);
			stopwatch.reset();
			while(stopwatch.elapsed() < 300)
			{
				Robot.drive(-400, 400);
			}
		} else if (sensorData[2] > sensorData[0]) {
			grab();
			ungrab();
		} else if (sensorData[0] > sensorData[2]) {
			Robot.drive(-10, -10);
			Robot.drive(10, 10);
			Sound.playTone(420, 100);
		} 

		Wanderer.goBack();
		Wanderer.turnAround();
	}
	
	void printColours() {
		float[] array = Robot.getData();
		System.out.println((int) array[0] * 100 + " "+ (int)array[1]* 100 +" "+ (int)array[2]* 100);
	}

	void lineObject(float whiteData) {
		PDController pd = new PDController();
		while (Robot.getUltraDistance() * 100 > 40) {
			pd.run(Robot.getData(), whiteData);
			if (Button.ESCAPE.isDown())
				break;
		}
		Robot.drive(100, 100);
		goToObject();

	}

	void turn() {
		Robot.drive(-100, 100);
	}

	void seekOutObject() {
		float distance = Robot.getUltraDistance();

		LCD.clear();
		LCD.drawString("Distance" + distance, 0, 0);

		while (distance * 100 > 300) {
			System.out.println("Distance" + distance * 100);
			Robot.drive(100, 100);

			if (Button.ESCAPE.isDown())
				break;
			distance = Robot.getUltraDistance();
		}
		Sound.beep();
		Robot.drive(0, 0);

		// turn();

		goToObject();
	}

	void goToObject() {
		float[] color = Robot.getData();
		while (color[0] * 100 < 2.5f && color[2] * 100 < 0.8f && color[0] * 100 + color[2] * 100 < 2.5f) {
			// System.out.println("Distance" + distance*100);
			System.out.println(color[0] * 100 + " " + color[2] * 100);
			Robot.drive(150, 155);
			if (Button.ESCAPE.isDown())
				break;
			// distance = Robot.getUltraDistance();
			color = Robot.getData();
		}
		// Sound.beep();
//		evaluateAndAct(Robot.getData());
	}

	void grab() {
		Motor.A.setSpeed(50);
		stopwatch.reset();
		while (stopwatch.elapsed() < 1500)
			Motor.A.forward();
	}

	void ungrab() {
		Motor.A.setSpeed(50);
		stopwatch.reset();
		while (stopwatch.elapsed() < 1500)
			Motor.A.backward();
	}

	void grid(float whiteData) {
		PDController pd = new PDController();
		float[] gyroData = new float[gyroProvider.sampleSize()];
		gyro.fetchSample(gyroData, 0);
		int turns = 0, cooldown = 0;

		while (Robot.getData()[0] * 100 < whiteData) {
			System.out.println(Robot.getData()[0]);
			Robot.drive(100, 100);
			if (Button.ESCAPE.isDown())
				break;
		}

		cooldown = 300;

		while (turns <= 4) {
			System.out.println(turns);
			gyro.fetchSample(gyroData, 0);
			pd.run(Robot.getData(), whiteData);
			if (Math.abs(gyroData[0]) > 130 && cooldown <= 0) {
				turns++;
				cooldown = 700;
			}
			if (Button.ESCAPE.isDown())
				break;
			cooldown--;
		}

		Robot.drive(0, 0);
		stopwatch.reset();
		while (stopwatch.elapsed() < 2000) {
			Robot.drive(-100, 200);
		}
		stopwatch.reset();
		while (stopwatch.elapsed() < 3000) {
			Robot.drive(100, 100);
		}

		int cooldown2 = 300;
		int turns2 = 0;
		while (turns2 <= 3) {
			System.out.println(turns2);
			pd.run(Robot.getData(), whiteData);
			if (Math.abs(gyroData[0]) > 130 && cooldown2 <= 0) {
				turns2++;
				cooldown2 = 700;
			}
			if (Button.ESCAPE.isDown())
				break;
			cooldown2--;
		}

		float distance = Robot.getUltraDistance();
		while (distance > 2.0f) {
			System.out.println("Searching");
			pd.run(Robot.getData(), whiteData);
			distance = Robot.getUltraDistance();
			if (Button.ESCAPE.isDown())
				break;
		}

		Robot.drive(0, 0);
		float mDistance = distance;
		boolean found = true;
		while (found) {
			found = false;
			stopwatch.reset();
			while (stopwatch.elapsed() < 11300) {
				Robot.drive(-100, 100);
				distance = Robot.getUltraDistance();
				System.out.println(found);
				if (distance < mDistance - 0.05f) {
					mDistance = distance;
					found = true;
					Sound.playTone(1000, 200);
					Robot.drive(0, 0);
					break;
				}
			}
			Robot.drive(0, 0);
		}

		Robot.drive(0, 0);
		Sound.playTone(1000, 500);
		float distanceRef;
		boolean wasBroken = false;
		stopwatch.reset();
		while (distance > 0.1f) {
			stopwatch.reset();
			while (stopwatch.elapsed() < 1000) {
				Robot.drive(200, 200);
				distance = Robot.getUltraDistance();
				if (Button.ESCAPE.isDown() || distance < 0.1f) {
					break;
				}
			}
			distanceRef = Robot.getUltraDistance();
			wasBroken = false;
			Robot.drive(0, 0);
			stopwatch.reset();
			while (stopwatch.elapsed() < 1000) {
				Robot.drive(-50, 50);
				System.out.println("1");
				distance = Robot.getUltraDistance();
				System.out.println(distanceRef);
				if (Button.ESCAPE.isDown() || distance < distanceRef || distance < 0.1f) {
					wasBroken = true;
					System.out.println("Break");
					Robot.drive(0, 0);
					break;
				}
			}
			Robot.drive(0, 0);
			stopwatch.reset();
			while (stopwatch.elapsed() < 2000 && !wasBroken) {
				Robot.drive(50, -50);
				System.out.println("2");
				distance = Robot.getUltraDistance();
				System.out.println(distanceRef);
				if (Button.ESCAPE.isDown() || distance < distanceRef || distance < 0.1f) {
					wasBroken = true;
					System.out.println("Break");
					Robot.drive(0, 0);
					break;
				}
			}
			Robot.drive(0, 0);
			stopwatch.reset();
			while (stopwatch.elapsed() < 1000 && !wasBroken) {
				Robot.drive(-50, 50);
				System.out.println("3");
				System.out.println(distanceRef);
				if (Button.ESCAPE.isDown() || distance < 0.1f) {
					System.out.println("Break");
					Robot.drive(0, 0);
					break;
				}
			}
			if (Button.ESCAPE.isDown())
				break;
		}
		Robot.drive(0, 0);
		Sound.playTone(1000, 1000);
		while (true) {
			pd.run(Robot.getData(), whiteData);
			if (Button.ESCAPE.isDown())
				break;
		}
	}

	void testTurnCheck() {
		Robot.drive(0, 0);
		float distance;
		float mDistance = Robot.getUltraDistance();
		boolean found = true;
		while (found) {
			found = false;
			stopwatch.reset();
			while (stopwatch.elapsed() < 11300) {
				Robot.drive(-100, 100);
				distance = Robot.getUltraDistance();
				System.out.println(found);
				if (distance < mDistance - 0.05f) {
					mDistance = distance;
					found = true;
					Sound.playTone(1000, 200);
					Robot.drive(0, 0);
					break;
				}
			}
			Robot.drive(0, 0);
		}
	}

	void testRange(float whiteData) {
		PDController pd = new PDController();
		Robot.drive(0, 0);
		float distance = Robot.getUltraDistance();
		while (distance > 2.0f) {
			System.out.println("Searching");
			pd.run(Robot.getData(), whiteData);
			distance = Robot.getUltraDistance();
			if (Button.ESCAPE.isDown())
				break;
		}

		Robot.drive(0, 0);
		Sound.playTone(1000, 500);
		stopwatch.reset();
		boolean wasBroken = false;
		float lastKnownDistance;
		while (distance > 0.1f) {
			Robot.drive(300, 300);
			distance = Robot.getUltraDistance();
			if (Button.ESCAPE.isDown())
				break;
			if (stopwatch.elapsed() > 1000) {
				if (!wasBroken)
					lastKnownDistance = distance;
				wasBroken = false;
				float mDistance = Robot.getUltraDistance();
				Robot.drive(0, 0);
				stopwatch.reset();
				while (stopwatch.elapsed() < 1000) {
					Robot.drive(-20, 20);
					System.out.println(mDistance);
					lastKnownDistance = mDistance;
					mDistance = Robot.getUltraDistance();
					if (mDistance > lastKnownDistance || mDistance < 0.1f) {
						wasBroken = true;
						break;
					}
				}
				Robot.drive(0, 0);
				stopwatch.reset();
				while (stopwatch.elapsed() < 2000) {
					Robot.drive(20, -20);
					System.out.println(mDistance);
					lastKnownDistance = mDistance;
					mDistance = Robot.getUltraDistance();
					if (mDistance > lastKnownDistance || mDistance < 0.1f) {
						wasBroken = true;
						break;
					}
				}
				stopwatch.reset();
				while (stopwatch.elapsed() < 1000) {
					Robot.drive(-20, 20);
					System.out.println(mDistance);
					lastKnownDistance = mDistance;
					mDistance = Robot.getUltraDistance();
					if (mDistance > lastKnownDistance || mDistance < 0.1f) {
						wasBroken = true;
						break;
					}
				}
			}
		}

		Robot.drive(0, 0);
		Sound.playTone(1000, 1000);
		while (true) {
			pd.run(Robot.getData(), whiteData);
			if (Button.ESCAPE.isDown())
				break;
		}
	}

	void testRangeNew(float whiteData) {
		Robot.drive(0, 0);
		float distance = Robot.getUltraDistance();
//		while (distance > 2.0f) {
//			System.out.println("Searching");
//			pd.run(Robot.getData(), whiteData);
//			distance = Robot.getUltraDistance();
//			if (Button.ESCAPE.isDown())
//				break;
//		}

//		Robot.drive(0, 0);
//		float mDistance = Robot.getUltraDistance();
//		boolean found = true;
//		while (found) {
//			found = false;
//			stopwatch.reset();
//			while (stopwatch.elapsed() < 11300) {
//				Robot.drive(-100, 100);
//				distance = Robot.getUltraDistance();
//				System.out.println(found);
//				if (distance < mDistance - 0.05f) {
//					mDistance = distance;
//					found = true;
//					Sound.playTone(1000, 200);
//					Robot.drive(0, 0);
//					break;
//				}
//			}
//			Robot.drive(0, 0);
//		}

		Robot.drive(0, 0);
		Sound.playTone(1000, 500);
		float distanceRef;
		boolean wasBroken = false;
		stopwatch.reset();
		while (distance > 0.1f) {
			stopwatch.reset();
			while (stopwatch.elapsed() < 1000) {
				Robot.drive(200, 200);
				distance = Robot.getUltraDistance();
				if (Button.ESCAPE.isDown() || distance < 0.1f) {
					break;
				}
			}
			distanceRef = Robot.getUltraDistance();
			wasBroken = false;
			Robot.drive(0, 0);
			stopwatch.reset();
			while (stopwatch.elapsed() < 1000) {
				Robot.drive(-50, 50);
				System.out.println("1");
				distance = Robot.getUltraDistance();
				System.out.println(distanceRef);
				if (Button.ESCAPE.isDown() || distance < distanceRef || distance < 0.1f) {
					wasBroken = true;
					System.out.println("Break");
					Robot.drive(0, 0);
					break;
				}
			}
			Robot.drive(0, 0);
			stopwatch.reset();
			while (stopwatch.elapsed() < 2000 && !wasBroken) {
				Robot.drive(50, -50);
				System.out.println("2");
				distance = Robot.getUltraDistance();
				System.out.println(distanceRef);
				if (Button.ESCAPE.isDown() || distance < distanceRef || distance < 0.1f) {
					wasBroken = true;
					System.out.println("Break");
					Robot.drive(0, 0);
					break;
				}
			}
			Robot.drive(0, 0);
			stopwatch.reset();
			while (stopwatch.elapsed() < 1000 && !wasBroken) {
				Robot.drive(-50, 50);
				System.out.println("3");
				System.out.println(distanceRef);
				if (Button.ESCAPE.isDown() || distance < 0.1f) {
					System.out.println("Break");
					Robot.drive(0, 0);
					break;
				}
			}
			if (Button.ESCAPE.isDown())
				break;
		}
		Robot.drive(0, 0);
		Sound.playTone(1000, 1000);
		
		evaluateAndAct(Robot.getData(), whiteData);
//		while (true) {
//			pd.run(Robot.getData(), whiteData);
//			if (Button.ESCAPE.isDown())
//				break;
//		}
	}
	
	void wandering(float whiteData) {
		PDController pd = new PDController();
		Robot.drive(0, 0);
		float distance = Robot.getUltraDistance();
		while (distance > 2.0f) {
			System.out.println("Searching");
			pd.run(Robot.getData(), whiteData);
			distance = Robot.getUltraDistance();
			if (Button.ESCAPE.isDown())
				break;
		}
		evaluateAndAct(Robot.getData(), whiteData);
		stopwatch.reset();
		while(stopwatch.elapsed() < 4000) {
			Robot.drive(-200, 200);
			distance = Robot.getUltraDistance();
			if (Button.ESCAPE.isDown() || distance < 2.0f)
				break;
		}
		Robot.drive(0, 0);
		
		testRangeNew(whiteData);
	}

	void readGyro() {
		float[] gyroData = new float[gyroProvider.sampleSize()];
		gyro.fetchSample(gyroData, 0);
		System.out.println(gyroData[0]);
	}

	void cuddle() {

	}

	void attack() {
		playCena();
	}

	void playVictoryTune() {
		final int[] PIANO = new int[] { 4, 25, 500, 7000, 5 };
		Sound.playNote(PIANO, 261, 200);
		Sound.playNote(PIANO, 261, 100);
		Sound.playNote(PIANO, 261, 100);
		Sound.playNote(PIANO, 392, 500);
	}

	void playDre() {
		final int[] PIANO = new int[] { 4, 25, 500, 7000, 5 };
		for (int i = 0; i < 8; i++) {
			Sound.playNote(PIANO, 261, 5);
			Sound.playNote(PIANO, 329, 5);
			Sound.playNote(PIANO, 440, 5);
			Sound.pause(50);
		}

		Sound.pause(50);

		for (int i = 0; i < 3; i++) {
			Sound.playNote(PIANO, 247, 5);
			Sound.playNote(PIANO, 329, 5);
			Sound.playNote(PIANO, 440, 5);
			Sound.pause(50);
		}

		Sound.pause(50);

		for (int i = 0; i < 5; i++) {
			Sound.playNote(PIANO, 247, 5);
			Sound.playNote(PIANO, 329, 5);
			Sound.playNote(PIANO, 392, 5);
			Sound.pause(50);
		}
	}

	void playCena() {
		final int[] PIANO = new int[] { 4, 25, 500, 7000, 5 };
		Sound.pause(300);
		Sound.playNote(PIANO, 392, 300);
		Sound.playNote(PIANO, 440, 150);
		Sound.playNote(PIANO, 349, 150);
		Sound.pause(150);
		Sound.playNote(PIANO, 392, 1200);
		Sound.pause(150);
		Sound.playNote(PIANO, 466, 300);
		Sound.playNote(PIANO, 440, 150);
		Sound.playNote(PIANO, 349, 150);
		Sound.pause(150);
		Sound.playNote(PIANO, 392, 1200);
		Sound.pause(500);
	}

}
