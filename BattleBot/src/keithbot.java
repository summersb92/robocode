/*******************************************************************************
 * Copyright (c) 2001-2013 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *******************************************************************************/
import robocode.AdvancedRobot;
import robocode.HitRobotEvent;
import robocode.Robot;
import robocode.ScannedRobotEvent;
import robocode.WinEvent;
import static robocode.util.Utils.normalRelativeAngleDegrees;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;


public class keithbot extends AdvancedRobot {
     /* Contstants */
	final double FIREPOWER = 3; // Max. power => violent as this robot can afford it!
	final double HALF_ROBOT_SIZE = 18; // Robot size is 36x36 units, so the half size is 18 units
	//final Map<String, RobotData> enemyMap; //key map of opponent name, then data.
	int direction = 1; // Current direction, where 1 means ahead (forward) and -1 means back

	public keithbot(){
		
	//	enemyMap = new LinkedHashMap<String, RobotData>(5, 2, true);
	}
//  double posX, posY;
    //robot is 36x36 so must be 20px away from walls at all times
    boolean canFire;
//  You can move at a maximum speed of 8.0 units/tick. You can modify (down) your maximum velocity by using setMaxVelocity(...). Note that your bot will always accelerate to reach its maximum velocity. 

    String target; //saves the name of the old target
    boolean isStill = false;;
    Random rand; //int value = rand.nextInt(50)
    /**
     * Run method
     */
    public void run() {
            setColors(); 
            rand = new Random();
//          posX = getX();
//          posY = getY();

//          turnGunRight(20); // Scans automatically
            while (true) {
                    move();
                    turnGunRight(20); // Scans automatically
            }


    }


	/**
	 * onScannedRobot:  We have a target.  Go get it.
	 */
    //getX - gets position from bottom left corner
    //GetY
    //get battlefield hieght/width
    /**
     *onScannedRobot:We have a target that has been scanned.
     */
    public void onScannedRobot(ScannedRobotEvent e) {
            double absoluteBearing = getHeading() + e.getBearing();
            double bearingFromGun = normalizeBearing(absoluteBearing - getGunHeading());
            evasiveManuvers(e);
            if (getGunHeat() == 0 && Math.abs(bearingFromGun) < 2){
                    fireGun(e);
            }
            turnGunRight(bearingFromGun);

            if (bearingFromGun == 0) {
                    scan();
            }
    }
    private void evasiveManuvers(ScannedRobotEvent e) {
            // TODO Auto-generated method stub
            ahead(10);
            turnRight(15);
            ahead(30);
            turnLeft(180);
    }

    /**
     * onHitRobot: When you collide with a robot
     */
    public void onHitRobot(HitRobotEvent e) {
            // If he's in front of us, set back up a bit.
                            if (e.getBearing() > -90 && e.getBearing() < 90) {
                                    back(100);
                            } // else he's in back of us, so set ahead a bit.
                            else {
                                    ahead(100);
                            }
    }
    private void move(){
            ahead(16);
            turnRight(10);
    }
    /*
     * Runs a method based on position to avoid the wall.
     */
    public void avoidWall(){

            getBattleFieldHeight();
            getBattleFieldWidth();
    }

    /*
     * Figures out aim, power, and weather or not to fire
     */
    public void fireGun(ScannedRobotEvent e){
//          getHeading();//our heading;
//          getGunHeading();// our Gun Heading;
//          e.getDistance(); //gets distance
//          e.getHeading(); //gets heading, degrees - relative to your north/fwd 
//          e.getVelocity(); //gets velocity
//          e.getBearing(); //- relative to north
//          e.getName();//gets target's name
            //TODO determin if it never moves
            //Fire Gun
            if(e.getVelocity()==0){ //if they are not moving
                    if(isStill){
                            fire(3.0);
                    }
                    isStill = true;
            }else if(getRelativeVelocity(e) < 1.0 && getRelativeHeading(e) < 2 && e.getDistance() < 200){
                    fire(2.4 - e.getDistance()/0.0125);
            }else if(e.getVelocity() < 2){
                    fire(0.5);
            }
    }
    public void lockRadar(){
            //radar needs to be moved to keep a lock
    }
    /**
     * 
Damage:         4 * firepower. If firepower > 1, it does an additional damage = 2 * (power - 1).
Velocity:       20 - 3 * firepower.
GunHeat generated:      1 + firepower / 5. You cannot fire if gunHeat > 0. All guns are hot at the start of each round.
Power returned on hit:  3 * firepower.

Max rate of rotation of robot:  (10 - 0.75 * abs(velocity)) deg / turn. The faster you're moving, the slower you turn.
Max rate of rotation of gun:    20 deg / turn. This is added to the current rate of rotation of the robot.
Max rate of rotation of radar:  45 deg / turn. This is added to the current rate of rotation of the gun. 

Acceleration (a):       Robots accelerate at the rate of 1 pixel/turn/turn. Robots decelerate at the rate of 2 pixels/turn/turn. Robocode determines acceleration for you, based on the distance you are trying to move.
Velocity Equation(v):   v = at. Velocity can never exceed 8 pixels/turn. Note that technically, velocity is a vector, but in Robocode we simply assume the direction of the vector to be the robot's heading.
Distance Equation (d):  d = vt. That is, distance = velocity * time 

	
	
    /**
     * Sets the colors of the robot
     */
    public void setColors(){
            setBodyColor(Color.pink);
            setGunColor(Color.pink);
            setRadarColor(Color.pink);
            setScanColor(Color.pink);
            setBulletColor(Color.pink);
    }

    double normalizeBearing(double angle) {
            while (angle >  180) angle -= 360;
            while (angle < -180) angle += 360;
            return angle;
    }
    /**
     * Returns the relative velocity of another bot
     * @param e - event
     * @return - double
     */
    private double getRelativeVelocity(ScannedRobotEvent e){
            return Math.abs(e.getVelocity() - getVelocity());
    }
    /**
     * Returns the relative heading bettween a scanned bot
     * @param e
     * @return
     */
    private double getRelativeHeading(ScannedRobotEvent e){
            return Math.abs(e.getHeading() - getHeading());
    }

	public void onWin(WinEvent e) {
		// Victory dance
		turnRight(36000);
	}
}				
