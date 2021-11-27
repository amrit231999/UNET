import org.arl.fjage.*
import org.arl.unet.*

class EchoDaemon extends UnetAgent {

  @Override
  void startup() {
    // subscribe to all agents that provide the datagram service
    subscribeForService(Services.DATAGRAM)
  }

  @Override
  void processMessage(Message msg) {
    if (msg instanceof DatagramNtf && msg.protocol == Protocol.USER) {
      // respond to protocol USER datagram with protocol DATA datagram
      def node = agentForService(Services.NODE_INFO)
      send new DatagramReq(
        recipient: msg.sender,
        to: msg.from,
        protocol: Protocol.DATA,
        data: [1, 500.m, 0.m, 0.m, 20.mps]
      )
    }
  }

}