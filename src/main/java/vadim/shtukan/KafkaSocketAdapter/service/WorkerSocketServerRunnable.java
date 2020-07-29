package vadim.shtukan.KafkaSocketAdapter.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import vadim.shtukan.KafkaSocketAdapter.KafkaSocketAdapterApplication;
import vadim.shtukan.KafkaSocketAdapter.controller.CommandListener;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;

@Component
@Scope("prototype")
public class WorkerSocketServerRunnable implements Runnable{

    protected final Socket clientSocket;
    protected final String serverText;
    private BufferedReader inStream;
    private PrintWriter outStream;
    private InputStream input ;
    private OutputStream output;
    private static final Logger logger = LogManager.getLogger(WorkerSocketServerRunnable.class);
    private final String socketSecurityKey;
    private static final int LOGIN_ATTEMPT = 2;

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
            //report exception somewhere.
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
                logger.debug("Read line from socket: " + readInputLine + " in thread: " + Thread.currentThread().getId());

                //TODO BL hear
                //Catch Exceptions!
                commandListener.readCommand(readInputLine);

                this.outStream.println("ER");

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
