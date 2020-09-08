package vadim.shtukan.KafkaSocketAdapter.Controller;

import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import vadim.shtukan.KafkaSocketAdapter.Model.CommandReadPermit;
import vadim.shtukan.KafkaSocketAdapter.Model.CommandStep;

@Component("CommandReadPermit")
@Scope("prototype")
public class CommandReadPermitController implements CommandOrchestrator<CommandReadPermit> {
    private static final Histogram raedPermitLatency = Histogram
            .build()
            .buckets(0.05, 0.1, 0.2, 0.4, 0.6, 0.8, 1, 1.4, 1.8, 2, 3, 5, 10, 20, 40, 50)
            .name("sca_read_permit_latency")
            .help("Время выполнения ReadPermit пакета в БД ЕИС")
            .labelNames("controllerId")
            .register();
    private Histogram.Timer timer_raedPermitLatency;

    private static final Counter readPermitAccessDenied = Counter
            .build()
            .name("sca_read_permit_access_denied_total")
            .help("Количество отказов в проезде на проходной (ReadPermit)")
            .labelNames("terminalId")
            .register();

    @Override
    public Boolean doCommand(CommandReadPermit socketCommand) {
        if(socketCommand.getAccess() != null && socketCommand.getAccess() == 0){
            readPermitAccessDenied.labels(socketCommand.getTerminalId().toString()).inc();
        }

        this.raedPermitLatencyCalc(socketCommand);

        return true;
    }

    private void raedPermitLatencyCalc(CommandReadPermit socketCommand) {
        if(socketCommand.getStep() == CommandStep.START){
            timer_raedPermitLatency = raedPermitLatency.labels(socketCommand.getTerminalId().toString()).startTimer();
        }

        if(timer_raedPermitLatency != null && socketCommand.getStep() == CommandStep.END){
            timer_raedPermitLatency.observeDuration();
            timer_raedPermitLatency = null;
        }
    }
}
