package vadim.shtukan.KafkaSocketAdapter.Service;

import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import vadim.shtukan.KafkaSocketAdapter.Controller.CommandListener;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;

@Component
@Scope("prototype")
public class WorkerSocketServerRunnable implements Runnable{

    protected Socket clientSocket;
    protected String serverText;
    private BufferedReader inStream;
    private PrintWriter outStream;
    private InputStream input ;
    private OutputStream output;
    private static final Logger logger = LogManager.getLogger(WorkerSocketServerRunnable.class);
    private String socketSecurityKey;
    private static final int LOGIN_ATTEMPT = 2;

    private static final Counter clientCommandErrorWorkerCounter = Counter.build()
            .name("kadapter_worker_client_command_error_total")
            .help("Количество ошибочных запросов от клиентов.")
            .register();

    private static final Histogram workerLatency = Histogram.build()
            .buckets(0.005, 0.01, 0.05, 0.1, 0.5, 1)
            .name("kadapter_worker_latency")
            .help("Время, которое обрабатывался запрос от клиента(SCA).")
            .register();
    private Histogram.Timer requestTimer_workerLatency;

    @Autowired
    private CommandListener commandListener;
    /**
     *
     * @param clientSocket
     * @param serverText
     */
    public WorkerSocketServerRunnable(Socket clientSocket, String serverText, String socketSecurityKey) {
        this.clientSocket = clientSocket;
        this.serverText   = serverText;
        this.socketSecurityKey= socketSecurityKey;
    }


    public WorkerSocketServerRunnable() {
    }

    /**
     *
     */
    public void run() {
        try {
            logger.info("Client connected in thread: " + Thread.currentThread().getId());

            this.input = this.clientSocket.getInputStream();
            this.output = this.clientSocket.getOutputStream();
            this.inStream = new BufferedReader(new InputStreamReader(input));
            this.outStream = new PrintWriter(output, true);

            this.authoriseClient(LOGIN_ATTEMPT);
            logger.info("Client has been login in thread: " + Thread.currentThread().getId());

            this.doReadCommandFromClient();

        } catch (LoginException | SocketException e){
            this.disconnect();
            logger.info("Client disconnected in thread: " + Thread.currentThread().getId());
        } catch (IOException e) {
            this.disconnect();
            e.printStackTrace();
        }
    }

    /**
     *
     * @throws IOException
     */
    private void doReadCommandFromClient() throws IOException {
        while(true) {
            try {
                String readInputLine = this.inStream.readLine();

                if(readInputLine == null){
                    throw new SocketException("Client return null");
                }
                requestTimer_workerLatency = workerLatency.startTimer();
                logger.debug("Read line from socket: " + readInputLine + " in thread: " + Thread.currentThread().getId());

                try {
                    if(commandListener.readCommand(readInputLine)) {
                        this.outStream.println("OK");
                    }
                    else{
                        clientCommandErrorWorkerCounter.inc();
                        this.outStream.println("ER");
                    }
                }catch (Exception e){
                    clientCommandErrorWorkerCounter.inc();

                    logger.error("CommandListener.readCommand exception: " + e.getMessage());
                    this.outStream.println("ER");
                }
                requestTimer_workerLatency.observeDuration();

            }catch (SocketException e){
                //breaker
                throw new SocketException(e.getMessage());
            }
        }
    }

    /**
     *
     * @throws IOException
     */
    private void disconnect() {
        try {
            this.output.close();
            this.input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param loginAttempts
     * @throws IOException
     * @throws LoginException
     */
    private void authoriseClient(int loginAttempts) throws IOException, LoginException {
        for(int i=0; i<=loginAttempts-1; i++){
            String readInputLine = this.inStream.readLine();
            if(readInputLine == null){
                throw new SocketException("Client return null");
            }
            try {
                this.checkLogin(readInputLine);
                this.outStream.println("OK");
                //Login is OK
                return;
            } catch (LoginException e) {
                logger.error("Incorrect login in thread: " + Thread.currentThread().getId());
                this.outStream.println("??");
            }
        }
        //login failed
        throw new LoginException();
    }

    /**
     *
     * @param key
     * @throws LoginException
     */
    private void checkLogin(String key) throws LoginException {
        if(!key.equals(this.socketSecurityKey)){
            throw new LoginException("Key is not correct!");
        }
    }
}
