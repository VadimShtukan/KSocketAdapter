package vadim.shtukan.KafkaSocketAdapter.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import vadim.shtukan.KafkaSocketAdapter.KafkaSocketAdapterApplication;
import vadim.shtukan.KafkaSocketAdapter.service.ThreadPooledServer;

import static org.junit.jupiter.api.Assertions.*;


@ComponentScan(basePackages="vadim.shtukan.KafkaSocketAdapter")
@PropertySource("classpath:/application.properties")
class CommandListenerTest {
    private static CommandListener commandListener;

    @BeforeAll
    public static void beforeAll()  {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(CommandListenerTest.class);

        commandListener = (CommandListener) ctx.getBean("commandListener", ctx);
    }

    @Test
    void readPolymorphicSocketCommand() {
        assertTrue(commandListener.readCommand("{\"command\":\"test\", \"val1\":\"1\", \"val2\":\"2\"}"));
    }

    @Test
    void readSocketCommand() {
        assertTrue(commandListener.readCommand("{\"command\":\"test\", \"val1\":\"1\", \"val2\":\"2\"}"));
    }

    @Test
    void readInvalidSocketCommand() {
        assertFalse(commandListener.readCommand("{\"command\":\"INVALIDcommand\", \"val1\":\"1\", \"val2\":\"2\"}"));
    }

    @Test
    void readNotRealisedSocketCommand() {
        assertFalse(commandListener.readCommand("{\"command\":\"NotRealised\", \"val1\":\"1\", \"val2\":\"2\"}"));
    }

    @Test
    void addEventsCommand(){
        assertTrue(commandListener.readCommand("{\"command\":\"addEvents\", \"eventid\":\"4311254\", \"controllerid\":\"142\", \"eventdatetime\":\"29.07.2020 18:26:48\", \"eventtype\":\"13\"}"));
    }
}