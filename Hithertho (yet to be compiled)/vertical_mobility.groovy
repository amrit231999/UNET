import org.arl.fjage.RealTimePlatform
import org.arl.unet.sim.MotionModel
import org.arl.fjage.*
import org.arl.unet.*
import org.arl.unet.phy.*
import org.arl.unet.sim.*
import org.arl.unet.sim.channels.*
import static org.arl.unet.Services.*
import static org.arl.unet.phy.Physical.*

platform = RealTimePlatform

simulate {
    
    
def C = node 'C', address: 31, location: [80.m, 0, -600.m]
def D = node 'D', address: 32, location: [-80.m, 0, -600.m]
def E = node 'E', address: 33, location: [50.m, 0, -1200.m]
def F = node 'F', address: 34, location: [-50.m, 0, -1200.m]
def B = node 'B', address: 21, location: [0, 0, 0], mobility: true
B.motionModel = [
[duration: 3.minutes, diveRate: 10.mps],
[duration: 3.minutes, diveRate: -10.mps],
[time: 6.minutes, turnRate: 0.dps, diveRate: 0.mps],
]
      C.startup = {
        def phy = agentForService PHYSICAL
        
          
               phy << new ClearReq()
               phy << new TxFrameReq(to: B, type: DATA)
        
      }
      
      F.startup = {
          
          add new WakerBehavior(18000, {
        def phy = agentForService PHYSICAL
        
          
               phy << new ClearReq()
               phy << new TxFrameReq(to: B, type: DATA)
      })
      
      }
 }
