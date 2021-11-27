import org.arl.fjage.*
import org.arl.fjage.Message
import org.arl.unet.*
import org.arl.unet.sim.MotionModel
import static org.arl.unet.phy.Physical.*
import org.arl.unet.PDU
import java.util.*
import org.arl.unet.phy.*


class h extends UnetAgent {
  final static int PING_PROTOCOL = Protocol.USER
  static int data = 1234
  private int addr
  ArrayList<Integer> Bh=new ArrayList<Integer>()
  def datapacket = PDU.withFormat {
   length(16)                     // 16 byte PDU {protocol data unit}
   uint8('Type')                  //datapacket of 16 bytes
   uint8('CHaddr')            
   padding(0xff)                  // padded with 0xff to make 16 bytes
  }
  
  double fun(double x,double y,double z)    //function of a cone of max radius 100m and height 2000m
  {

    if(z<=0 && z>=-2000)
    {
      double p=-1*z/2000*100;
      double l =p-(Math.sqrt((x-55)*(x-55)+y*y));
      if(l>=0)
      {
      return l;
      }
    }
    return 0;
  }
  
  void startup() {
          
          def node = agentForService(Services.NODE_INFO)
          def phy = agentForService Services.PHYSICAL
          subscribe topic(phy)
          addr = node.Address
          phy.minPowerLevel = -100.dB
         if(addr<100) //for all static nodes
         {       
          add new WakerBehavior((1000*addr), {
            // add new TickerBehavior(15000, {
                 ArrayList<Integer> neighbouraddr4=new ArrayList<Integer>()
                 int x1=node.location[0]          
                  int y1=node.location[1]
                  int z1=node.location[2]
                  double g=fun(x1,y1,z1);
                  println "${g}";
                  if(g>30)                  //if plume detected send msg to addr-1 with address
                  {
                  int xx1=x1/100
                  int xx2=x1%100
                  int yy1=y1/100
                  int yy2=y1%100
                  int zz1=z1/100
                  int zz2=z1%100
                  neighbouraddr4.addAll([addr,xx1,xx2,yy1,yy2,zz1,zz2])
                  phy[1].powerLevel = -15.dB;    // must be non- positive
                  phy[2].powerLevel = -15.dB;
                  println "Sending data from ${addr} to ${addr-1}"
                  phy << new ClearReq()
                  phy << new DatagramReq(to: (addr-1) ,data : neighbouraddr4 , protocol:PING_PROTOCOL)  
                  }
       //     })
           })
         }
            
  }
  void processMessage(Message msg) {
    
     def node = agentForService(Services.NODE_INFO)
     def phy = agentForService Services.PHYSICAL
     if (msg instanceof DatagramNtf && msg.protocol == PING_PROTOCOL && addr<100)
     {  
        ArrayList<Integer> collectB=new ArrayList<Integer>()
        collectB.addAll(msg.data)
        int d =msg.data[0]
        int flag=0;
        for(int i=0;i<Bh.size();i++) //checking for redundancy
        {
          if(Bh[i]==msg.data[0])
          {
            flag=1;
            break;
          }
        }
        if(flag==0) //if new data
        {
          println "Received data from ${msg.from} by ${addr} :" +collectB;
          Bh.addAll([msg.data[0]])
          //int z2=collectB[5]*100+collectB[6]
          //int z1=node.location[2]
          //int r1=z1/100
          //int r2=z1%100
          if(addr%10 != 0)  //if not the topmost node forwarding the msg
          {
            add new WakerBehavior(2200, {
                   //ArrayList<Integer> neighbouraddr2=new ArrayList<Integer>()
                   //neighbouraddr2.addAll([collectB[0],collectB[1],r1,r2])
                   phy[1].powerLevel = -5.dB;    // must be non- positive
                   phy[2].powerLevel = -5.dB;
                   println "Sending data from ${addr} to ${addr-1}"
                   phy << new ClearReq()
                   phy << new DatagramReq(to: (addr-1) ,data : collectB , protocol:PING_PROTOCOL)  
              })
          }
          if(addr%10 == 0)
          {
            phy[1].powerLevel = -5.dB;    // must be non- positive
            phy[2].powerLevel = -5.dB;
            add new WakerBehavior(1000,{
            phy << new ClearReq()
            phy << new DatagramReq(to: 101 ,data : collectB , protocol:PING_PROTOCOL) 
            println "sent msg to 101 with data" + collectB;
            })
            add new WakerBehavior(1800,{
            phy << new ClearReq()
            phy << new DatagramReq(to: 102 ,data : collectB , protocol:PING_PROTOCOL) 
            println "sent msg to 102 with data" + collectB;
            })
          }   
        }
     }
  }
}