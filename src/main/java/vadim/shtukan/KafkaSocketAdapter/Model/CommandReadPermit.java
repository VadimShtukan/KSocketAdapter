package vadim.shtukan.KafkaSocketAdapter.Model;

import com.fasterxml.jackson.annotation.JsonTypeName;


@JsonTypeName("readPermit")
public class CommandReadPermit extends SocketCommand {
    private Integer terminalId;
    private Integer access;
    private CommandStep step;

    public Integer getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(Integer terminalId) {
        this.terminalId = terminalId;
    }

    public Integer getAccess() {
        return access;
    }

    public void setAccess(Integer access) {
        this.access = access;
    }

    public CommandStep getStep() {
        return step;
    }

    public void setStep(CommandStep step) {
        this.step = step;
    }
}
