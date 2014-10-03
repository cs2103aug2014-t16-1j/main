package tkLibrary;

public class UserInput {
    private String command;
    private Task task;

    public UserInput(String command, Task task) {
        this.command = command;
        this.task = task;
    }

    public String getCommand() {
        return this.command;
    }

    public Task getTask() {
        return this.task;
    }
}
