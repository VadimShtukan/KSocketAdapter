package vadim.shtukan.KafkaSocketAdapter.model;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
