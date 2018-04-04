/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team6690.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

@SuppressWarnings("unused")
public class Robot extends IterativeRobot implements PIDOutput {

	DifferentialDrive myDrive;

	// Joystick driveStick;
	// Joystick liftStick = new Joystick(1);

	XboxController xbox;

	Compressor comp = new Compressor(0);
	Solenoid solenoid = new Solenoid(0);
	Solenoid sol2 = new Solenoid(1);

	Spark raiseSpark = new Spark(7);
	Spark liftSpark = new Spark(5);
	Spark endSpark = new Spark(0);
	Spark endSpark2 = new Spark(6);
	Spark leftDrive1 = new Spark(3);
	Spark leftDrive2 = new Spark(4);
	Spark rightDrive1 = new Spark(1);
	Spark rightDrive2 = new Spark(2);

	SpeedControllerGroup leftDriveSide = new SpeedControllerGroup(leftDrive1, leftDrive2);
	SpeedControllerGroup rightDriveSide = new SpeedControllerGroup(rightDrive1, rightDrive2);

	Timer autoTimer = new Timer();

	DigitalInput endSwitch;

	PowerDistributionPanel pdp = new PowerDistributionPanel();

	final String baseline = "Baseline";
	final String leftAuto = "Left Auto";
	final String rightAuto = "Right Auto";
	final String centerAuto = "Center Auto";
	String autoSelected;
	SendableChooser<String> chooser = new SendableChooser<>();

	@Override
	public void robotInit() {

		solenoid.set(false);

		UsbCamera Camera = CameraServer.getInstance().startAutomaticCapture(0); // usb camera
		Camera.setFPS(35);
		Camera.setResolution(640, 640);

		pdp.clearStickyFaults();

		myDrive = new DifferentialDrive(leftDriveSide, rightDriveSide);// (1,2,3,4); //Arcade Drive
		// driveStick = new Joystick(1); // Arcade Drive

		xbox = new XboxController(1);

		endSwitch = new DigitalInput(0);

		chooser.addDefault("Baseline", baseline);
		chooser.addObject("Center Auto", centerAuto);
		chooser.addObject("Left Auto", leftAuto);
		chooser.addObject("Right Auto", rightAuto);
		SmartDashboard.putData("Auto choices", chooser);
		System.out.println("Robot initialized");
	}

	// Keeps track of time state was entered
	private Timer autonStateTimer;

	// Keeps track of current state
	private int autonState;

	// List of possible states
	private final static int LLsoltrue = 1; // left auto, left fms steps
	private final static int LLdrive1 = 2;
	private final static int LLright = 3;
	private final static int LLliftup = 4;
	private final static int LLdriveoff = 5;
	private final static int LLliftoff = 6;
	private final static int LLsolfalse = 7;
	private final static int LLsol2true = 8;
	private final static int LLsol2false = 9;

	private final static int LRsoltrue = 10; // left auto, right fms steps
	private final static int LRdrive1 = 11;
	private final static int LRright1 = 12;
	private final static int LRdrive2 = 13;
	private final static int LRright2 = 14;
	private final static int LRdrive3 = 15;
	private final static int LRleft1 = 16;
	private final static int LRdriveoff = 17;
	private final static int LRliftup = 18;
	private final static int LRliftoff = 19;
	private final static int LRsolfalse = 20;
	private final static int LRsol2true = 21;
	private final static int LRsol2false = 22;

	private final static int RRsoltrue = 23; // right auto, right fms steps
	private final static int RRdrive1 = 24;
	private final static int RRleft = 25;
	private final static int RRliftup = 26;
	private final static int RRdriveoff = 27;
	private final static int RRliftoff = 28;
	private final static int RRsolfalse = 29;
	private final static int RRsol2true = 30;
	private final static int RRsol2false = 31;

	private final static int RLsoltrue = 32; // right auto, left fms steps
	private final static int RLdrive1 = 33;
	private final static int RLleft1 = 34;
	private final static int RLdrive2 = 35;
	private final static int RLleft2 = 36;
	private final static int RLdrive3 = 37;
	private final static int RLright1 = 38;
	private final static int RLdriveoff = 39;
	private final static int RLliftup = 40;
	private final static int RLliftoff = 41;
	private final static int RLsolfalse = 42;
	private final static int RLsol2true = 43;
	private final static int RLsol2false = 44;

	private final static int CLsoltrue = 45; // center auto, left fms
	private final static int CLleft1 = 46;
	private final static int CLdrive1 = 47;
	private final static int CLright1 = 48;
	private final static int CLdrive2 = 49;
	private final static int CLright2 = 50;
	private final static int CLdriveoff = 51;
	private final static int CLliftup = 52;
	private final static int CLliftoff = 53;
	private final static int CLsolfalse = 54;
	private final static int CLsol2true = 55;
	private final static int CLsol2false = 56;

	private final static int CRsoltrue = 57; // center auto, left fms
	private final static int CRright1 = 58;
	private final static int CRdrive1 = 59;
	private final static int CRleft1 = 60;
	private final static int CRdrive2 = 61;
	private final static int CRleft2 = 62;
	private final static int CRdriveoff = 63;
	private final static int CRliftup = 64;
	private final static int CRliftoff = 65;
	private final static int CRsolfalse = 66;
	private final static int CRsol2true = 67;
	private final static int CRsol2false = 68;

