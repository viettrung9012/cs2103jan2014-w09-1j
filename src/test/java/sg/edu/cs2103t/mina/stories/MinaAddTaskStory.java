package sg.edu.cs2103t.mina.stories;

import static org.jbehave.core.reporters.Format.CONSOLE;
import static org.jbehave.core.reporters.Format.TXT;
import static org.jbehave.core.reporters.Format.HTML;
import static org.jbehave.core.reporters.Format.IDE_CONSOLE;

import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.junit.JUnitStory;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.InstanceStepsFactory;

import sg.edu.cs2103t.mina.steps.MinaAddTaskSteps;

public class MinaAddTaskStory extends JUnitStory {

    @Override
    public Configuration configuration() {
        return super.configuration().useStoryReporterBuilder(
                new StoryReporterBuilder().withDefaultFormats().withFormats(
                        CONSOLE, TXT, IDE_CONSOLE, HTML));
    }

    @Override
    public InjectableStepsFactory stepsFactory() {
        return new InstanceStepsFactory(configuration(), new MinaAddTaskSteps());
    }
}