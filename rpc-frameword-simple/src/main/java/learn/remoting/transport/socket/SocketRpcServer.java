package learn.remoting.transport.socket;

import learn.entity.RpcServiceProperties;
import learn.factory.SingletonFactory;
import learn.provider.ServiceProvider;
import learn.provider.ServiceProviderImpl;
import learn.utils.concurrent.threadpool.ThreadPoolFactoryUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class SocketRpcServer {
    public static final int PORT = 9998;
    private final ExecutorService threadPool;
    private final ServiceProvider serviceProvider;


    public SocketRpcServer() {
        threadPool = ThreadPoolFactoryUtils.createCustomThreadPoolIfAbsent("socket-server-rpc-pool");
        serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
    }
    //调用这两个函数把服务添加到zooKeeper
    public void registerService(Object service) {
        serviceProvider.publishService(service);
    }

    public void registerService(Object service, RpcServiceProperties rpcServiceProperties) {
        serviceProvider.publishService(service, rpcServiceProperties);
    }
    //调用此方法等待客户端连接开始工作
    public void start(){
        try(ServerSocket serverSocket = new ServerSocket()){
            String host = InetAddress.getLocalHost().getHostAddress();
            serverSocket.bind(new InetSocketAddress(host,PORT));
            //TODO

            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                threadPool.execute(new SocketRpcRequestHandlerRunnable(socket));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
