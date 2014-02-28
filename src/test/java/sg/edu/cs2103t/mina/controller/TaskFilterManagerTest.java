package sg.edu.cs2103t.mina.controller;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import org.junit.Test;

import sg.edu.cs2103t.mina.stub.TaskDataManagerStub;
import sg.edu.nus.cs2103t.mina.controller.TaskFilterManager;
import sg.edu.nus.cs2103t.mina.model.*;
import sg.edu.nus.cs2103t.mina.model.parameter.FilterParameter;


public class TaskFilterManagerTest {

	private TaskDataManagerStub tdmStub= new TaskDataManagerStub();
	private TaskFilterManager tfmTest = new TaskFilterManager(tdmStub);
	
//	@Test
//	public void viewOutput(){
//		//check for output
//		TaskDataManagerStub tdm = tdmStub;
//		
//		Iterator<TodoTask> todoIterator; 
//		todoIterator = tdm.getTodoTasks().iterator();
//		
//		while(todoIterator.hasNext()){
//			TodoTask task = todoIterator.next();
//			if(task!=null)
//				printTodo(task);
//		}
//		
//		Iterator<EventTask> eventIterator;
//		eventIterator = tdm.getEventTasks().iterator();
//		
//		while(eventIterator.hasNext()){
//			printEvent(eventIterator.next());
//		}
//		
//		Iterator<DeadlineTask> deadlineIterator;
//		deadlineIterator = tdm.getDeadlineTasks().iterator();
//		
//		while(deadlineIterator.hasNext()){
//			printDeadline(deadlineIterator.next());
//		}
//		assertTrue(true);
//	}
	
	@Test (expected=NullPointerException.class)
	public void testFilterVoid() {
		tfmTest.filterTask(null);
	}
	
	
	/**
	 * Test for passing an empty filter parameter
	 * Expected: Returned all uncompleted tasks. 
	 * 					 In this case with the dummy data, everything should
	 * 					 be there.
	 * 
	 */
	@Test
	public void testNoFilter() {
		
		ArrayList<Task<?>> test = tfmTest.filterTask(new FilterParameter());
		boolean isExist = true;
		
		for (Task t: test) {
			
			if (t instanceof TodoTask) {
				isExist = TaskDataManagerStub.getTodoTasks().contains((TodoTask) t);
			} else if(t instanceof EventTask) {
				isExist = TaskDataManagerStub.getEventTasks().contains((EventTask)t);
			} else if(t instanceof DeadlineTask) {
				isExist = TaskDataManagerStub.getDeadlineTasks().contains((DeadlineTask)t);
			}
			if(!isExist) {
				break;
			}
		}
		
		assertTrue("Must have everything!", isExist);
		
	}
	
	/**
	 * Test for displaying deadlines only.
	 * Expected: Deadlines only. In this case,
	 * 					 all dummy data in deadlines.
	 */
	@Test
	public void testDeadlinesOnly(){
		
		ArrayList<Task<?>> test = getResult(TaskFilterManager.DEADLINE);
		
		TreeSet<DeadlineTask> deadlines = TaskDataManagerStub.getDeadlineTasks();
		int numOfDeadlines = deadlines.size();
		
		assertTrue("Must be all deadlines!", numOfDeadlines == test.size() &&
																				isTaskExist(test, deadlines,
																										TaskType.DEADLINE));
		
	}
	
	/**
	 * Test for displaying Todos only.
	 * Expected: Todos only. In this case,
	 * 					 all dummy data in Todos.
	 */
	@Test
	public void testTodosOnly(){

		ArrayList<Task<?>> test = getResult(TaskFilterManager.TODO);
		TreeSet<TodoTask> todos = TaskDataManagerStub.getTodoTasks();
		int numOfTodos = todos.size();
		
		assertTrue("Must be all todos!", numOfTodos == test.size() &&
																		 isTaskExist(test, todos,
																				 				 TaskType.TODO));		
	}
	
	/**
	 * Test for displaying events only.
	 * Expected: Events only. In this case,
	 * 					 all dummy data in Events.
	 */
	@Test
	public void testEventsOnly(){

		ArrayList<Task<?>> test = getResult(TaskFilterManager.EVENT);
		TreeSet<EventTask> events = TaskDataManagerStub.getEventTasks();
		int numOfEvents = events.size();
		
		assertTrue("Must be all events!", numOfEvents == test.size() &&
																		 isTaskExist(test, events,
																				 				 TaskType.EVENT));		
	}
	
	/**
	 * Get the result based on the keywords entered
	 * 
	 * @param cmd the string of keywords
	 * @return An arrayList of tasks.
	 */
	private ArrayList<Task<?>> getResult(String cmd){
		
		String[] tokens = cmd.split(" ");
		ArrayList<String> keywords = new ArrayList<String>();
		
		for (int i = 0; i < tokens.length; i++) {
			keywords.add(tokens[i]);
		}
		
		FilterParameter filter = new FilterParameter(keywords);
		return tfmTest.filterTask(filter);
	}
	
	/**
	 * Compare the returned result to dummy data
	 * 
	 * @param test the arraylist of tasks
	 * @param taskTree the dummy data for task
	 * @param type the TaskType specified.
	 * @return true if everything in the result is in specified tasks
	 */
	private boolean isTaskExist(ArrayList<Task<?>> test,
															TreeSet<? extends Task<?>> taskTree,
															TaskType type) {
		
		for (Task<?> task: test) {
			if( !(isTask(task, type) && taskTree.contains(task)) ) {
				return false;
			}
		}
		
		return true;
	}
	
	private boolean isTask(Task<?> task, TaskType type) {
		
		boolean isTask;
		
		switch (type) {
			case DEADLINE :
				isTask = task instanceof DeadlineTask;
				break;
			case TODO :
				isTask = task instanceof TodoTask;
				break;
			case EVENT :
				isTask = task instanceof EventTask;
				break;
			default:
				isTask = false;
		}
		
		return isTask;
	}
	
	public String toStringForTask(Task<?> task){
		String format = "UUID: %1$s\n" +
										"Type: %2$s\n" +
										"Description: %3$s\n" +
										"Tags: %4$s\n" +
										"Priority: %5$s\n" +
										"Created: %6$s\n" +
										"Last modified: %7$s\n" +
										"Completed: %8$s\n\n";
		String output = String.format(format, task.getId(),
																					task.getType().getType(),
																					task.getDescription(),
																					task.getTags(),
																					task.getPriority(),
																					task.getCreatedTime(),
																					task.getLastEditedTime(),
																					task.isCompleted());
		return output;
	}
	
	public void printEvent(EventTask task){
		String format = "Start: %1$s\n" + 
										"End: %2$s\n";
		System.out.println(toStringForTask(task));
		System.out.println(String.format(format, task.getStartTime(),
																						 task.getEndTime()));
		System.out.println("------------------");
	}
	
	public void printDeadline(DeadlineTask task){
		String format = "End: %1$s\n";
		System.out.println(toStringForTask(task));
		System.out.println(String.format(format, task.getEndTime()));		
		System.out.println("------------------");
	}	
	
	public void printTodo(TodoTask task){
		System.out.println(toStringForTask(task));	
		System.out.println("------------------");		
	}
	
}
