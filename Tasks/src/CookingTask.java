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
	private JLabel lblResult;
	private JLabel lblQuestion;
	
	private JButton btnA, btnB, btnC, btnD;
	private JCheckBox checkBox;

	private ArrayList<Question> questions = new ArrayList<>();
	private Question currentQuestion;
	
	
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
		
		lblQuestion = new JLabel("Question");
		lblQuestion.setHorizontalAlignment(SwingConstants.CENTER);
		lblQuestion.setFont(new Font("Nirmala Text Semilight", Font.PLAIN, 20));
		lblQuestion.setBounds(12, 76, 915, 96);
		contentPane.add(lblQuestion);
		
		btnA = new JButton("A");
        btnA.setBounds(220, 150, 492, 54);
        contentPane.add(btnA);

        btnB = new JButton("B");
        btnB.setBounds(220, 235, 492, 54);
        contentPane.add(btnB);

        btnC = new JButton("C");
        btnC.setBounds(220, 320, 492, 54);
        contentPane.add(btnC);

        btnD = new JButton("D");
        btnD.setBounds(220, 405, 492, 54);
        contentPane.add(btnD);
		
        btnA.setBounds(220, 150, 492, 54);
        btnB.setBounds(220, 235, 492, 54);
        btnC.setBounds(220, 320, 492, 54);
        btnD.setBounds(220, 405, 492, 54);

        contentPane.add(btnA);
        contentPane.add(btnB);
        contentPane.add(btnC);
        contentPane.add(btnD);
        
		checkBox = new JCheckBox("");
		checkBox.setEnabled(false);
		checkBox.setBounds(861, 31, 21, 24);
		contentPane.add(checkBox);
	
        loadQuestions();  
		displayQuestion(); 

		btnA.addActionListener(e -> checkAnswer(0));
		btnB.addActionListener(e -> checkAnswer(1));
		btnC.addActionListener(e -> checkAnswer(2));
		btnD.addActionListener(e -> checkAnswer(3));
	}


public class Question{
	String question;
	String[] options;
	int correctIndex;
	
	public Question(String question, String[] options, int correctIndex) {
		this.question = question;
		this.options = options;
		this.correctIndex = correctIndex;
	}
}

