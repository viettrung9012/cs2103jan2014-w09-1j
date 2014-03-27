package sg.edu.nus.cs2103t.mina.view;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import sg.edu.nus.cs2103t.mina.controller.CommandManager;
import sg.edu.nus.cs2103t.mina.model.DeadlineTask;
import sg.edu.nus.cs2103t.mina.model.EventTask;
import sg.edu.nus.cs2103t.mina.model.Task;
import sg.edu.nus.cs2103t.mina.model.TaskType;
import sg.edu.nus.cs2103t.mina.model.TaskView;
import sg.edu.nus.cs2103t.mina.model.TodoTask;
import sg.edu.nus.cs2103t.mina.utils.DateUtil;

public class MinaGuiUI extends MinaView {

    private static final String EMPTY_STRING = "";

    private TaskView _taskView;

    private Calendar _today;
    private Calendar _tomorrow;

    private Shell _shell;
    private Display _display;
    private Text _userInputTextField;
    private Label _statusBar;
    private StyledText _eventListUI;
    private StyledText _deadlineListUI;
    private StyledText _todoListUI;
    private StyledText _backgroundBox;
    private Label _lblEvent;
    private Label _lblDeadline;
    private Label _lblTodo;

    private int _eventPage;
    private int _deadlinePage;
    private int _todoPage;

    private Label _eventPrevPage;
    private Label _eventNextPage;

    private Label _deadlinePrevPage;
    private Label _deadlineNextPage;

    private Label _todoPrevPage;
    private Label _todoNextPage;

    private final String RIGHT_ARROW = "\u2192";
    private final String LEFT_ARROW = "\u2190";
    
    private LinkedList<String> _commandHistory;
    private int _commandPosition;
    private boolean _historyUp;

    private boolean _isExpanded;

    private int _currentTab;
    // private boolean _highlightOn;

    private static final String ERROR = "Operation failed. Please try again.";
    private static final String INVALID_COMMAND = "Invalid command. Please re-enter.";
    private static final String SUCCESS = "Operation completed";

    private static Logger logger = LogManager.getLogger(MinaGuiUI.class
            .getName());

    public MinaGuiUI(CommandManager commandController) {
        super(commandController);
        createContents();
    }

    /**
     * Open the window.
     */
    public Shell open() {
        logger.log(Level.INFO, "shell open");
        Monitor primary = _display.getPrimaryMonitor();        
        Rectangle bounds = primary.getBounds();
        Rectangle rect = _shell.getBounds();
        int x = bounds.x + (bounds.width - rect.width) / 2;
        int y = bounds.y + (bounds.height - rect.height) / 2;
        _shell.setLocation(x, y);

        logger.log(Level.INFO, "shell set position");
        _shell.open();
        _shell.layout();
        return _shell;
    }

