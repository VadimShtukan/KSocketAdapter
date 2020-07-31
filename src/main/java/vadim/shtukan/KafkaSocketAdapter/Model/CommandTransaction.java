package vadim.shtukan.KafkaSocketAdapter.Model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import vadim.shtukan.KafkaSocketAdapter.Model.CommandStep;

@JsonTypeName("transaction")
public class CommandTransaction extends SocketCommand{
    private Integer terminalId;
    private CommandStep step;

    public Integer getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(Integer terminalId) {
        this.terminalId = terminalId;
    }

    public CommandStep getStep() {
        return step;
    }

    public void setStep(CommandStep step) {
        this.step = step;
    }
}
