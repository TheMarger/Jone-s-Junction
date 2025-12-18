package main;

public class keyHandler implements java.awt.event.KeyListener {
	public boolean upPressed, downPressed, leftPressed, rightPressed;

	@Override
	public void keyTyped(java.awt.event.KeyEvent e) {
		// Handle key typed event
	}

	@Override
	public void keyPressed(java.awt.event.KeyEvent e) {
		// Handle key pressed event
		int code = e.getKeyCode();
		if (code == java.awt.event.KeyEvent.VK_W) {
			System.out.println("Up key pressed");
			upPressed = true;
		}
		if (code == java.awt.event.KeyEvent.VK_S) {
			System.out.println("Down key pressed");
			downPressed = true;
		}
		if (code == java.awt.event.KeyEvent.VK_A) {
			System.out.println("Left key pressed");
			leftPressed = true;
		}
		if (code == java.awt.event.KeyEvent.VK_D) {
			System.out.println("Right key pressed");
			rightPressed = true;
		}
	}

	@Override
	public void keyReleased(java.awt.event.KeyEvent e) {
		// Handle key released event
		int code = e.getKeyCode();
		if (code == java.awt.event.KeyEvent.VK_W) {
			System.out.println("Up key pressed");
			upPressed = false;
		}
		if (code == java.awt.event.KeyEvent.VK_S) {
			System.out.println("Down key pressed");
			downPressed = false;
		}
		if (code == java.awt.event.KeyEvent.VK_A) {
			System.out.println("Left key pressed");
			leftPressed = false;
		}
		if (code == java.awt.event.KeyEvent.VK_D) {
			System.out.println("Right key pressed");
			rightPressed = false;
		}
		
	}

}