	private final static int autodone = 69;

	// Helper method to change to new state and reset timer so
	// states can keep track of how long they have been running.

	private void changeAutonState(int nextState) {
		if (nextState != autonState) {
			autonState = nextState;
			autonStateTimer.reset();
		}
	}

	@Override
	public void autonomousInit() { // 11.75 second full climb

		System.out.println("starting auto");
		autoTimer.reset();
		autoTimer.start();
		autoSelected = chooser.getSelected();
		System.out.println("Auto selected: " + autoSelected);
		autonStateTimer = new Timer();
		// Not sure if start() is required anymore, but it shouldn't hurt
		autonStateTimer.start();
	}

	@Override
	public void autonomousPeriodic() {
		String gameData;
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		switch(autoSelected) {
    	case leftAuto:
    		if (gameData.length() > 0) {
				if (gameData.charAt(0) == 'L') {
					autonState = LLsoltrue;

				} else if (gameData.charAt(0) == 'R') { // right
					autonState = LRsoltrue;
				} 
            break;
    		}
    	case rightAuto: 
			if (gameData.length() > 0) {
				if (gameData.charAt(0) == 'L') {
					autonState = RLsoltrue;

				} else if (gameData.charAt(0) == 'R') {
					autonState = RRsoltrue;
				}
				break;
			}
		

    		break;
    	case centerAuto:
    		if (gameData.length() > 0) {
				if (gameData.charAt(0) == 'L') {
					autonState = CLsoltrue;

				} else if (gameData.charAt(0) == 'R') {
					autonState = CRsoltrue;
				}
				break;
			}
    		break;
    	case baseline:
    		if (autoTimer.get() < 3.0) {
    			myDrive.arcadeDrive(.75, .3);
    		} else {
    			myDrive.arcadeDrive(0, 0);
    		}
                break;
        	}
switch (autonState) {
    	
    	case LLsoltrue: {
    		// Drive forward at half power for 3 seconds
    		solenoid.set(true);
    		if (autonStateTimer.hasPeriodPassed(.1)) {
    			changeAutonState(2);
    		}
    		break;
    	}

    	case LLdrive1: {
    		// Turn off drive motors
    		myDrive(0.75, 0.3);
    		// After 1/2 elapses (time to stop) transition 
    		if (autonStateTimer.hasPeriodPassed(3.5)) {
    			changeAutonState(3);
    		}
    		break;
    	}
case LLright: {
    		myDrive(.5, 1);
    		if (autonStateTimer.hasPeriodPassed(.25)) {
    			changeAutonState(4);
    		}
    		break;
    	}
case LLliftup: {
	liftSpark.set(1);
	if (autonStateTimer.hasPeriodPassed(3)) {
		changeAutonState(5);
	}
	break;
}
case LLdriveoff: {
	myDrive(0,0);
	if (autonStateTimer.hasPeriodPassed(.05)) {
		changeAutonState(6);
	}
	break;
}
case LLliftoff: {
	liftSpark.set(0);
	if (autonStateTimer.hasPeriodPassed(.05)) {
		changeAutonState(7);
	}
	break;
}
case LLsolfalse: {
	solenoid.set(false);
	if (autonStateTimer.hasPeriodPassed(.05)) {
		changeAutonState(8);
	}
	break;
}
case LLsol2true: {
	sol2.set(true);
	if (autonStateTimer.hasPeriodPassed(.1)) {
		changeAutonState(9);
	}
	break;
}
case LLsol2false: {
	sol2.set(false);
	if (autonStateTimer.hasPeriodPassed(.05)) {
		changeAutonState(69);
	}
	break;
}



case LRsoltrue: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(10);
	}
	break;
}
	case LRdrive1: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(11);
	}
	break;
}
	case LRright1: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(12);
	}
	break;
}

    	case LRdrive2: {
    		
    		if (autonStateTimer.hasPeriodPassed()) {
    			changeAutonState(13);
    		}
    		break;
    	}
case LRright2: {
    		
    		if (autonStateTimer.hasPeriodPassed()) {
    			changeAutonState(14);
    		}
    		break;
    	}
case LRright2: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(15);
	}
	break;
}
case LRdrive3: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(16);
	}
	break;
}
case LRleft1: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(17);
	}
	break;
}
case LRdriveoff: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(18);
	}
	break;
}
case LRliftup: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(19);
	}
	break;
}
case LRliftoff: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(20);
	}
	break;
}
case LRsolfalse: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(21);
	}
	break;
}
case LRsol2true: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(22);
	}
	break;
}
case LRsol2false: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(69);
	}
	break;
}


case RRsoltrue: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(23);
	}
	break;
}
case RRdrive1: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(24);
	}
	break;
}
case RRleft: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(25);
	}
	break;
}
case RRliftup: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(26);
	}
	break;
}
case RRdriveoff: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(27);
	}
	break;
}
case RRliftoff: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(28);
	}
	break;
}
case RRsolfalse: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(29);
	}
	break;
}
case RRsol2true: {
	
	if (autonStateTimer.hasPeriodPassed(.2)) {
		changeAutonState(30);
	}
	break;
}
case RRsol2false: {
	
	if (autonStateTimer.hasPeriodPassed(.2)) {
		changeAutonState(69);
	}
	break;
}



