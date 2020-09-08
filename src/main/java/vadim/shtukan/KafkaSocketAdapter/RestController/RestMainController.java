package vadim.shtukan.KafkaSocketAdapter.RestController;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vadim.shtukan.KafkaSocketAdapter.Model.HealthModel;
import vadim.shtukan.KafkaSocketAdapter.Model.Version;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

@RestController
public class RestMainController {

    @Value("${app.version}")
    private String appVersion;

    @GetMapping
    @RequestMapping("version")
    public Version getVersion(){
        Version version = new Version();

        version.setNumber(appVersion);

        return version;
    }

    @GetMapping
    @RequestMapping("health")
    public HealthModel getHealth(){
        HealthModel healthModel = new HealthModel();
        healthModel.setStatusOk();

        return healthModel;
    }

    @GetMapping
    @RequestMapping(value = "metrics", produces = TextFormat.CONTENT_TYPE_004)
    public void getMetrics(HttpServletResponse response) throws IOException {
        response.setHeader("Content-Type", "text/plain; charset=UTF-8");
        try (Writer writer = response.getWriter()) {
            TextFormat.write004(writer, CollectorRegistry.defaultRegistry.metricFamilySamples());
        }

    }
}
