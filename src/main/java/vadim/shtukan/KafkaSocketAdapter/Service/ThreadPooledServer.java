package vadim.shtukan.KafkaSocketAdapter.Service;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Component
@Scope("prototype")
public class ThreadPooledServer implements Runnable{

    private static final Logger logger = LogManager.getLogger();
    @Value("${app.socket.port}")
    protected int          serverPort;
    protected ServerSocket serverSocket = null;
    protected boolean      isStopped    = false;
    protected Thread       runningThread= null;
    protected ExecutorService threadPool;
    @Value("${app.socket.maxSocketConnections}")
    protected int maxSocketConnections;
    @Value("${app.socket.socketSecurityKey}")
    protected String socketSecurityKey;
    private final ApplicationContext ctx;

    public ThreadPooledServer(ApplicationContext ctx){
        this.ctx = ctx;
    }

    public void run(){
        this.threadPool = Executors.newFixedThreadPool(this.maxSocketConnections);

        synchronized(this){
            this.runningThread = Thread.currentThread();
        }

        openServerSocket();
        while(! isStopped()){
            Socket clientSocket;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if(isStopped()) {
                    logger.info("Server Stopped.");
                    break;
                }
                throw new RuntimeException(
                        "Error accepting client connection", e);
            }
            WorkerSocketServerRunnable workerSocketServerRunnable = (WorkerSocketServerRunnable) ctx.getBean("workerSocketServerRunnable", clientSocket, "Thread Pooled Server", this.socketSecurityKey);

            this.threadPool.execute(
                    workerSocketServerRunnable
//                    new WorkerSocketServerRunnable(clientSocket, "Thread Pooled Server", this.socketSecurityKey)
            );
        }
        this.threadPool.shutdown();
        logger.info("Server Stopped.");
    }


    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port", e);
        }
    }
}