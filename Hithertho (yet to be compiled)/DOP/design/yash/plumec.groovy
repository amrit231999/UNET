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
 /* private int check = 0
  private int checkk = 0
  private int temp = 0*/
  
  ArrayList<Integer> neighbouraddr1=new ArrayList<Integer>()
  ArrayList<Integer> neighbouraddr2=new ArrayList<Integer>()
  ArrayList<Integer> neighbouraddr3=new ArrayList<Integer>()
  ArrayList<Integer> neighbouraddr4=new ArrayList<Integer>()
  ArrayList<Integer> neighbouraddr5=new ArrayList<Integer>()
  ArrayList<Integer> collectB=new ArrayList<Integer>()
  ArrayList<Integer> datalist=new ArrayList<Integer>()
  
  def datapacket = PDU.withFormat {
     length(16)                     // 16 byte PDU {protocol data unit}
     uint8('Type')                  //datapacket of 16 bytes
     uint8('CHaddr')             
     padding(0xff)                  // padded with 0xff to make 16 bytes
   }

  void startup() 
  {   
     neighbouraddr1=[20,-2,00,00,00,-5,00]
     neighbouraddr2=[22,-1,00,00,00,-5,00]  
     neighbouraddr3=[24,00,00,00,00,-5,00]
     neighbouraddr4=[28,-1,00,00,00,-5,00]
     neighbouraddr5=[23,-2,00,00,00,-5,00]
     int ab=10
     int cd=20

     
     def phy = agentForService Services.PHYSICAL    //to communicate between two nodes
         subscribe topic(phy)
  
     def node = agentForService(Services.NODE_INFO)
        addr = node.Address
  
     /*if(addr==31)
     {       
        add new WakerBehavior(1000, {
             phy[1].powerLevel = -10.dB;    // must be non- positive
             phy[2].powerLevel = -10.dB;
             phy << new ClearReq()
             phy << new DatagramReq(to: 21 ,data : neighbouraddr1 , protocol:cluster_protocol)  
             println "data sent by 31 to 21"
        })
     }

     if(addr==32)
     {   
        add new WakerBehavior(2000, {
            phy[1].powerLevel = -10.dB;    // must be non- positive  
            phy[2].powerLevel = -10.dB;
            phy << new ClearReq()
            phy << new DatagramReq(to: 21 ,data : neighbouraddr2 , protocol:cluster_protocol)  
            println "data sent by 32 to 21"
        })
     }  
  
     if(addr==33)
     {   
          add new WakerBehavior(3000, {
              phy[1].powerLevel = -10.dB;              
              phy[2].powerLevel = -10.dB;// must be non- positive  
              //phy << new ClearReq()
              phy << new DatagramReq(to: 21 ,data : neighbouraddr3 , protocol:cluster_protocol)  
              println "data sent by 33 to 21"
          })   
     }*/
  
     if(addr==34)
     {   
          add new WakerBehavior(4000, {
              phy[1].powerLevel = -10.dB;    // must be non- positive
              phy[2].powerLevel = -10.dB;  
              //phy << new ClearReq()
              phy << new DatagramReq(to: 21 ,data : neighbouraddr4 , protocol:cluster_protocol)  
              println "data sent by 34 to 21"
          })
      
     }
  
    /* if(addr==35)
     {   
            add new WakerBehavior(5000, {
                phy[1].powerLevel = -10.dB;    // must be non- positive  
                phy[2].powerLevel = -10.dB;
                //phy << new ClearReq()
                phy << new ClearReq()
                phy << new DatagramReq(to: 21 ,data :neighbouraddr5, protocol:cluster_protocol)  
                println "data sent by 35 to 21"
            })
     }*/

      /*if(addr==41)
     {       
        add new WakerBehavior(6000, {
             phy[1].powerLevel = -10.dB;    // must be non- positive
             phy[2].powerLevel = -10.dB;
             phy << new ClearReq()
             phy << new DatagramReq(to: 21 ,data : ab , protocol:cluster_protocol)  
             println "data sent by 41 to 21"
        })
     }

     if(addr==42)
     {   
        add new WakerBehavior(7000, {
            phy[1].powerLevel = -10.dB;    // must be non- positive  
            phy[2].powerLevel = -10.dB;
            phy << new ClearReq()
            phy << new DatagramReq(to: 21 ,data : cd , protocol:cluster_protocol)  
            println "data sent by 42 to 21"
        })
     }*/
  }
  
  void processMessage(Message msg) 
  {
      def node = agentForService(Services.NODE_INFO)
      def phy = agentForService Services.PHYSICAL
      
      if(node.address == 21)
      {
          println "message processed in address:"+addr 
          if (msg instanceof DatagramNtf && msg.protocol == cluster_protocol)
          {
              println "DATA Received by ${addr} from ${msg.from}, DATA VALUE is ${msg.data}"
              datalist = msg.data
              
              if(datalist[0]>25)
              {
                  println "Plume detected from address " + msg.from
                 
                  add new WakerBehavior(10000,{
                  phy << new ClearReq()
                  phy << new DatagramReq(to: 41 ,data : datalist, protocol:cluster_protocol)
                  println "sent msg to 41 with data";
                  })
                  add new WakerBehavior(15000,{
                  phy << new ClearReq()
                  phy << new DatagramReq(to: 42 ,data : datalist, protocol:cluster_protocol)
                  println "sent msg to 42 with data";
                  })
              }
          }
      }

      if(node.address == 41)
      {
          collectB=msg.data
          int x2=collectB[1]*100+collectB[2]
          int y2=collectB[3]*100+collectB[4] 
          int z2=collectB[5]*100+collectB[6]
          int x1=node.location[0]
          int y1=node.location[1]
          int z1=node.location[2]

          double x= 90-Math.toDegrees(Math.atan((y2-y1)/(x2-x1)))
          double z=(z1-z2)/(Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1))/node.speed)
          if(x2<x1)
          {
            x=x-180
          }
          node.diveRate=z
          println "changed diveRate to: "+ z+ " mps"         
          node.heading=270

          add new TickerBehavior(10000,{
          println "Current location of 41 "+node.location;
          })

      }

      if(addr == 42)
      {
          collectB=msg.data
          int x2=collectB[1]*100+collectB[2]
          int y2=collectB[3]*100+collectB[4] 
          int z2=collectB[5]*100+collectB[6]
          int x1=node.location[0]
          int y1=node.location[1]
          int z1=node.location[2]

          double x= 90-Math.toDegrees(Math.atan((y2-y1)/(x2-x1)))
          double z=(z1-z2)/(Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1))/node.speed)
          if(x2<x1)
          {
            x=x-180
          }
          node.diveRate=z
          println "changed diveRate to: "+ z+ " mps"
          node.heading=270

          add new TickerBehavior(10000,{
          println "Current location of 42 "+node.location;
          })

      }
  }
}