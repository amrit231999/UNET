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

platform = RealTimePlatform   // use real-time mode
origin = [15.417424, 73.874299]

simulate {
    node 'A', location: [0, 0, 0], heading: 90.deg, mobility: true, web: 8081, api: 1101, stack: "$home/etc/setup"
    node 'B', location: [100.m, 100.m, 0], heading: 0.deg, mobility: true, web: 8082, api: 1102, stack: "$home/etc/setup" 
}

