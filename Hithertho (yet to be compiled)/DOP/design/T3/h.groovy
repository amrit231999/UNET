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
  private int addr
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
          addr = node.Address

         if(addr==101)
         {       
    
            add new WakerBehavior(50200, {
                 ArrayList<Integer> neighbouraddr4=new ArrayList<Integer>()
                 neighbouraddr4.addAll([addr,30])
                 phy[1].powerLevel = -45.dB;    // must be non- positive
                 phy[2].powerLevel = -45.dB;
                 println "Sending data from ${addr} to ${addr-1}"
                 phy << new ClearReq()
                 phy << new DatagramReq(to: 0 ,data : neighbouraddr4 , protocol:PING_PROTOCOL)  
            })
         }           
  }
  void processMessage(Message msg) {
    
    def node = agentForService(Services.NODE_INFO)
    def phy = agentForService Services.PHYSICAL
       if (msg instanceof DatagramNtf && msg.protocol == PING_PROTOCOL && addr%10 != 0)
       {  
          ArrayList<Integer> collectB=new ArrayList<Integer>()
          collectB.addAll(msg.data)
          println "Received data from ${msg.from} by ${addr} :" +collectB
          int d =msg.data[0]
          int d2=msg.data[1]
          if(d<100)
          {
          int z2=collectB[2]*100+collectB[3]
          int z1=node.location[2]
          int r1=z1/100
          int r2=z1%100

          add new WakerBehavior(2200, {
                 ArrayList<Integer> neighbouraddr2=new ArrayList<Integer>()
                 neighbouraddr2.addAll([collectB[0],collectB[1],r1,r2])
                 phy[1].powerLevel = -5.dB;    // must be non- positive
                 phy[2].powerLevel = -5.dB;
                 println "Sending data from ${addr} to ${addr-1}"
                 phy << new ClearReq()
                 phy << new DatagramReq(to: (addr-1) ,data : neighbouraddr2 , protocol:PING_PROTOCOL)  
            })
          }
          else if(d2>25)
          {
            int z1=node.location[2]
            int r1=z1/100
            int r2=z1%100
            add new WakerBehavior(2200, {
                 ArrayList<Integer> neighbouraddr2=new ArrayList<Integer>()
                 neighbouraddr2.addAll([addr,1,r1,r2])
                 phy[1].powerLevel = -5.dB;    // must be non- positive
                 phy[2].powerLevel = -5.dB;
                 println "Sending data from ${addr} to ${addr-1}"
                 phy << new ClearReq()
                 phy << new DatagramReq(to: (addr-1) ,data : neighbouraddr2 , protocol:PING_PROTOCOL)  
            })
          }
    }
      if (msg instanceof DatagramNtf && msg.protocol == PING_PROTOCOL && addr%10==0)
       {
          ArrayList<Integer> collectB=new ArrayList<Integer>()
          collectB.addAll(msg.data)
          println "Received data from ${msg.from} by ${addr} :" +collectB
          int d =msg.data[0]
          int z2=collectB[2]*100+collectB[3]
          int z1=node.location[2]
          int r1=z1/100
          int r2=z1%100
      }
  }
}