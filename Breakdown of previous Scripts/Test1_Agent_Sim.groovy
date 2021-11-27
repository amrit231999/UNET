
import org.arl.fjage.*
import org.arl.unet.*
import org.arl.unet.phy.*
import org.arl.unet.sim.*
import org.arl.unet.sim.channels.*
import static org.arl.unet.Services.*
import static org.arl.unet.phy.Physical.*
/* We create hashmap with numbers marked to addresses we will use in our startup procedure */
def addressbook = [:]
addressbook[1] = 20
addressbook[2] = 21
addressbook[3] = 22
addressbook[4] = 30
addressbook[5] = 31
addressbook[6] = 32
addressbook[7] = 10
def locations = [:]
locations[1] = [180.m, 0.m, -500.m]
locations[2] = [200.m, 0.m, -500.m]
locations[3] = [220.m, 0.m, -500.m]
locations[4] = [180.m, 0.m, -1000.m]
locations[5] = [200.m, 0.m, -1000.m]
locations[6] = [220.m, 0.m, -1000.m]
locations[7] = [200.m, 0.m, 0.m]

def nodes = 1..7                      // list with 6 nodes
def T = 2.hours                       // simulation duration
def minLoad = 0.1                     // mimimum load
def maxLoad = 0.2                    // maximum load
def loadStep = 0.1                    // step size for load
trace.warmup = 15.minutes             // collect statistics after a while

println """ 
Simulation Script to test the Test1_Agent.groovy script \n
"""

simulate T, {
	nodes.each { i ->
		def myNode = node "${addressbook[i]}", address: addressbook[i], location: locations[i]
		println """${i},\t ${addressbook[i]}, \t ${locations[i]} """
		
		myNode.startup = {
			def phy = agentForService PHYSICAL
			if (i == 1){
				add new WakerBehavior(0000, {
				phy << new ClearReq()
        		phy << new DatagramReq(to: 21 ,data : 51)  
        		println "data sent by 20 to 21"
        		})
			}
			if (i ==2){
				add new WakerBehavior(580, {
       			phy << new ClearReq()
       			phy << new DatagramReq(to: 10 ,data : 51)  
       			println "data sent by 21 to 10"
       			})
			}
			if (i==3){
				add new WakerBehavior(90, {  
        		phy << new ClearReq()
        		phy << new DatagramReq(to: 21 ,data : 51)  
        		println "data sent by 22 to 21"
  				})
			}
			if (i==4){
				add new WakerBehavior(0000, {
        		phy << new ClearReq()
        		phy << new DatagramReq(to: 31 ,data :51)  
        		println "data sent by 30 to 31"
  			})
			}
			if (i==5){
				add new WakerBehavior(180, {
       			phy << new ClearReq()
       			phy << new DatagramReq(to: 21 ,data : 51)  
      			println "data sent by 31 to 21"
  				})
			}
			if (i==6){
				add new WakerBehavior(90, {
        		phy << new ClearReq()
        		phy << new DatagramReq(to: 31 ,data : 51)  
        		println "data sent by 32 to 31"
  				})
			}
		}
	}
	println sprintf('%6d\t\t%6d\t\t%7.3f\t\t%7.3f',
    [trace.txCount, trace.rxCount, trace.offeredLoad, trace.throughput])
}

