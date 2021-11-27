import org.arl.fjage.*
import org.arl.fjage.Message
import org.arl.unet.*
import org.arl.unet.sim.MotionModel
import static org.arl.unet.phy.Physical.*
import org.arl.unet.PDU
import java.util.*
import org.arl.unet.phy.*


class AUV1_Agent extends UnetAgent {
  static int data = 1234

  def datapacket = PDU.withFormat {
   length(16)                     // 16 byte PDU {protocol data unit}
   uint8('Type')                  //datapacket of 16 bytes
   uint8('CHaddr')            
   padding(0xff)                  // padded with 0xff to make 16 bytes
  }
    def y=2
    def slot=1.667.seconds
    int sequ = 1
  void startup() {
          def node = agentForService(Services.NODE_INFO)
          def phy = agentForService Services.PHYSICAL
          subscribe topic(phy)
          phy[1].powerLevel = -7.dB;    // must be non- positive
          phy[2].powerLevel = -7.dB;
          add new TickerBehavior((long)(1000*slot), {
              phy << new ClearReq()
              println "Sending data to : " + "AUV-2"
              phy << new DatagramReq(to: y, data : sequ , protocol:Protocol.USER)
              sequ++
              println "AUV-1 ${node.location}"
          })
              
  }
  
  void processMessage(Message msg) {
    
    def node = agentForService(Services.NODE_INFO)
    def phy = agentForService Services.PHYSICAL
          add new TickerBehavior((long)(1000*slot), {
       if (msg instanceof DatagramNtf && msg.protocol == Protocol.USER )
        {
          ArrayList<Integer> collectB=new ArrayList<Integer>()
          collectB.addAll(msg.data)
          int data=msg.data[0]
          //println "Confirm ${msg.data}"
        }
          })
  }
}