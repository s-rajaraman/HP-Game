package finalHPGame.States;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.imageout.ImageOut;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.EmptyTransition;
import org.newdawn.slick.state.transition.FadeInTransition;
import finalHPGame.CharList;
import finalHPGame.Characters.Magician;
import finalHPGame.Map.*;
import finalHPGame.Spell.nonrange.*;



public class Play extends BasicGameState {

	private int level;
	static int playLevel=1;
	private Map worldMap;
	private CharList list;
	private float decreaser;
	private boolean pause;


	public Play(int play1) throws SlickException {
		level = play1;
		pause=false;
	}

	public void init(GameContainer arg0, StateBasedGame arg1)
			throws SlickException {
		if(level==1){
			decreaser = 5;
			worldMap = new MapLevel1();
		}
		else if(level==2){
			worldMap = new MapLevel2();
			decreaser = 10;
		}
		else if(level==3){
			worldMap= new MapLevel3();
			decreaser = 12;
		}
		list = new CharList(level);
		pause = false;
	}

	public void render(GameContainer gc, StateBasedGame sbg, Graphics g)
			throws SlickException {
		worldMap.drawMap();
		list.drawEveryone();
		list.getMainCharacter().getMagicBar().drawBar(g);
		list.getMainCharacter().getHealthBar().drawBar(g);
		list.getMainCharacter().getCirclePower().drawPowerCircle(g);
		worldMap.render(gc, sbg, g);
		list.drawPowers(g);
		if(pause){
			Color r = new Color(255,0,0);
			g.setColor(r);
			g.drawString("Resume (r)", 270, 400);
			g.drawString("Menu (m)", 270, 415);
			g.drawString("Quit (q)", 270, 430);
		}
	}


	public void update(GameContainer gc, StateBasedGame arg1, int delta)
			throws SlickException {
		if(!pause){
			list.updateAllBullet(delta);
			worldMap.update(delta);

			if(worldMap.isinHarm(list.getMainCharacter().getPersonalSpace())){
				System.out.println("yes");
				list.getMainCharacter().getHealthBar().decreaseHealthBar((float) (delta*decreaser));
			}
			/*Checks if game should enter new state because playableCharacter is dead or 
			 * or the playable killed everyone and got the horcrux
			 */
			if(list.getMainCharacter().getHealthBar().getHealthBarX()==0){
				arg1.enterState(200, new EmptyTransition(), new FadeInTransition(new Color(200,0,0),1000));
			}
			else if(list.getCharacterList().size()==1 && list.getHorcruxes().size()==0){
				playLevel++;
				arg1.enterState(300);
			}

			//Temp Variables
			Input input = gc.getInput();
			Magician playable = list.getMainCharacter();
			CirclePower circle = playable.getCirclePower();
			InvisiblePower invisible = playable.getInvisiblePower();
			SpeedPower speed = playable.getSpeedPower();

			list.killEnemies();
			list.removeHorcruxes();
			list.moveEnemies(delta);
			list.moveHorcruxes(delta);

			if(input.isKeyDown(Input.KEY_UP) && !circle.isPowerOn()){
				playable.setPositionY(-delta*speed.getSpeed());
				playable.getAnimationHolder().getMainChar().setAutoUpdate(true);
			}

			if(input.isKeyDown(Input.KEY_DOWN) && !circle.isPowerOn()){
				playable.setPositionY(delta*speed.getSpeed());
				playable.getAnimationHolder().getMainChar().setAutoUpdate(true);
			}

			if(input.isKeyDown(Input.KEY_LEFT) && !circle.isPowerOn()){
				playable.setPositionX(-delta*speed.getSpeed());
				playable.getAnimationHolder().getMainChar().setAutoUpdate(true);
			}

			if(input.isKeyDown(Input.KEY_RIGHT) && !circle.isPowerOn()){
				playable.setPositionX(delta*speed.getSpeed());
				playable.getAnimationHolder().getMainChar().setAutoUpdate(true);
			}

			//Cannot be invisible and use the powerCircle
			if(input.isKeyDown(Input.KEY_SPACE) && !circle.isPowerOn()){
				playable.setInvisibleTrue(delta);
			}
			//Cannot be invisible and use the powerCircle
			if(input.isKeyDown(Input.KEY_Z) && !invisible.isPowerOn()){
				playable.powerCircleOn(delta);
			}

			if(!input.isKeyDown(Input.KEY_Z)){
				circle.setPowerOff();
			}

			if(!input.isKeyDown(Input.KEY_SPACE)){
				invisible.setPowerOff();
			}
			if(input.isKeyPressed(Input.KEY_X)){
				playable.addBullet();
			}
			
			if(input.isKeyPressed(Input.KEY_ESCAPE)){
				pause = true;
			}
			//if the user is pressing down a,
			//then the speed of main character increases
			if(input.isKeyDown(Input.KEY_A)){
				playable.setSpeedFast(delta);
			}

			//if the user is not pressing a,
			//the speed is back to normal
			if(!input.isKeyDown(Input.KEY_A)){
				speed.setPowerOff();
			}

			//If the user is not using any of the powers, then load the magicbar
			if(!input.isKeyDown(Input.KEY_A) 
					&& ! input.isKeyDown(Input.KEY_SPACE) 
					&& ! input.isKeyDown(Input.KEY_F)){
				playable.getMagicBar().reviveMagicBar(delta);
			}

			//When the user press s, then it takes a screenshoot
			if (gc.getInput().isKeyPressed(Input.KEY_S)) {
				Image target = new Image(gc.getWidth(), gc.getHeight());
				gc.getGraphics().copyArea(target, 0, 0);
				ImageOut.write(target.getFlippedCopy(false, false), "screenshot.png", false);
				target.destroy();
			}

			if(!(input.isKeyDown(Input.KEY_DOWN)||input.isKeyDown(Input.KEY_LEFT)||
					input.isKeyDown(Input.KEY_UP)||input.isKeyDown(Input.KEY_RIGHT))){
				playable.getAnimationHolder().getMainChar().setAutoUpdate(false);
			}
		}
		else{
			
			Input input = gc.getInput();
			if(input.isKeyDown(Input.KEY_Q)){
				System.exit(0);
			}
			if(input.isKeyDown(Input.KEY_R)){
				pause = false;
			}
			if(input.isKeyDown(Input.KEY_M)){
				arg1.enterState(Game.menu);
			}
		}

	}

	//returns level which is the ID of this GameState
	public int getID() {
		return level;
	}

}
