/*

Name: Samir Bhagat

Course: ICS4U0

Date Completed: 12/01/2025

Assignment Title: Jone's Junction Final Project

File: Level.java

Program Description:

The Level class stores all core data for a single stage of Jone's Junction, 



Stores:

 - level number and name (Cell Block, Workshop, etc.)

 - a Map object for the tile layout

 - all Guards on this level

 - the list of tasks that must be completed



Also:

 - tracks which tasks are completed

 - can report how many tasks are left / if level is done

 - has helper methods for level specific rules like required task count, speedrun time, and throwable spawn ranges.

*/



public class level { // represents one level in the game



	private int levelNum; // level number 1->4

	private String name; // level name (Cell Block, Workshop, etc.)

	private Map map; // map layout object for this level

	private Guard[] guards;// guards that patrol/chase on this level

	private String[] tasks; // text descriptions of tasks on this level



	private boolean[] taskCompleted; // parallel array to tasks[] for which ones are done



	public Level() { // no-arg constructor (overload)

		levelNum = 0; // default level number

		name = "";// empty name

		map = null;// no map yet

		guards = new Guard[0];// start with no guards

		tasks = new String[0];// start with no tasks

		taskCompleted = new boolean[0]; // no completion flags

	}



	public Level(int levelNum, String name, Map map, Guard[] guards, String[] tasks) { // main all-arg constructor

		this.levelNum = levelNum; // store level number

		this.name = name; // store level name

		this.map = map; // store map reference

		setGuards(guards); // call setter so null is handled safely

		setTasks(tasks); // call setter to also build taskCompleted[]

	}



	public int getLevelNum() { // get level number

		return levelNum;

	}



	public void setLevelNum(int levelNum) { // set level number

		this.levelNum = levelNum;

	}



	public String getName() { // get level name

		return name;

	}



	public void setName(String name) { // set level name

		this.name = name;

	}



	public Map getMap() { // get map object for this level

		return map;

	}



	public void setMap(Map map) { // set map object

		this.map = map;

	}



	public Guard[] getGuards() { // get guards array

		return guards;

	}



	public void setGuards(Guard[] guards) { // set guards array

		if (guards == null) { // if null passed in

			this.guards = new Guard[0]; // use empty array to avoid null checks later

		} else {

			this.guards = guards; // store reference

		}

	}



	public String[] getTasks() { // get full task list

		return tasks;

	}



	public void setTasks(String[] tasks) { // set full task list and reset completion

		if (tasks == null) { // if no tasks

			this.tasks = new String[0]; // empty task list

			this.taskCompleted = new boolean[0]; // empty completed list

		} else {

			this.tasks = tasks; // store tasks

			this.taskCompleted = new boolean[tasks.length]; // create flags same length (all false by default)

		}

	}



	public boolean[] getTaskCompletedFlags() { // get a copy of which tasks are completed

		boolean[] copy = new boolean[taskCompleted.length]; // new array same size

		for (int i = 0; i < taskCompleted.length; i++) { // loop through flags

			copy[i] = taskCompleted[i]; // copy value

		}

		return copy; // return copy so outside code cannot change internal array

	}



	public void completeTask(int index) { // mark task at index as done

		if (index < 0 || index >= taskCompleted.length) { // check for invalid index

			return; // ignore bad index instead of crashing

		}

		taskCompleted[index] = true; // mark this task as completed

	}



	public void completeTaskByName(String taskName) { // mark first task that matches given name as done

		if (taskName == null) { // if no name

			return; // nothing to do

		}

		for (int i = 0; i < tasks.length; i++) { // loop through tasks

			if (tasks[i] != null && tasks[i].equalsIgnoreCase(taskName)) { // case-insensitive match

				taskCompleted[i] = true; // mark completed

				return; // stop after first match

			}

		}

	}



	public int getRemainingTaskCount() { // how many tasks are still not completed

		int remaining = 0; // counter

		for (int i = 0; i < taskCompleted.length; i++) { // loop flags

			if (!taskCompleted[i]) { // if this task is not done

				remaining++; // increase count

			}

		}

		return remaining; // return how many tasks left

	}



	public String[] getRemainingTasks() { // return only the tasks that are not completed

		int remaining = getRemainingTaskCount(); // number of tasks left

		String[] out = new String[remaining];// new array sized to that

		int k = 0; // index in output array

		for (int i = 0; i < tasks.length; i++) { // loop over tasks

			if (!taskCompleted[i]) { // if not done

				out[k++] = tasks[i]; // copy task into output and move pointer

			}

		}

		return out; // return remaining task descriptions

	}



	public boolean isLevelComplete() { // true if all tasks are completed

		return getRemainingTaskCount() == 0; // level done when none remain

	}



	public void resetTasks() { // reset all tasks back to "not completed"

		for (int i = 0; i < taskCompleted.length; i++) { // loop flags

			taskCompleted[i] = false; // mark not done

		}

	}



	public void resetLevel() { // reset dynamic state of this level (used when restarting level)

		resetTasks(); // all tasks back to not done



		for (int i = 0; i < guards.length; i++) { // loop through guards

			if (guards[i] != null) { // if guard exists

				guards[i].resetToStart(); // tell guard to go back to start of patrol (method to be added in Guard)

			}

		}

		// player position will be handled by Game class, not here

	}



	public int getRequiredTaskCountForLevel() { // how many tasks should exist per level as per design

		switch (levelNum) { // check level number

		case 1:

			return 2; // level 1: 2 tasks

		case 2:

			return 4; // level 2: 4 tasks

		case 3:

			return 6; // level 3: 6 tasks

		case 4:

			return 8; // level 4: 8 tasks

		default:

			return 0; // other value: no required tasks

		}

	}



	public int getSpeedrunTimeSeconds() { // speedrun time limit (seconds) for this level

		switch (levelNum) { // check level

		case 1:

			return 5 * 60; // 5 minutes for level 1

		case 2:

			return 6 * 60; // 6 minutes for level 2

		case 3:

			return 7 * 60; // 7 minutes for level 3

		case 4:

			return 8 * 60; // 8 minutes for level 4

		default:

			return 0; // unknown level: no time

		}

	}



	public int getMinThrowables() { // minimum throwable items for this level

		switch (levelNum) { // based on requirement doc

		case 1:

			return 2; // level 1: 2-3

		case 2:

			return 3; // level 2: 3-4

		case 3:

			return 4; // level 3: 4-5

		case 4:

			return 5; // level 4: 5-6

		default:

			return 0; // unknown: 0

		}

	}



	public int getMaxThrowables() { // maximum throwable items for this level

		switch (levelNum) { // based on requirement doc

		case 1:

			return 3; // level 1: 2-3

		case 2:

			return 4; // level 2: 3-4

		case 3:

			return 5; // level 3: 4-5

		case 4:

			return 6; // level 4: 5-6

		default:

			return 0; // unknown: 0

		}

	}



	public String toString() { // simple debug text for this level

		return "Level " + levelNum + " - " + name + " (tasks left: " + getRemainingTaskCount() + ")"; // short summary

	}

}