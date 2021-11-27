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
private int addr
private int x = -35
final static int PING_PROTOCOL = Protocol.USER
  
ArrayList<Integer> neighbouraddr0=new ArrayList<Integer>()
 ArrayList<Integer> neighbouraddr1=new ArrayList<Integer>()
  ArrayList<Integer> neighbouraddr2=new ArrayList<Integer>()
  ArrayList<Integer> neighbouraddr3=new ArrayList<Integer>()
  ArrayList<Integer> neighbouraddr4=new ArrayList<Integer>()
  ArrayList<Integer> neighbouraddr5=new ArrayList<Integer>()
ArrayList<Integer> data00=new ArrayList<Integer>()
/*
     neighbouraddr0[0]=[2,1900, 2300, -1000];
     neighbouraddr1[0]=[0,0, 0, -1000];
     neighbouraddr2[0]=[5,3200, 1300, -1000];  
     neighbouraddr3[0]=[4,800, 1500, -1000];
     neighbouraddr4[0]=[1,1000, 2000, -1000];
     neighbouraddr5[0]=[3,1600, 2900, -1000];
*/
/*
     neighbouraddr0[0]=2;
     neighbouraddr1[0]=0;
     neighbouraddr2[0]=5;  
     neighbouraddr3[0]=4;
     neighbouraddr4[0]=1;
     neighbouraddr5[0]=3;
*/
int arr=2
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
          phy[1].powerLevel = -2.dB;
          phy[2].powerLevel = -2.dB;// must be non- positive
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
          phy << new DatagramReq(to: 100 ,data :arr, protocol:PING_PROTOCOL) 
          println "sent msg to 100 with data" : arr;   
          }
          if(node.address==1)
          {
          phy << new ClearReq()
          phy << new DatagramReq(to: 100 ,data :neighbouraddr1, protocol:PING_PROTOCOL)    
          }
          if(node.address==2)
          {
          phy << new ClearReq()
          phy << new DatagramReq(to: 100 ,data :neighbouraddr2, protocol:PING_PROTOCOL)    
          }
          if(node.address==3)
          {
          phy << new ClearReq()
          phy << new DatagramReq(to: 100 ,data :neighbouraddr3, protocol:PING_PROTOCOL)    
          }
          if(node.address==4)
          {
          phy << new ClearReq()
          phy << new DatagramReq(to: 100 ,data :neighbouraddr4, protocol:PING_PROTOCOL)    
          }
          if(node.address==5)
          {
          phy << new ClearReq()
          phy << new DatagramReq(to: 100 ,data :neighbouraddr5, protocol:PING_PROTOCOL)    
          }
         
    }
  }
}