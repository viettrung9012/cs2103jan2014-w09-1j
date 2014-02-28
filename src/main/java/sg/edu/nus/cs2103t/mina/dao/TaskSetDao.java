package sg.edu.nus.cs2103t.mina.dao;

import java.io.IOException;
import java.util.Set;

import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TaskType;

/**
 * TaskSetDAO for MINA
 * 
 * @author wgx731
 * @author viettrung9012
 * @author duzhiyuan
 * @author joannemah
 */
public interface TaskSetDao {

    /**
     * Save task set into storage
     * 
     * @param taskSet given task to be saved
     * @param taskType the task type to be saved
     * @param isCompleted whether the task in the set is completed
     * @throws IOException
     */
    public void saveTaskSet(Set<? extends Task<?>> taskSet, TaskType taskType,
            boolean isCompleted) throws IOException, IllegalArgumentException;

    /**
     * load task set into memory from storage
     * 
     * @param taskType the task type to be loaded
     * @param isCompleted whether the task in the set is completed
     * @return loaded task set
     * @throws IOException
     */
    public Set<? extends Task<?>> loadTaskSet(TaskType taskType,
            boolean isCompleted) throws IOException, IllegalArgumentException;
}