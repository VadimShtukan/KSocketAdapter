package vadim.shtukan.KafkaSocketAdapter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import vadim.shtukan.KafkaSocketAdapter.service.ThreadPooledServer;

@Service
@ComponentScan(basePackages="vadim.shtukan.KafkaSocketAdapter")
@PropertySource("classpath:/application.properties")
public class KafkaSocketAdapterApplication {

	public static void main(String[] args) {
		final Logger logger = LogManager.getLogger();

		ApplicationContext ctx = new AnnotationConfigApplicationContext(KafkaSocketAdapterApplication.class);

		ThreadPooledServer threadPooledServer = (ThreadPooledServer) ctx.getBean("threadPooledServer", ctx);
		threadPooledServer.run();

		try {
			while(true) {
				Thread.sleep(200 * 1000);
				//TODO BREAKE
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		logger.error("Stopping Server");
		threadPooledServer.stop();
}
}
//@SpringBootApplication
//public class KafkaSocketAdapterApplication {
//
//	public static void main(String[] args) {
//		ConfigurableApplicationContext ctx = SpringApplication.run(KafkaSocketAdapterApplication.class, args);
//		final Logger logger = LogManager.getLogger();
//
//		String socketPort = ctx.getEnvironment().getProperty("app.socket.port");
//		String maxSocketConnections = ctx.getEnvironment().getProperty("app.socket.maxSocketConnections");
//		String socketSecurityKey = ctx.getEnvironment().getProperty("app.socket.socketSecurityKey");
//
//		logger.info("Starting socket server on port:" + socketPort + " with maximum connection:"+ maxSocketConnections);
//
//		ThreadPooledServer server = new ThreadPooledServer(Integer.parseInt(socketPort), Integer.parseInt(maxSocketConnections), socketSecurityKey);
//		new Thread(server).start();
//
//		try {
//			while(true) {
//				Thread.sleep(200 * 1000);
//				//TODO BREAKE
//			}
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		logger.error("Stopping Server");
//		server.stop();
//	}
//
//}
