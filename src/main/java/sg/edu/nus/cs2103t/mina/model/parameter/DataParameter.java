package sg.edu.nus.cs2103t.mina.model.parameter;

import java.util.Date;
import java.util.List;

import sg.edu.nus.cs2103t.mina.model.DeadlineTask;
import sg.edu.nus.cs2103t.mina.model.EventTask;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.model.TimePair;

public class DataParameter {
    /* for all tasks */
    private String _description;
    private char _priority;
    private Date _start;
    private Date _end;
    private TaskType _originalTaskType;
    private TaskType _newTaskType;
    private int _taskID;

    /* for recurring and block tasks */
    private String _tag; // either 'RECUR' or 'BLOCK'
    private boolean _modifyAll;

    /* for BLOCK tasks */
    private List<TimePair> _timeSlots; // for BLOCK only
    /* for RECURRING Tasks */
    private String _timeType; // refer to field values of CALENDAR
    private int _freqOfTimeType;

    private Date _endRecurOn;

    private Task<?> _taskObject;

    /**
     * Constructors for DataParameter
     */
    // default constructor
    public DataParameter() {
        setDescription(null);
        setPriority('M');
        setStartDate(null);
        setEndDate(null);
        setOriginalTaskType(null);
        setNewTaskType(null);
        setTaskID(-1);

        setTag(null);
        setModifyAll(false);

        setTimeSlots(null);

        setTimeType(null);
        setFreqOfTimeType(0);

        setEndRecurOn(null);

        setTaskObject(null);

    }

    // if task is not recurring or block, for adding task
    public DataParameter(String des, char pri, Date start, Date end,
            TaskType origType, TaskType newType, int id) {
        setDescription(des);
        setPriority(pri);
        setStartDate(start);
        setEndDate(end);
        setOriginalTaskType(origType);
        setNewTaskType(newType);
        setTaskID(id);

        setTag(null);
        setModifyAll(false);

        setTimeSlots(null);

        setTimeType(null);
        setFreqOfTimeType(0);

        setEndRecurOn(null);

        setTaskObject(null);
    }

    // if task is not recurring or block, for deleting, modifying, marking tasks
    public DataParameter(String des, char pri, Date start, Date end,
            TaskType origType, TaskType newType, int id, Task<?> taskObj) {
        setDescription(des);
        setPriority(pri);
        setStartDate(start);
        setEndDate(end);
        setOriginalTaskType(origType);
        setNewTaskType(newType);
        setTaskID(id);

        setTaskObject(taskObj);

        if (taskObj.getType() == TaskType.DEADLINE) {
            DeadlineTask deadlineTaskObj = (DeadlineTask) taskObj;

            setEndDate(end == null ? deadlineTaskObj.getEndTime() : end);
        }

        if (taskObj.getType() == TaskType.EVENT) {
            EventTask eventTaskObj = (EventTask) taskObj;

            setEndDate(end == null ? eventTaskObj.getEndTime() : end);
            setStartDate(start == null ? eventTaskObj.getStartTime() : start);
        }

        setTag(null);
        setModifyAll(false);

        setTimeSlots(null);

        setTimeType(null);
        setFreqOfTimeType(0);
        setEndRecurOn(null);

    }

    // for adding recurring or block tasks
    public DataParameter(String des, char pri, Date start, Date end,
            TaskType origType, TaskType newType, int id, String tag,
            Date endRecurOn, String timeType, int freqOfTimeType,
            List<TimePair> timeSlots, boolean isModifyAll) throws Exception {
        assert (!tag.equals(null));

        if (tag.equals("BLOCK")) {
            assert (newType.equals(TaskType.EVENT));
            createAddBlockParameters(des, pri, start, end, origType, newType,
                    id, tag, timeSlots);

        } else if (tag.equals("RECUR")) {
            assert (!newType.equals(TaskType.TODO));
            createAddRecurParameters(des, pri, start, end, origType, newType,
                    id, tag, timeType, freqOfTimeType, endRecurOn);

        } else {
            throw new Exception("invalid tag used");
        }

    }

    // for modifying or deleting recurring or block tasks
    public DataParameter(String des, char pri, Date start, Date end,
            TaskType origType, TaskType newType, int id, Task<?> taskObj,
            String tag, String timeType, int freqOfTimeType, Date endRecurOn,
            List<TimePair> timeSlots, boolean isModifyAll) throws Exception {
        assert (!tag.equals(null));

        if (tag.equals("BLOCK")) {
            assert (!origType.equals(null) && origType.equals(TaskType.EVENT) || !newType
                    .equals(null) && newType.equals(TaskType.EVENT));
            createBlockParameters(des, pri, start, end, origType, newType, id,
                    taskObj, tag, timeSlots, isModifyAll);

        } else if (tag.equals("RECUR")) {
            assert (!origType.equals(null) && !origType.equals(TaskType.TODO) || !newType
                    .equals(null) && !newType.equals(TaskType.TODO));
            createRecurParameters(des, pri, start, end, origType, newType, id,
                    taskObj, tag, timeType, freqOfTimeType, endRecurOn,
                    isModifyAll);

        } else {
            throw new Exception("invalid tag used by CC");
        }

    }

