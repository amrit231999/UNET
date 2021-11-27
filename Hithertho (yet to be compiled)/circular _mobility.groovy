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
    def n1 = node 'AUV-1', location: [0, 0, 0], mobility: false
    n1.motionModel = [speed: 5.mps, turnRate: 1.dps]
    
    def n3 = node 'AUV-3', location: [57.3, 0, 0], mobility: false

    def n2 = node 'AUV-2', location: [0, 0, 0], mobility: true
    n2.motionModel = [speed: 5.mps, turnRate: 10.dps]
    
      n1.startup = {
        def phy = agentForService PHYSICAL
        
          
               phy << new ClearReq()
               phy << new TxFrameReq(to: n2, type: DATA)
        
      }
      
      n3.startup = {
          
          add new WakerBehavior(18000, {
        def phy = agentForService PHYSICAL
        
          
               phy << new ClearReq()
               phy << new TxFrameReq(to: n2, type: DATA)
      })
      
      }
 }
 