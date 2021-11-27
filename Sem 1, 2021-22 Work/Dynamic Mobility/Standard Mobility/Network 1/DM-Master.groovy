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
  
  def n1 = node('1', address: 1, location: [-5000.m, 0.m, -1.m], heading: 0.deg, mobility: true, stack: { container ->
            container.add 'ping', new DM_Node_1()
          })
  def n2 = node('2', address: 2, location: [5000.m, 0.m, -1.m], heading: 180.deg, mobility: true, stack: { container ->
            container.add 'ping', new DM_Node_2()
          })
  def n = node('Base', address: 100, location: [0.m, 0.m, -1.m], mobility: true, stack: { container ->
            container.add 'ping', new DM_Node_Base()
          })
          
  //double x= 90-Math.toDegrees(Math.atan((nodelocations[0][1]-(-50.m))/(nodelocations[0][0]-(-50.m))))
  n.motionModel = [
      [time: 0.minutes, speed: 1000.mps, heading: -90.deg],
      [time: 0.083.minutes, speed: 1000.mps, heading: 90.deg],
      [time: 0.249.minutes, speed: 1000.mps, heading: -90.deg],
      [time: 0.332.minutes, speed: 0.mps, heading: -90.deg]
  ]
  n1.motionModel = [
  [time: 0.083.minutes, speed: 0.mps, heading: -90.deg]
  ]
  n1.motionModel += MotionModel.lawnmower(speed: 500.mps, leg: 200.m, spacing: 1250.m, legs: 10)
  
  n2.motionModel = [
  [time: 0.249.minutes, speed: 0.mps, heading: 90.deg]
  ]
  n2.motionModel += MotionModel.lawnmower(speed: 500.mps, leg: 200.m, spacing: 1250.m, legs: 10)
  
}