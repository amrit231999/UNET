//! Simulation TDMA

import org.arl.fjage.RealTimePlatform
import org.arl.unet.sim.*
import org.arl.unet.sim.channels.*
import org.arl.unet.phy.*
import org.arl.unet.*
import org.arl.fjage.*
import java.util.*
import static org.arl.unet.phy.Physical.*
import org.arl.fjage.*
import org.arl.unet.*
import org.arl.unet.phy.*
import org.arl.unet.sim.*
import org.arl.unet.sim.channels.*
import static org.arl.unet.Services.*
import static org.arl.unet.phy.Physical.*

  final int cluster_protocol = Protocol.USER
   int addr

   ArrayList<Integer> neighbouraddr1=new ArrayList<Integer>()
  ArrayList<Integer> neighbouraddr2=new ArrayList<Integer>()
  ArrayList<Integer> neighbouraddr3=new ArrayList<Integer>()
  ArrayList<Integer> neighbouraddr4=new ArrayList<Integer>()
  ArrayList<Integer> neighbouraddr5=new ArrayList<Integer>()
  ArrayList<Integer> collectB=new ArrayList<Integer>()

  
     neighbouraddr1[0]=51;
     neighbouraddr2[0]=52;  
     neighbouraddr3[0]=53;
     neighbouraddr4[0]=54;
     neighbouraddr5[0]=55;
///////////////////////////////////////////////////////////////////////////////
// simulation settings

//platform = RealTimePlatform           // use real-time mode

///////////////////////////////////////////////////////////////////////////////
// channel and modem settings

channel = [
  model:                BasicAcousticChannel,     //by default
  carrierFrequency:     25.kHz,
  bandwidth:            4096.Hz,
  spreading:            2,         //spherical or cylindrical (spreading loss factor)
  temperature:          25.C,
  salinity:             35.ppt,
  noiseLevel:           60.dB,   //PSD of ambient noise
  waterDepth:           1120.m  // 20m on net
]
//channel.model = ProtocolChannelModel


modem.dataRate = [2400, 2400].bps
modem.frameLength = [8, 8].bytes
modem.txDelay = 0


///////////////////////////////////////////////////////////////////////////////
// simulation settings


def myNode = [31,32,33,34,21]                   // list of nodes
def T = 1.hours                   // simulation horizon
//trace.warmup = 15.minutes               // collect statistics after a while

println '''

Time Division Multiplexing
=====================

TX Count\tRX Count\tLoss % 
--------\t--------\t------'''
int rx1 = 0
float loss1 = 0
int tx1 = 0
int sim = 1

for(int i=0; i<sim; i++){

simulate T, {
   myNode.each { myAddr ->
   if(myAddr==21)
   {
    def n1 = node('B', address: 21, location: [0, 0, 0], mobility: true)
      n1.motionModel = [
          [duration: 3.minutes,  diveRate: 10.mps],
          [duration: 3.minutes,  diveRate: -10.mps],
          [time: 6.minutes, turnRate: 0.dps, diveRate: 0.mps],
        ]
        n1.startup={ 
  def phy = agentForService PHYSICAL //to communicate between two nodes
         subscribe topic(phy)
  
     def node = agentForService(NODE_INFO)
       addr = node.Address
   }
   
    //    n1.processMessage(Message msg)
          /*={
      if(addr == 21)
      {
          println "message processed in address:"+addr 
          if (msg instanceof DatagramNtf && msg.protocol == cluster_protocol  && (  msg.from == 31 || msg.from == 32 || msg.from == 33  || msg.from == 34 || msg.from == 35) )      //notfication recieved
          {
              println "DATA Received by ${addr} from ${msg.from}, DATA VALUE is ${msg.data}"
              collectB.addAll(msg.data)
              println "now total value is ${collectB}"
          }
      }
  }*/
   } 
   if(myAddr==31)
   {
      def n1 =node("C", address: 31, location: [80.m, 0, -600.m] )
      n1.startup={ 
  def phy = agentForService PHYSICAL //to communicate between two nodes
         subscribe topic(phy)
  
     def node = agentForService(NODE_INFO)
       addr = node.Address
  
     if(addr==31)
     {       
        add new WakerBehavior(59000, {
             phy[1].powerLevel = -35.dB;    // must be non- positive
             phy[2].powerLevel = -35.dB;
             phy << new ClearReq()
             phy << new DatagramReq(to: 21 ,data : neighbouraddr1 , protocol:cluster_protocol)  
             println "data sent by 31 to 21"
        })
     }
   }
   }
   if(myAddr==32)
   {
      def n1 =node 'D', address: 32, location: [-80.m, 0, -600.m]
      n1.startup={ 
  def phy = agentForService PHYSICAL //to communicate between two nodes
         subscribe topic(phy)
  
     def node = agentForService(NODE_INFO)
       addr = node.Address
  

     if(addr==32)
     {   
        add new WakerBehavior(61000, {
            phy[1].powerLevel = -35.dB;    // must be non- positive  
            phy[2].powerLevel = -35.dB;
            phy << new ClearReq()
            phy << new DatagramReq(to: 21 ,data : neighbouraddr2 , protocol:cluster_protocol)  
            println "data sent by 32 to 21"
        })
     }  
   }
   }
   if(myAddr==33)
   {
     def n1 = node 'E', address: 33, location: [50.m, 0, -1200.m]
     n1.startup={ 
  def phy = agentForService PHYSICAL //to communicate between two nodes
         subscribe topic(phy)
  
     def node = agentForService(NODE_INFO)
       addr = node.Address
    
  
     if(addr==33)
     {   
          add new WakerBehavior(119000, {
              phy[1].powerLevel = -35.dB;
              phy[2].powerLevel = -35.dB;// must be non- positive  
              //phy << new ClearReq()
              phy << new DatagramReq(to: 21 ,data : neighbouraddr3 , protocol:cluster_protocol)  
              println "data sent by 33 to 21"
          })   
     }
     }
   }
   if(myAddr==34)
   {
      def n1 =node 'F', address: 34, location: [-50.m, 0, -1200.m]
         n1.startup={ 
      def phy = agentForService PHYSICAL //to communicate between two nodes
         subscribe topic(phy)
  
     def node = agentForService(NODE_INFO)
       addr = node.Address
  
     if(addr==34)
     {   
          add new WakerBehavior(121000, {
              phy[1].powerLevel = -35.dB;    // must be non- positive
              phy[2].powerLevel = -35.dB;  
              //phy << new ClearReq()
              phy << new DatagramReq(to: 21 ,data : neighbouraddr4 , protocol:cluster_protocol)  
              println "data sent by 34 to 21"
          })
      
     }   
   }
   
    

   }
      
 }
}
  
  

  float loss = trace.txCount ? 100*trace.dropCount/trace.txCount : 0
  println sprintf('%6d\t\t%6d\t\t%5.1f',
    [trace.txCount, trace.rxCount, loss])
  tx1 = tx1 + trace.txCount
  rx1 = rx1 + trace.rxCount
  loss1 = loss1 +loss
}

println '''


Average Values
=====================

TX Count\tRX Count\tLoss % 
--------\t--------\t------'''

tx1 = tx1/sim
rx1 = rx1/sim
loss1 = loss1/sim

println sprintf('%6d\t\t%6d\t\t%5.1f',
    [tx1, rx1, loss1])