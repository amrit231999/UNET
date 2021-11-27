import org.arl.fjage.*
import org.arl.unet.*
import org.arl.unet.phy.*
import org.arl.unet.sim.*
import org.arl.unet.sim.channels.*
import static org.arl.unet.Services.*
import static org.arl.unet.phy.Physical.*

channel.model = ProtocolChannelModel

modem.dataRate = [2400, 2400].bps
modem.frameLength = [2400/8, 2400/8].bytes
modem.headerLength = 0
modem.preambleDuration = 0
modem.txDelay = 0

def nodes = 1..3                      // list with 4 nodes
def T = 2.hours                       // simulation duration
def minLoad = 0.1                     // mimimum load
def maxLoad = 1.0                    // maximum load
def loadStep = 0.1                    // step size for load
trace.warmup = 15.minutes             // collect statistics after a while

def nodeLocation = [:]
nodeLocation[1] = [ 0.m, 0.m, -15.m]
nodeLocation[2] = [ 0.m, 500.m, -15.m]
nodeLocation[3] = [ 500.m, 0.m, -15.m]

println '''
TX Count\tRX Count\tOffered Load\tThroughput
--------\t--------\t------------\t----------'''

for (def load = minLoad; load <= maxLoad; load += loadStep) {

  simulate T, {   
                  // simulate 2 hours of elapsed time
    nodes.each { myAddr ->
      def myNode = node "${myAddr}", address: myAddr, location: nodeLocation[myAddr]

      myNode.startup = {                      // startup script to run on each node
        if (myAddr==1){
        def phy = agentForService PHYSICAL
        def arrivalRate = load/nodes.size()   // arrival rate per node
        add new TickerBehavior((long)(1000), 
        {   // avg time between events in ms
          // drop any ongoing TX/RX and then send frame to random node, except myself
          phy << new ClearReq()
          phy << new TxFrameReq(to: 2, type: DATA)
        })
        }
        if (myAddr==2){
        def phy = agentForService PHYSICAL
        def arrivalRate = load/nodes.size()   // arrival rate per node
        add new TickerBehavior((long)(25000), 
        {   // avg time between events in ms
          // drop any ongoing TX/RX and then send frame to random node, except myself
          phy << new ClearReq()
          phy << new TxFrameReq(to: 3, type: DATA)
        })
        }
        if (myAddr==3){
        def phy = agentForService PHYSICAL
        def arrivalRate = load/nodes.size()   // arrival rate per node
        add new TickerBehavior((long)(50000), 
        {   // avg time between events in ms
          // drop any ongoing TX/RX and then send frame to random node, except myself
          phy << new ClearReq()
          phy << new TxFrameReq(to: 2, type: DATA)
        })
        }

      }
    }
  } // simulate

  // display collected statistics
  println sprintf('%6d\t\t%6d\t\t%7.3f\t\t%7.3f',
    [trace.txCount, trace.rxCount, trace.offeredLoad, trace.throughput])

}
