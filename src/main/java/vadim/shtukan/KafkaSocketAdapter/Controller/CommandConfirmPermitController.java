package vadim.shtukan.KafkaSocketAdapter.Controller;

import io.prometheus.client.Histogram;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import vadim.shtukan.KafkaSocketAdapter.Model.CommandConfirmPermit;
import vadim.shtukan.KafkaSocketAdapter.Model.CommandStep;

@Component("CommandConfirmPermit")
@Scope("prototype")
public class CommandConfirmPermitController implements CommandOrchestrator<CommandConfirmPermit> {

    private static final Histogram confirmPermitLatency = Histogram
            .build()
            .buckets(0.05, 0.1, 0.2, 0.4, 0.6, 0.8, 1, 1.4, 1.8, 2, 3, 5, 10, 20, 40, 50)
            .name("sca_confirm_permit_latency")
            .help("Время выполнения ConfirmPermit пакета в БД ЕИС")
            .labelNames("controllerId")
            .register();
    private Histogram.Timer timer_confirmPermitControllerLatency;

    @Override
    public Boolean doCommand(CommandConfirmPermit socketCommand) {

        this.confirmPermitLatencyCalc(socketCommand);

        return true;
    }

    private void confirmPermitLatencyCalc(CommandConfirmPermit socketCommand) {
        if(socketCommand.getStep() == CommandStep.START){
            timer_confirmPermitControllerLatency = confirmPermitLatency.labels(socketCommand.getTerminalId().toString()).startTimer();
        }

        if(timer_confirmPermitControllerLatency != null && socketCommand.getStep() == CommandStep.END){
            timer_confirmPermitControllerLatency.observeDuration();
            timer_confirmPermitControllerLatency = null;
        }
    }
}
