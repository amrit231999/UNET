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
    int y=6
  
  void startup() {
          def node = agentForService(Services.NODE_INFO)
          def phy = agentForService Services.PHYSICAL
          subscribe topic(phy)
          phy[1].powerLevel = -40.dB;    // must be non- positive
          phy[2].powerLevel = -40.dB;
          
          add new TickerBehavior(5000, {
              phy << new ClearReq()
              println "Sending data to : " + y
              phy << new DatagramReq(to:y, data : "Send Data(If Any)" , protocol:PING_PROTOCOL)
              println "${node.location}"
          })
              
  }
  void processMessage(Message msg) {
    
    def node = agentForService(Services.NODE_INFO)
    def phy = agentForService Services.PHYSICAL
       if (msg instanceof DatagramNtf && msg.protocol == PING_PROTOCOL )
       {
          ArrayList<Integer> collectB=new ArrayList<Integer>()
          collectB.addAll(msg.data)
          println "Received data from ${msg.from}: " +collectB
          int d =msg.data[0]
          println "Next node adrress: " + d
          int x2=collectB[1]*100+collectB[2]
          int y2=collectB[3]*100+collectB[4] 
          int z2=collectB[5]*100+collectB[6]
          int x1=node.location[0]
          int y1=node.location[1]
          int z1=node.location[2]
          println "Nodes ${d} coordinates: (" +x2 +".m , " +y2 +".m)"
            //double x= 90-Math.toDegrees(Math.atan((msg.data[2]-node.location[1])/(msg.data[1]-node.location[0])))
          double x= 90-Math.toDegrees(Math.atan((y2-y1)/(x2-x1)))
          double z=(z1-z2)/(Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1))/node.speed)
          if(x2<x1)
          {
            x=x-180
          }
          node.diveRate=z
          println "changed diveRate to: "+ z+ " mps"
          println "changed heading to: "+ x+ " deg"
          node.heading=x
          y=msg.data[0];
          
    }
  }
}