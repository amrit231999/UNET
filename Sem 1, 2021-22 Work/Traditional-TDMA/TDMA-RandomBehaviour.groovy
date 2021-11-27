import org.arl.fjage.*
import org.arl.unet.*
import org.arl.unet.phy.*
import org.arl.unet.sim.*
import org.arl.unet.sim.channels.*

println '''
TDMA w/ Random Arrival Simulation
=================================

TX Count\tRX Count\tOffered Load\tThroughput\tPDR
--------\t--------\t------------\t----------\t---'''

channel.model = ProtocolChannelModel        // use the protocol channel model
modem.dataRate = [2400, 2400].bps           // arbitrary data rate
modem.frameLength = [2400/8, 2400/8].bytes  // 1 second worth of data per frame
modem.headerLength = 0                      // no overhead from header
modem.preambleDuration = 0                  // no overhead from preamble
modem.txDelay = 0                           // don't simulate hardware delays

def nodes = 1..3                          // list with 4 nodes
slot = 423.ms
range = 650.m
trace.warmup = 15.minutes                   // collect statistics after a while

for (def load = 0.1; load <= 1.0; load += 0.1) {

  simulate 2.hours, {                       // simulate 2 hours of elapsed time
    def no = 1..3
    def n = []
  n << node('1', address: 1, location: [0, 0, 0])
  n << node('2', address: 2, location: [range, 0, 0])
  n << node('3', address: 3, location: [0.5*range, 0.866*range, 0])
  
  n.eachWithIndex { n1, i ->
    n1.startup = {                   // startup script to run on each node
        def phy = agentForService(Services.PHYSICAL)
        def arrivalRate = load/n.size() // arrival rate per node
        //println("$arrivalRate")
        add new PoissonBehavior((long)(1000*slot/arrivalRate), {   // avg time between events in ms
          def dst = rnditem(no-i)
          //println("$dst")
          phy << new ClearReq()
          phy << new TxFrameReq(to: dst, type: Physical.DATA)
        })
      }
    }
  } // simulate

  // tabulate collected statistics
  println sprintf('%6d\t\t%6d\t\t%7.3f\t\t%7.3f\t\t%7.3f',
    [trace.txCount, trace.rxCount, trace.offeredLoad, trace.throughput, trace.rxCount/trace.txCount])

}