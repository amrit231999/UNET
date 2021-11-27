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

//def nodes = 1..5                     // list of nodes
def T = 1.hours                   // simulation horizon



  simulate T ,
  {
    node 'C', address: 31, location: [80.m, 80.m, -50.m],  stack: { container ->
      container.add 'ping', new traj1c()
    }
    node 'D', address: 32, location: [80.m, -80.m, -50.m],  stack: { container ->
      container.add 'ping', new traj1c()
    }
    node 'E', address: 33, location: [520.m, 80.m, -50.m],  stack: { container ->
      container.add 'ping', new traj1c()
    }
     node 'F', address: 34, location: [520.m, -80.m, -50.m],  stack: { container ->
      container.add 'ping', new traj1c()
    }
    def n1 = node 'B', address: 21, location: [0, 0, -25.m], mobility: true, stack: { container ->
      container.add 'ping', new traj1c() }
      n1.motionModel = [speed: 47.mps, turnRate: 9.dps]

}
