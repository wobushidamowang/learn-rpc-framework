package learn.remoting.transport;

import learn.extension.SPI;
import learn.remoting.dto.RpcRequest;
@SPI
public interface RpcRequestTransport {

    /**
     * send rpc request to server and get result
     *
     * @param rpcRequest message body
     * @return data from server
     */
    Object sendRpcRequest(RpcRequest rpcRequest);
}
