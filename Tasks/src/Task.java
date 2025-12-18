
public class Task {
	static String taskName = "";
	boolean taskCompleted;
	TaskFrame frame = new TaskFrame();
	
	public Task(String name) {
		this.taskName = name;
		taskCompleted = false;
		callFrame();
	}
	
	public void callFrame() {
		frame.setVisible(true);
	}
		
	public String getTaskName() {
		return taskName;
	}
	
	public boolean isTaskCompleted() {
		return taskCompleted;
	}
	
	public void setTaskCompleted(boolean completed) {
		this.taskCompleted = completed;
	}

}
