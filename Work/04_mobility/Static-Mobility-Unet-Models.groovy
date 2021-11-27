//! Simulation: AUV motion patterns
///////////////////////////////////////////////////////////////////////////////
///
/// To run simulation:
///   bin/unet samples/mobility/mobility
/// OR
///   click on the Run button (▶) in UnetSim
///
/// Output trace file: logs/trace.nam
/// Plot results: bin/unet samples/mobility/plot-tracks
///
///////////////////////////////////////////////////////////////////////////////

import org.arl.fjage.*
import org.arl.unet.sim.MotionModel
import static org.arl.unet.Services.*

println '''
Motion model simulation (real-time)
=======================
Use the Map button in the simulator web interface to visualize the nodes moving.
Press stop button (web interface), ^D (command line) to exit.
'''
platform = RealTimePlatform
origin = [15.339008, 73.833501]
//////////////////////////////////////////////////////////////////////////
////// Utility closure to log AUV locations every 10 seconds

trackAuvLocation = {
  def nodeinfo = agentForService NODE_INFO
  trace.moved(nodeinfo.address, nodeinfo.location, null)
  add new TickerBehavior(10000, {
    trace.moved(nodeinfo.address, nodeinfo.location, null)
  })
}

//////////////////////////////////////////////////////////////////////////
////// Linear motion

println 'Simulation AUV-1: Linear motion'
simulate 20.seconds, {
  def n = node('AUV-1', location: [0, 0, 0], mobility: true)
  n.startup = trackAuvLocation
  n.motionModel = [speed: 20.mps, heading: 0.deg]
}

//////////////////////////////////////////////////////////////////////////
////// Circular motion

println 'Simulation AUV-2: Circular motion'
simulate 20.seconds, {
  def n = node('AUV-2', location: [0, 0, 0], mobility: true)
  n.startup = trackAuvLocation
  n.motionModel = [speed: 30.mps, turnRate: 10.dps]
}



//////////////////////////////////////////////////////////////////////////
////// Lawnmower survey (with diving)

println 'Simulation AUV-4: Lawnmower survey (with dive)'
simulate 60.seconds, {
  def n5 = node 'AUV-5', location: [-2.m, -2.m, 0], heading: 0.deg, mobility: true

// dive to 30m before starting survey
n5.motionModel = [
  [duration: 2.seconds, speed: 50.mps, diveRate: 1.mps],
  [diveRate: 0.mps]
]

// then do a lawnmower survey
n5.motionModel += MotionModel.lawnmower(speed: 50.mps, leg: 400.m, spacing: 40.m, legs: 10)

// finally, come back to the surface and stop
n5.motionModel += [
  [duration: 2.seconds, speed: 50.mps, diveRate: -0.5.mps],
  [diveRate: 0.mps, speed: 0.mps]
]
}

//////////////////////////////////////////////////////////////////////////
////// Done!
