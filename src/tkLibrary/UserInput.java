package tkLibrary;

public class UserInput {
    private CommandType command;
    private Task task;
    private Task editedTask;

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

    public void setEditedTask(Task editedTask) {
        this.editedTask = editedTask;
    }

    public Task getEditedTask() {
        return this.editedTask;
    }
}