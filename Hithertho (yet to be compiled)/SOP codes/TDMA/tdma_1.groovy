// agent

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
     neighbouraddr1[0]=51;
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
        add new WakerBehavior(0100, {
              phy.minPowerLevel = -200.dB;
             phy[1].powerLevel = 0.dB;    // must be non- positive
             phy << new ClearReq()
             phy << new DatagramReq(to: 21 ,data : neighbouraddr1 , protocol:cluster_protocol)  
             println "data sent by 31 to 21"
        })
     }

     if(addr==32)
     {   
        add new WakerBehavior(249, {
          phy.minPowerLevel = -200.dB;
            phy[1].powerLevel = 0.dB;    // must be non- positive  
            phy << new ClearReq()
            phy << new DatagramReq(to: 21 ,data : neighbouraddr2 , protocol:cluster_protocol)  
            println "data sent by 32 to 21"
        })
     }  
  
     if(addr==33)
     {   
          add new WakerBehavior(419, {
              phy.minPowerLevel = -200.dB;
              phy[1].powerLevel = 0.dB;    // must be non- positive  
              //phy << new ClearReq()
              phy << new DatagramReq(to: 21 ,data : neighbouraddr3 , protocol:cluster_protocol)  
              println "data sent by 33 to 21"
          })   
     }
  
     if(addr==34)
     {   
          add new WakerBehavior(561, {
              phy.minPowerLevel = -200.dB;
              phy[1].powerLevel = 0.dB;    // must be non- positive  
              //phy << new ClearReq()
              phy << new DatagramReq(to: 21 ,data : neighbouraddr4 , protocol:cluster_protocol)  
              println "data sent by 34 to 21"
          })
      
     }
  
     if(addr==35)
     {   
            add new WakerBehavior(721, {
                      phy.minPowerLevel = -200.dB;
                phy[1].powerLevel = 0.dB;    // must be non- positive  
      
                phy << new ClearReq()
                phy << new DatagramReq(to: 21 ,data : neighbouraddr5 , protocol:cluster_protocol)  
                println "data sent by 35 to 21"
            })
     }
  
     if(addr==21)
     {  
            add new WakerBehavior(920, {
                 phy.minPowerLevel = -200.dB;
                 phy[1].powerLevel = 0.dB;    // must be non- positive
                  //println "data sent by 21 to 10"
                 println "${collectB} in startup in addr: 21"
                 phy << new ClearReq()
                 phy << new DatagramReq(to: 10 ,data : collectB , protocol:cluster_protocol1)  
                 println "data sent by 21 to 10"
            })    
     }
  }
  void processMessage(Message msg) {
                  rxx=getRxPower(msg)
}
}