package tkLibrary;

public class UserInput {
    private CommandType command;
    private Task task;

    public UserInput(CommandType command, Task task) {
        this.command = command;
        this.task = task;
    }

    public CommandType getCommand() {
        return this.command;
    }

    public Task getTask() {
        return this.task;
    }
}
