//! Simulation

import org.arl.fjage.RealTimePlatform
import org.arl.unet.sim.*
import org.arl.unet.sim.channels.*
import org.arl.fjage.*
import org.arl.unet.sim.MotionModel
import org.arl.unet.*
import org.arl.unet.phy.*
import org.arl.unet.sim.*
import org.arl.unet.sim.channels.*
import static org.arl.unet.Services.*
import static org.arl.unet.phy.Physical.*
import java.util.*

platform = RealTimePlatform 
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
  
  def n1 = node('0', address: 6, location: [-2.km, 0, -1.km], mobility: true, stack: { container ->
            container.add 'ping', new dynamic_mobility_node_B()
          })
  def n = node('AUV-3', address: 100, location: [0.km, 0.km, -1.km], mobility: true, stack: { container ->
            container.add 'ping', new dynamic_mobility_node_A()
          })
          
  //double x= 90-Math.toDegrees(Math.atan((nodelocations[0][1]-(-50.m))/(nodelocations[0][0]-(-50.m))))
  n.motionModel = [[ speed: 0.mps, heading: 90.deg ]]
  
  n1.motionModel = MotionModel.lawnmower(speed: 50.mps, leg: 400.m, spacing: 500.m, legs: 10)
  
}