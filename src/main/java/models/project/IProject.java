package models.project;

import java.util.ArrayList;
import java.util.HashMap;
import models.member.IMemberList;
import models.member.Member;
import models.task.Task;
import models.task.TaskList;

public interface IProject {
    String getName();

    IMemberList getMembers();

    TaskList getTasks();

    int getNumOfMembers();

    int getNumOfTasks();

    void addMember(Member newMember);

    void editMember(int memberIndexNumber, String updatedMemberDetails);

    void removeMember(Member toBeRemoved);

    void addTask(Task newTask);

    void removeTask(int taskIndexNumber);

    boolean memberIndexExists(int indexNumber);

    Task getTask(int taskIndex);

    String[] editTask(int taskIndexNumber, String updatedTaskDetails);

    String[] editTaskRequirements(int taskIndexNumber, String updatedTaskRequirements);

    ArrayList<String> getCredits();

    void createAssignment(Task task, Member member);

    void removeAssignment(Member member, Task task);

    boolean containsAssignment(Task task, Member member);

    HashMap<Member, ArrayList<Task>> getMembersIndividualTaskList();

    HashMap<Task, ArrayList<Member>> getTasksAndAssignedMembers();
}
