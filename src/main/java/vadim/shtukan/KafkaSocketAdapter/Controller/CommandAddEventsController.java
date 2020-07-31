package vadim.shtukan.KafkaSocketAdapter.Controller;

import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import vadim.shtukan.KafkaSocketAdapter.Model.CommandAddEvents;
import vadim.shtukan.KafkaSocketAdapter.Model.EventCode;
import vadim.shtukan.KafkaSocketAdapter.Service.WorkerSocketServerRunnable;

@Component("CommandAddEvents")
@Scope("prototype")
public class CommandAddEventsController implements CommandOrchestrator<CommandAddEvents> {
    private static final Logger logger = LogManager.getLogger(WorkerSocketServerRunnable.class);

    private static final Counter eventCommandCounter = Counter.build()
            .name("sca_event_command_total")
            .help("Количество событий с контроллера, по EventType.")
            .labelNames("controllerId", "eventType")
            .register();

    private static final Histogram carKppPassLatency = Histogram
            .build()
            .buckets(1, 30, 60, 80, 100, 120, 140, 160, 180, 200, 220, 240, 260, 280)
            .labelNames("controllerId")
            .name("sca_car_pass_kpp_latency")
            .help("Время, которое авто проверяется на КПП.")
            .register();
    private Histogram.Timer requestTimer_carKppPassLatency;

//    private static final Histogram terminalOutOfWorkLatency = Histogram
//            .build()
//            .buckets(60, 120, 240, 360, 480, 600, 720, 840, 960, 1080, 1200, 1320, 1440, 1560)
//            .labelNames("controllerId")
//            .name("sca_terminal_out_of_work_latency")
//            .help("Время, которое не работал терминал.")
//            .register();
//    private Histogram.Timer requestTimer_terminalOutOfWorkLatency;

    @Override
    public Boolean doCommand(CommandAddEvents commandAddEvents) {
        logger.debug("Run command addEvents");

        eventCommandCounter.labels(commandAddEvents.getControllerId().toString(), commandAddEvents.getEventType().toString()).inc();

        this.carKppLatencyCalc(commandAddEvents);

//        this.terminalOutOfWorkLatencyCalc(commandAddEvents);

        return true;
    }

//    /**
//     * Подсчет времени, которое не работал терминал
//     * @param commandAddEvents CommandAddEvents
//     */
//    private void terminalOutOfWorkLatencyCalc(CommandAddEvents commandAddEvents) {
//        if(commandAddEvents.getEventType() == EventCode.EV_TERM_POWER_ON.getCode()) {
//            requestTimer_terminalOutOfWorkLatency.observeDuration();
//        }
//        else {
//            requestTimer_terminalOutOfWorkLatency = terminalOutOfWorkLatency.labels(commandAddEvents.getControllerId().toString()).startTimer();
//        }
//    }

    /**
     * Подсчте времени нахождения авто на проходной
     * @param commandAddEvents CommandAddEvents
     */
    private void carKppLatencyCalc(CommandAddEvents commandAddEvents) {
        //Есть данные электронного пропуска (начала отсчета времени)
        if(commandAddEvents.getEventType() == EventCode.EV_TERM_CARD_DATA.getCode()){
            requestTimer_carKppPassLatency = carKppPassLatency.labels(commandAddEvents.getControllerId().toString()).startTimer();
            return;
        }

        //Шлагбаум закрыт (конец отсчета времени)
        if(requestTimer_carKppPassLatency != null && commandAddEvents.getEventType() == EventCode.EV_BARR_AUTOCLOSED.getCode()){
            requestTimer_carKppPassLatency.observeDuration();
            requestTimer_carKppPassLatency = null;
        }
    }
}
