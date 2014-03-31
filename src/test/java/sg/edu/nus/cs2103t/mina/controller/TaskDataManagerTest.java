package sg.edu.nus.cs2103t.mina.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Calendar;
import java.util.Date;
import org.junit.Test;

import sg.edu.nus.cs2103t.mina.controller.TaskDataManager;
import sg.edu.nus.cs2103t.mina.dao.impl.JsonFileTaskDaoImpl;
import sg.edu.nus.cs2103t.mina.model.DeadlineTask;
import sg.edu.nus.cs2103t.mina.model.EventTask;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.model.TodoTask;
import sg.edu.nus.cs2103t.mina.model.parameter.DataParameter;

public class TaskDataManagerTest {
    // TODO: check if date modify changes.
    // TODO: add test cases for delete completed

    @Test
    public void testAddTask() {
        TaskDataManager tdmTest = new TaskDataManager();
        Long currDateMilliSec = System.currentTimeMillis();

        /* Basic add */
        // Partition: All parameters except _priority specified
        assertEquals("Adding To-do task.", new TodoTask("TodoTask1"),
                tdmTest.addTask(new DataParameter("TodoTask1", 'M', null, null,
                        null, TaskType.TODO, 123)));
        assertEquals(1, tdmTest.getUncompletedTodoTasks().size());

        assertEquals("Adding Deadline task.", new DeadlineTask("DeadlineTask1",
                new Date(currDateMilliSec)), tdmTest.addTask(new DataParameter(
                "DeadlineTask1", 'M', null, new Date(currDateMilliSec), null,
                TaskType.DEADLINE, 123)));
        assertEquals(1, tdmTest.getUncompletedDeadlineTasks().size());

        assertEquals("Adding Event task.", new EventTask("EventTask1",
                new Date(currDateMilliSec), new Date(currDateMilliSec)),
                tdmTest.addTask(new DataParameter("EventTask1", 'M', new Date(
                        currDateMilliSec), new Date(currDateMilliSec), null,
                        TaskType.EVENT, 123)));
        assertEquals(1, tdmTest.getUncompletedEventTasks().size());

        // Partition: All parameters specified
        assertEquals("Adding To-do task.", new TodoTask("TodoTask2", 'H'),
                tdmTest.addTask(new DataParameter("TodoTask2", 'H', null, null,
                        null, TaskType.TODO, 123)));
        assertEquals(2, tdmTest.getUncompletedTodoTasks().size());

        assertEquals("Adding Deadline task.", new DeadlineTask("DeadlineTask2",
                new Date(currDateMilliSec), 'M'),
                tdmTest.addTask(new DataParameter("DeadlineTask2", 'M', null,
                        new Date(currDateMilliSec), null, TaskType.DEADLINE,
                        123)));
        assertEquals(2, tdmTest.getUncompletedDeadlineTasks().size());

        assertEquals("Adding Event task.", new EventTask("EventTask2",
                new Date(currDateMilliSec), new Date(currDateMilliSec), 'H'),
                tdmTest.addTask(new DataParameter("EventTask2", 'H', new Date(
                        currDateMilliSec), new Date(currDateMilliSec), null,
                        TaskType.EVENT, 123)));
        assertEquals(2, tdmTest.getUncompletedEventTasks().size());

        /* Task added is exactly the same as a task that already exists. */
        // Partition: Task does not exist
        assertNull(tdmTest.addTask(new DataParameter("TodoTask2", 'H', null,
                null, null, TaskType.TODO, 123)));

        tdmTest.resetTrees();

        /* Adding a recurring task */
        // Partition: all parameters added correctly, recur every 2 weeks for 4
        // months
        Calendar calendar = Calendar.getInstance();
        calendar.set(2014, 1, 1);
        Date firstDeadline = calendar.getTime();

        DeadlineTask expectedDeadlineTask = new DeadlineTask(
                "RecurDeadlineTask every 2 weeks, for 4 months.",
                firstDeadline, 'M');
        expectedDeadlineTask.addTag("RECUR_0");

        int currMth = calendar.get(Calendar.MONTH);
        calendar.set(Calendar.MONTH, currMth + 4);

        try {
            assertEquals("Adding Deadline Task that reccurs every fortnight.",
                    expectedDeadlineTask, tdmTest.addTask(new DataParameter(
                            "RecurDeadlineTask every 2 weeks, for 4 months.",
                            'M', null, firstDeadline, null, TaskType.DEADLINE,
                            123, "RECUR", (1000 * 3600 * 24 * 14), calendar
                                    .getTime(), null, false)));
            assertEquals(9, tdmTest.getUncompletedDeadlineTasks().size());

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Partition: all parameters added correctly, recur every month

        /* Adding a block task */
        // TODO: set a limit to how many block tasks can be added at one go
    }

    @Test
    public void testDeleteTask() {
        TaskDataManager tdmTest = new TaskDataManager();
        Long currDateMilliSec = System.currentTimeMillis();

        tdmTest.addTask(new DataParameter("Sleep", 'H', null, null,
                TaskType.TODO, TaskType.TODO, 1));
        Task<?> testDeleteTodo1 = tdmTest.addTask(new DataParameter("Lie down",
                'H', null, null, TaskType.TODO, TaskType.TODO, 2));
        tdmTest.addTask(new DataParameter("Bed...", 'H', null, null,
                TaskType.TODO, TaskType.TODO, 3));

        tdmTest.addTask(new DataParameter("Sleep", 'H', null, new Date(
                currDateMilliSec), TaskType.DEADLINE, TaskType.DEADLINE, 1));
        Task<?> testDeleteDeadeline1 = tdmTest.addTask(new DataParameter(
                "Lie down", 'H', null, new Date(currDateMilliSec),
                TaskType.DEADLINE, TaskType.DEADLINE, 2));
        tdmTest.addTask(new DataParameter("Bed...", 'H', null, new Date(
                currDateMilliSec), TaskType.DEADLINE, TaskType.DEADLINE, 3));

        tdmTest.addTask(new DataParameter("Sleep", 'H', new Date(
                currDateMilliSec), new Date(currDateMilliSec), TaskType.EVENT,
                TaskType.EVENT, 1));
        Task<?> testDeleteEvent1 = tdmTest.addTask(new DataParameter(
                "Lie down", 'H', new Date(currDateMilliSec), new Date(
                        currDateMilliSec), TaskType.EVENT, TaskType.EVENT, 2));
        tdmTest.addTask(new DataParameter("Bed...", 'H', new Date(
                currDateMilliSec), new Date(currDateMilliSec), TaskType.EVENT,
                TaskType.EVENT, 3));

        /* Basic Delete */
        // Partition: Task exist in the file
        assertEquals("Todo tasks.", new TodoTask("Lie down", 'H'),
                tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                        TaskType.TODO, null, 2, testDeleteTodo1)));
        assertEquals(2, tdmTest.getUncompletedTodoTasks().size());

