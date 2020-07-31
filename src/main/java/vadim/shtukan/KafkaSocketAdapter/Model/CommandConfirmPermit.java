package vadim.shtukan.KafkaSocketAdapter.Model;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("confirmPermit")
public class CommandConfirmPermit extends SocketCommand{
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
