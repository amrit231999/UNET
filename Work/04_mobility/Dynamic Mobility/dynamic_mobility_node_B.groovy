import org.arl.fjage.Message
import org.arl.unet.*
import org.arl.fjage.*
import org.arl.unet.PDU
import java.util.*
import org.arl.unet.phy.*
import org.arl.unet.nodeinfo.NodeInfoParam
import org.arl.unet.remote.*
 
class Test1_Agent extends UnetAgent {

private AgentID phy
final static int cluster_protocol = Protocol.USER
private AgentID node
final static int PING_PROTOCOL = Protocol.USER


def datapacket = PDU.withFormat {
   length(16)                     // 16 byte PDU {protocol data unit}
   uint8('Type')                  //datapacket of 16 bytes
   uint8('CHaddr')               
   padding(0xff)                  // padded with 0xff to make 16 bytes
  }

void startup() {
    
          def node = agentForService(Services.NODE_INFO)
          def phy = agentForService Services.PHYSICAL
          subscribe topic(phy)
          phy[1].powerLevel = -40.dB;
          phy[2].powerLevel = -40.dB;// must be non- positive

          add new TickerBehavior(5000, {
              println "n1 ${node.location}"
          })
     }

 void processMessage(Message msg) {
    def node = agentForService(Services.NODE_INFO)
    def phy = agentForService Services.PHYSICAL
    if (msg instanceof DatagramNtf && msg.protocol == PING_PROTOCOL ){
      
          String data = new String(msg.data)
          println "Data recieved as from 100: " + data
          if(node.address==6)
          {
          phy << new ClearReq()
          phy << new DatagramReq(to: 100 ,data :[20,100], protocol:PING_PROTOCOL) 
          }
         
         
    }
  }
}