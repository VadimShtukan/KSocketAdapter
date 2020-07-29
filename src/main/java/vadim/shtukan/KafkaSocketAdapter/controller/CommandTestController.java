package vadim.shtukan.KafkaSocketAdapter.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import vadim.shtukan.KafkaSocketAdapter.model.SocketCommand;
import vadim.shtukan.KafkaSocketAdapter.service.WorkerSocketServerRunnable;

@Component("CommandTest")
public class CommandTestController implements CommandOrchestrator {
    private static final Logger logger = LogManager.getLogger(WorkerSocketServerRunnable.class);

    @Override
    public Boolean doCommand(SocketCommand socketCommand) {
        logger.debug("Run command TEST");

        return true;
    }
}
