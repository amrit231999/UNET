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

def nodes = 1..2  
def T = 2.hours
def loadRange = [20, 30, 1]       // min, max, step
trackAuvLocation = {
  def nodeinfo = agentForService NODE_INFO
  trace.moved(nodeinfo.address, nodeinfo.location, null)
  add new TickerBehavior(10000, {
    trace.moved(nodeinfo.address, nodeinfo.location, null)
  })
}

def nodelocations = [:]
nodelocations[0] = [0, 0, -1.km]
nodelocations[1] = [1.km, 2.km, -1.km]
nodelocations[2] = [1.9.km, 2.3.km, -1.km]
nodelocations[3] = [1.6.km, 2.9.km, -1.km]
nodelocations[4] = [0.8.km, 1.5.km, -1.km]
nodelocations[5] = [3.2.km, 1.3.km, -1.km]

LinkedList<Integer> ll = new LinkedList<>();
def nextNodeInRoute = [6,2,5,3,4,1,6];

println "Traversel route: "
for(int i = 0; i<nextNodeInRoute.size() ; i++){
  ll.add(nextNodeInRoute[i]);
}              
simulate 2.minutes, {
  node '0', address: 6, location: nodelocations[0], shell: true, stack: { container ->
    container.add 'ping', new Test1_Agent()
  }
  node '1', address: 1, location: nodelocations[1], stack: { container ->
    container.add 'ping', new Test1_Agent()
  }  
  node '2', address: 2, location: nodelocations[2], stack: { container ->
    container.add 'ping', new Test1_Agent()
  }
  node '3', address: 3, location: nodelocations[3], stack: { container ->
    container.add 'ping', new Test1_Agent()
  }
  node '4', address: 4, location: nodelocations[4], stack: { container ->
    container.add 'ping', new Test1_Agent()
  }
  node '5', address: 5, location: nodelocations[5], stack: { container ->
    container.add 'ping', new Test1_Agent()
  }
  def n = node('AUV-3', address: 100, location: [-50.m, -50.m, -1.km], mobility: true, stack: { container ->
            container.add 'ping', new HeadingDaemon()
          })
  n.startup = trackAuvLocation
  double x= 90-Math.toDegrees(Math.atan((nodelocations[0][1]-(-50.m))/(nodelocations[0][0]-(-50.m))))
  println(x)
  int c = ll.remove()
  //int t = 0;
  n.motionModel = [[ speed: 1.mps, heading: (x).deg ]]
  /*
  while(!ll.isEmpty())
  {
      c = ll.remove()
      n.motionModel.add([time:  (t).minutes, location: nodelocations[c] ])
      t = t+1;
      println 'Next Cluster No.: '+ c + 'Cluster Location: ' + nodelocations[c];   
  }*/
}