import org.arl.fjage.Message
import org.arl.unet.*
import org.arl.fjage.*
import org.arl.unet.PDU
import java.util.*
import org.arl.unet.phy.*
import org.arl.unet.nodeinfo.NodeInfoParam
import org.arl.unet.remote.*
 
class AUV2_Agent extends UnetAgent {

private AgentID phy
private AgentID node

def datapacket = PDU.withFormat {
   length(16)                     // 16 byte PDU {protocol data unit}
   uint8('Type')                  //datapacket of 16 bytes
   uint8('CHaddr')               
   padding(0xff)                  // padded with 0xff to make 16 bytes
  }
def slot = 1.667.seconds
def y = 3
def storage = new int[1000]
void startup() {
    
          def node = agentForService(Services.NODE_INFO)
          def phy = agentForService Services.PHYSICAL
          subscribe topic(phy)
          phy[1].powerLevel = -7.dB;
          phy[2].powerLevel = -7.dB;// must be non- positive
          add new TickerBehavior((long)(1000*slot), {
              //println "AUV-2 ${node.location}"
          })
     }
 int conf = 1
 int j = 1
 int k = 1
 void processMessage(Message msg) {
    def node = agentForService(Services.NODE_INFO)
    def phy = agentForService Services.PHYSICAL
    if (msg instanceof DatagramNtf && msg.protocol == Protocol.USER ){
          ArrayList<Integer> collectB=new ArrayList<Integer>()
          collectB.addAll(msg.data)
          storage[j] = msg.data[0]
          println "Data recieved as from AUV-1: ${storage[j]} "
          j++
          phy << new ClearReq()
          phy << new DatagramReq(to: 1 ,data : conf, protocol:Protocol.USER)
          k=1
    }
    if (msg instanceof DatagramNtf && msg.protocol == Protocol.DATA ){
          phy << new ClearReq()
          println "Sending data to : " + "AUV-3"
          phy << new DatagramReq(to: y, data : storage[k] , protocol:Protocol.DATA)
          k++
    }
  }

}