    // only for event TaskTypes
    private void createAddBlockParameters(String des, char pri, Date start,
            Date end, TaskType origType, TaskType newType, int id, String tag,
            List<TimePair> timeSlots) {
        setDescription(des);
        setPriority(pri);
        setStartDate(start);
        setEndDate(end);
        setOriginalTaskType(origType);
        setNewTaskType(newType);
        setTaskID(id);

        setTag(tag);
        setTimeType(null);
        setFreqOfTimeType(0);

        setTimeSlots(timeSlots);
        setModifyAll(false);

    }

    private void createBlockParameters(String des, char pri, Date start,
            Date end, TaskType origType, TaskType newType, int id,
            Task<?> taskObj, String tag, List<TimePair> timeSlots,
            boolean isModifyAll) {
        setDescription(des);
        setPriority(pri);
        setStartDate(start);
        setEndDate(end);
        setOriginalTaskType(origType);
        setNewTaskType(newType);
        setTaskID(id);

        // parameters specific to EventTask
        EventTask eventTaskObj = (EventTask) taskObj;
        setEndDate(end == null ? eventTaskObj.getEndTime() : end);
        setStartDate(start == null ? eventTaskObj.getStartTime() : start);

        setTag(tag);
        setTimeType(null);
        setFreqOfTimeType(0);
        setEndRecurOn(null);

        setTimeSlots(timeSlots);
        setModifyAll(isModifyAll);

    }

    // only for event and deadline TaskTypes
    private void createAddRecurParameters(String des, char pri, Date start,
            Date end, TaskType origType, TaskType newType, int id, String tag,
            String timeType, int freqOfTimeType, Date endRecurOn) {
        setDescription(des);
        setPriority(pri);
        setStartDate(start);
        setEndDate(end);
        setOriginalTaskType(origType);
        setNewTaskType(newType);
        setTaskID(id);

        setTag(tag);
        setTimeType(timeType);
        setFreqOfTimeType(freqOfTimeType);
        setEndRecurOn(endRecurOn);

        setTimeSlots(null);

        setModifyAll(false);

    }

    private void createRecurParameters(String des, char pri, Date start,
            Date end, TaskType origType, TaskType newType, int id,
            Task<?> taskObj, String tag, String timeType, int freqOfTimeType,
            Date endRecurOn, boolean isModifyAll) {
        setDescription(des);
        setPriority(pri);
        setStartDate(start);
        setEndDate(end);
        setOriginalTaskType(origType);
        setNewTaskType(newType);
        setTaskID(id);

        setTaskObject(taskObj);

        if (taskObj.getType() == TaskType.DEADLINE) {
            DeadlineTask deadlineTaskObj = (DeadlineTask) taskObj;

            setEndDate(end == null ? deadlineTaskObj.getEndTime() : end);
        }

        if (taskObj.getType() == TaskType.EVENT) {
            EventTask eventTaskObj = (EventTask) taskObj;

            setEndDate(end == null ? eventTaskObj.getEndTime() : end);
            setStartDate(start == null ? eventTaskObj.getStartTime() : start);
        }

        setTag(tag);
        setTimeType(timeType);
        setFreqOfTimeType(freqOfTimeType);
        setEndRecurOn(endRecurOn);

        setTag(tag);
        setTimeSlots(null);
        setModifyAll(isModifyAll);

        setEndRecurOn(endRecurOn);

    }

    /**
     * This method checks the existing parameters within DataParameter and
     * infers the possible TaskType that it can model after. If there are not
     * enough parameters, it returns and UNKNOWN type.
     * 
     * @return TaskType inferredTaskType.
     */
    public TaskType determineTaskType() {

        if (_description != null) {
            if (_end != null) {
                if (_start != null) {
                    return TaskType.EVENT;
                }
                return TaskType.DEADLINE;
            }
            return TaskType.TODO;
        }

        return TaskType.UNKNOWN;
    }

    /**
     * This method takes in an existing task, and loads all of its old
     * parameters. Caution: overrides any existing data if they existed.
     */
    public void loadOldTask(Task<?> taskToLoad) {
        if (taskToLoad == null) {
            return;
        }

        setDescription(taskToLoad.getDescription());
        setPriority(taskToLoad.getPriority());
        // setTaskID(taskToLoad.getId());
        setOriginalTaskType(taskToLoad.getType());

        setTag(taskToLoad.getTag());

        if (taskToLoad.getType() == TaskType.DEADLINE) {
            DeadlineTask taskToLoadDeadline = (DeadlineTask) taskToLoad;
            setEndDate(taskToLoadDeadline.getEndTime());
        }

        if (taskToLoad.getType() == TaskType.EVENT) {
            EventTask taskToLoadEvent = (EventTask) taskToLoad;
            setEndDate(taskToLoadEvent.getEndTime());
            setStartDate(taskToLoadEvent.getStartTime());

        }
    }

