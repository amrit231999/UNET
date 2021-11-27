import org.arl.fjage.*
import org.arl.fjage.Message
import org.arl.unet.*
import org.arl.unet.sim.MotionModel
import static org.arl.unet.phy.Physical.*
import org.arl.unet.PDU
import java.util.*
import org.arl.unet.phy.*


class HeadingDaemon extends UnetAgent {
  final static int PING_PROTOCOL = Protocol.USER
  static int data = 1234

  def datapacket = PDU.withFormat {
   length(16)                     // 16 byte PDU {protocol data unit}
   uint8('Type')                  //datapacket of 16 bytes
   uint8('CHaddr')            
   padding(0xff)                  // padded with 0xff to make 16 bytes
  }
    int y=100
    int flag=1
  
  void startup() {
          def node = agentForService(Services.NODE_INFO)
          def phy = agentForService Services.PHYSICAL
          subscribe topic(phy)
          phy[1].powerLevel = -10.dB;    // must be non- positive
          phy[2].powerLevel = -10.dB;
          
          add new TickerBehavior(5000, {
            
              
              phy << new ClearReq()
              println "Sending data to : " + y
              phy << new DatagramReq(to:y, data : "If near send data" , protocol:PING_PROTOCOL)
              println "n0 ${node.location}"
          })
              
  }
  void processMessage(Message msg) {
    
    def node = agentForService(Services.NODE_INFO)
    def phy = agentForService Services.PHYSICAL
       if (msg instanceof DatagramNtf && msg.protocol == PING_PROTOCOL )
       {
          ArrayList<Integer> collectB=new ArrayList<Integer>()
          collectB.addAll(msg.data)
          int sp=msg.data[0]
          if (flag==1) {
          node.speed=sp 
          }
          flag=0
    }
  }
}