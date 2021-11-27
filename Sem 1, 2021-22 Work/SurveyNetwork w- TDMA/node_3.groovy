import org.arl.fjage.Message
import org.arl.unet.*
import org.arl.fjage.*
import org.arl.unet.PDU
import java.util.*
import org.arl.unet.phy.*
import org.arl.unet.nodeinfo.NodeInfoParam
import org.arl.unet.remote.*
 
class AUV3_Agent extends UnetAgent {

private AgentID phy
private AgentID node

def datapacket = PDU.withFormat {
   length(16)                     // 16 byte PDU {protocol data unit}
   uint8('Type')                  //datapacket of 16 bytes
   uint8('CHaddr')               
   padding(0xff)                  // padded with 0xff to make 16 bytes
  }
def slot = 1000.ms
int msgRx = 0
def storage = new int[1000]
int j = 0
int k = 0

void startup() {
          def schedule = [[1, 0, 0, -1],   
                          [0, -1, 1, 0]]
          int i=0
          int conf = 1
          float TickCount=1
          def node = agentForService(Services.NODE_INFO)
          def phy = agentForService Services.PHYSICAL
          subscribe topic(phy)
          phy[1].powerLevel = -14.dB;
          phy[2].powerLevel = -14.dB;// must be non- positive
          add new TickerBehavior((long)(1000*slot), {
          def slen = schedule[i].size()
          def s = schedule[i][((int)(TickCount-1))%slen]
          if (s == 1) {
                if(msgRx==1) {
                    println "Sending confirmation data to : AUV-2"
                    phy << new DatagramReq(to: 2 ,data : conf, protocol:Protocol.DATA)
                    msgRx = 0
                }
          }
          TickCount = TickCount + 1
          })
     }

 void processMessage(Message msg) {
    def node = agentForService(Services.NODE_INFO)
    def phy = agentForService Services.PHYSICAL
    if (msg instanceof DatagramNtf && msg.protocol == Protocol.DATA ){
          ArrayList<Integer> collectB=new ArrayList<Integer>()
          collectB.addAll(msg.data)
          storage[j] = msg.data[0]
          println "Slot 4"
          println "Data recieved as from AUV-2: ${storage[j]} "
          //if(j>1) {
          //println "${storage[j-2]}${storage[j-1]}${storage[j]}" }
          j++
          msgRx=1
    }
  }

}