        assertEquals("Deadline tasks.", new DeadlineTask("Lie down", new Date(
                currDateMilliSec), 'H'), tdmTest.deleteTask(new DataParameter(
                null, 'M', null, null, TaskType.DEADLINE, null, 2,
                testDeleteDeadeline1)));
        assertEquals(2, tdmTest.getUncompletedDeadlineTasks().size());

        assertEquals("Event tasks.", new EventTask("Lie down", new Date(
                currDateMilliSec), new Date(currDateMilliSec), 'H'),
                tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                        TaskType.EVENT, null, 2, testDeleteEvent1)));
        assertEquals(2, tdmTest.getUncompletedEventTasks().size());

        /* deleting a task that does not exist */
        // Partition: Delete Task non-existent
        assertNull("Deleting todo task that does not exist.",
                tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                        TaskType.TODO, null, 2, testDeleteTodo1)));
        assertNull("Deleting deadline task that does not exist.",
                tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                        TaskType.DEADLINE, null, 2, testDeleteDeadeline1)));
        assertNull("Deleting event task that does not exist.",
                tdmTest.deleteTask(new DataParameter(null, 'M', null, null,
                        TaskType.EVENT, null, 2, testDeleteEvent1)));
    }

    @Test
    public void testModifyTask() {
        TaskDataManager tdmTest = new TaskDataManager(new DataSyncManager(
                new JsonFileTaskDaoImpl()));
        Long currDateMilliSec = System.currentTimeMillis();

        /* Partition: modify all task parameters but don't change the task type */
        Task<?> testModifyTodo1 = tdmTest
                .addTask(new DataParameter("TodoTask I am slack.", 'L', null,
                        null, null, TaskType.TODO, 1));
        assertEquals("Modify todo description and priority.", new TodoTask(
                "TodoTask I am not slack anymore.", 'H'),
                tdmTest.modifyTask(new DataParameter(
                        "TodoTask I am not slack anymore.", 'H', null, null,
                        TaskType.TODO, TaskType.TODO, 1, testModifyTodo1)));

        Task<?> testModifyDeadline1 = tdmTest.addTask(new DataParameter(
                "Deadline I am slack.", 'L', null, new Date(currDateMilliSec),
                null, TaskType.DEADLINE, 1));
        assertEquals("Modify deadline description, endDate and priority.",
                new DeadlineTask("Deadline I am not slack anymore.", new Date(
                        currDateMilliSec - 100000), 'H'),
                tdmTest.modifyTask(new DataParameter(
                        "Deadline I am not slack anymore.", 'H', null,
                        new Date(currDateMilliSec - 100000), TaskType.DEADLINE,
                        TaskType.DEADLINE, 1, testModifyDeadline1)));

        Task<?> testModifyEvent1 = tdmTest.addTask(new DataParameter(
                "Event I am slack.", 'L', new Date(currDateMilliSec), new Date(
                        currDateMilliSec), null, TaskType.EVENT, 1));
        assertEquals("Modify deadline description, endDate and priority.",
                new EventTask("Deadline I am not slack anymore.", new Date(
                        currDateMilliSec - 100000), new Date(
                        currDateMilliSec - 100000), 'H'),
                tdmTest.modifyTask(new DataParameter(
                        "Deadline I am not slack anymore.", 'H', new Date(
                                currDateMilliSec - 100000), new Date(
                                currDateMilliSec - 100000), TaskType.EVENT,
                        TaskType.EVENT, 1, testModifyEvent1)));

        assertEquals(1, tdmTest.getUncompletedTodoTasks().size());
        assertEquals(1, tdmTest.getUncompletedDeadlineTasks().size());
        assertEquals(1, tdmTest.getUncompletedEventTasks().size());

        tdmTest.resetTrees();

        /* Partition: modify task type and all its parameters */
        Task<?> testModifyTaskType1 = tdmTest.addTask(new DataParameter(
                "TodoTask becomes a DeadlineTask.", 'M', null, null, null,
                TaskType.TODO, 123));

        assertEquals("Modify to-do to deadline.",
                new DeadlineTask("TodoTask becomes a DeadlineTask.", new Date(
                        currDateMilliSec)),
                tdmTest.modifyTask(new DataParameter(null, 'M', null, new Date(
                        currDateMilliSec), TaskType.TODO, TaskType.DEADLINE, 1,
                        testModifyTaskType1)));
        assertEquals(0, tdmTest.getUncompletedTodoTasks().size());
        assertEquals(1, tdmTest.getUncompletedDeadlineTasks().size());

        Task<?> testModifyTaskType2 = tdmTest.addTask(new DataParameter(
                "TodoTask becomes an EventTask.", 'M', null, null, null,
                TaskType.TODO, 123));
        assertEquals("Modify to-do to event.", new EventTask(
                "TodoTask becomes an EventTask.", new Date(currDateMilliSec),
                new Date(currDateMilliSec)),
                tdmTest.modifyTask(new DataParameter(null, 'M', new Date(
                        currDateMilliSec), new Date(currDateMilliSec),
                        TaskType.TODO, TaskType.EVENT, 1, testModifyTaskType2)));
        assertEquals(0, tdmTest.getUncompletedTodoTasks().size());
        assertEquals(1, tdmTest.getUncompletedEventTasks().size());

        tdmTest.resetTrees();

        Task<?> testModifyTaskType3 = tdmTest.addTask(new DataParameter(
                "Deadline task becomes a TodoTask.", 'M', null, new Date(
                        currDateMilliSec), null, TaskType.DEADLINE, 123));
        assertEquals("Modify deadline to to-do.", new TodoTask(
                "Deadline task becomes a TodoTask."),
                tdmTest.modifyTask(new DataParameter(null, 'M', null, null,
                        TaskType.DEADLINE, TaskType.TODO, 1,
                        testModifyTaskType3)));
        assertEquals(1, tdmTest.getUncompletedTodoTasks().size());
        assertEquals(0, tdmTest.getUncompletedDeadlineTasks().size());

        Task<?> testModifyTaskType4 = tdmTest.addTask(new DataParameter(
                "Deadline task becomes an EventTask.", 'M', null, new Date(
                        currDateMilliSec), null, TaskType.DEADLINE, 1));
        assertEquals("Modify deadline to event", new EventTask(
                "Deadline task becomes an EventTask.", new Date(
                        currDateMilliSec), new Date(currDateMilliSec), 'M'),
                tdmTest.modifyTask(new DataParameter(null, 'M', new Date(
                        currDateMilliSec), null, TaskType.DEADLINE,
                        TaskType.EVENT, 1, testModifyTaskType4)));
        assertEquals(0, tdmTest.getUncompletedDeadlineTasks().size());
        assertEquals(1, tdmTest.getUncompletedEventTasks().size());

        tdmTest.resetTrees();

        Task<?> testModifyTaskType5 = tdmTest.addTask(new DataParameter(
                "Event task becomes TodoTask.", 'M',
                new Date(currDateMilliSec), new Date(currDateMilliSec), null,
                TaskType.EVENT, 1));
        assertEquals("Modify deadline to event", new TodoTask(
                "Event task becomes TodoTask.", 'M'),
                tdmTest.modifyTask(new DataParameter(null, 'M', null, null,
                        TaskType.EVENT, TaskType.TODO, 1, testModifyTaskType5)));

        assertEquals(0, tdmTest.getUncompletedEventTasks().size());
        assertEquals(1, tdmTest.getUncompletedTodoTasks().size());

        Task<?> testModifyTaskType6 = tdmTest.addTask(new DataParameter(
                "Event task becomes DeadlineTask.", 'M', new Date(
                        currDateMilliSec), new Date(currDateMilliSec), null,
                TaskType.EVENT, 2));
        assertEquals("Modify deadline to event", new TodoTask(
                "Event task becomes DeadlineTask."),
                tdmTest.modifyTask(new DataParameter(null, 'M', null, null,
                        TaskType.EVENT, TaskType.DEADLINE, 1,
                        testModifyTaskType6)));

        assertEquals(0, tdmTest.getUncompletedEventTasks().size());
        assertEquals(1, tdmTest.getUncompletedDeadlineTasks().size());

        tdmTest.resetTrees();

        /* modify a task that does not exist */
        Task<?> testModifyTaskType7 = new TodoTask(
                "Event task becomes TodoTask.", 'M');
        assertNull("Modifying a task that does not exist.",
                tdmTest.modifyTask(new DataParameter(null, 'M', null, null,
                        TaskType.EVENT, TaskType.DEADLINE, 1,
                        testModifyTaskType7)));
        assertEquals(0, tdmTest.getUncompletedTodoTasks().size());

    }

    @Test
    public void markCompleted() {
        TaskDataManager tdmTest = new TaskDataManager();
        Long currDateMilliSec = System.currentTimeMillis();

        Task<?> testMarkTodo1 = tdmTest.addTask(new DataParameter(
                "Mark this TodoTask complete", 'M', null, null, null,
                TaskType.TODO, 1));

        TodoTask expectedTodo = new TodoTask("Mark this TodoTask complete", 'M');
        expectedTodo.setCompleted(true);
        assertEquals("Mark Todo completed", expectedTodo,
                tdmTest.markCompleted(new DataParameter(null, 'M', null, null,
                        TaskType.TODO, null, 1, testMarkTodo1)));
        assertEquals(0, tdmTest.getUncompletedTodoTasks().size());
        assertEquals(1, tdmTest.getCompletedTodoTasks().size());

        Task<?> testMarkDeadline1 = tdmTest.addTask(new DataParameter(
                "Mark this DeadlineTask complete", 'M', null, new Date(
                        currDateMilliSec), null, TaskType.DEADLINE, 123));

        DeadlineTask expectedDeadline = new DeadlineTask(
                "Mark this DeadlineTask complete", new Date(currDateMilliSec),
                'M');
        expectedDeadline.setCompleted(true);
        assertEquals("Mark Deadline completed", expectedDeadline,
                tdmTest.markCompleted(new DataParameter(null, 'M', null, null,
                        TaskType.DEADLINE, null, 1, testMarkDeadline1)));
        assertEquals(0, tdmTest.getUncompletedDeadlineTasks().size());
        assertEquals(1, tdmTest.getCompletedDeadlineTasks().size());

        Task<?> testMarkEvent1 = tdmTest.addTask(new DataParameter(
                "Mark this EventTask complete", 'M',
                new Date(currDateMilliSec), new Date(currDateMilliSec), null,
                TaskType.EVENT, 123));

        EventTask expectedEvent = new EventTask("Mark this EventTask complete",
                new Date(currDateMilliSec), new Date(currDateMilliSec), 'M');
        expectedEvent.setCompleted(true);
        assertEquals("Mark Event completed", expectedEvent,
                tdmTest.markCompleted(new DataParameter(null, 'M', null, null,
                        TaskType.EVENT, null, 1, testMarkEvent1)));
        assertEquals(0, tdmTest.getUncompletedEventTasks().size());
        assertEquals(1, tdmTest.getCompletedEventTasks().size());
    }

}
