package main;

public class keyHandler implements java.awt.event.KeyListener {
	public boolean upPressed, downPressed, leftPressed, rightPressed, sprintPressed, crouchPressed;

	@Override
	public void keyTyped(java.awt.event.KeyEvent e) {
		// Handle key typed event
	}

	@Override
	public void keyPressed(java.awt.event.KeyEvent e) {
		// Handle key pressed event
		int code = e.getKeyCode();
		if (code == java.awt.event.KeyEvent.VK_W) {
			upPressed = true;
		}
		if (code == java.awt.event.KeyEvent.VK_S) {
			downPressed = true;
		}
		if (code == java.awt.event.KeyEvent.VK_A) {
			leftPressed = true;
		}
		if (code == java.awt.event.KeyEvent.VK_D) {
			rightPressed = true;
		}
		if (code == java.awt.event.KeyEvent.VK_SHIFT) {
			sprintPressed = true;
		}
		if (code == java.awt.event.KeyEvent.VK_CONTROL) {
			crouchPressed = true;
		}
	}

	@Override
	public void keyReleased(java.awt.event.KeyEvent e) {
		// Handle key released event
		int code = e.getKeyCode();
		if (code == java.awt.event.KeyEvent.VK_W) {
			upPressed = false;
		}
		if (code == java.awt.event.KeyEvent.VK_S) {
			downPressed = false;
		}
		if (code == java.awt.event.KeyEvent.VK_A) {
			leftPressed = false;
		}
		if (code == java.awt.event.KeyEvent.VK_D) {
			rightPressed = false;
		}
		if (code == java.awt.event.KeyEvent.VK_SHIFT) {
			sprintPressed = false;
		}
		if (code == java.awt.event.KeyEvent.VK_CONTROL) {
			crouchPressed = false;
		}
		
	}

}
