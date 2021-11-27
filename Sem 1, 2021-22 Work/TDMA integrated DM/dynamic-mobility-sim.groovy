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
 
def T = 59.seconds
trace.warmup = 32.seconds
            
simulate T, {
    
  def n1 = node('1', address: 1, location: [20.m, 0.m, -1.m], mobility: true, web: 8082, api: 1102, stack: { container ->
           // container.add 'mac', new TDMAmac()
            container.add 'ping', new dynamic_mobility_node_A()
          })
  
  def n2 = node('2', address: 2, location: [-4880.m, 0.m, -1.m], mobility: true, web: 8081, api: 1101, stack: { container ->
           // container.add 'mac', new TDMAmac()
            container.add 'ping', new dynamic_mobility_node_B()
          })

  //double x= 90-Math.toDegrees(Math.atan((nodelocations[0][1]-(-50.m))/(nodelocations[0][0]-(-50.m))))
  n1.motionModel = [[ speed: 0.mps, heading: 90.deg ]]
  
  n2.motionModel = MotionModel.lawnmower(speed: 250.mps, leg: 200.m, spacing: 1250.m, legs: 3)

  
}

println '''
TX Count\tRX Count\tOffered Load\tThroughput\tPDR
--------\t--------\t------------\t----------\t--- '''  
  println sprintf('%6d\t\t%6d\t\t%7.3f\t\t%7.3f\t\t%7.3f',
    [trace.txCount, trace.rxCount, trace.offeredLoad, trace.throughput, trace.rxCount/trace.txCount])