import robocode.*;

public class Robotron extends AdvancedRobot {

  public void run(){
    while(true){
      setTurnGunRight(1000);
      execute();
    }
  }

  public void onScannedRobot(ScannedRobotEvent e){
    setTurnRight(e.getBearing());
    fire(Rules.MAX_BULLET_POWER);
    if(getDistanceRemaining() > -10){
        setAhead(e.getDistance() + 100);
    }
  }
}
