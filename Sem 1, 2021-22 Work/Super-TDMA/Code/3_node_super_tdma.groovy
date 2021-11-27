//! Simulation: Equilateral triangle network
import org.arl.fjage.*
import org.arl.unet.*
import org.arl.unet.phy.*
import static org.arl.unet.Services.*

///////////////////////////////////////////////////////////////////////////////
// System Parameters
def slot = 423.ms               // range/(speed of sound)
def range = 650.m               // distance between each node
def time = 15.minutes           // total simulation time
def schedule = [[2, 3, 0, 0],   
                [0, 0, 1, 3],
                [0, 1, 0, 2]]

///////////////////////////////////////////////////////////////////////////////
// Print Parameters
println """
Equilateral triangle network
----------------------------
Internode distance:     ${range} m
Slot length:            ${(1000*slot).round()} ms
Simulation time:        ${time} s"""

println '''
TX Count\tRX Count\tOffered Load\tThroughput
--------\t--------\t------------\t----------'''

///////////////////////////////////////////////////////////////////////////////
// Run Simulation 10 times
for(int j=0; j<10; j++) {
    
simulate time, {
      
  def n = []
  n << node('1', address: 1, location: [0, 0, 0])
  n << node('2', address: 2, location: [range, 0, 0])
  n << node('3', address: 3, location: [0.5*range, 0.866*range, 0])
  
  float TickCount=1
  n.eachWithIndex { n1, i ->
    n1.startup = {
      def phy = agentForService PHYSICAL
      phy[Physical.DATA].frameLength = phy[Physical.CONTROL].frameLength
      add new TickerBehavior((long)(1000*slot), {
        // debug variable: intic = (int)(TickCount-1)
        // debug code: println """"${TickCount} ${intic}  ${i}""" 
        def slen = schedule[i].size()
        def s = schedule[i][((int)(TickCount-1))%slen]
        if (s) {
            phy << new TxFrameReq(to: s, type: Physical.DATA)
        }
        TickCount = TickCount + 1/3 //Usually 1/n, n=number of nodes
      })
    }
  }
  
}

// display stats
println sprintf('%6d\t\t%6d\t\t%7.3f\t\t%7.3f',
    [trace.txCount, trace.rxCount, trace.offeredLoad, trace.throughput])
    
}

