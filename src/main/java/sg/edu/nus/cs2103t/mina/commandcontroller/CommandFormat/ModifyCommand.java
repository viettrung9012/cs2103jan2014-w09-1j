package sg.edu.nus.cs2103t.mina.commandcontroller.CommandFormat;

import java.text.ParseException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.CommandType;
import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.SimpleKeyword;
import sg.edu.nus.cs2103t.mina.commandcontroller.keyword.StandardKeyword;

//@author A0099151B
public class ModifyCommand extends CommandFormat {
    
    private static Logger logger = LogManager.getLogger(ModifyCommand.class
            .getName());
    
    public ModifyCommand(CommandType commandType, String argumentStr) {
        super(commandType, argumentStr);
    }
    
    @Override
    protected void preProcessArgument() throws ParseException {
        _argumentStr = extractWrappedDescription(_argumentStr);
        _argumentStr = preceedTaskIdFlag(_argumentStr);
    }

    private String preceedTaskIdFlag(String argumentStr) {
        StringBuilder argumentBuild = new StringBuilder(argumentStr);
        String taskIdFlag = "-taskid ";
        argumentBuild.insert(0, taskIdFlag, 0, taskIdFlag.length());
        return argumentBuild.toString();
    }

    @Override
    protected void postProcessing(ArrayList<String> tokens) {
        return;
    }

    @Override
    protected String getProperArgument(String properCommand) {
        String log = _argument.getKeywordValueView();
        StringBuilder commandBuilder = new StringBuilder(properCommand);
        
        logger.info(log);
        
        //get taskid, nullify the id so that it wouldn't be added.
        commandBuilder = appendValue(commandBuilder, SimpleKeyword.TASKID);
        _argument.setKeywordValue(SimpleKeyword.TASKID, null);
        
        for (StandardKeyword keyword: _argument.getArgumentKeys()) {
            
            if(_argument.getKeywordValue(keyword)==null){
                continue;
            }
            
            commandBuilder = appendKeyword(commandBuilder, keyword);

        }
        
        return trimmedCommand(commandBuilder);
    }

}
