package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import models.data.Project;
import models.temp.tasks.TaskList;
import repositories.ProjectRepository;
import views.CLIView;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ConsoleInputController implements IViewController {

    private CLIView consoleView;
    private TaskList taskList;
    private ProjectRepository projectRepository;
    private ProjectInputController projectInputController;
    private String filePath = System.getProperty("user.dir") + "/savedProjects.json";

    /**
     * Constructor.
     * @param view : takes in a View model, in this case a command line view.
     */
    public ConsoleInputController(CLIView view) {
        this.consoleView = view;
        this.taskList = new TaskList();
//        this.projectRepository = new ProjectRepository();
        loadProjectsData();
        this.projectInputController = new ProjectInputController(this.consoleView, this.projectRepository);
    }

    /**
     * Method that is called upon receiving commands from CLI.
     * @param input : Input typed by user into CLI
     */
    @Override
    public void onCommandReceived(String input) {
        Scanner inputReader = new Scanner(input);
        String command = inputReader.next();

        switch (command) {
        case "bye":
            consoleView.end();
            break;
        case "list":
            ArrayList<Project> allProjects = projectRepository.getAll();
            consoleView.viewAllProjects(allProjects);
            break;
        case "done":
        case "delete":
            if (inputReader.hasNext()) {
                switch (command) {
                case "done":
                    consoleView.markDone(taskList, input);
                    break;
                case "delete":
                    consoleView.deleteTask(taskList, input);
                    break;
                default:
                }
            } else {
                consoleView.consolePrint("Oops! Please enter task number.");
            }
            break;
        case "find":
            // Can consider implementation in the future
        case "create":
            // Creation of a new project with a given name and a number of numbers
            boolean isProjectCreated = projectRepository.addToRepo(input);
            if (!isProjectCreated) {
                consoleView.consolePrint("Creation of Project failed. Please check parameters given!");
            } else {
                consoleView.consolePrint("Project created!");
                saveProjectsData();
            }
            break;
        case "manage":
            if (inputReader.hasNext()) {
                this.projectInputController.manageProject(inputReader.next());
            } else {
                consoleView.consolePrint("Please enter a project number!");
            }
            break;
        case "help":
            // TODO help page displaying all commands available
            // Not implemented
        default:
            consoleView.consolePrint("Invalid inputs. Please refer to User Guide or type help!");
        }
    }


    private void saveProjectsData() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        try {
            FileWriter fileWriter = new FileWriter(filePath);
            gson.toJson(this.projectRepository, fileWriter);
            fileWriter.flush();
            fileWriter.close();
        } catch (java.io.IOException err) {
            consoleView.consolePrint("savedProjects file does not exist or is not created!");
        }
    }

    private void loadProjectsData() {
        Gson gson = new Gson();
        try (FileReader fileReader = new FileReader(filePath)) {
            this.projectRepository = gson.fromJson(fileReader, ProjectRepository.class);
            if (this.projectRepository == null) {
                this.projectRepository = new ProjectRepository();
            }
        } catch (IOException err) {
            this.projectRepository = new ProjectRepository();
        }
    }
}