private void loadQuestions() {
	
	questions.add(new Question(
	        "What temperature does water boil at (at sea level)?",
	        new String[]{"90°C", "100°C", "110°C", "95°C"},
	        1 //Index of answer ranging from 0 to 1
	    ));

	    questions.add(new Question(
	        "Which ingredient makes bread rise?",
	        new String[]{"Sugar", "Salt", "Yeast", "Flour"},
	        2
	    ));

	    questions.add(new Question(
	        "What is the main ingredient in guacamole?",
	        new String[]{"Tomato", "Onion", "Avocado", "Lime"},
	        3
	    ));

	    questions.add(new Question(
	        "At approximately what temperature does chocolate begin to melt?",
	        new String[]{"0–5°C", "15–18°C", "30–32°C", "40–45°C"},
	        4
	    ));
	    
	    questions.add(new Question(
	    	    "Which cooking method uses dry heat and circulating air to cook food?",
	    	    new String[]{"Boiling", "Baking", "Steaming", "Poaching"},
	    	    1
	    	));

	    	questions.add(new Question(
	    	    "Which knife is best for chopping vegetables?",
	    	    new String[]{"Bread knife", "Fillet knife", "Chef’s (cook’s) knife", "Paring knife"},
	    	    2
	    	));

	    	questions.add(new Question(
	    	    "What does “al dente” refer to?",
	    	    new String[]{"Overcooked rice", "Soft vegetables", "Pasta cooked to be firm to the bite", "Crispy fried food"},
	    	    2
	    	));

	    	questions.add(new Question(
	    	    "Which of these is a dry-heat cooking technique?",
	    	    new String[]{"Steaming", "Grilling", "Poaching", "Boiling"},
	    	    1
	    	));

	    	questions.add(new Question(
	    	    "Which fat is commonly used for sautéing because of its high smoke point?",
	    	    new String[]{"Vegetable oil", "Butter", "Olive oil (extra virgin)", "Margarine"},
	    	    0
	    	));

	    	questions.add(new Question(
	    	    "What is the recommended minimum internal temperature for cooked chicken (whole or pieces)?",
	    	    new String[]{"55°C", "60°C", "74°C (165°F)", "80°C"},
	    	    2
	    	));

	    	questions.add(new Question(
	    	    "What is the purpose of blanching vegetables?",
	    	    new String[]{"To fry them", "To briefly cook and stop enzyme action for color/texture", "To freeze them immediately", "To add sweetness"},
	    	    1
	    	));

	    	questions.add(new Question(
	    	    "Which of the following is a leavening agent?",
	    	    new String[]{"Baking powder", "Salt", "Water", "Cocoa"},
	    	    0
	    	));

	    	questions.add(new Question(
	    	    "What does “mise en place” mean?",
	    	    new String[]{"Cook quickly", "Everything in its place (prep before cooking)", "Taste as you go", "Finish with sauce"},
	    	    1
	    	));

	    	questions.add(new Question(
	    	    "Which cut of meat is typically most tender?",
	    	    new String[]{"Shank", "Round", "Brisket", "Tenderloin / Filet"},
	    	    3
	    	));

	    	questions.add(new Question(
	    	    "Which of these is best for making mayonnaise?",
	    	    new String[]{"Boiled egg", "Raw egg yolk emulsified with oil and acid", "Bread crumbs", "Grated cheese"},
	    	    1
	    	));

	    	questions.add(new Question(
	    	    "Which spice is the main ingredient in curry powder?",
	    	    new String[]{"Turmeric", "Cumin", "Cinnamon", "Oregano"},
	    	    0
	    	));

	    	questions.add(new Question(
	    	    "Which method cooks food by surrounding it with steam?",
	    	    new String[]{"Frying", "Steaming", "Grilling", "Roasting"},
	    	    1
	    	));

	    	questions.add(new Question(
	    	    "What ingredient is primarily responsible for browning via the Maillard reaction?",
	    	    new String[]{"Water", "Fat", "Proteins and reducing sugars", "Acids"},
	    	    2
	    	));

	    	questions.add(new Question(
	    	    "Which of the following is a dry, cured pork product?",
	    	    new String[]{"Ham (cooked)", "Prosciutto", "Fresh bacon", "Sausage (uncooked)"},
	    	    1
	    	));

	    	questions.add(new Question(
	    	    "Which of these is NOT a mother sauce in classical French cuisine?",
	    	    new String[]{"Béchamel", "Velouté", "Hollandaise", "Pesto"},
	    	    3
	    	));

	    	questions.add(new Question(
	    	    "What is the culinary term for cutting food into long, thin strips?",
	    	    new String[]{"Brunoise", "Chiffonade", "Julienne", "Dice"},
	    	    2
	    	));

	    	questions.add(new Question(
	    	    "Which cheese is traditionally used on pizza for its meltability?",
	    	    new String[]{"Cheddar", "Feta", "Mozzarella", "Parmesan"},
	    	    2
	    	));

	    	questions.add(new Question(
	    	    "What does it mean to “deglaze” a pan?",
	    	    new String[]{"Clean it with soap", "Add liquid to dissolve browned bits for sauce", "Flip food quickly", "Add flour to thicken"},
	    	    1
	    	));

	    	questions.add(new Question(
	    	    "Which of these flours has the highest protein (gluten) content, typically used for bread?",
	    	    new String[]{"Cake flour", "Pastry flour", "Bread (strong) flour", "Rice flour"},
	    	    2
	    	));

	    	questions.add(new Question(
	    	    "What is the safe refrigerator temperature to slow bacterial growth?",
	    	    new String[]{"10°C", "7°C", "4°C or below", "8°C"},
	    	    2
	    	));

	    	questions.add(new Question(
	    	    "Which fruit is high in vitamin C and commonly used to prevent scurvy?",
	    	    new String[]{"Orange", "Banana", "Apple", "Pear"},
	    	    0
	    	));

	    	questions.add(new Question(
	    	    "What does “folding” mean in baking?",
	    	    new String[]{"Rapidly beat ingredients", "Cut ingredients finely", "Gently combine delicate ingredients to preserve air", "Stretch dough"},
	    	    2
	    	));

	    	questions.add(new Question(
	    	    "Which method uses water just below boiling to cook delicate foods?",
	    	    new String[]{"Poaching", "Boiling", "Frying", "Stewing"},
	    	    0
	    	));

	    	questions.add(new Question(
	    	    "Which of these is a common emulsifier used in cooking?",
	    	    new String[]{"Water", "Sugar", "Lecithin/egg yolk", "Salt"},
	    	    2
	    	));

	    	questions.add(new Question(
	    	    "What kitchen tool measures dry ingredient volume most accurately?",
	    	    new String[]{"Tablespoon", "Cup", "Kitchen scale (weight)", "Measuring jug"},
	    	    2
	    	));

	    	questions.add(new Question(
	    	    "Which grain is used to make risotto?",
	    	    new String[]{"Long-grain rice", "Jasmine rice", "Arborio rice", "Basmati rice"},
	    	    2
	    	));

	    	questions.add(new Question(
	    	    "Which method is best for making stock from bones?",
	    	    new String[]{"Frying bones quickly", "Simmering bones for hours", "Freezing bones", "Microwaving bones"},
	    	    1
	    	));

	    	questions.add(new Question(
	    	    "Which herb is the primary ingredient in pesto?",
	    	    new String[]{"Basil", "Parsley", "Cilantro", "Thyme"},
	    	    0
	    	));

	    	questions.add(new Question(
	    	    "What’s the common thickener in cream soups?",
	    	    new String[]{"Lemon juice", "Roux (butter + flour)", "Vinegar", "Soy sauce"},
	    	    1
	    	));

	    	questions.add(new Question(
	    	    "Which oil is traditionally used in Japanese tempura for frying?",
	    	    new String[]{"Olive oil", "Butter", "Vegetable oil (neutral)/sesame blend", "Coconut oil"},
	    	    2
	    	));

	    	questions.add(new Question(
	    	    "Which of these indicates a cake is fully baked?",
	    	    new String[]{"It sinks in the middle", "The top is wet", "It slips off the pan", "A toothpick inserted comes out clean"},
	    	    3
	    	));

	    	questions.add(new Question(
	    	    "Which foodborne pathogen is commonly associated with undercooked eggs?",
	    	    new String[]{"E. coli", "Salmonella", "Listeria", "Botulism"},
	    	    1
	    	));

	    	questions.add(new Question(
	    	    "What is \"umami\"?",
	    	    new String[]{"A texture", "A bitter taste", "A savory taste ", "A cooking method"},
	    	    2
	    	));

	    	questions.add(new Question(
	    	    "Which of the following is a quick method for tenderizing meat?",
	    	    new String[]{"Roasting whole for hours", "Pounding with a mallet", "Freezing only", "Deep-frying until dry"},
	    	    1
	    	));

	    	questions.add(new Question(
	    	    "Which acid is commonly used to “cook” fish in ceviche?",
	    	    new String[]{"Lactic acid", "Malic acid", "Tartaric acid", "Citric acid (lime/lemon juice)"},
	    	    3
	    	));

}

	private void displayQuestion() {
	
	checkBox.setSelected(false);
	lblResult.setText("");
	
	currentQuestion = questions.get((int)(Math.random() * questions.size()));
	
	lblQuestion.setText(currentQuestion.question);
	
	btnA.setText(currentQuestion.options[0]);
	btnB.setText(currentQuestion.options[1]);
	btnC.setText(currentQuestion.options[2]);
	btnD.setText(currentQuestion.options[3]);
	
	btnA.setEnabled(true);
	btnB.setEnabled(true);
	btnC.setEnabled(true);
	btnD.setEnabled(true);
	}
	
	private void checkAnswer(int chosenIndex) {

		   if (chosenIndex == currentQuestion.correctIndex) {
		        checkBox.setSelected(true);
		        lblResult.setText("Correct");
		    } else {
		        lblResult.setText("Wrong answer. Task fail");

		        btnA.setEnabled(false);
		        btnB.setEnabled(false);
		        btnC.setEnabled(false);
		        btnD.setEnabled(false);

		        new javax.swing.Timer(10000, e -> displayQuestion()).start();
		    }
		}

}