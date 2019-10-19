package controllers;

import java.util.ArrayList;
import java.util.Arrays;
import models.data.Project;
import models.member.Member;
import models.task.Task;

public class AssignmentController {
    private ArrayList<Integer> validMembersToAssign;
    private ArrayList<Integer> validMembersToUnassign;
    private ArrayList<Integer> validTaskIndexes;
    private ArrayList<String> errorMessages;
    private ArrayList<String> successMessages;
    private Project project;

    /**
     * A Controller class which parses the input from the user to get a list of valid
     * task and member indexes, and manages the assignment to and removal of tasks from members.
     * @param project The which the user is managing.
     */
    public AssignmentController(Project project) {
        this.validMembersToAssign = new ArrayList<>();
        this.validMembersToUnassign = new ArrayList<>();
        this.validTaskIndexes = new ArrayList<>();
        this.errorMessages = new ArrayList<>();
        this.successMessages = new ArrayList<>();
        this.project = project;
    }

    /**
     * Does the actual assignment of task by calling on the project and establishing the
     * necessary links between tasks and members. Collates messages to inform user of
     * successful assignments/unassignments.
     * @param input The input from the user.
     */
    public void assignAndUnassign(String input) {
        parseAssignmentInput(input);
        for (Integer taskIndex : validTaskIndexes) {
            Task task = project.getTask(taskIndex);
            successMessages.add("For task " + taskIndex + " (" + task.getTaskName() + "):");

            //assigning tasks
            for (Integer assigneeIndex : validMembersToAssign) {
                Member member = project.getMembers().getMember(assigneeIndex);
                if (task.isAlreadyAssignedTo(member)){
                    successMessages.add("Task has already been assigned to member "
                    + assigneeIndex + " (" + member.getName() +").");
                } else {
                    project.addTaskToMemberTaskList(task, member);
                    successMessages.add("Assigned to member " + assigneeIndex +
                        " (" + member.getName() +").");
                }
            }
            //unassigning tasks
            for (Integer unassigneeIndex : validMembersToUnassign) {
                Member member = project.getMembers().getMember(unassigneeIndex);
                if (!task.isAlreadyAssignedTo(member)){
                    successMessages.add("Task cannot be unassigned from member "
                        + unassigneeIndex + " (" + member.getName() +") as it was "
                        + "not assigned in the first place!");
                } else {
                    project.unassignMemberFromTask(member, task);
                    successMessages.add("Unassigned task from member " + unassigneeIndex +
                        " (" + member.getName() +").");
                }
            }
            successMessages.add("\n");
        }
    }

    /**
     * Makes sense of user input for task assignment. Parses input String to get valid task and member index
     * numbers, as well as error messages for invalid index numbers.
     * @param input The input from the user.
     */
    public void parseAssignmentInput(String input) {
        //assign task -i 1 2 -to 1 2 3 -rm 4 5
        ArrayList<String> allIndexesToAssign = new ArrayList<>();
        ArrayList<String> allIndexesToUnassign = new ArrayList<>();
        ArrayList<String> allTasksIndexes = new ArrayList<>();

        String [] inputParts = input.split("-");

        for (String s : inputParts) {
            String [] part = s.split(" ");
            switch (part[0]) {
                case "i":
                    allTasksIndexes = new ArrayList<>(Arrays.asList(part));
                    allTasksIndexes.remove("i");
                    break;
                case "to":
                    allIndexesToAssign = new ArrayList<>(Arrays.asList(part));
                    allIndexesToAssign.remove("to");
                    break;
                case "rm":
                    allIndexesToUnassign = new ArrayList<>(Arrays.asList(s.split(" ")));
                    allIndexesToUnassign.remove("rm");
                    break;
                default:
            }
        }

        getValidTaskIndexes(allTasksIndexes); //shortlists all valid task numbers
        if (!allIndexesToAssign.isEmpty()) {
            getValidAssignees(allIndexesToAssign);
        }
        if (!allIndexesToUnassign.isEmpty()) {
            getValidUnassignees(allIndexesToUnassign);
        }

        //check if any assignees and unassignees are the same, remove if necessary
        checkForSameMemberIndexes();
    }

    /**
     * Checks if the user keyed in the same index number for assigning and removing tasks.
     * Removes index number and informs user if so.
     */
    private void checkForSameMemberIndexes() {
        for (Integer index: validMembersToAssign) {
            if (validMembersToUnassign.contains(index)) {
                errorMessages.add("Cannot assign and unassign task to member " + index + " ("
                + project.getMembers().getMember(index).getName() + ") at the same time");
                validMembersToAssign.remove(validMembersToAssign.indexOf(index));
                validMembersToUnassign.remove(validMembersToUnassign.indexOf(index));
            }
        }
    }

    /**
     * Checks if task number is valid.
     * @param taskNumber the index number of task as indicated by user.
     * @return true if the task exists in the project.
     */
    public boolean isValidTaskIndex(int taskNumber) {
        return (taskNumber > 0 && taskNumber <= project.getNumOfTasks());
    }

    /**
     * Stores only valid task index numbers in validTaskIndexes, which is
     * used later to do task assignment.
     * @param allTaskIndexes all index numbers as indicated by user.
     */
    public void getValidTaskIndexes(ArrayList<String> allTaskIndexes) {
        for (String taskIndex : allTaskIndexes) {
            try {
                Integer taskNumber = Integer.parseInt(taskIndex);
                if (isValidTaskIndex(taskNumber)) {
                    validTaskIndexes.add(taskNumber);
                } else {
                    errorMessages.add("The task " + taskNumber + " does not exist.");
                }
            } catch (NumberFormatException e) {
                errorMessages.add("Could not find task: " + taskIndex);
                errorMessages.add("Please ensure that task numbers are integers");
            }
        }
    }

    /**
     * Retrieves valid/existing index numbers of members to be assigned/unassigned tasks.
     * @param allMemberIndexes the member index numbers indicated by the user.
     * @param validIndexes the ArrayList of either assignees or unassignees, to store valid index numbers.
     */
    private void getValidMemberIndexes(ArrayList<String> allMemberIndexes, ArrayList<Integer> validIndexes) {
        for (String memberIndex : allMemberIndexes) {
            try {
                Integer indexNumber = Integer.parseInt(memberIndex);
                if (isValidMemberIndex(indexNumber)) {
                    validIndexes.add(indexNumber);
                } else {
                    errorMessages.add("The member with index " + memberIndex + " does not exist.");
                }
            } catch (NumberFormatException e) {
                errorMessages.add("Could not find member: " + memberIndex);
                errorMessages.add("Please ensure that member index numbers are integers");
            }
        }
    }

    private boolean isValidMemberIndex(Integer indexNumber) {
        return indexNumber > 0 && indexNumber <= project.getNumOfMembers();
    }

    private void getValidAssignees(ArrayList<String> allIndexesToAssign) {
        getValidMemberIndexes(allIndexesToAssign, this.validMembersToAssign);
    }

    private void getValidUnassignees(ArrayList<String> allIndexesToUnassign) {
        getValidMemberIndexes(allIndexesToUnassign, this.validMembersToUnassign);
    }

    /**
     * Returns messages about invalid inputs to be shown to the user.
     * @return an ArrayList of error messages that arise from invalid inputs.
     */
    public ArrayList<String> getErrorMessages() {
        return errorMessages;
    }

    /**
     * Returns messages about successful assignments for valid tasks.
     * @return an ArrayList of success messages.
     */
    public ArrayList<String> getSuccessMessages() {
        return successMessages;
    }

}
