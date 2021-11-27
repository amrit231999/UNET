import org.arl.fjage.*
import org.arl.fjage.Message
import org.arl.unet.*
import org.arl.unet.sim.MotionModel
import static org.arl.unet.phy.Physical.*
import org.arl.unet.PDU
import java.util.*
import org.arl.unet.phy.*

class ResponseDaemon extends UnetAgent {

  @Override
  void startup() {
    // subscribe to all agents that provide the datagram service
    subscribeForService(Services.DATAGRAM)
  }

  @Override
  void processMessage(Message msg) {
    if (msg instanceof DatagramNtf && msg.protocol == Protocol.DATA) {
      // respond to protocol DATA datagram with protocol USER datagram
      def node = agentForService(Services.NODE_INFO)
      send new DatagramReq(
        recipient: msg.sender,
        to: msg.from,
        protocol: Protocol.USER,
        data: "Recieved"
      )
      if(msg.data[0]==1) {
      int xloc=msg.data[1]
      int yloc=msg.data[2]
      int zloc=msg.data[3]
      int sp=msg.data[4]
      node.speed=sp 
      int t=((xloc)/sp)*1000
      delay t
      node.speed=0.mps 
      }
    }
  }

}