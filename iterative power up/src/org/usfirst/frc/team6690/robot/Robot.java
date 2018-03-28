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
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

@SuppressWarnings({ "unused", "deprecation" })
public class Robot extends IterativeRobot implements PIDOutput {
	
		RobotDrive myDrive;
		
		Joystick driveStick;
		Joystick liftStick = new Joystick(1);
		
		XboxController xbox;
		
		Spark liftSpark = new Spark(5);
		Spark endSpark = new Spark(0);
		public static int gyroPort = 1;
		
		/* P, I, and D constants. Arbitrary */
	    public static final double Kp = .3;
	    public static final double Ki = .2;
	    public static final double Kd = .1;
	    
	    /* Tolerance, or how many degrees the robot can be off it's heading. */
	    public static final double kTolerance = 2f;
	    
	    public static PIDController turnController;

		
		DigitalInput endSwitch;
		private ADXRS450_Gyro gyro;
		PowerDistributionPanel pdp = new PowerDistributionPanel();
		
		double rotateToAngle;
		
		
		final String baseline = "Baseline";
		final String leftAuto = "Left Auto";
		final String rightAuto = "Right Auto";
		final String centerAuto = "Center Auto";
		String autoSelected;
		SendableChooser<String> chooser = new SendableChooser<>();
		
		

		
	@Override
	public void robotInit() {
		turnController = new PIDController(Kp, Ki, Kd, gyro, (PIDOutput) myDrive);
		turnController.setInputRange(-180f, 180f); /* Input from gyro with wrapping */
		turnController.setOutputRange(-1, 1); /* Max and min voltage for motors */
		turnController.setAbsoluteTolerance(kTolerance);
		turnController.setContinuous(true);
		turnController.disable();
		UsbCamera Camera = CameraServer.getInstance().startAutomaticCapture(0); //usb camera
		Camera.setFPS(35);
		Camera.setResolution(640, 640);
		
		pdp.clearStickyFaults();
		
		myDrive = new RobotDrive(1,2,3,4); //Arcade Drive
    	driveStick = new Joystick(1); //Arcade Drive
    	
    	xbox = new XboxController(1);
    	
    	endSwitch = new DigitalInput(0);
    	
    	ADXRS450_Gyro gyro = new ADXRS450_Gyro();
    	gyro.reset();

    	
    	chooser.addDefault("Baseline", baseline);
		chooser.addObject("Center Auto", centerAuto);
		chooser.addObject("Left Auto", leftAuto);
		chooser.addObject("Right Auto", rightAuto);
		SmartDashboard.putData("Auto choices", chooser);
	    
		
	}