    /**
     * Create contents of the window.
     */
    protected void createContents() {
        logger.log(Level.INFO, "shell create contents");
        _display = Display.getDefault();

        _commandHistory = new LinkedList<String>();
        _commandPosition = 0;
        _historyUp = true;

        _currentTab = 0;
        // _highlightOn = false;

        _eventPage = 1;
        _deadlinePage = 1;
        _todoPage = 1;

        _today = Calendar.getInstance();
        _tomorrow = Calendar.getInstance();
        _tomorrow.add(Calendar.DAY_OF_YEAR, 1);

        _taskView = _commandController.getTaskView();

        _shell = new Shell(_display, SWT.NO_TRIM);
        _shell.setBackground(SWTResourceManager.getColor(0, 0, 0));
        _shell.setSize(1080, 580);
        _shell.setText("MINA");

        _statusBar = new Label(_shell, SWT.NONE);
        _statusBar.setFont(SWTResourceManager.getFont("Trebuchet MS", 15,
                SWT.NORMAL));
        _statusBar.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        _statusBar.setBounds(722, 540, 354, 36);

        _userInputTextField = new Text(_shell, SWT.NONE);
        _userInputTextField
                .setForeground(SWTResourceManager.getColor(0, 51, 0));
        _userInputTextField.setFont(SWTResourceManager.getFont("Trebuchet MS",
                15, SWT.NORMAL));
        _userInputTextField.setBounds(4, 540, 714, 36);

        _lblEvent = new Label(_shell, SWT.NONE);
        _lblEvent.setAlignment(SWT.CENTER);
        _lblEvent.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        _lblEvent.setFont(SWTResourceManager.getFont("Trebuchet MS", 15,
                SWT.BOLD));
        _lblEvent.setBackground(SWTResourceManager
                .getColor(89,89,89));
        _lblEvent.setText("Events(e)");

        _lblDeadline = new Label(_shell, SWT.NONE);
        _lblDeadline.setAlignment(SWT.CENTER);
        _lblDeadline
                .setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        _lblDeadline.setFont(SWTResourceManager.getFont("Trebuchet MS", 15,
                SWT.BOLD));
        _lblDeadline.setBackground(SWTResourceManager
                .getColor(89,89,89));
        _lblDeadline.setText("Deadlines(d)");

        _lblTodo = new Label(_shell, SWT.NONE);
        _lblTodo.setAlignment(SWT.CENTER);
        _lblTodo.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        _lblTodo.setFont(SWTResourceManager.getFont("Trebuchet MS", 15,
                SWT.BOLD));
        _lblTodo.setBackground(SWTResourceManager.getColor(89,89,89));
        _lblTodo.setText("To-do(td)");

        _todoNextPage = new Label(_shell, SWT.NONE);
        _todoNextPage.setForeground(SWTResourceManager
                .getColor(SWT.COLOR_WHITE));
        _todoNextPage.setFont(SWTResourceManager.getFont("Trebuchet MS", 20,
                SWT.BOLD));
        _todoNextPage.setBackground(SWTResourceManager
                .getColor(89,89,89));
        _todoNextPage.setAlignment(SWT.CENTER);

        _todoPrevPage = new Label(_shell, SWT.NONE);
        _todoPrevPage.setForeground(SWTResourceManager
                .getColor(SWT.COLOR_WHITE));
        _todoPrevPage.setFont(SWTResourceManager.getFont("Trebuchet MS", 20,
                SWT.BOLD));
        _todoPrevPage.setBackground(SWTResourceManager
                .getColor(89,89,89));
        _todoPrevPage.setAlignment(SWT.CENTER);

        _deadlinePrevPage = new Label(_shell, SWT.NONE);
        _deadlinePrevPage.setForeground(SWTResourceManager
                .getColor(SWT.COLOR_WHITE));
        _deadlinePrevPage.setFont(SWTResourceManager.getFont("Trebuchet MS",
                20, SWT.BOLD));
        _deadlinePrevPage.setBackground(SWTResourceManager
                .getColor(89,89,89));
        _deadlinePrevPage.setAlignment(SWT.CENTER);

        _deadlineNextPage = new Label(_shell, SWT.NONE);
        _deadlineNextPage.setForeground(SWTResourceManager
                .getColor(SWT.COLOR_WHITE));
        _deadlineNextPage.setFont(SWTResourceManager.getFont("Trebuchet MS",
                20, SWT.BOLD));
        _deadlineNextPage.setBackground(SWTResourceManager
                .getColor(89,89,89));
        _deadlineNextPage.setAlignment(SWT.CENTER);

        _eventPrevPage = new Label(_shell, SWT.NONE);
        _eventPrevPage.setForeground(SWTResourceManager
                .getColor(SWT.COLOR_WHITE));
        _eventPrevPage.setFont(SWTResourceManager.getFont("Trebuchet MS", 20,
                SWT.BOLD));
        _eventPrevPage.setBackground(SWTResourceManager
                .getColor(89,89,89));
        _eventPrevPage.setAlignment(SWT.CENTER);

        _eventNextPage = new Label(_shell, SWT.NONE);
        _eventNextPage.setForeground(SWTResourceManager
                .getColor(SWT.COLOR_WHITE));
        _eventNextPage.setFont(SWTResourceManager.getFont("Trebuchet MS", 20,
                SWT.BOLD));
        _eventNextPage.setBackground(SWTResourceManager
                .getColor(89,89,89));
        _eventNextPage.setAlignment(SWT.CENTER);

        updateArrowNavigation();

        _eventListUI = new StyledText(_shell, SWT.NONE | SWT.WRAP);
        _eventListUI.setEnabled(false);
        _eventListUI.setEditable(false);
        _eventListUI
                .setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        _eventListUI.setFont(SWTResourceManager.getFont("Trebuchet MS", 15,
                SWT.NORMAL));
        _eventListUI.setBackground(SWTResourceManager
                .getColor(89,89,89));

        _deadlineListUI = new StyledText(_shell, SWT.NONE | SWT.WRAP);
        _deadlineListUI.setEnabled(false);
        _deadlineListUI.setEditable(false);
        _deadlineListUI.setForeground(SWTResourceManager
                .getColor(SWT.COLOR_WHITE));
        _deadlineListUI.setFont(SWTResourceManager.getFont("Trebuchet MS", 15,
                SWT.NORMAL));
        _deadlineListUI.setBackground(SWTResourceManager
                .getColor(89,89,89));

        _todoListUI = new StyledText(_shell, SWT.NONE | SWT.WRAP);
        _todoListUI.setEnabled(false);
        _todoListUI.setEditable(false);
        _todoListUI.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        _todoListUI.setFont(SWTResourceManager.getFont("Trebuchet MS", 15,
                SWT.NORMAL));
        _todoListUI.setBackground(SWTResourceManager
                .getColor(89,89,89));

        _userInputTextField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int key = e.keyCode;
                switch (key) {
                    case SWT.CR:
                        String command = _userInputTextField.getText();
                        if (_commandHistory.size() < 5) {
                            _commandHistory.push(command);
                        } else {
                            _commandHistory.removeLast();
                            _commandHistory.push(command);
                        }
                        _commandPosition = 0;
                        _taskView = _commandController.processUserInput(
                                command, _eventPage, _deadlinePage, _todoPage);
                        _userInputTextField.setText(EMPTY_STRING);
                        displayOutput();
                        updateLists();
                        updateArrowNavigation();
                        if (_taskView.hasOnlyOneType(TaskType.EVENT)) {
                            _currentTab = 0;
                            expand();
                        } else if (_taskView.hasOnlyOneType(TaskType.DEADLINE)) {
                            _currentTab = 1;
                            expand();
                        } else if (_taskView.hasOnlyOneType(TaskType.TODO)) {
                            _currentTab = 2;
                            expand();
                        } else {
                            resetPanel();
                        }
                        break;
                    case SWT.ARROW_UP:
                        if (_commandHistory.size() != 0) {
                            if (_historyUp) {
                                if (_commandPosition < _commandHistory.size() - 1) {
                                    _userInputTextField.setText(_commandHistory
                                            .get(_commandPosition) + " ");
                                    _userInputTextField
                                            .setSelection(_userInputTextField
                                                    .getCharCount());
                                    _commandPosition++;
                                } else {
                                    _userInputTextField.setText(_commandHistory
                                            .get(_commandPosition) + " ");
                                    _userInputTextField
                                            .setSelection(_userInputTextField
                                                    .getCharCount());
                                }
                            } else {
                                _commandPosition++;
                                _historyUp = true;
                                _userInputTextField.setText(_commandHistory
                                        .get(_commandPosition) + " ");
                                _userInputTextField
                                        .setSelection(_userInputTextField
                                                .getCharCount());
                                if (_commandPosition < _commandHistory.size() - 1) {
                                    _commandPosition++;
                                }
                            }
                        }
                        break;
                    case SWT.ARROW_DOWN:
                        if (_commandHistory.size() != 0) {
                            if (_commandPosition > 0) {
                                _commandPosition--;
                                _userInputTextField.setText(_commandHistory
                                        .get(_commandPosition));
                                _userInputTextField
                                        .setSelection(_userInputTextField
                                                .getCharCount());
                            } else {
                                _commandPosition = -1;
                                _userInputTextField.setText(EMPTY_STRING);
                            }
                            _historyUp = false;
                        }
                        break;
                    case SWT.ESC:
                        _taskView = _commandController
                                .processUserInput("display", _eventPage,
                                        _deadlinePage, _todoPage);
                        resetPanel();
                        updateLists();
                        break;
                    default:
                        break;
                }
            }

