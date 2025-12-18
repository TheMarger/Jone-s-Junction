import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JFormattedTextField;

public class MathQuestionTaskFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	JLabel lblMathQuestion;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MathQuestionTaskFrame frame = new MathQuestionTaskFrame();
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
	public MathQuestionTaskFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 586, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Solve the Math Question!");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblNewLabel.setBounds(0, 0, 265, 66);
		contentPane.add(lblNewLabel);
		
		lblMathQuestion = new JLabel("math questio n");
		lblMathQuestion.setBounds(198, 77, 197, 45);
		contentPane.add(lblMathQuestion);
		
		textField = new JTextField();
		textField.setBounds(198, 133, 197, 31);
		contentPane.add(textField);
		textField.setColumns(10);

	}
	public void setQuestion(String question) {
		lblMathQuestion.setText(question);
		repaint();
	}
}
