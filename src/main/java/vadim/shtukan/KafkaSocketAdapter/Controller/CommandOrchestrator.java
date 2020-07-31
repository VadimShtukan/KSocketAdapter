package vadim.shtukan.KafkaSocketAdapter.Controller;

import vadim.shtukan.KafkaSocketAdapter.Model.SocketCommand;

public interface CommandOrchestrator<T> {

    Boolean doCommand(T socketCommand);
}
