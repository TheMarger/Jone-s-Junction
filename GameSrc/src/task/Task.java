package task;

public class Task {
	
	private String description;
	private boolean isCompleted;
	public String name = "Generic Task";

	public Task(String description) {
		this.description = description;
		this.isCompleted = false;
	}

	public String getDescription() {
		return description;
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return (isCompleted ? "[X] " : "[ ] ") + description;
	}

}
