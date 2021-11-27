import org.arl.fjage.*
import org.arl.fjage.Message
import org.arl.unet.*
import org.arl.unet.sim.MotionModel
import static org.arl.unet.phy.Physical.*
import org.arl.unet.PDU
import java.util.*
import org.arl.unet.phy.*
import org.arl.unet.mac.*


class Node_1 extends UnetAgent {
  final static int PING_PROTOCOL = Protocol.USER
  static int data = 1234

  def datapacket = PDU.withFormat {
   length(16)                     // 16 byte PDU {protocol data unit}
   uint8('Type')                  //datapacket of 16 bytes
   uint8('CHaddr')            
   padding(0xff)                  // padded with 0xff to make 16 bytes
  }
    int y=2
    int flag=1

  
  void startup() {
      
          def slot = 1000.ms
    def schedule = [[1, 0, 0, -1],   //Node 1   //Node 2
                    [0, -1, 1, 0]]
    int i=0
    float TickCount=1
          def node = agentForService(Services.NODE_INFO)
          def phy = agentForService Services.PHYSICAL
          subscribe topic(phy)
          phy[1].powerLevel = -30.dB;    // must be non- positive
          phy[2].powerLevel = -30.dB;
          
          //add new TickerBehavior(5000, {
            //def mac = agentForService(Services.MAC)
            //if (mac) {
              //  def req = new ReservationReq(recipient: mac, to: 2, duration: 1.seconds) 
                //def rsp = request(req)
                //if (rsp && rsp.performative == Performative.AGREE) {
                  //  def ntf = receive(ReservationStatusNtf)       
                    //if (ntf && ntf.inReplyTo == req.messageID && ntf.status == ReservationStatus.START) {
      //phy[Physical.DATA].frameLength = phy[Physical.CONTROL].frameLength
      add new TickerBehavior((long)(1000*slot), {
        // debug variable: intic = (int)(TickCount-1)
        // debug code: println """"${TickCount} ${intic}  ${i}""" 
        def slen = schedule[i].size()
        def s = schedule[i][((int)(TickCount-1))%slen]
        if (s == 1) {
                        println "Slot 1"
                        phy << new ClearReq()
                        println "Sending data to : " + y
                        phy << new DatagramReq(to:y, data : "If near send data" , protocol:PING_PROTOCOL)
                        println "n1 ${node.location}"
        }
        if(s==-1) {
            println "Slot 4"
        }
        TickCount = TickCount + 1 //Usually 1/n, n=number of nodes
      })

                    //}
                //}
            //}
          //})
              
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
          println "Data Recieved from 2"
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