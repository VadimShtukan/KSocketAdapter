package vadim.shtukan.KafkaSocketAdapter.Model;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("test")
public class CommandTest extends SocketCommand {

    private Integer val1;
    private Integer val2;


    public Integer getVal1() {
        return val1;
    }

    public void setVal1(Integer val1) {
        this.val1 = val1;
    }

    public Integer getVal2() {
        return val2;
    }

    public void setVal2(Integer val2) {
        this.val2 = val2;
    }
}
