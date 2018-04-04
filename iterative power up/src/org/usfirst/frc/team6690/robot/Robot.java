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
    private final static int AUTON_STATE_DRIVE_FORWARD = 1;
    private final static int AUTON_STATE_STOP = 2;
    private final static int AUTON_STATE_SHOOT = 3;
    private final static int AUTON_STATE_FINISHED = 4;
    
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
					autonState= AUTON_STATE_DRIVE_FORWARD;
					myDrive.arcadeDrive(1, .4); // drives straight
					Timer.delay(3);
					myDrive.arcadeDrive(-1, 1); // turns
					Timer.delay(2);
					myDrive.arcadeDrive(0, 0);
					liftSpark.set(1);// raises lift
					Timer.delay(5);
					liftSpark.set(0);
					endSpark.set(1);// launches cube
					Timer.delay(.5);
					endSpark.set(0);
					liftSpark.set(-1); // lowers lift
					Timer.delay(4);
					liftSpark.set(0);

				} else if (gameData.charAt(0) == 'R') { // right
					myDrive.arcadeDrive(-1, .45); // drives straight
					Timer.delay(7);
					myDrive.arcadeDrive(-1, 1); // turns
					Timer.delay(3);
					myDrive.arcadeDrive(0, 0);
					myDrive.arcadeDrive(-1, .45); // straight
					Timer.delay(6);
					myDrive.arcadeDrive(-1, 1); // turns
					Timer.delay(2);
					myDrive.arcadeDrive(-1, 1);// straight
					Timer.delay(1);
					myDrive.arcadeDrive(0, 0);
					liftSpark.set(1);// raises lift
					Timer.delay(5);
					liftSpark.set(0);
					endSpark.set(1);// launches cube
					Timer.delay(.5);
					endSpark.set(0);
					liftSpark.set(-1); // lowers lift
					Timer.delay(4);
					liftSpark.set(0);
				} 
            break;
    		}
    	case rightAuto: 
			if (gameData.length() > 0) {
				if (gameData.charAt(0) == 'L') {
					myDrive.arcadeDrive(-1, .45); // drives straight
					Timer.delay(10);
					myDrive.arcadeDrive(-1, -1); // turns
					Timer.delay(3);
					myDrive.arcadeDrive(0, 0);
					myDrive.arcadeDrive(-1, .45); // straight
					Timer.delay(9);
					myDrive.arcadeDrive(-1, -1); // turns
					Timer.delay(3);
					myDrive.arcadeDrive(-1, 1);// straight
					Timer.delay(1);
					myDrive.arcadeDrive(0, 0);
					liftSpark.set(1);// raises lift
					Timer.delay(5);
					liftSpark.set(0);
					endSpark.set(1);// launches cube
					Timer.delay(.5);
					endSpark.set(0);
					liftSpark.set(-1); // lowers lift
					Timer.delay(4);
					liftSpark.set(0);

				} else if (gameData.charAt(0) == 'R') {
					myDrive.arcadeDrive(-1, .45); // drives straight
					Timer.delay(10);
					myDrive.arcadeDrive(-1, -1); // turns
					Timer.delay(3);
					myDrive.arcadeDrive(0, 0);
					liftSpark.set(1);// raises lift
					Timer.delay(4);
					liftSpark.set(0);
					endSpark.set(1);// launches cube
					Timer.delay(.5);
					endSpark.set(0);
					liftSpark.set(-1);
					Timer.delay(4);
					liftSpark.set(0);
				}
				break;
			}
		

    		break;
    	case centerAuto:
    		if (gameData.length() > 0) {
				if (gameData.charAt(0) == 'L') {
					myDrive.arcadeDrive(-1, -1); // turns
					Timer.delay(2);
					myDrive.arcadeDrive(-1, .45); // straight
					Timer.delay(4);
					myDrive.arcadeDrive(-1, 1); // turns
					Timer.delay(1);
					myDrive.arcadeDrive(-1, .45);// straight
					Timer.delay(2);
					myDrive.arcadeDrive(-1, 1); // turns
					Timer.delay(1);
					myDrive.arcadeDrive(-1, .45); // straight
					Timer.delay(1);
					myDrive.arcadeDrive(0, 0);
					liftSpark.set(1);// raises lift
					Timer.delay(4);
					liftSpark.set(0);
					endSpark.set(1);// launches cube
					Timer.delay(.5);
					endSpark.set(0);
					liftSpark.set(-1); // lowers lift
					Timer.delay(4);
					liftSpark.set(0);

				} else if (gameData.charAt(0) == 'R') {
					myDrive.arcadeDrive(-1, 1); // turns
					Timer.delay(2);
					myDrive.arcadeDrive(-1, .45); // straight
					Timer.delay(4);
					myDrive.arcadeDrive(-1, -1); // turns
					Timer.delay(1);
					myDrive.arcadeDrive(-1, .45);// straight
					Timer.delay(2);
					myDrive.arcadeDrive(-1, -1); // turns
					Timer.delay(1);
					myDrive.arcadeDrive(-1, .45); // straight
					Timer.delay(1);
					myDrive.arcadeDrive(0, 0);
					liftSpark.set(1);// raises lift
					Timer.delay(4);
					liftSpark.set(0);
					endSpark.set(1);// launches cube
					Timer.delay(.5);
					endSpark.set(0);
					liftSpark.set(-1); // lowers lift
					Timer.delay(4);
					liftSpark.set(0);
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
    	
    	case AUTON_STATE_DRIVE_FORWARD: {
    		// Drive forward at half power for 3 seconds
    		myDrive(.5, .3);
    		if (autonStateTimer.hasPeriodPassed(3.0)) {
    			changeAutonState(AUTON_STATE_STOP);
    		}
    		break;
    	}

    	case AUTON_STATE_STOP: {
    		// Turn off drive motors
    		myDrive(0.0, 0.0);
    		// After 1/2 elapses (time to stop) transition to pickup
    		if (autonStateTimer.hasPeriodPassed(.5)) {
    			changeAutonState(AUTON_STATE_SHOOT);
    		}
    		break;
    	}

    	case AUTON_STATE_SHOOT: {
    		// Some auton method that fires a boulder
    		fireBoulder();
    		if (autonStateTimer.hasPeriodPassed(1.5)) {
    			changeAutonState(AUTON_STATE_FINISHED);
    		}
    		break;
    	}

    	case AUTON_STATE_FINISHED: {
    		stopShooter();
    		break;
    	}
    	}
		}

	private void myDrive(double d, double e) {
		// TODO Auto-generated method stub
		
	}

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
