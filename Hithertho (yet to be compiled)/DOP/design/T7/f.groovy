import org.arl.fjage.*
import org.arl.fjage.Message
import org.arl.unet.*
import org.arl.unet.sim.MotionModel
import static org.arl.unet.phy.Physical.*
import org.arl.unet.PDU
import java.util.*
import org.arl.unet.phy.*


class f extends UnetAgent {
  final static int PING_PROTOCOL = Protocol.USER
  static int data = 1234
  private int addr
  int sens=0,c=0,k=1,flag=0,i,angle=0,r=0;
  double x4,y4,z4;
  ArrayList<Integer> Bh=new ArrayList<Integer>()
  ArrayList<Integer> first=new ArrayList<Integer>()
  ArrayList<Integer> second=new ArrayList<Integer>()
  
  def datapacket = PDU.withFormat {
   length(16)                     // 16 byte PDU {protocol data unit}
   uint8('Type')                  //datapacket of 16 bytes
   uint8('CHaddr')            
   padding(0xff)                  // padded with 0xff to make 16 bytes
  }

  double fun(double x,double y,double z)
  {

    if((x*x+y*y+(z+1500)*(z+1500))<=(100*100))
    {
      return 100-(Math.sqrt(x*x+y*y+(z+1500)*(z+1500)));
    }
    else 
      return 0;
  }
  
  void startup() {
          
          def node = agentForService(Services.NODE_INFO)
          def phy = agentForService Services.PHYSICAL
          subscribe topic(phy)
          addr = node.Address
          phy.minPowerLevel = -100.dB   
  }

  
  void processMessage(Message msg) {
    
     def node = agentForService(Services.NODE_INFO)
     def phy = agentForService Services.PHYSICAL
     
     if (msg instanceof DatagramNtf && msg.protocol == PING_PROTOCOL && addr==101)
     {
        ArrayList<Integer> collectA=new ArrayList<Integer>()
        collectA=msg.data
        double x2=collectA[1]*100+collectA[2]
        double y2=collectA[3]*100+collectA[4] 
        double z2=collectA[5]*100+collectA[6]
        double x1=node.location[0]
        double y1=node.location[1]
        double z1=node.location[2]
        double x= 90-Math.toDegrees(Math.atan((y2-y1)/(x2-x1)))
        double z=(z1-z2)/(Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1))/node.speed)
        if(x2<x1)
        {
          x=x-180
        }
        node.diveRate=z
        println "changed diveRate to: "+ z+ " mps"     
        node.heading=x  
        println "node heading is : "+node.heading  

        add new TickerBehavior(100,{
         if(flag==0)
         {
             node.speed=3.mps
             //println "Heading = "+node.heading
             x1=node.location[0] 
             y1=node.location[1]
             z1=node.location[2]
              println "Current location of ${addr} "+[x1,y1,z1];
              double g=fun(x1,y1,z1)
                  if(g>25 && g<35)
                  {
                    if(r==0)
                    {
                      flag=1;
                      r++;
                    }
                     node.diveRate=0   
                     node.speed=0
                     double diff=100
                     double diff2=90
                     int direc=0;
                     int direc2=0;
                     for(i=0;i<360;i=i+20)
                     {
                        double x3=x1+0.28*Math.sin(Math.toRadians(i))
                        double y3=y1+0.28*Math.cos(Math.toRadians(i))
                        double g3=fun(x3,y3,node.location[2])
                       // println i+": "+Math.abs(g3-30)
                        if((Math.abs(g3-30))<diff)
                        {
                          if((Math.abs(g3-30))<diff2)
                          {
                            diff=diff2;
                            direc=direc2;
                            diff2=Math.abs(g3-30);
                            direc2=i
                          }
                          else
                          {
                          diff=Math.abs(g3-30);
                          direc=i
                          }
                        }
                     }
                     int dir=(int)(node.heading)
                     if(dir>=0 && dir <=40 && (direc>=320 || direc2>=320))
                     {
                      if(direc2>direc)
                      {
                        direc=direc2
                      }
                     }
                     else if(dir>300  && (direc<61 || direc2<61))
                     {
                        if(direc2<direc)
                          direc=direc2
                     }
                     else if(Math.abs((dir-direc2))<Math.abs((dir-direc)))
                     {
                        direc=direc2
                     }
                     double x3=x1+0.28*Math.sin(Math.toRadians(direc))
                     double y3=y1+0.28*Math.cos(Math.toRadians(direc))
                     println Math.toRadians(direc)
                     node.heading=direc
                     node.speed=3.mps
                     println "node heading is : "+node.heading
                     
                     if(g<30.0228 && g>29.992)
                     {
                        if(c==0)
                        {
                          first=node.location
                          println "Circle chalu no. "+k
                        }
                        if(c==5)
                        {
                          second=node.location
                          println "Second "+node.location
                        }
                        if(c>5 && (/*(node.location[0]<=first[0] && node.location[1]<=first[1] && node.location[0]>=second[0] && node.location[1]>=second[1]) || */(node.location[0]>=first[0] && node.location[1]<=first[1] && node.location[0]<=second[0] && node.location[1]>=second[1])))
                        {
                            flag=1
                            println "Circle complete no "+k++
                            c=0
                            k=0
                         }
                         c++
                     }
                   }
                  println "Sensor: "+g
         }
         
         if(flag==1)
         {
            double max=25
            x1=node.location[0] 
            y1=node.location[1]
            z1=node.location[2]
            
            println "Now at places "+node.location
            for(i=0;i<=350;i=i+10)
            {
               double x3=x1+0.28*Math.sin(Math.toRadians(i))
               double y3=y1+0.28*Math.cos(Math.toRadians(i))
               double g3=fun(x3,y3,node.location[2])
               
               if(g3>max)
               {
                  angle=i
                  max=g3
               }
            }
            if(node.heading-angle==180)
            {
              println "Center pahuche"
              flag=2
            }
            node.heading=angle.dps
            println "Locating : "+node.location+"  "+angle
       }

       if(flag==2)
       {
        node.diveRate=3.mps
        node.speed=0
        if(Math.abs(z1-node.location[2])>30)
        {
          println "30m neeche pahuche Naya cenetr "+node.location
          flag=0;
          node.heading=90
          node.diveRate=0
        }        
       }
     })
  }
 }
}