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
    def slot=1000.ms
    def auv1_loc = new float[3]
    int sequ = 1
    int msgRx = 0
    def sendData = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30]
    int j=0
  void startup() {
          def schedule = [[1, 0, 0, -1],   
                          [0, -1, 1, 0]]
          int i=0
          float TickCount=1
          def node = agentForService(Services.NODE_INFO)
          def phy = agentForService Services.PHYSICAL
          subscribe topic(phy)
          phy[1].powerLevel = -14.dB;    // must be non- positive
          phy[2].powerLevel = -14.dB;
          add new TickerBehavior((long)(1000*slot), {
          def slen = schedule[i].size()
          def s = schedule[i][((int)(TickCount-1))%slen]
          if (s == 1) {
                        auv1_loc = node.location
                        println "Slot 1"
                        if (auv1_loc[0] >= 1500) {
                        println "Sending data to : " + "AUV-2 : ${sendData[j]}"
                        phy << new DatagramReq(to: y, data : [sendData[j]] , protocol:Protocol.USER)
                        j++ 
                        }
                        println "AUV-1 ${node.location}"
          }
          TickCount = TickCount + 1 //Usually 1/n, n=number of nodes
          })
              
  }
  
  void processMessage(Message msg) {
    def node = agentForService(Services.NODE_INFO)
    def phy = agentForService Services.PHYSICAL
       if (msg instanceof DatagramNtf && msg.protocol == Protocol.USER )
        {
          ArrayList<Integer> collectB=new ArrayList<Integer>()
          collectB.addAll(msg.data)
          int data=msg.data[0]
          msgRx = 1
          println "Slot 4"
          println "Recieved Data from AUV-2"
        }
  }
}