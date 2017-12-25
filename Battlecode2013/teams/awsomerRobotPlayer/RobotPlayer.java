package awsomerRobotPlayer;

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
					if(Clock.getRoundNum()<200) {
						goToLocation(rallyPoint);
					}else {
						goToLocation(rc.senseEnemyHQLocation());
					}
				}
				else {
					//HQ action
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