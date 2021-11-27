//! Simulation TDMA

import org.arl.fjage.RealTimePlatform
import org.arl.unet.sim.*
import org.arl.unet.sim.channels.*
import org.arl.unet.phy.*


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

def nodes = 1..7                     // list of nodes
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
  
  simulate T ,{
  
  
    node 'C', address: 31, location: [80.m, 0, -600.m],  stack: { container ->
      container.add 'ping', new mob1c()
  
    }
    
    node 'D', address: 32, location: [-80.m, 0, -600.m],  stack: { container ->
      container.add 'ping', new mob1c()
  
    }
    
    node 'E', address: 33, location: [50.m, 0, -1200.m],  stack: { container ->
      container.add 'ping', new mob1c()
      
    }
    
   
     node 'F', address: 34, location: [-50.m, 0, -1200.m],  stack: { container ->
      container.add 'ping', new mob1c()
      
    }
  
  
    def n1 = node 'B', address: 21, location: [0, 0, 0], mobility: true, stack: { container ->
      container.add 'ping', new mob1c() }
      n1.motionModel = [
          [duration: 3.minutes,  diveRate: 10.mps],
        //  [duration: 3.minutes,  diveRate: -10.mps],
          [time: 6.minutes, turnRate: 0.dps, diveRate: 0.mps],
        ]
   
  
    
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