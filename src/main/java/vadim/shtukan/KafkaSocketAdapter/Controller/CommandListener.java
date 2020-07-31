package vadim.shtukan.KafkaSocketAdapter.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import vadim.shtukan.KafkaSocketAdapter.Model.SocketCommand;
import vadim.shtukan.KafkaSocketAdapter.Service.WorkerSocketServerRunnable;

import java.util.Map;

@Controller
@Scope("prototype")
public class CommandListener {
    private static final Logger logger = LogManager.getLogger(WorkerSocketServerRunnable.class);

    @Autowired
    private Map<String, CommandOrchestrator> commandOrchestratorMap;

    public Boolean readCommand(String strCommand) {
        logger.debug("Read line from socket in readSocketCommand: " + strCommand + " in thread: ");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
        objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
        SocketCommand socketCommand = null;
        try {
            socketCommand = objectMapper.readValue(strCommand, SocketCommand.class);
        } catch (JsonProcessingException e) {
            logger.error("Error in JSON parsing. Json string: " + strCommand + " case: " + e.getMessage());
            return false;
        }

        String debug = socketCommand.getClass().getSimpleName();

        CommandOrchestrator commandOrchestrator = commandOrchestratorMap.get(socketCommand.getClass().getSimpleName());
        if(commandOrchestrator == null){
            logger.error("Command: " + socketCommand.getClass().getSimpleName() + " not realised. Json string: " + strCommand);
            return false;
        }

        logger.debug("Try to run command: " + socketCommand.getClass().getSimpleName());

        return commandOrchestrator.doCommand(socketCommand);
    }
}
