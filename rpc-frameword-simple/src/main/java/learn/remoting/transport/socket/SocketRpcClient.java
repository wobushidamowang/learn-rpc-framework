package learn.remoting.transport.socket;

import learn.entity.RpcServiceProperties;
import learn.exception.RpcException;
import learn.registry.ServiceDiscovery;
import learn.registry.zk.ZkServiceDiscovery;
import learn.remoting.dto.RpcRequest;
import learn.remoting.transport.RpcRequestTransport;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketRpcClient implements RpcRequestTransport {

    private final ServiceDiscovery serviceDiscovery;

    public SocketRpcClient() {
        this.serviceDiscovery = new ZkServiceDiscovery();
    }
    //将rpcRequest发送出去
    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        //由RpcRequest得到RpcServiceName
        String rpcServiceName = RpcServiceProperties.builder().serviceName(rpcRequest.getInterfaceName())
                .group(rpcRequest.getGroup()).version(rpcRequest.getVersion()).build().toRpcServiceName();
        //根据服务名获得服务的地址
        InetSocketAddress inetSocketAddress =
                serviceDiscovery.lookupService(rpcServiceName);
        try(Socket socket = new Socket()) {
            socket.connect(inetSocketAddress);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            //发送数据到服务端
            objectOutputStream.writeObject(rpcRequest);
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            // Read RpcResponse from the input stream
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RpcException("服务调用失败",e);
        }
    }
}
