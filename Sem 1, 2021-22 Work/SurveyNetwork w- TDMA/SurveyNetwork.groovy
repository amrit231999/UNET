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

//Speed of Sound = 1500 m/s
//Distance when communication starts = 2500 m
//Propagation Delay = 2500/1500 = 1.667s = 1666.667 ms

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
origin = [15.442184, 73.565236]
def T = 5.minutes
            
simulate T, {

def n1 = node '1', location: [0.m, 0.m, -100.m], heading: 90.deg, mobility: true, stack: { container ->
            container.add 'ping', new node_1() }
n1.motionModel = MotionModel.lawnmower(speed: 200.mps, leg: 1500.m, spacing: 300.m, legs: 5) // scaled by 20 - 20, 300, 50, 5 - 1000 sinusoid og

def n2 = node '2', location: [4000.m, 0.m, -100.m], heading: -90.deg, mobility: true, stack: { container ->
            container.add 'ping', new node_2() }
n2.motionModel = MotionModel.lawnmower(speed: 200.mps, leg: 1500.m, spacing: -300.m, legs: 5) // distance in between ~300m

def n3 = node '3', location: [4800.m, 0.m, -100.m], heading: 90.deg, mobility: true,  stack: { container ->
            container.add 'ping', new node_3() }
n3.motionModel = MotionModel.lawnmower(speed: 200.mps, leg: 1500.m, spacing: 300.m, legs: 5) // distance in between ~300m

def n4 = node 'Base', location: [ 23300.m, -3500.m, -100.m], web: 8081, api: 1101, stack: "$home/etc/setup"

}

println '''
TX Count\tRX Count\tOffered Load\tThroughput\tPDR
--------\t--------\t------------\t----------\t--- '''  
  println sprintf('%6d\t\t%6d\t\t%7.3f\t\t%7.3f\t\t%7.3f',
    [trace.txCount, trace.rxCount, trace.offeredLoad, trace.throughput, trace.rxCount/trace.txCount])