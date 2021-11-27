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
  
  int neighbouraddr1,neighbouraddr2,neighbouraddr3,neighbouraddr4
  
  def datapacket = PDU.withFormat {
     length(16)                     // 16 byte PDU {protocol data unit}
     uint8('Type')                  //datapacket of 16 bytes
     uint8('CHaddr')             
     padding(0xff)                  // padded with 0xff to make 16 bytes
   }

  void startup() 
  {   
     neighbouraddr1=51;
     neighbouraddr2=52;  
     neighbouraddr3=53;
     neighbouraddr4=54;
  
     def phy = agentForService Services.PHYSICAL    //to communicate between two nodes
         subscribe topic(phy)
  
     def node = agentForService(Services.NODE_INFO)
        addr = node.Address
  
     if(addr==21)
     {       
        add new WakerBehavior(2100, {
             phy[1].powerLevel = -43.dB;    // must be non- positive
             phy[2].powerLevel = -43.dB;
             phy << new ClearReq()
             phy << new DatagramReq(to: 31 ,data : neighbouraddr1 , protocol:cluster_protocol)  
             println "data sent by 21 to 31"
        })

        add new WakerBehavior(38700, {
            phy[1].powerLevel = -43.dB;    // must be non- positive  
            phy[2].powerLevel = -43.dB;
            phy << new ClearReq()
            phy << new DatagramReq(to: 32 ,data : neighbouraddr2 , protocol:cluster_protocol)  
            println "data sent by 21 to 32"
        })

        add new WakerBehavior(22000, {
              phy[1].powerLevel = -43.dB;
              phy[2].powerLevel = -43.dB;// must be non- positive  
              phy << new ClearReq()
              phy << new DatagramReq(to: 34 ,data : neighbouraddr4 , protocol:cluster_protocol)  
              println "data sent by 21 to 34"
          }) 

           add new WakerBehavior(17700, {
              phy[1].powerLevel = -43.dB;    // must be non- positive
              phy[2].powerLevel = -43.dB;  
              phy << new ClearReq()
              phy << new DatagramReq(to: 33 ,data : neighbouraddr3 , protocol:cluster_protocol)  
              println "data sent by 21 to 33"
          })
     }
 
  }
  void processMessage(Message msg) 
  {
      if(addr == 31)
      {
          println "message processed in address:"+addr 
          if (msg instanceof DatagramNtf && msg.protocol == cluster_protocol && msg.from == 21)      //notfication recieved 
          {
                println "DATA Received by ${addr} from ${msg.from}, DATA VALUE is ${msg.data}"
                def node = agentForService(Services.NODE_INFO)
                node.mobility=true
                println "${node.location}"
                node.heading=270
                double d =msg.data[0]
                node.speed = d
                println "Speed = ${node.speed}"
  
                add new WakerBehavior(46000, {
                println "${node.Address}      ${node.location}"
                }) 
                add new WakerBehavior(56000, {
                println "${node.Address}      ${node.location}"
                }) 
          }          
      }

      if(addr == 32)
      {
          println "message processed in address:"+addr 
          if (msg instanceof DatagramNtf && msg.protocol == cluster_protocol && msg.from == 21)      //notfication recieved 
          {
                println "DATA Received by ${addr} from ${msg.from}, DATA VALUE is ${msg.data}"
                def node = agentForService(Services.NODE_INFO)
                node.mobility=true
                println "${node.location}"
                node.heading=180
                double d =msg.data[0]
                node.speed = d
                println "Speed = ${node.speed}"
                
                add new WakerBehavior(9900, {
                println "${node.Address}      ${node.location}"
                }) 
                add new WakerBehavior(19900, {
                println "${node.Address}      ${node.location}"
                }) 
          }          
      }

      if(addr == 33)
      {
          println "message processed in address:"+addr 
          if (msg instanceof DatagramNtf && msg.protocol == cluster_protocol && msg.from == 21)      //notfication recieved 
          {
                println "DATA Received by ${addr} from ${msg.from}, DATA VALUE is ${msg.data}"
                def node = agentForService(Services.NODE_INFO)
                node.mobility=true
                println "${node.location}"
                node.heading=90
                double d =msg.data[0]
                node.speed = d
                println "Speed = ${node.speed}"
                
                add new WakerBehavior(31000, {
                println "${node.Address}      ${node.location}"
                }) 
                add new WakerBehavior(41000, {
                println "${node.Address}      ${node.location}"
                }) 
          }
      }

      if(addr == 34)
      {
          println "message processed in address:"+addr 
          if (msg instanceof DatagramNtf && msg.protocol == cluster_protocol && msg.from == 21)      //notfication recieved 
          {
                println "DATA Received by ${addr} from ${msg.from}, DATA VALUE is ${msg.data}"
                def node = agentForService(Services.NODE_INFO)
                node.mobility=true
                println "${node.location}"
                node.heading=0
                double d =msg.data[0]
                node.speed = d
                println "Speed = ${node.speed}"
                
                add new WakerBehavior(27000, {
                println "${node.Address}      ${node.location}"
                }) 
                add new WakerBehavior(37000, {
                println "${node.Address}      ${node.location}"
                })
          }          
      }

     /*  def node = agentForService(Services.NODE_INFO)
       add new WakerBehavior(47000, {
                println "${node.Address}      ${node.location}"
                }) 
                add new WakerBehavior(57000, {
                println "${node.Address}      ${node.location}"
                }) */
  }
}