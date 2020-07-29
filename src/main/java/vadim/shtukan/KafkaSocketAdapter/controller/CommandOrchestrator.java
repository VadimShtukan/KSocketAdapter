package vadim.shtukan.KafkaSocketAdapter.controller;

import vadim.shtukan.KafkaSocketAdapter.model.SocketCommand;

public interface CommandOrchestrator {

    Boolean doCommand(SocketCommand socketCommand);
}
