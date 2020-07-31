package vadim.shtukan.KafkaSocketAdapter.Model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "command")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CommandTest.class, name = "test"),
        @JsonSubTypes.Type(value = NotRealised.class, name = "NotRealised"),
        @JsonSubTypes.Type(value = CommandAddEvents.class, name = "addEvents")
})
public class SocketCommand {
}
