import java.util.ArrayList; 
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class CookingTask extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private int currentQuestion;
	private JLabel lblResult;
	
	private String[] CookingQuestions;
	private String[] answersArray;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CookingTask frame = new CookingTask();
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
	public CookingTask() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 941, 527);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Task: Cooking Question");
		lblNewLabel.setBounds(60, 12, 816, 96);
		lblNewLabel.setFont(new Font("Palatino Linotype", Font.PLAIN, 60));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblNewLabel);
		
		lblResult = new JLabel("");
		lblResult.setFont(new Font("Arial", Font.BOLD, 30));
		lblResult.setHorizontalAlignment(SwingConstants.CENTER);
		lblResult.setBounds(60, 460, 816, 40);
		contentPane.add(lblResult);
		
		JButton btnA = new JButton("A");
        btnA.setBounds(220, 150, 492, 54);
        contentPane.add(btnA);

        JButton btnB = new JButton("B");
        btnB.setBounds(220, 235, 492, 54);
        contentPane.add(btnB);

        JButton btnC = new JButton("C");
        btnC.setBounds(220, 320, 492, 54);
        contentPane.add(btnC);

        JButton btnD = new JButton("D");
        btnD.setBounds(220, 405, 492, 54);
        contentPane.add(btnD);
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("");
		chckbxNewCheckBox.setEnabled(false);
		chckbxNewCheckBox.setBounds(861, 31, 21, 24);
		contentPane.add(chckbxNewCheckBox);
		
		JLabel lblQuestion = new JLabel("Question");
		lblQuestion.setHorizontalAlignment(SwingConstants.CENTER);
		lblQuestion.setFont(new Font("Nirmala Text Semilight", Font.PLAIN, 20));
		lblQuestion.setBounds(12, 76, 915, 96);
		contentPane.add(lblQuestion);
		
		// Initialize arrays
        CookingQuestions = new String[40];
        answersArray = new String[40];
        loadQuestionsAndAnswers();
        
        // Randomly select question and answer
        loadQuestion(lblQuestion, btnA, btnB, btnC, btnD, chckbxNewCheckBox);
        
        // Option buttons' action listeners
        btnA.addActionListener(e -> checkAnswer(btnA.getText(), chckbxNewCheckBox));
        btnB.addActionListener(e -> checkAnswer(btnB.getText(), chckbxNewCheckBox));
        btnC.addActionListener(e -> checkAnswer(btnC.getText(), chckbxNewCheckBox));
        btnD.addActionListener(e -> checkAnswer(btnD.getText(), chckbxNewCheckBox));
	}
	
