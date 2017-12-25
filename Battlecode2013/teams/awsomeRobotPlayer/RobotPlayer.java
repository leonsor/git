package awsomeRobotPlayer;

import battlecode.common.*;

public class RobotPlayer {
	
	private static RobotController rc;
	private static MapLocation rallyPoint;
	
	public static void run(RobotController myRC) {
	
		rc = myRC;
		rallyPoint = findRallyPoint();
		
		while(true) {
			try {
				if(rc.getType()==RobotType.SOLDIER) {
					//soldier action
					Robot[] enemyRobots = rc.senseNearbyGameObjects(Robot.class, 1000000, rc.getTeam().opponent());
					if(enemyRobots.length==0) {//no robots spotted
						if(Clock.getRoundNum()<200) {
							goToLocation(rallyPoint);
						}else {
						goToLocation(rc.senseEnemyHQLocation());
						}
					}else {//someone spotted
						int closestDist = 1000000;
						MapLocation closestEnemy=null;
						for(int i=0;i<enemyRobots.length; i++) {
							Robot aRobot = enemyRobots[i];
							RobotInfo aRobotInfo = rc.senseRobotInfo(aRobot);
							int dist = aRobotInfo.location.distanceSquaredTo(rc.getLocation());
							if(dist<closestDist) {
								closestDist=dist;
								closestEnemy = aRobotInfo.location;
							}
						}
						goToLocation(closestEnemy);
					}
				}else {
					//hq action
					hqAction();
				}
			} catch (Exception e) {
				System.out.println("caught exeption before it killed us");
				e.printStackTrace();
			}
				rc.yield();
		}
	}

	/**
	 * @throws GameActionException
	 */
	public static void goToLocation(MapLocation whereToGo) throws GameActionException {
		int dist = rc.getLocation().distanceSquaredTo(whereToGo);
		if(dist>0 && rc.isActive()) {
			Direction dir = rc.getLocation().directionTo(whereToGo);
			Direction lookingAtCurrently = dir;
			int[] directionOffsets = {0, 1, -1, 2, -2};
			lookAround: for(int d:directionOffsets) {
				lookingAtCurrently = Direction.values()[(dir.ordinal()+d+8)%8];
				if(rc.canMove(dir)) {
					rc.move(dir);
					break lookAround;
				}
			}
			rc.move(lookingAtCurrently);
		}
	}

	private static MapLocation findRallyPoint() {
		MapLocation enemyLocation = rc.senseEnemyHQLocation();
		MapLocation ownLocation = rc.senseHQLocation();
		int x = (enemyLocation.x+3*ownLocation.x)/4;
		int y = (enemyLocation.y+3*ownLocation.y)/4;
		MapLocation rallyPoint = new MapLocation(x,y);
		return rallyPoint;
	}

	/**
	 * @throws GameActionException
	 */
	public static void hqAction() throws GameActionException {
		if(rc.isActive()) {
			//spawn soldier
			Direction dir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
			if (rc.canMove(dir))
				rc.spawn(dir);
		}
	}
}