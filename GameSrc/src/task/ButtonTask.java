/*
Name: Sukhmanpreet, Rafay, Jeevan, Christina, Samir
Course: ICS4U0
Assignment Title: Jone's Junction
File: ButtonTask.java
Program Description:
Button Match task class. Extends the Task parent class. Requires the player
to press a button within a timeframe  when a timer hits zero. Used as one of the
tasks to progress through levels.
*/

package task; // package

import javax.imageio.ImageIO; // image loading

import main.gamePanel; // game panel

public class ButtonTask extends Task { // button task child class of Task

	public ButtonTask(gamePanel gp) { // constructor
		super(gp); // call parent constructor
		name = "Button Match Task"; // task name
		description = "press the button to complete the task."; // task description
		try { // load button task con image
			image = gp.uTool.scaleImage(ImageIO.read(getClass().getResourceAsStream("/tasks/buttonMatch.png")), gp.tileSize, gp.tileSize); // read and scale image
		} catch (Exception e) { // handle image load error
			e.printStackTrace(); // print error
		}
	}

}
