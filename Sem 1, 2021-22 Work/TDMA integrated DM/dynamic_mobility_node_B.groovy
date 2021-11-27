import org.arl.fjage.*
import org.arl.fjage.Message
import org.arl.unet.*
import org.arl.unet.sim.MotionModel
import static org.arl.unet.phy.Physical.*
import org.arl.unet.PDU
import java.util.*
import org.arl.unet.phy.*
import org.arl.unet.mac.*
 
class Node_2 extends UnetAgent {

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
  

int msgRx = 0

void startup() {
    
        def slot = 1000.ms
    def schedule2 = [[1, 0, 0, -1],   //Node 1   //Node 2
                     [0, -1, 1, 0]]
    int i=1
    float TickCount=1
    
          def node = agentForService(Services.NODE_INFO)
          def phy = agentForService Services.PHYSICAL
          subscribe topic(phy)
          phy[1].powerLevel = -30.dB;
          phy[2].powerLevel = -30.dB;// must be non- positive
          
          //phy[Physical.DATA].frameLength = phy[Physical.CONTROL].frameLength
          add new TickerBehavior((long)(1000*slot), {
        // debug variable: intic = (int)(TickCount-1)
        // debug code: println """"${TickCount} ${intic}  ${i}""" 
        def slen = schedule2[i].size()
        def s = schedule2[i][((int)(TickCount-1))%slen]
        if (s == 1) {
            println "Slot 3"
            if(msgRx==1) {
            println "Sending Data back to 1"
            phy << new ClearReq()
            phy << new DatagramReq(to: 1 ,data :[100.m, 0.m, -1.m, 10.mps], protocol:PING_PROTOCOL) 
            msgRx=0
            }

        }
       if (s == -1) {
           println "Slot 2"
       }
        TickCount = TickCount + 1 //Usually 1/n, n=number of nodes
      })

     }

 void processMessage(Message msg) {
    def node = agentForService(Services.NODE_INFO)
    def phy = agentForService Services.PHYSICAL
    if (msg instanceof DatagramNtf && msg.protocol == PING_PROTOCOL ){
      
          String data = new String(msg.data)
//          def mac = agentForService(Services.MAC)
//if (mac) {
  //def req = new ReservationReq(recipient: mac, to: 1, duration: 1.seconds) 
  //def rsp = request(req)
  //if (rsp && rsp.performative == Performative.AGREE) {
    //def ntf = receive(ReservationStatusNtf)       
    //if (ntf && ntf.inReplyTo == req.messageID && ntf.status == ReservationStatus.START) {
          println "Data recieved as from 1: " + data
          msgRx=1
          
    //}
 // }
//}

         
         
    }
  }
}