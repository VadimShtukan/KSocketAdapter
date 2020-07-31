package vadim.shtukan.KafkaSocketAdapter.Model;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CommandStep {
    START("start"),
    END("end");

    private String strCommand;

    CommandStep(String strCommand) {
        this.strCommand = strCommand;
    }

    public String getStrCommand() {
        return strCommand;
    }

    public void setStrCommand(String strCommand) {
        this.strCommand = strCommand;
    }
}
