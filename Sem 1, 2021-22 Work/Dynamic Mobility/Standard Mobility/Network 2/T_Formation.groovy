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
    
  def n0 = node('Base', address: 100, location: [0.m, 0.m, 0.m], mobility: true)
  def n1 = node('1', address: 1, location: [0.m, 0.m, -10.m], heading: 0.deg, mobility: true)
  def n2 = node('2', address: 2, location: [0.m, 0.m, -20.m], heading: 180.deg, mobility: true)

  n0.motionModel = [
  [duration: 0.083.minutes, speed: 0.mps, diveRate: 4.mps],
  [time: 0.083.minutes, speed: 5.mps, heading: -90.deg, diveRate: 0.mps],
  [time: 0.166.minutes, speed:0.mps, heading: -90.deg]
  ]
  n0.motionModel += MotionModel.lawnmower(speed: 10.mps, leg: 200.m, spacing: 10.m, legs: 10)
  
  n1.motionModel = [
  [duration: 0.0415.minutes, speed: 0.mps, diveRate: 4.mps],
  [time: 0.0415.minutes, speed: 0.mps, diveRate: 4.mps],
  [time: 0.083.minutes, speed: 5.mps, heading: 90.deg, diveRate: 0.mps],
  [time: 0.166.minutes, speed: 0.mps, heading: 90.deg]
  ]
  n1.motionModel += MotionModel.lawnmower(speed: 10.mps, leg: 200.m, spacing: 10.m, legs: 10)
  
  n2.motionModel = [
  [time: 0.minutes, speed: 0.mps, heading: 180.deg],
  [time: 0.083.minutes, speed: 0.mps, diveRate: 1.mps],
  [time: 0.249.minutes, speed: 0.mps, diveRate: 0.mps]
  ]
  
}