import org.arl.fjage.Message
import org.arl.unet.*
import org.arl.fjage.*
import org.arl.unet.PDU
import java.util.*
import org.arl.unet.phy.*
import org.arl.unet.nodeinfo.NodeInfoParam
import org.arl.unet.remote.*
 
class Test1_Agent extends UnetAgent {

private AgentID phy
final static int cluster_protocol = Protocol.USER
private AgentID node
final static int PING_PROTOCOL = Protocol.USER
  
ArrayList<Integer> neighbouraddr1=new ArrayList<Integer>()
  ArrayList<Integer> neighbouraddr2=new ArrayList<Integer>()
  ArrayList<Integer> neighbouraddr3=new ArrayList<Integer>()
  ArrayList<Integer> neighbouraddr4=new ArrayList<Integer>()
  ArrayList<Integer> neighbouraddr5=new ArrayList<Integer>()
  ArrayList<Integer> neighbouraddr6=new ArrayList<Integer>()
/*
     
    neighbouraddr0=[2,1900, 2300, -1000];
     neighbouraddr1=[0,0, 0, -1000];
     neighbouraddr2=[5,3200, 1300, -1000];  
     neighbouraddr3=[4,800, 1500, -1000];
     neighbouraddr4=[1,1000, 2000, -1000];
     neighbouraddr5=[3,1600, 2900, -1000];
*/
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
          phy[1].powerLevel = -40.dB;
          phy[2].powerLevel = -40.dB;// must be non- positive
     }

 void processMessage(Message msg) {
    def node = agentForService(Services.NODE_INFO)
    def phy = agentForService Services.PHYSICAL
    if (msg instanceof DatagramNtf && msg.protocol == PING_PROTOCOL ){
      
          String data = new String(msg.data)
          println "Data recieved as from 100: " + data
          if(node.address==6)
          {
          phy << new ClearReq()
          phy << new DatagramReq(to: 100 ,data :[2,19,00, 23,00, 00,00], protocol:PING_PROTOCOL) 
          println "sent msg to 100 with data: [2,19,00, 23,00, 00,00]";   
          }
          if(node.address==1)
          {
          phy << new ClearReq()
          phy << new DatagramReq(to: 100 ,data :[6,0,0,0, 0, -10,00], protocol:PING_PROTOCOL)    
          }
          if(node.address==2)
          {
          phy << new ClearReq()
          phy << new DatagramReq(to: 100 ,data :[5,32,00, 13,00, -10,00], protocol:PING_PROTOCOL)    
          println "sent msg to 100 with data: [5,32,00, 13,00, -10,00]";
          }
          if(node.address==3)
          {
          phy << new ClearReq()
          phy << new DatagramReq(to: 100 ,data :[4,8,00, 15,00, -10,00], protocol:PING_PROTOCOL)    
          }
          if(node.address==4)
          {
          phy << new ClearReq()
          phy << new DatagramReq(to: 100 ,data :[1,10,00, 20,00, -10,00], protocol:PING_PROTOCOL)    
          }
          if(node.address==5)
          {
          phy << new ClearReq()
          phy << new DatagramReq(to: 100 ,data :[3,16,00, 29,00, -10,00], protocol:PING_PROTOCOL)    
          }
         
    }
  }
}