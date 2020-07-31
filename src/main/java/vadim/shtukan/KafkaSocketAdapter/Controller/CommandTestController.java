package vadim.shtukan.KafkaSocketAdapter.Controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import vadim.shtukan.KafkaSocketAdapter.Model.CommandTest;
import vadim.shtukan.KafkaSocketAdapter.Model.SocketCommand;
import vadim.shtukan.KafkaSocketAdapter.Service.WorkerSocketServerRunnable;

@Component("CommandTest")
public class CommandTestController implements CommandOrchestrator<CommandTest> {
    private static final Logger logger = LogManager.getLogger(WorkerSocketServerRunnable.class);


    @Override
    public Boolean doCommand(CommandTest socketCommand) {
        logger.debug("Run command TEST");

        return true;
    }
}