    /**
     * For the modify function. Call this function after you call
     * loadOldTask(Task taskToModify).Takes in the values that user wants to
     * modify, and adds it onto existing values.
     */
    public void loadNewParameters(DataParameter modifyParam) {
        if (modifyParam.getDescription() != null) {
            setDescription(modifyParam.getDescription());
        }
        if (modifyParam.getPriority() != _priority) {
            setPriority(modifyParam.getPriority());
        }
        if (modifyParam.getStartDate() != null) {
            setStartDate(modifyParam.getStartDate());
        }
        if (modifyParam.getEndDate() != null) {
            setEndDate(modifyParam.getEndDate());
        }
        if (modifyParam.getOriginalTaskType() != null) {
            setOriginalTaskType(modifyParam.getOriginalTaskType());
        } else {
            assert (false); // shouldn't be null
        }
        if (modifyParam.getNewTaskType() != null) {
            setNewTaskType(modifyParam.getNewTaskType());
        } else {
            assert (false); // shouldn't be null
        }
        if (modifyParam.getTaskId() != -1) {
            setTaskID(modifyParam.getTaskId());
        }
        if (modifyParam.getTag() != "") {
            setTag(modifyParam.getTag());
        }
        if (modifyParam.getNewTaskType() != null) {
            setNewTaskType(modifyParam.getNewTaskType());
        }

        // if (_originalTaskType != _newTaskType) {
        // if (_originalTaskType == TaskType.DEADLINE && _newTaskType ==
        // TaskType.TODO) {
        // // _description += (" by " + _end);
        // // _end = null;
        // } else if (_originalTaskType == TaskType.EVENT && _newTaskType ==
        // TaskType.TODO) {
        // // _description += (" from " + _start + " to " + _end);
        // // _start = null;
        // // _end = null;
        // } else if (_originalTaskType == TaskType.EVENT && _newTaskType ==
        // TaskType.DEADLINE) {
        // // _end = _start;
        // // _start = null;
        // }
        //
        // _originalTaskType = _newTaskType;
        // }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DataParameter other = (DataParameter) obj;
        if (_description == null) {
            if (other._description != null)
                return false;
        } else if (!_description.equals(other._description))
            return false;
        if (_end == null) {
            if (other._end != null)
                return false;
        } else if (!_end.equals(other._end))
            return false;
        if (_newTaskType != other._newTaskType)
            return false;
        if (_originalTaskType != other._originalTaskType)
            return false;
        if (_priority != other._priority)
            return false;
        if (_start == null) {
            if (other._start != null)
                return false;
        } else if (!_start.equals(other._start))
            return false;
        if (_taskID != other._taskID)
            return false;
        return true;
    }

    /** get Methods */
    public String getDescription() {
        return _description;
    }

    public char getPriority() {
        return _priority;
    }

    public Date getStartDate() {
        return _start;
    }

    public Date getEndDate() {
        return _end;
    }

    public TaskType getOriginalTaskType() {
        return _originalTaskType;
    }

    public TaskType getNewTaskType() {
        return _newTaskType;
    }

    public int getTaskId() {
        return _taskID;
    }

    public Task<?> getTaskObject() {
        return _taskObject;
    }

    public String getTag() {
        return _tag;
    }

    public boolean isModifyAll() {
        return _modifyAll;
    }

    public List<TimePair> getTimeSlots() {
        return _timeSlots;
    }

    public Date getEndRecurOn() {
        return _endRecurOn;
    }

    public int getFreqOfTimeType() {
        return _freqOfTimeType;
    }

    public String getTimeType() {
        return _timeType;
    }

    /** set Methods */
    public void setDescription(String description) {
        _description = description;
    }

    public void setPriority(char priority) {
        _priority = priority;
    }

    public void setStartDate(Date start) {
        _start = start;
    }

    public void setEndDate(Date end) {
        _end = end;
    }

    public void setOriginalTaskType(TaskType originalTaskType) {
        _originalTaskType = originalTaskType;
    }

    public void setNewTaskType(TaskType newTaskType) {
        _newTaskType = newTaskType;
    }

    public void setTaskID(int taskID) {
        _taskID = taskID;
    }

    public void setTaskObject(Task<?> taskObject) {
        _taskObject = taskObject;
    }

    public void setTag(String tag) {
        _tag = tag;
    }

    public void setModifyAll(boolean modifyAll) {
        _modifyAll = modifyAll;
    }

    public void setTimeSlots(List<TimePair> timeSlots) {
        _timeSlots = timeSlots;
    }

    public void setEndRecurOn(Date endRecurOn) {
        _endRecurOn = endRecurOn;
    }

    public void setFreqOfTimeType(int freqOfTimeType) {
        _freqOfTimeType = freqOfTimeType;
    }

    public void setTimeType(String timeType) {
        _timeType = timeType;
    }

}