private void loadQuestionsAndAnswers() {
	
		CookingQuestions[0] = "What temperature does water boil at (at sea level)?";
		CookingQuestions[1] = "Which ingredient makes bread rise?";
		CookingQuestions[2] = "What is the main ingredient in guacamole?";
		CookingQuestions[3] = "At approximately what temperature does chocolate begin to melt?";
		CookingQuestions[4] = "Which cooking method uses dry heat and circulating air to cook food?";
		CookingQuestions[5] = "Which knife is best for chopping vegetables?";
		CookingQuestions[6] = "What does “al dente” refer to?";
		CookingQuestions[7] = "Which of these is a dry-heat cooking technique?";
		CookingQuestions[8] = "Which fat is commonly used for sautéing because of its high smoke point?";
		CookingQuestions[9] = "What is the recommended minimum internal temperature for cooked chicken (whole or pieces)?";
		CookingQuestions[10] = "What is the purpose of blanching vegetables?";
		CookingQuestions[11] = "Which of the following is a leavening agent?";
		CookingQuestions[12] = "What does “mise en place” mean?";
		CookingQuestions[13] = "Which cut of meat is typically most tender?";
		CookingQuestions[14] = "Which of these is best for making mayonnaise?";
		CookingQuestions[15] = "Which spice is the main ingredient in curry powder?";
		CookingQuestions[16] = "Which method cooks food by surrounding it with steam?";
		CookingQuestions[17] = "What ingredient is primarily responsible for browning via the Maillard reaction?";
		CookingQuestions[18] = "Which of the following is a dry, cured pork product?";
		CookingQuestions[19] = "Which of these is NOT a mother sauce in classical French cuisine?";
		CookingQuestions[20] = "What is the culinary term for cutting food into long, thin strips?";
		CookingQuestions[21] = "Which cheese is traditionally used on pizza for its meltability?";
		CookingQuestions[22] = "What does it mean to “deglaze” a pan?";
		CookingQuestions[23] = "Which of these flours has the highest protein (gluten) content, typically used for bread?";
		CookingQuestions[24] = "What is the safe refrigerator temperature to slow bacterial growth?";
		CookingQuestions[25] = "Which fruit is high in vitamin C and commonly used to prevent scurvy?";
		CookingQuestions[26] = "What does “folding” mean in baking?";
		CookingQuestions[27] = "Which method uses water just below boiling to cook delicate foods?";
		CookingQuestions[28] = "Which of these is a common emulsifier used in cooking?";
		CookingQuestions[29] = "What kitchen tool measures dry ingredient volume most accurately?";
		CookingQuestions[30] = "Which grain is used to make risotto?";
		CookingQuestions[31] = "Which method is best for making stock from bones?";
		CookingQuestions[32] = "Which herb is the primary ingredient in pesto?";
		CookingQuestions[33] = "What’s the common thickener in cream soups?";
		CookingQuestions[34] = "Which oil is traditionally used in Japanese tempura for frying?";
		CookingQuestions[35] = "Which of these indicates a cake is fully baked?";
		CookingQuestions[36] = "Which foodborne pathogen is commonly associated with undercooked eggs?";
		CookingQuestions[37] = "What is \"umami\"?";
		CookingQuestions[38] = "Which of the following is a quick method for tenderizing meat?";
		CookingQuestions[39] = "Which acid is commonly used to “cook” fish in ceviche?";

		answersArray[0] = "100°C";
		answersArray[1] = "Yeast";
		answersArray[2] = "Avacado";
		answersArray[3] = "30–32°C";
		answersArray[4] = "Baking";
		answersArray[5] = "Chef’s (cook’s) knife";
		answersArray[6] = "Pasta cooked to be firm to the bite ";
		answersArray[7] = "Grilling";
		answersArray[8] = "Vegetable oil (e.g., canola, sunflower)";
		answersArray[9] = "74°C (165°F)";
		answersArray[10] = "To briefly cook and stop enzyme action for color/texture";
		answersArray[11] = "Baking powder";
		answersArray[12] = "Everything in its place (prep before cooking)";
		answersArray[13] = "Tenderloin / Filet";
		answersArray[14] = "Raw egg yolk emulsified with oil and acid";
		answersArray[15] = "Turmeric";
		answersArray[16] = "Steaming";
		answersArray[17] = "Proteins and reducing sugars";
		answersArray[18] = "Prosciutto";
		answersArray[19] = "Pesto";
		answersArray[20] = "Julienne";
		answersArray[21] = "Mozzarella";
		answersArray[22] = "Add liquid to dissolve browned bits for sauce";
		answersArray[23] = "Bread (strong) flour";
		answersArray[24] = "4°C or below";
		answersArray[25] = "Orange";
		answersArray[26] = "Gently combine delicate ingredients to preserve air";
		answersArray[27] = "Poaching";
		answersArray[28] = "Lecithin (egg yolk)";
		answersArray[29] = "Kitchen scale (weight)";
		answersArray[30] = "Arborio rice";
		answersArray[31] = "Simmering bones for hours";
		answersArray[32] = "Basil";
		answersArray[33] = "Roux (butter + flour)";
		answersArray[34] = "Vegetable oil (neutral) or sesame blend";
		answersArray[35] = "A toothpick inserted comes out clean ";
		answersArray[36] = "Salmonella";
		answersArray[37] = "A savory taste (fifth basic taste)";
		answersArray[38] = "Pounding with a mallet";
		answersArray[39] = "Citric acid (lime or lemon juice)";
}

private void checkAnswer(String chosenAnswer, JCheckBox checkBox) {
    if (chosenAnswer.equals(answersArray[currentQuestion])) {
        checkBox.setSelected(true);
        lblResult.setText("CORRECT ✅");
    } else {
        lblResult.setText("FAIL ❌");
    }
}

private void loadQuestion(JLabel lblQuestion, JButton btnA, JButton btnB, JButton btnC, JButton btnD, JCheckBox chckbxNewCheckBox) {
    // reset UI
    chckbxNewCheckBox.setSelected(false);
    lblResult.setText("");

    // pick random question
    currentQuestion = (int)(Math.random() * CookingQuestions.length);
    lblQuestion.setText(CookingQuestions[currentQuestion]);

    // correct answer
    String correct = answersArray[currentQuestion];

    // randomly shuffle answers and assign them to buttons
    String[] options = { "100°C", "Yeast", "Avocado", "30–32°C" }; // example options
    String correctAnswer = options[(int) (Math.random() * options.length)];

    // Set correct answer to one random button
    String[] allButtons = { btnA.getText(), btnB.getText(), btnC.getText(), btnD.getText() };

    for (int i = 0; i < allButtons.length; i++) {
        if (allButtons[i].equals(correctAnswer)) {
            // assign the correct answer to button
        } else {
            // assign wrong options to other buttons
        }
    }
}
}