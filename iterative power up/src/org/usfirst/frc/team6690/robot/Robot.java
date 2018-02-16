/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team6690.robot;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.Trigger;
//egit 
/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
@SuppressWarnings({ "unused", "deprecation" })
public class Robot extends IterativeRobot {
	
		@SuppressWarnings("deprecation")
		RobotDrive myDrive;
		Joystick driveStick;
		Joystick liftStick = new Joystick(1);
		Spark liftSpark = new Spark(6);
		Spark endSpark = new Spark(5);
		
		PowerDistributionPanel pdp = new PowerDistributionPanel();
		
		
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void robotInit() {
		CameraServer.getInstance().startAutomaticCapture(); 
		
		pdp.clearStickyFaults();
		
		myDrive = new RobotDrive(1,2,3,4); //Arcade Drive
    	driveStick = new Joystick(1); //Arcade Drive
    	

    	
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * <p>You can add additional auto modes by adding additional comparisons to
	 * the switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
		String gameData;
		gameData = DriverStation.getInstance().getGameSpecificMessage();
                if(gameData.length() > 0)
                {
		  if(gameData.charAt(0) == 'L')
		  {
			myDrive.arcadeDrive(1, .5);
			Timer.delay(1);
			myDrive.arcadeDrive(1, .5);
			Timer.delay(.25);
			
		  } else {
			//Put right auto code here
		  }
		  if(gameData.charAt(0) == 'R')
		  {
			myDrive.arcadeDrive(1, -.5);
			Timer.delay(1);
			myDrive.arcadeDrive(1, .5);
			Timer.delay(.25);
			
		  } else {
			//Put right auto code here
		  }
		  
                }
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		
		
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void teleopPeriodic() {
		
		{while (isOperatorControl() && isEnabled()) {
    		myDrive.arcadeDrive(driveStick);
    		Timer.delay(0.01);
    		
    		double rightStickValue = liftStick.getRawAxis(5);
    		liftSpark.set(rightStickValue);
   
    		endSpark.set(driveStick.getRawAxis(3)); 
    		endSpark.set(driveStick.getRawAxis(4));
    		
			}
		}
	}}
		
		