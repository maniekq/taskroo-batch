package com.taskroo.batch;


import com.google.inject.Guice;
import com.google.inject.Injector;
import com.taskroo.batch.dao.TasksDao;
import com.taskroo.batch.dao.UsersDao;
import com.taskroo.batch.domain.Task;
import com.taskroo.batch.domain.User;
import org.springframework.mail.javamail.JavaMailSender;

import javax.inject.Inject;
import javax.mail.MessagingException;
import java.util.LinkedList;
import java.util.List;

public class TasksBatch
{
    private final TasksDao tasksDao;
    private final UsersDao usersDao;
    private final JavaMailSender sender;

    public static void main(String[] args) throws MessagingException {
        Injector injector = Guice.createInjector(new MyModule());
        TasksBatch tasksBatch = injector.getInstance(TasksBatch.class);

        tasksBatch.sendEmailsToUsersWithDueDateTasks();
    }

    @Inject
    public TasksBatch(TasksDao tasksDao, UsersDao usersDao, JavaMailSender sender) {
        this.tasksDao = tasksDao;
        this.usersDao = usersDao;
        this.sender = sender;
    }

    public void sendEmailsToUsersWithDueDateTasks() throws MessagingException {
        List<Email> emails = new LinkedList<>();
        List<User> users = usersDao.getEnabledUsersList();
        System.out.println("" + users.size() + " users found.");
        for (User user : users) {
            List<Task> tasks = tasksDao.getAllDueDateTasksOfUser(user.getId());
            System.out.println("" + tasks.size() + " tasks found for user " + user.getEmail());
            if (tasks.size() > 0) {
                emails.add(createEmailWithUserTasks(user, tasks));
            }
        }

        System.out.println("Sending emails...");
        for (Email email : emails) {
            email.send();
        }
        System.out.println("Sending emails finished.");
    }

    private Email createEmailWithUserTasks(User user, List<Task> tasks) {
        String emailContent = "These tasks are due today or overdue:\n";
        for (Task task : tasks) {
            emailContent += "- " + task.getTitle() + " (" + task.getDueDate().toString("yyyy-MM-dd") + ")\n";
        }
        emailContent += "\nYours truly,\nTaskRoo";
        return new Email(sender, user.getEmail(), emailContent);
    }
}
