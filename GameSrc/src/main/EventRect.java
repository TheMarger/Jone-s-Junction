/*
 * Name: Rafay
 * Date: 1/19/2026
 * Course Code: ICS4U0
 * Description: EventRect class extends the Java Rectangle class to represent event trigger
 *              zones on the game map. Each EventRect stores its default position and tracks
 *              whether its associated event has already been triggered. This allows for
 *              one-time events that won't repeat after being activated. The class inherits
 *              standard rectangle properties (x, y, width, height) from the Rectangle class
 *              and adds event-specific functionality.
 */

package main; // Declares this class belongs to the main package

import java.awt.Rectangle; // Imports the Rectangle class from Java AWT library

public class EventRect extends Rectangle{ // Declares EventRect class that extends Rectangle to inherit position and size properties

	int eventRectDefaultX, eventRectDefaultY; // Stores the default X and Y offsets for the event rectangle within its tile
	boolean eventDone = false; // Boolean flag to track if this event has already been triggered, initialized to false to allow the event to occur

	// in case we want to make the event only happen once

}