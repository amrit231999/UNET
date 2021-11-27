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

  int fun(int x,int y,int z)
  {

    if((x*x+y*y+(z+1500)*(z+1500))<=100*100)
    {
      return 100-(int)(Math.sqrt(x*x+y*y+(z+1500)*(z+1500)));
    }
    /*else if((x*x+y*y+(z+2000)*(z+2000))<=100*100)
    {
      return 100-(int)(Math.sqrt(x*x+y*y+(z+2000)*(z+2000)));
    }*/
    else 
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
                  int g=fun(x1,y1,z1);
                  println "${g}";
                  if(g>30)
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
     if (msg instanceof DatagramNtf && msg.protocol == PING_PROTOCOL && addr>100)
     {
        ArrayList<Integer> collectA=new ArrayList<Integer>()
        collectA=msg.data
        int x2=collectA[1]*100+collectA[2]
        int y2=collectA[3]*100+collectA[4] 
        int z2=collectA[5]*100+collectA[6]
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
        node.heading=x  
        println "node heading is : "+node.heading  

        add new TickerBehavior(100,{
          println "Current location of ${addr} "+node.location;
          if(node.location[0]<102 && node.location[1]<1.5 && node.location[2]<-998)
          {
              node.speed=0;
              node.diveRate=0;
              println "Reached near the 100,0,-1000";
              stop();
              /*h1 = done();
              println h1;
              node.heading=90
              
              if(h1 == true)
              {
                  println "Halo?"
                  node.speed=157.mps
                  node.diveRate=-5.mps
                  node.turnRate=18.dps
                  add new TickerBehavior(10000,{
                      println "Now at"+node.location  
                  })  
                  }*/
          }
          })
     }
  }
}