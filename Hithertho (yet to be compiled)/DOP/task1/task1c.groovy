import org.arl.fjage.Message
import org.arl.unet.*
import org.arl.fjage.*
import org.arl.unet.PDU
import java.util.*
import org.arl.unet.phy.*
class Test_Agent extends UnetAgent {
  
  private AgentID phy
  final static int cluster_protocol = Protocol.USER
  final static int cluster_protocol1 = Protocol.DATA
  private AgentID node
  private int addr
  private int check = 0
  private int checkk = 0
  private int temp = 0
 // double neighbouraddr1=51
  ArrayList<Integer> neighbouraddr1=new ArrayList<Integer>()
  ArrayList<Integer> neighbouraddr2=new ArrayList<Integer>()
  ArrayList<Integer> neighbouraddr3=new ArrayList<Integer>()
  ArrayList<Integer> neighbouraddr4=new ArrayList<Integer>()
  ArrayList<Integer> neighbouraddr5=new ArrayList<Integer>()
  ArrayList<Integer> collectB=new ArrayList<Integer>()
  
  def datapacket = PDU.withFormat {
     length(16)                     // 16 byte PDU {protocol data unit}
     uint8('Type')                  //datapacket of 16 bytes
     uint8('CHaddr')             
     padding(0xff)                  // padded with 0xff to make 16 bytes
   }

  void startup() 
  {   
     neighbouraddr1[0]=1;
     neighbouraddr2[0]=52;  
     neighbouraddr3[0]=53;
     neighbouraddr4[0]=54;
     neighbouraddr5[0]=55;
  
     def phy = agentForService Services.PHYSICAL    //to communicate between two nodes
         subscribe topic(phy)
  
     def node = agentForService(Services.NODE_INFO)
        addr = node.Address
  
     if(addr==31)
     {       
        add new WakerBehavior(2200, {
             phy[1].powerLevel = -35.dB;    // must be non- positive
             phy[2].powerLevel = -35.dB;
             phy << new ClearReq()
             phy << new DatagramReq(to: 21 ,data : neighbouraddr1 , protocol:cluster_protocol)  
             println "data sent by 31 to 21"
        })
     }

     if(addr==32)
     {   
        add new WakerBehavior(37700, {
            phy[1].powerLevel = -35.dB;    // must be non- positive  
            phy[2].powerLevel = -35.dB;
            phy << new ClearReq()
            phy << new DatagramReq(to: 21 ,data : neighbouraddr2 , protocol:cluster_protocol)  
            println "data sent by 32 to 21"
        })
     }  
  
     if(addr==33)
     {   
          add new WakerBehavior(22000, {
              phy[1].powerLevel = -35.dB;
              phy[2].powerLevel = -35.dB;// must be non- positive  
              phy << new ClearReq()
              phy << new DatagramReq(to: 21 ,data : neighbouraddr3 , protocol:cluster_protocol)  
              println "data sent by 33 to 21"
          })   
     }
  
     if(addr==34)
     {   
          add new WakerBehavior(17700, {
              phy[1].powerLevel = -35.dB;    // must be non- positive
              phy[2].powerLevel = -35.dB;  
              phy << new ClearReq()
              phy << new DatagramReq(to: 21 ,data : neighbouraddr4 , protocol:cluster_protocol)  
              println "data sent by 34 to 21"
          })
      
     }
 
  }
  void processMessage(Message msg) {
      if(addr == 21)
      {
          println "message processed in address:"+addr 
          if (msg instanceof DatagramNtf && msg.protocol == cluster_protocol  && (  msg.from == 31 || msg.from == 32 || msg.from == 33  || msg.from == 34 || msg.from == 35) )      //notfication recieved 
          {
              println "DATA Received by ${addr} from ${msg.from}, DATA VALUE is ${msg.data}"
              ArrayList<Integer> collect=new ArrayList<Integer>()
              collect.addAll(msg.data)
              collectB.addAll(msg.data)
              println "now total value is ${collectB}"
              def node = agentForService(Services.NODE_INFO)
              println "Speed = ${node.speed}"
              println "${node.location}"
              node.speed=collect[0]
              println "Speed = ${node.speed}"
          }
          
      }
  }
}