case RLsoltrue: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(33);
	}
	break;
}
case RLdrive1: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(34);
	}
	break;
}
case RLleft1: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(35);
	}
	break;
}
case RLdrive2: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(36);
	}
	break;
}
case RLleft2: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(37);
	}
	break;
}
case RLdrive3: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(38);
	}
	break;
}
case RLright1: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(39);
	}
	break;
}
case RLdriveoff: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(40);
	}
	break;
}
case RLliftup: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(41);
	}
	break;
}
case RLliftoff: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(42);
	}
	break;
}
case RLsolfalse: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(43);
	}
	break;
}
case RLsol2true: {
	
	if (autonStateTimer.hasPeriodPassed(.2)) {
		changeAutonState(44);
	}
	break;
}
case RLsol2false: {
	
	if (autonStateTimer.hasPeriodPassed(.2)) {
		changeAutonState(69);
	}
	break;
}


case CLsoltrue: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(45);
	}
	break;
}
case CLleft1: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(46);
	}
	break;
}
case CLdrive1: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(47);
	}
	break;
}
case CLright1: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(48);
	}
	break;
}
case CLdrive2: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(49);
	}
	break;
}
case CLright2: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(50);
	}
	break;
}
case CLdriveoff: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(52);
	}
	break;
}
case CLliftup: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(53);
	}
	break;
}
case CLliftoff: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(54);
	}
	break;
}
case CLsolfalse: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(55);
	}
	break;
}
case CLsol2true: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(56);
	}
	break;
}
case CLsol2false: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(69);
	}
	break;
}


case CR: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(57);
	}
	break;
}
case CR: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(58);
	}
	break;
}
case CR: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(59);
	}
	break;
}
case CR: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(60);
	}
	break;
}
case CR: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(61);
	}
	break;
}
case CR: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(62);
	}
	break;
}
case CR: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(63);
	}
	break;
}
case CR: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(64);
	}
	break;
}
case CR: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(65);
	}
	break;
}
case CR: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(66);
	}
	break;
}
case CR: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(67);
	}
	break;
}
case CR: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(68);
	}
	break;
}
case CR: {
	
	if (autonStateTimer.hasPeriodPassed()) {
		changeAutonState(69);
	}
	break;
}


    	case autodone: {
    		myDrive(0,0);
    		break;
    	}
    	}
		}

	private void myDrive(double d, double e) {
		// TODO Auto-generated method stub

	}

	/*
	 * private void myDrive(double d, double e) { // TODO Auto-generated method stub
	 * 
	 * }
	 */

	@Override
	public void teleopInit() {
		autoTimer.stop();
	}

	@Override
	public void teleopPeriodic() {

		while (isOperatorControl() && isEnabled()) {
			myDrive.arcadeDrive(-xbox.getRawAxis(1), -xbox.getRawAxis(0));

			comp.setClosedLoopControl(true);

			double c1 = pdp.getCurrent(1);
			double c2 = pdp.getCurrent(2);
			double c3 = pdp.getCurrent(3);
			double c4 = pdp.getCurrent(4);
			double v = pdp.getVoltage();
			SmartDashboard.putNumber("spark 1", c1);
			SmartDashboard.putNumber("spark 2", c2);
			SmartDashboard.putNumber("spark 3", c3);
			SmartDashboard.putNumber("spark 4", c3);
			SmartDashboard.putNumber("voltage", v);

			double rightStickValue = xbox.getRawAxis(5);
			liftSpark.set(rightStickValue);
			
			if (xbox.getXButton()) {
				raiseSpark.set(1);
			}
			if (xbox.getYButton()) {
				raiseSpark.set(-1);
			}
			if (endSwitch.get()) {
				liftSpark.set(-1);
				Timer.delay(.2);
				liftSpark.set(0);
			}
			if (xbox.getAButton()) {
				solenoid.set(true);
			}
			if (xbox.getBButton()) {
				sol2.set(true);
			}
			if (xbox.getBButtonReleased()) {
				sol2.set(false);
			}
			if (xbox.getAButtonReleased()) {
				solenoid.set(false);

			} else {
				solenoid.set(false);
			}
			if (xbox.getTriggerAxis(Hand.kRight) >= .05) {
				endSpark.set(xbox.getTriggerAxis(Hand.kRight));
				endSpark2.set(xbox.getTriggerAxis(Hand.kRight));
			} else if (xbox.getTriggerAxis(Hand.kLeft) >= .05) {
				endSpark.set(xbox.getTriggerAxis(Hand.kLeft) * (-1));
				endSpark2.set(xbox.getTriggerAxis(Hand.kLeft) * (-1));
			} else {
				endSpark.set(0);
				endSpark2.set(0);
			}
		}
	}

	@Override
	public void pidWrite(double output) {
		// TODO Auto-generated method stub

	}
}
