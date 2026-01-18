/*
Name: Sukhmanpreet, Rafay, Jeevan, Christina, Samir
Course: ICS4U0
Assignment Title: Jone's Junction
File: CookingTask.java
Program Description:
Cooking task class. Extends the Task parent class. Presents the player with
a trivia question chosen from a pool of cooking based questions. Selecting the correct
answer completes the task. Used as one of tasks to progress through levels.
*/

package task; // package

import javax.imageio.ImageIO; // image loading

import main.gamePanel; // game panel

public class CookingTask extends Task { // cooking task child class of Task

	public CookingTask(gamePanel gp) { // constructor
		super(gp); // call parent constructor
		name = "Cooking Task"; // task name
		description = "Prepare a delicious meal by following the recipe!"; // task description
		try { // load and scale cooking task image
			image = gp.uTool.scaleImage(ImageIO.read(getClass().getResourceAsStream("/tasks/cookingTask.png")), gp.tileSize, gp.tileSize); 
		} catch (Exception e) { // handle image load error
			e.printStackTrace(); // print error
		}
	}
}
