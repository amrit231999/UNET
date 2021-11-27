//! Simulation: Aloha wireless network
///////////////////////////////////////////////////////////////////////////////
///
/// To run simulation:
///   bin/unet samples/aloha/aloha
///
/// Output trace file: logs/trace.nam
/// Plot results: bin/unet samples/aloha/plot-results
///
///////////////////////////////////////////////////////////////////////////////

import org.arl.fjage.*
import org.arl.unet.*
import org.arl.unet.phy.*
import org.arl.unet.sim.*
import org.arl.unet.sim.channels.*
import static org.arl.unet.Services.*
import static org.arl.unet.phy.Physical.*

channel = [
  model:                BasicAcousticChannel,     //by default
  carrierFrequency:     30.kHz,
  bandwidth:            10000.Hz,
  spreading:            2,         //spherical or cylindrical (spreading loss factor)
  temperature:          15.C,
  salinity:             35.ppt,
  noiseLevel:           40.dB,   //PSD of ambient noise
  waterDepth:           1000.m  // 20m on net
]


modem.dataRate = [2400, 2400].bps
modem.frameLength = [2400/8, 2400/8].bytes



///////////////////////////////////////////////////////////////////////////////
// simulation settings
def myNode = 1..2
def T = 2.hours                       // simulation horizon
trace.warmup = 15.minutes             // collect statistics after a while

///////////////////////////////////////////////////////////////////////////////
// simulation details

for(i=0; i<=10;i++)
 { 
  def dist = i*10
  println sprintf("\nDistance %6d",dist)
  println '''
Power(-dB) \tTX Count\tRX Count\tLoss
--------\t--------\t--------\t------'''
  for(j=40;j<=40;j++)
  { 
    load=j;
  
    simulate T, {

    // setup each node at origin to ensure no propagation delay between nodes
 myNode.each { myAddr ->
      def myNode1 = node("$myAddr", address: myAddr, location: [((myAddr-1)*i*10).m, 0, -100])
      
      myNode1.startup = {
        def phy = agentForService PHYSICAL
        
        add new TickerBehavior(400000, {
          phy.minPowerLevel = -2000.dB
          if(myAddr == 1){
        phy[1].powerLevel = -load.dB;
        phy[2].powerLevel = -load.dB;
               phy << new ClearReq()
               phy << new TxFrameReq(to: rnditem(myNode-myAddr), type: DATA)
        }}
        );
        
      }
      if(myAddr==2)
          {
          dist= ((myAddr-1)*1000+(myAddr-1)*i*100)
          }
 }
            

  }  // simulate

  // display statistics

  
  float loss = trace.txCount ? 100*trace.dropCount/trace.txCount : 0
 println sprintf('%6d\t\t%6d\t\t%6d\t\t%5.5f\t\t',
    [load, trace.txCount, trace.rxCount, loss])

 }}