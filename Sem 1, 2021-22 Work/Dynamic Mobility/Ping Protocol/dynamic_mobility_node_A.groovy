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
          int xloc=msg.data[0]
          int yloc=msg.data[1]
          int zloc=msg.data[2]
          //int sp=msg.data[20]
          int sp=msg.data[3]
          //int hd=msg.data[1]
        if (flag==1) {
        node.speed=sp }
        int t=((xloc-20)/sp)*1000
        //for (float i=0; i<t; i=i+1)  {
        delay t
          //node.heading=hd
          //node.speed=sp
        //}
        node.speed=0.mps 
        flag=0
          //else {
            //  node.speed=0.mps;
          //}
    }
  }
}