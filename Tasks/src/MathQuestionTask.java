
public class MathQuestionTask extends Task {
	int difficultyLevel;
	MathQuestionTaskFrame frame = new MathQuestionTaskFrame();

	public MathQuestionTask(int level) {
		super("Math Question Task");
		difficultyLevel = level;
		String[] question = generateQuestion(difficultyLevel);
		frame.setQuestion(question[0]);
	}

	private String[] generateQuestion(int difficultyLevel) {
		String[] operations = {"+", "-", "*", "/"}; String question; int answer;
		String operation1 = operations[(int)(Math.random() * operations.length)];
		int number1 = (int)(Math.random() * (difficultyLevel * 10)) + 1;
		int number2 = (int)(Math.random() * (difficultyLevel * 10)) + 1;
		boolean is3numbers = Math.random() < 0.5;
		if (is3numbers) {
			int number3 = (int)(Math.random() * (difficultyLevel * 10)) + 1;
			String operation2 = operations[(int)(Math.random() * operations.length)];
			question = ("What is " + number1 + " " + operation1 + " " + number2 + " " + operation2 + " " + number3 + "?");
			int answer1 = switch (operation1) {
				case "+" -> number1 + number2;
				case "-" -> number1 - number2;
				case "*" -> number1 * number2;
				case "/" -> number1 / number2;
				default -> 0;
			};
			answer = switch (operation2) {
				case "+" -> answer1 + number3;
				case "-" -> answer1 - number3;
				case "*" -> answer1 * number3;
				case "/" -> answer1 / number3;
				default -> 0;
			};
		} else {
			question = ("What is " + number1 + " " + operation1 + " " + number2 + "?");
			answer = switch (operation1) {
				case "+" -> number1 + number2;
				case "-" -> number1 - number2;
				case "*" -> number1 * number2;
				case "/" -> number1 / number2;
				default -> 0;
			};
		}
		return new String[] {question, Integer.toString(answer)};
	}

}
