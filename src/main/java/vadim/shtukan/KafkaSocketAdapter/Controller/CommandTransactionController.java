package vadim.shtukan.KafkaSocketAdapter.Controller;

import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import vadim.shtukan.KafkaSocketAdapter.Model.CommandStep;
import vadim.shtukan.KafkaSocketAdapter.Model.CommandTransaction;

@Component("CommandTransaction")
@Scope("prototype")
public class CommandTransactionController implements CommandOrchestrator<CommandTransaction> {

    private static final Histogram transactionLatency = Histogram
            .build()
            .buckets(0.1, 0.5, 1, 2, 5, 10, 20, 40, 50)
            .name("sca_transaction_latency")
            .help("Время выполнения Transaction пакета в БД ЕИС")
            .labelNames("controllerId")
            .register();
    private Histogram.Timer timer_transactionLatency;


    @Override
    public Boolean doCommand(CommandTransaction socketCommand) {
        this.transactionLatencyCalc(socketCommand);
        return true;
    }

    private void transactionLatencyCalc(CommandTransaction socketCommand) {
        if(socketCommand.getStep() == CommandStep.START){
            timer_transactionLatency = transactionLatency.labels(socketCommand.getTerminalId().toString()).startTimer();
        }

        if(timer_transactionLatency != null && socketCommand.getStep() == CommandStep.END){
            timer_transactionLatency.observeDuration();
            timer_transactionLatency = null;
        }
    }
}
