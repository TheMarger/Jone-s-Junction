import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Font;

public class TaskFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TaskFrame frame = new TaskFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public TaskFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 564, 382);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(255, 255, 255));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnNewButton = new JButton("Play Task");
		btnNewButton.setBounds(212, 141, 126, 33);
		contentPane.add(btnNewButton);
		
		JLabel lblTaskName = new JLabel(Task.taskName);
		lblTaskName.setFont(new Font("Tahoma", Font.BOLD, 24));
		lblTaskName.setBounds(197, 53, 166, 39);
		contentPane.add(lblTaskName);
		
		btnNewButton.addActionListener(e -> {
		    // Action to perform when button is clicked
			if (Task.taskName.equals("Math Question Task")) {
				MathQuestionTaskFrame mathFrame = new MathQuestionTaskFrame();
				mathFrame.setVisible(true);
			}
			
			
		});
	}
}
