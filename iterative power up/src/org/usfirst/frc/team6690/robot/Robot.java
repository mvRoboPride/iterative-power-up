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
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;



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
		JoystickButton A;
		JoystickButton B;
		JoystickButton rTrig;
		JoystickButton lTrig;
		
		Spark liftSpark = new Spark(5);
		Spark endSpark = new Spark(0);
		
		DigitalInput endSwitch;
		private ADXRS450_Gyro Gyro;
		PowerDistributionPanel pdp = new PowerDistributionPanel();
		
		final String baseline = "Baseline";
		final String leftAuto = "Left Auto";
		final String rightAuto = "Right Auto";
		final String centerAuto = "Center Auto";
		String autoSelected;
		SendableChooser<String> chooser = new SendableChooser<>();
		
		
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void robotInit() {
		UsbCamera Camera = CameraServer.getInstance().startAutomaticCapture(0); //usb camera
		Camera.setFPS(35);
		Camera.setResolution(640, 640);
		UsbCamera Camera2 = CameraServer.getInstance().startAutomaticCapture(1);
		Camera2.setFPS(35);
		Camera2.setResolution(640, 640);
		
		pdp.clearStickyFaults();
		
		myDrive = new RobotDrive(1,2,3,4); //Arcade Drive
    	driveStick = new Joystick(1); //Arcade Drive
    	
    	A = new JoystickButton(driveStick,1);
    	B = new JoystickButton(driveStick,2);
    	lTrig = new JoystickButton(driveStick, 3);
    	rTrig = new JoystickButton(driveStick, 4);
    	
    	endSwitch = new DigitalInput(0);
    	
    	Gyro = new ADXRS450_Gyro();
    	
    	chooser.addDefault("Baseline", baseline);
		chooser.addObject("Center Auto", centerAuto);
		chooser.addObject("Left Auto", leftAuto);
		chooser.addObject("Right Auto", rightAuto);
		SmartDashboard.putData("Auto choices", chooser);
    	
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
			//myDrive.arcadeDrive(-1, -.45);
			//Timer.delay(1.5); 3 feet 
	//	myDrive.arcadeDrive(-1, 1);
		//Timer.delay(2);
    		liftSpark.set(1);
    		Timer.delay(4); //11.75 second full climb
    		liftSpark.set(0); 
		
       /*	autoSelected = chooser.getSelected();
		System.out.println("Auto selected: " + autoSelected);
		
		String gameData;
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		switch (autoSelected) {
		case leftAuto:
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
			break;
		case centerAuto: 
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
			break;
		case rightAuto: 
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
			break;
		case baseline:
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
			break;
		default:
			// Put default auto code here
			break;
		*/
       }

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
	
			
		}
	//}

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
    	//	liftSpark.set(rightStickValue);
   
    		if(A.get()) {
    		    endSpark.set(1);
    		} else if (B.get()) {
    			endSpark.set(-1);
    		} else {
    			endSpark.set(0);
			}
    		if(endSwitch.get()){
	    		liftSpark.set(0);
	    		Timer.delay(1);
	    		liftSpark.set(rightStickValue);
	    	} else {
	    		liftSpark.set(rightStickValue);
	    	}
	      Timer.delay(0.01); 
			}
		}
	}
}
		
		