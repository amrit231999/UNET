//! Simulation

import org.arl.fjage.RealTimePlatform
import org.arl.unet.sim.*
import org.arl.unet.sim.channels.*
import org.arl.fjage.*
import org.arl.unet.*
import org.arl.unet.phy.*
import org.arl.unet.sim.*
import org.arl.unet.sim.channels.*
import static org.arl.unet.Services.*
import static org.arl.unet.phy.Physical.*
import java.util.*

//platform = RealTimePlatform 
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
 
def T = 2.hours
            
simulate T, {

  node '10', address: 10, location: [0, 0, 0.m], stack: { container ->
            container.add 'ping1', new h()
          }
  node '11', address: 11, location: [0, 0, -500.m], stack: { container ->
            container.add 'ping1', new h()
          }
  node '12', address: 12, location: [0, 0, -1000.m], stack: { container ->
            container.add 'ping1', new h()
          }
  node '13', address: 13, location: [0, 0, -1500.m], stack: { container ->
            container.add 'ping1', new h()
          }
  node '14', address: 14, location: [0, 0, -2000.m], stack: { container ->
            container.add 'ping1', new h()
          }   
  def n1 = node 'X', address: 101, location: [200.m, 0, 0], mobility: true , stack: { container ->
      container.add 'ping', new h()
  
    }
    n1.motionModel=[
      speed:3.mps]
    
  def n2 = node 'Y', address: 102, location: [300.m, 0, 0], mobility: true , stack: { container ->
      container.add 'ping', new h()
  
    }
    n2.motionModel=[
      speed:3.mps]
      
}