	@Override
	public void autonomousInit() { //11.75 second full climb
		turnController.setSetpoint(90.0f);
		turnController.enable();
      /* 	autoSelected = chooser.getSelected();
		System.out.println("Auto selected: " + autoSelected);
		
		String gameData;
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		switch (autoSelected) {
		case leftAuto:
        if(gameData.length() > 0)
                { 
		  if(gameData.charAt(0) == 'L')
		  {
			myDrive.arcadeDrive(-1, .45); //drives straight
			Timer.delay(7);
			myDrive.arcadeDrive(-1, 1); //turns
			Timer.delay(2);
			myDrive.arcadeDrive(0, 0);
			liftSpark.set(1);//raises lift
			Timer.delay(5);
			liftSpark.set(0);
			endSpark.set(1);//launches cube
			Timer.delay(.5);
			endSpark.set(0);
			liftSpark.set(-1); //lowers lift
			Timer.delay(4);
			liftSpark.set(0);
			
		  } else if(gameData.charAt(0) == 'R') { // right  
				myDrive.arcadeDrive(-1, .45); //drives straight
				Timer.delay(7);
				myDrive.arcadeDrive(-1, 1); //turns
				Timer.delay(3);
				myDrive.arcadeDrive(0, 0);
				myDrive.arcadeDrive(-1, .45); //straight
				Timer.delay(6);
				myDrive.arcadeDrive(-1, 1); //turns
				Timer.delay(2);
				myDrive.arcadeDrive(-1, 1);//straight
				Timer.delay(1);
				myDrive.arcadeDrive(0, 0);
				liftSpark.set(1);//raises lift
				Timer.delay(5);
				liftSpark.set(0);
				endSpark.set(1);//launches cube
				Timer.delay(.5);
				endSpark.set(0);
				liftSpark.set(-1); //lowers lift
				Timer.delay(4);
				liftSpark.set(0);
		  }
			break;
                }
		case centerAuto: 
			   if(gameData.length() > 0)
               {
		  if(gameData.charAt(0) == 'L')
		  {
				myDrive.arcadeDrive(-1, -1); //turns
				Timer.delay(2);
				myDrive.arcadeDrive(-1, .45); //straight
				Timer.delay(4);
				myDrive.arcadeDrive(-1, 1); //turns
				Timer.delay(1);
				myDrive.arcadeDrive(-1, .45);//straight
				Timer.delay(2);
				myDrive.arcadeDrive(-1, 1); //turns
				Timer.delay(1);
				myDrive.arcadeDrive(-1, .45); //straight
				Timer.delay(1);
				myDrive.arcadeDrive(0, 0);
				liftSpark.set(1);//raises lift
				Timer.delay(4);
				liftSpark.set(0);
				endSpark.set(1);//launches cube
				Timer.delay(.5);
				endSpark.set(0);
				liftSpark.set(-1); //lowers lift
				Timer.delay(4);
				liftSpark.set(0);
			
		  } else if(gameData.charAt(0) == 'R') {
			  myDrive.arcadeDrive(-1, 1); //turns
				Timer.delay(2);
				myDrive.arcadeDrive(-1, .45); //straight
				Timer.delay(4);
				myDrive.arcadeDrive(-1, -1); //turns
				Timer.delay(1);
				myDrive.arcadeDrive(-1, .45);//straight
				Timer.delay(2);
				myDrive.arcadeDrive(-1, -1); //turns
				Timer.delay(1);
				myDrive.arcadeDrive(-1, .45); //straight
				Timer.delay(1);
				myDrive.arcadeDrive(0, 0);
				liftSpark.set(1);//raises lift
				Timer.delay(4);
				liftSpark.set(0);
				endSpark.set(1);//launches cube
				Timer.delay(.5);
				endSpark.set(0);
				liftSpark.set(-1); //lowers lift
				Timer.delay(4);
				liftSpark.set(0);
				}
			break;
               }
		case rightAuto: 
			   if(gameData.length() > 0)
               {
		  if(gameData.charAt(0) == 'L')
		  {
				myDrive.arcadeDrive(-1, .45); //drives straight
				Timer.delay(10);
				myDrive.arcadeDrive(-1, -1); //turns
				Timer.delay(3);
				myDrive.arcadeDrive(0, 0);
				myDrive.arcadeDrive(-1, .45); //straight
				Timer.delay(9);
				myDrive.arcadeDrive(-1, -1); //turns
				Timer.delay(3);
				myDrive.arcadeDrive(-1, 1);//straight
				Timer.delay(1);
				myDrive.arcadeDrive(0, 0);
				liftSpark.set(1);//raises lift
				Timer.delay(5);
				liftSpark.set(0);
				endSpark.set(1);//launches cube
				Timer.delay(.5);
				endSpark.set(0);
				liftSpark.set(-1); //lowers lift
				Timer.delay(4);
				liftSpark.set(0);
			
		  } else if(gameData.charAt(0) == 'R')  {
			  myDrive.arcadeDrive(-1, .45); //drives straight
				Timer.delay(10);
				myDrive.arcadeDrive(-1, -1); //turns
				Timer.delay(3);
				myDrive.arcadeDrive(0, 0);
				liftSpark.set(1);//raises lift
				Timer.delay(4);
				liftSpark.set(0);
				endSpark.set(1);//launches cube
				Timer.delay(.5);
				endSpark.set(0);
				liftSpark.set(-1);
				Timer.delay(4);
				liftSpark.set(0);		  
				}
			break;
               }
		case baseline:
		default:
			myDrive.arcadeDrive(-1, .45);
			Timer.delay(10);
			myDrive.arcadeDrive(0, 0);
			break;
          } */
       }

	@Override
	public void autonomousPeriodic() {
	
			
		}
	//}

	@SuppressWarnings("deprecation")
	@Override
	public void teleopPeriodic() {
		
		{while (isOperatorControl() && isEnabled()) {
    		myDrive.arcadeDrive(driveStick);
    		Timer.delay(0.01);
   
    		
    		double rightStickValue = liftStick.getRawAxis(5);
    		liftSpark.set(rightStickValue);
  
    		if (xbox.getTriggerAxis(Hand.kRight) >= .05) {
				endSpark.set(xbox.getTriggerAxis(Hand.kRight));
    		} else if (xbox.getTriggerAxis(Hand.kLeft) >= .05) {
    			endSpark.set(xbox.getTriggerAxis(Hand.kLeft) * (-1));
    		} else {
    			endSpark.set(0);
    		}
	      Timer.delay(0.01); 
			}
		}
	}

	@Override
	public void pidWrite(double output) {
		// TODO Auto-generated method stub
		
	}
}
		
		