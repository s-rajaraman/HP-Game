package finalHPGame.ShapeUpdater.CharacterUpdater;

import org.newdawn.slick.geom.Rectangle;

import finalHPGame.Location.Location;
import finalHPGame.ShapeUpdater.ShapeUpdater;

public class HarryPotterUpdater implements ShapeUpdater {

	/**Updates personal to match the Harry Potter*/
	@Override
	public void update(Rectangle s, int direction, Location loc) {
		//Checks the direction harry is in
		if(direction==3||direction==6){
			//updates rectangle
			s.setBounds(new Rectangle(loc.getX()+40,loc.getY()+20,20,55));
		}

		else{
			s.setBounds(new Rectangle(loc.getX()+33,loc.getY()+20,20,55));
		}
	}
}