            public void keyReleased(KeyEvent e) {
                int key = e.keyCode;
                switch (key) {
                    case SWT.CR:
                        break;
                    case SWT.ARROW_UP:
                        _userInputTextField.setText(_userInputTextField
                                .getText().trim());
                        _userInputTextField.setSelection(_userInputTextField
                                .getCharCount());
                        break;
                    case SWT.ARROW_DOWN:
                        break;
                    default:
                        break;
                }
            }
        });

        _backgroundBox = new StyledText(_shell, SWT.NONE);
        _backgroundBox.setDoubleClickEnabled(false);
        _backgroundBox.setEnabled(false);
        _backgroundBox.setEditable(false);

        resetPanel();

        _shell.addListener(SWT.KeyDown, new Listener() {
            public void handleEvent(Event event) {
                if (event.stateMask == SWT.CTRL && event.keyCode == SWT.TAB) {
                    _currentTab = (_currentTab + 1) % 3;
                    if (!_isExpanded) {
                        positionBackgroundBox();
                        showBackgroundBox();
                    } else {
                        if (_currentTab == 0) {
                            expandEvent();
                        } else if (_currentTab == 1) {
                            expandDeadline();
                        } else {
                            expandTodo();
                        }
                    }
                }
                if (event.stateMask == SWT.CTRL
                        && event.keyCode == SWT.ARROW_RIGHT) {
                    if (_currentTab == 0) {
                        int maxNumberOfEventPages = _taskView.maxEventPage();
                        if (_eventPage < maxNumberOfEventPages) {
                            _eventPage++;
                            updateEventList();
                        }
                    } else if (_currentTab == 1) {
                        int maxNumberOfDeadlinePages = _taskView
                                .maxDeadlinePage();
                        if (_deadlinePage < maxNumberOfDeadlinePages) {
                            _deadlinePage++;
                            updateDeadlineList();
                        }
                    } else {
                        int maxNumberOfTodoPages = _taskView.maxTodoPage();
                        if (_todoPage < maxNumberOfTodoPages) {
                            _todoPage++;
                            updateTodoList();
                        }
                    }
                    updateArrowNavigation();
                }
                if (event.stateMask == SWT.CTRL
                        && event.keyCode == SWT.ARROW_LEFT) {
                    if (_currentTab == 0) {
                        if (_eventPage > 1) {
                            _eventPage--;
                            updateEventList();
                        }
                    } else if (_currentTab == 1) {
                        if (_deadlinePage > 1) {
                            _deadlinePage--;
                            updateDeadlineList();
                        }
                    } else {
                        if (_todoPage > 1) {
                            _todoPage--;
                            updateTodoList();
                        }
                    }
                    updateArrowNavigation();
                }
                if (event.stateMask == SWT.CTRL && event.keyCode == 'e') {
                    if (_isExpanded) {
                        resetPanel();
                    } else {
                        expand();
                    }
                }
                if (event.stateMask != SWT.CTRL
                        && ((event.keyCode > 31 && event.keyCode < 127) || (event.keyCode == SWT.ARROW_UP
                                || event.keyCode == SWT.ARROW_DOWN
                                || event.keyCode == SWT.ARROW_LEFT || event.keyCode == SWT.ARROW_RIGHT))) {
                    _userInputTextField.forceFocus();
                    if (event.keyCode > 31 && event.keyCode < 127) {
                        _userInputTextField.append(String
                                .valueOf((char) event.keyCode));
                    }
                }
                if (event.keyCode == SWT.ESC) {
                    _taskView = _commandController.processUserInput("display",
                            _eventPage, _deadlinePage, _todoPage);
                    resetPanel();
                    updateLists();
                }
            }
        });       
    }

    @Override
    public String getUserInput() {
        logger.log(Level.INFO, "shell get user input");
        return _userInputTextField.getText();
    }

    @Override
    public void displayOutput() {
        logger.log(Level.INFO, "shell diplay output");
        final String outputMessage = _taskView.getStatus();
        _display.asyncExec(new Runnable() {
            @Override
            public void run() {
                if (outputMessage.contains("Error")
                        || outputMessage.contains("invalid")) {
                    _statusBar.setBackground(SWTResourceManager
                            .getColor(247, 150, 70));
                    if (outputMessage.contains("Error")) {
                        _statusBar.setText(ERROR);
                    } else {
                        _statusBar.setText(INVALID_COMMAND);
                    }
                } else {
                    _statusBar.setBackground(SWTResourceManager
                            .getColor(155, 187, 89));
                    if (outputMessage.contains("welcome")) {
                        _statusBar.setText(outputMessage);
                    } else {
                        _statusBar.setText(SUCCESS);
                    }
                }
            }
        });
    }

    @Override
    public void updateLists() {
        logger.log(Level.INFO, "shell update lists");

        updateEventList();
        updateDeadlineList();
        updateTodoList();

    }

    public void loop() {
        logger.log(Level.INFO, "shell running loop");
        while (!_shell.isDisposed()) {
            if (!_display.readAndDispatch()) {
                _display.sleep();
            }
        }
    }

    private void updateEventList() {
        ArrayList<Task<?>> eventList = _taskView.getPage(TaskType.EVENT,
                _eventPage);
        _eventListUI.setText(EMPTY_STRING);
        Calendar currentDate = null;
        String currentDateString = "";
        Calendar itemStartDate;
        Calendar itemEndDate;
        int initialCursorPosition = 0;
        for (int i = 0; i < eventList.size(); i++) {
            EventTask event = (EventTask) eventList.get(i);
            itemStartDate = DateUtil.toCalendar(event.getStartTime());
            itemEndDate = DateUtil.toCalendar(event.getEndTime());
            if ((currentDate == null)
                    || (!DateUtil
                            .isSameDateCalendar(currentDate, itemStartDate))) {
                currentDate = itemStartDate;
                StyleRange eventStyle = new StyleRange();
                if (currentDate.before(_today)&&!DateUtil.isSameDateCalendar(_today, currentDate)){
                	eventStyle.start = initialCursorPosition;
                    currentDateString = currentDate.get(Calendar.DATE) + "/"
                            + (currentDate.get(Calendar.MONTH) + 1) + "/"
                            + currentDate.get(Calendar.YEAR) + "\n";
                    eventStyle.length = currentDateString.length();
                    eventStyle.foreground = SWTResourceManager
                            .getColor(SWT.COLOR_GRAY);
                    _eventListUI.append(currentDateString);
                    _eventListUI.setStyleRange(eventStyle);
                    initialCursorPosition += currentDateString.length();
                } else if (DateUtil.isSameDateCalendar(_today, currentDate)) {
                    eventStyle.start = initialCursorPosition;
                    currentDateString = "Today\n";
                    eventStyle.length = currentDateString.length();
                    eventStyle.foreground = SWTResourceManager
                            .getColor(247, 150, 70);
                    _eventListUI.append(currentDateString);
                    _eventListUI.setStyleRange(eventStyle);
                    initialCursorPosition += currentDateString.length();
                } else if (DateUtil.isSameDateCalendar(_tomorrow, currentDate)) {
                    eventStyle.start = initialCursorPosition;
                    currentDateString = "Tomorrow\n";
                    eventStyle.length = currentDateString.length();
                    eventStyle.foreground = SWTResourceManager
                            .getColor(225, 212, 113);
                    _eventListUI.append(currentDateString);
                    _eventListUI.setStyleRange(eventStyle);
                    initialCursorPosition += currentDateString.length();
                } else {
                    eventStyle.start = initialCursorPosition;
                    currentDateString = currentDate.get(Calendar.DATE) + "/"
                            + (currentDate.get(Calendar.MONTH) + 1) + "/"
                            + currentDate.get(Calendar.YEAR) + "\n";
                    eventStyle.length = currentDateString.length();
                    eventStyle.foreground = SWTResourceManager
                            .getColor(155, 187, 89);
                    _eventListUI.append(currentDateString);
                    _eventListUI.setStyleRange(eventStyle);
                    initialCursorPosition += currentDateString.length();
                }
            }
            String eventStringIndex = "\t" + (i+1) +".\t";
            String eventDescription = event.getDescription()+"\n";
            String eventTime = "\t\t"+itemStartDate.get(Calendar.HOUR) + ":"
                    + String.format("%02d", itemStartDate.get(Calendar.MINUTE))
                    + " "
                    + (itemStartDate.get(Calendar.AM_PM) == 0 ? "am" : "pm")
                    + " - "
                    + (DateUtil.isSameDateCalendar(itemStartDate, itemEndDate) ? ""
                            : String.format("%02d",
                                    itemEndDate.get(Calendar.DATE))
                                    + "/"
                                    + String.format("%02d",
                                            itemEndDate.get(Calendar.MONTH) + 1)
                                    + "/"
                                    + itemEndDate.get(Calendar.YEAR)
                                    + " ") + itemEndDate.get(Calendar.HOUR)
                    + ":"
                    + String.format("%02d", itemEndDate.get(Calendar.MINUTE))
                    + " "
                    + (itemEndDate.get(Calendar.AM_PM) == 0 ? "am" : "pm")
                    + "\n";
            String eventCompleted = (event.isCompleted()?"\t\tdone\n":"");
            _eventListUI.append(eventStringIndex);
            initialCursorPosition += eventStringIndex.length();
            
            _eventListUI.append(eventDescription);
            StyleRange styleRange = new StyleRange();
            styleRange.start = initialCursorPosition;
            styleRange.length = eventDescription.length();
            styleRange.fontStyle = SWT.BOLD;
            _eventListUI.setStyleRange(styleRange);
            initialCursorPosition += eventDescription.length();
            
            _eventListUI.append(eventTime);
            initialCursorPosition += eventTime.length();
            
            	_eventListUI.append(eventCompleted);
            	StyleRange completedStyle = new StyleRange();
            	completedStyle.start = initialCursorPosition;
            	completedStyle.length = eventCompleted.length();
            	completedStyle.fontStyle = SWT.ITALIC;
            	_eventListUI.setStyleRange(completedStyle);
            	initialCursorPosition += eventCompleted.length();
        }
    }

    private void updateDeadlineList() {
        ArrayList<Task<?>> deadlineList = _taskView.getPage(TaskType.DEADLINE,
                _deadlinePage);
        _deadlineListUI.setText(EMPTY_STRING);
        Calendar currentDate = null;
        String currentDateString = "";
        Calendar itemDate;
        int initialCursorPosition = 0;
        for (int i = 0; i < deadlineList.size(); i++) {
            DeadlineTask deadline = (DeadlineTask) deadlineList.get(i);
            itemDate = DateUtil.toCalendar(deadline.getEndTime());
            if ((currentDate == null)
                    || (!DateUtil.isSameDateCalendar(currentDate, itemDate))) {
                currentDate = itemDate;
                StyleRange deadlineStyle = new StyleRange();
            	if (currentDate.before(_today)&&!DateUtil.isSameDateCalendar(_today, currentDate)){
            		deadlineStyle.start = initialCursorPosition;
                    currentDateString = currentDate.get(Calendar.DATE) + "/"
                            + (currentDate.get(Calendar.MONTH) + 1) + "/"
                            + currentDate.get(Calendar.YEAR) + "\n";
                    deadlineStyle.length = currentDateString.length();
                    deadlineStyle.foreground = SWTResourceManager
                            .getColor(SWT.COLOR_GRAY);
                    _deadlineListUI.append(currentDateString);
                    _deadlineListUI.setStyleRange(deadlineStyle);
                    initialCursorPosition += currentDateString.length();
            	} else if (DateUtil.isSameDateCalendar(_today, currentDate)) {
                    deadlineStyle.start = initialCursorPosition;
                    currentDateString = "Today\n";
                    deadlineStyle.length = currentDateString.length();
                    deadlineStyle.foreground = SWTResourceManager
                            .getColor(247, 150, 70);
                    _deadlineListUI.append(currentDateString);
                    _deadlineListUI.setStyleRange(deadlineStyle);
                    initialCursorPosition += currentDateString.length();
                } else if (DateUtil.isSameDateCalendar(_tomorrow, currentDate)) {
                    deadlineStyle.start = initialCursorPosition;
                    currentDateString = "Tomorrow\n";
                    deadlineStyle.length = currentDateString.length();
                    deadlineStyle.foreground = SWTResourceManager
                            .getColor(225, 212, 113);
                    _deadlineListUI.append(currentDateString);
                    _deadlineListUI.setStyleRange(deadlineStyle);
                    initialCursorPosition += currentDateString.length();
                } else {
                    deadlineStyle.start = initialCursorPosition;
                    currentDateString = currentDate.get(Calendar.DATE) + "/"
                            + (currentDate.get(Calendar.MONTH) + 1) + "/"
                            + currentDate.get(Calendar.YEAR) + "\n";
                    deadlineStyle.length = currentDateString.length();
                    deadlineStyle.foreground = SWTResourceManager
                            .getColor(155, 187, 89);
                    _deadlineListUI.append(currentDateString);
                    _deadlineListUI.setStyleRange(deadlineStyle);
                    initialCursorPosition += currentDateString.length();
                }
            }
            String deadlineStringIndex = "\t" + (i + 1) + ". ";
            String deadlineDescription = deadline.getDescription();
            String deadlineTime = " by "
                    + itemDate.get(Calendar.HOUR) + ":"
                    + String.format("%02d", itemDate.get(Calendar.MINUTE))
                    + " "
                    + (itemDate.get(Calendar.AM_PM) == 0 ? "am" : "pm")
                    + "\n";
            String deadlineCompleted = (deadline.isCompleted()?"\t\tdone\n":"");
            _deadlineListUI.append(deadlineStringIndex);
            initialCursorPosition += deadlineStringIndex.length();
            
            _deadlineListUI.append(deadlineDescription);
            StyleRange styleRange = new StyleRange();
            styleRange.start = initialCursorPosition;
            styleRange.length = deadlineDescription.length();
            styleRange.fontStyle = SWT.BOLD;
            _deadlineListUI.setStyleRange(styleRange);
            initialCursorPosition += deadlineDescription.length();
            
            _deadlineListUI.append(deadlineTime);
            initialCursorPosition += deadlineTime.length();
            
            	_deadlineListUI.append(deadlineCompleted);
            	StyleRange completedStyle = new StyleRange();
            	completedStyle.start = initialCursorPosition;
            	completedStyle.length = deadlineCompleted.length();
            	completedStyle.fontStyle = SWT.ITALIC;
            	_deadlineListUI.setStyleRange(completedStyle);
            	initialCursorPosition += deadlineCompleted.length();
        }
    }

    private void updateTodoList() {
        ArrayList<Task<?>> todoList = _taskView.getPage(TaskType.TODO,
                _todoPage);
        _todoListUI.setText(EMPTY_STRING);
        int initialCursorPosition = 0;
        for (int i = 0; i < todoList.size(); i++) {
            TodoTask todo = (TodoTask) todoList.get(i);
            String todoString = (i + 1) + ". " + todo.getDescription() +"\n";
            _todoListUI.append(todoString);
            StyleRange todoStyle = new StyleRange();
            todoStyle.start = initialCursorPosition;
            todoStyle.length = todoString.length();
            if (todo.getPriority() == 'H') {
                todoStyle.foreground = SWTResourceManager
                        .getColor(247, 150, 70);
            } else if (todo.getPriority() == 'M') {
                todoStyle.foreground = SWTResourceManager
                        .getColor(225, 212, 113);
            } else {
                todoStyle.foreground = SWTResourceManager
                        .getColor(155, 187, 89);
            }
            _todoListUI.setStyleRange(todoStyle);
            initialCursorPosition += todoString.length();
            
            String todoCompleted = (todo.isCompleted()?"\t\tdone\n":"");
            
               	_todoListUI.append(todoCompleted);
            	StyleRange completedStyle = new StyleRange();
            	completedStyle.start = initialCursorPosition;
            	completedStyle.length = todoCompleted.length();
            	completedStyle.fontStyle = SWT.ITALIC;
            	_todoListUI.setStyleRange(completedStyle);
            	initialCursorPosition += todoCompleted.length();
        }

    }

    private void expand() {
        if (_currentTab == 0) {
            expandEvent();
        } else if (_currentTab == 1) {
            expandDeadline();
        } else {
            expandTodo();
        }
    }

    private void updateArrowNavigation() {
        if (_eventPage >= _taskView.maxEventPage()) {
            _eventNextPage.setText("");
        } else {
            _eventNextPage.setText(RIGHT_ARROW);
        }
        if (_eventPage == 1) {
            _eventPrevPage.setText("");
        } else {
            _eventPrevPage.setText(LEFT_ARROW);
        }
        if (_deadlinePage >= _taskView.maxDeadlinePage()) {
            _deadlineNextPage.setText("");
        } else {
            _deadlineNextPage.setText(RIGHT_ARROW);
        }
        if (_deadlinePage == 1) {
            _deadlinePrevPage.setText("");
        } else {
            _deadlinePrevPage.setText(LEFT_ARROW);
        }
        if (_todoPage >= _taskView.maxTodoPage()) {
            _todoNextPage.setText("");
        } else {
            _todoNextPage.setText(RIGHT_ARROW);
        }
        if (_todoPage == 1) {
            _todoPrevPage.setText("");
        } else {
            _todoPrevPage.setText(LEFT_ARROW);
        }
    }

    private void showEvent() {
        _lblEvent.setBounds(4, 4, 354, 36);
        _lblEvent.setBackground(SWTResourceManager
                .getColor(89,89,89));
        setEventPanelSize(4, 40, 354, 496);
    }

    private void hideEvent() {
        _lblEvent.setBounds(4, 4, 354, 32);
        _lblEvent.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
        setEventPanelSize(4, 40, 354, 0);
    }

    private void showDeadline() {
        _lblDeadline.setBounds(362, 4, 356, 36);
        _lblDeadline.setBackground(SWTResourceManager
                .getColor(89,89,89));
        setDeadlinePanelSize(362, 40, 356, 496);
    }

    private void hideDeadline() {
        _lblDeadline.setBounds(362, 4, 356, 32);
        _lblDeadline
                .setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
        setDeadlinePanelSize(362, 40, 356, 0);
    }

    private void showTodo() {
        _lblTodo.setBounds(722, 4, 354, 36);
        _lblTodo.setBackground(SWTResourceManager.getColor(89,89,89));
        setTodoPanelSize(722, 40, 354, 496);
    }

    private void hideTodo() {
        _lblTodo.setBounds(722, 4, 354, 32);
        _lblTodo.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
        setTodoPanelSize(722, 40, 354, 0);
    }

    private void positionBackgroundBox() {
        if (_currentTab == 0) {
            _backgroundBox.setBounds(0, 0, 362, 540);
        } else if (_currentTab == 1) {
            _backgroundBox.setBounds(358, 0, 364, 540);
        } else {
            _backgroundBox.setBounds(718, 0, 362, 540);
        }
    }

    private void hideBackgroundBox() {
        _backgroundBox.setBackground(SWTResourceManager
                .getColor(SWT.COLOR_BLACK));
    }

    private void showBackgroundBox() {
        _backgroundBox.setBackground(SWTResourceManager.getColor(153, 153, 51));
    }

    private void expandEvent() {
        hideDeadline();
        hideTodo();
        _lblEvent.setBounds(4, 4, 354, 36);
        _lblEvent.setBackground(SWTResourceManager
                .getColor(89,89,89));
        setEventPanelSize(4, 40, 1072, 496);
        hideBackgroundBox();
        _isExpanded = true;
    }

    private void expandDeadline() {
        hideEvent();
        hideTodo();
        _lblDeadline.setBounds(362, 4, 356, 36);
        _lblDeadline.setBackground(SWTResourceManager
                .getColor(89,89,89));
        setDeadlinePanelSize(4, 40, 1072, 496);
        hideBackgroundBox();
        _isExpanded = true;
    }

    private void expandTodo() {
        hideDeadline();
        hideEvent();
        _lblTodo.setBounds(722, 4, 354, 36);
        _lblTodo.setBackground(SWTResourceManager.getColor(89,89,89));
        setTodoPanelSize(4, 40, 1072, 496);
        hideBackgroundBox();
        _isExpanded = true;
    }

    private void resetPanel() {
        showDeadline();
        showTodo();
        showEvent();
        positionBackgroundBox();
        showBackgroundBox();
        _isExpanded = false;
    }

    private void setEventPanelSize(int x_coordinate, int y_coordinate,
            int width, int height) {
        if (height != 0) {
            _eventListUI.setBounds(x_coordinate, y_coordinate, width, height);
            _eventPrevPage.setBounds(x_coordinate, y_coordinate + height - 36,
                    84, 36);
            _eventNextPage.setBounds(x_coordinate + width - 84, y_coordinate
                    + height - 36, 84, 36);
        } else {
            _eventListUI.setBounds(0, 0, 0, 0);
            _eventPrevPage.setBounds(0, 0, 0, 0);
            _eventNextPage.setBounds(0, 0, 0, 0);
        }
    }

    private void setDeadlinePanelSize(int x_coordinate, int y_coordinate,
            int width, int height) {
        if (height != 0) {
            _deadlineListUI
                    .setBounds(x_coordinate, y_coordinate, width, height);
            _deadlinePrevPage.setBounds(x_coordinate, y_coordinate + height
                    - 36, 84, 36);
            _deadlineNextPage.setBounds(x_coordinate + width - 84, y_coordinate
                    + height - 36, 84, 36);
        } else {
            _deadlineListUI.setBounds(0, 0, 0, 0);
            _deadlinePrevPage.setBounds(0, 0, 0, 0);
            _deadlineNextPage.setBounds(0, 0, 0, 0);
        }
    }

    private void setTodoPanelSize(int x_coordinate, int y_coordinate,
            int width, int height) {
        if (height != 0) {
            _todoListUI.setBounds(x_coordinate, y_coordinate, width, height);
            _todoPrevPage.setBounds(x_coordinate, y_coordinate + height - 36,
                    84, 36);
            _todoNextPage.setBounds(x_coordinate + width - 84, y_coordinate
                    + height - 36, 84, 36);
        } else {
            _todoListUI.setBounds(0, 0, 0, 0);
            _todoPrevPage.setBounds(0, 0, 0, 0);
            _todoNextPage.setBounds(0, 0, 0, 0);
        }
    }
}
