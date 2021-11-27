import org.arl.fjage.*
import org.arl.fjage.Message
import org.arl.unet.*
import org.arl.unet.sim.MotionModel
import static org.arl.unet.phy.Physical.*
import org.arl.unet.PDU
import java.util.*
import org.arl.unet.phy.*

class f2 extends UnetAgent {
  final static int PING_PROTOCOL = Protocol.USER
  static int data = 1234
  private int addr
  int sens=0,c=0,k=1,flag=0,i,angle=0,r=0,yo=0,ms=0;
  int counter=73;
  double x4,y4,z4;
  ArrayList<Integer> Bh=new ArrayList<Integer>()
  ArrayList<Integer> first=new ArrayList<Integer>()
  ArrayList<Integer> center=new ArrayList<Integer>()
  
  def datapacket = PDU.withFormat {
   length(16)                     // 16 byte PDU {protocol data unit}
   uint8('Type')                  //datapacket of 16 bytes
   uint8('CHaddr')            
   padding(0xff)                  // padded with 0xff to make 16 bytes
  }

  double fun(double x,double y,double z)
  {
    if(z<=0 && z>=-2000)
    {
      double p=-1*z/2000*100+5;
      double l =p-(Math.sqrt((x-55)*(x-55)+y*y));
      if(l>=0)
      {
      return l;
      }
    }
    return 0;
  }
  
  void startup() {
          
          def node = agentForService(Services.NODE_INFO)
          def phy = agentForService Services.PHYSICAL
          subscribe topic(phy)
          addr = node.Address
          phy.minPowerLevel = -100.dB 
          add new TickerBehavior(100,{
            if(flag==0 && r==1&& yo==0)
            {
              phy[1].powerLevel = -15.dB;    // must be non- positive
              phy[2].powerLevel = -15.dB;
              println "Sending HELLO from "+ addr+" to 101"
              phy << new ClearReq()
              phy << new DatagramReq(to:101 ,data : "HELLO" , protocol:PING_PROTOCOL) 
               yo=1
            }
            counter++;
          })
  }

  
  void processMessage(Message msg) {
    
     def node = agentForService(Services.NODE_INFO)
     def phy = agentForService Services.PHYSICAL
     
     if (msg instanceof DatagramNtf && msg.protocol == PING_PROTOCOL && addr==102 && msg.from!=101)
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
        //println "changed diveRate to: "+ z+ " mps"     
        node.heading=x  
        //println "node heading is : "+node.heading  
        
        add new TickerBehavior(100,{
        //  println " "+node.location
         if(flag==0)                                                //circle in a plane
         {
             node.speed=3.mps
             //println "Heading = "+node.heading
             x1=node.location[0] 
             y1=node.location[1]
             z1=node.location[2]
             double g=fun(x1,y1,z1)
                  if(g>25 && g<35)
                  {
                    //println "MOVING IN A CIRCLE: Current location of ${addr} "+[x1,y1,z1];
                     if(r==0)
                     {
                        flag=1
                        println " "+node.location
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
                     //println Math.toRadians(direc)
                     node.heading=direc
                     node.speed=3.mps
                     //println "node heading is : "+node.heading
                     
                     //println(counter+" "  + ms)
                     if(counter%200==0 && ms==1)
                     {
                        ArrayList<Integer> neighbouraddr11=new ArrayList<Integer>()
                        int x11=node.location[0]          
                        int y11=node.location[1]
                        int z11=node.location[2]                    
                        int xx1=x11/100
                        int xx2=x11%100
                        int yy1=y11/100
                        int yy2=y11%100
                        int zz1=z11/100
                        int zz2=z11%100
                        neighbouraddr11.addAll([addr,xx1,xx2,yy1,yy2,zz1,zz2])
                        phy[1].powerLevel = -30.dB;    // must be non- positive
                        phy[2].powerLevel = -30.dB;
                        println "Sending location from "+ addr+"                                   :"+node.location
                        phy << new ClearReq()
                        phy << new DatagramReq(to:0 ,data : neighbouraddr11 , protocol:PING_PROTOCOL)  
                        
                        
                     }
                     if(g<30.1 && g>29.9)
                     {
                        if(c==0)
                        {
                          first=node.location
                          println "CIRCLE STARTS NO: "+(k-1)
                        }
                        if(x1>center[0] && ((y4<0 && y1>0) || ( y4>0 && y1<0)) && c>3)
                        {
                            //flag=1
                            println "Circle complete no "+ r +"  "+node.Address + " "+node.location
                            r++
                            c=0
                         }
                         c++
                     }
                   }
                   else
                   {
                    //println "GOING TOWARDS PLUME: Current location of ${addr} "+[x1,y1,z1];
                   }
                  //println "Sensor: "+g
                  y4=node.location[1]
         }
         
         if(flag==1)                                                          //towards center
         {
          
            node.diveRate=0
            double max=25
            node.speed=3.mps
            x1=node.location[0] 
            y1=node.location[1]
            z1=node.location[2]
            
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
              //println "REACHED CENTER @ "+node.location
              center=node.location;
              flag=2

              if(r==0)
              {
                flag=4
                r++
              }
            }
            node.heading=angle
       }

       if(flag==2)                                                            //30m up
       {
        node.diveRate=-3.mps
        node.speed=0
        if(Math.abs(z1-node.location[2])>30)
        {
          //println "REACHED 30m ABOVE "+node.location
          flag=3
          node.heading=90
          node.diveRate=0
          
          if(fun(node.location[0],node.location[1],node.location[2])<30)
          {
              stop();
              //println "SHOULD GO BACK TO THE BASESTATION"
          }
        }        
       }

       if(flag==3)                                                            //towards circumference
       {
          node.heading=90
          node.speed=1.mps
          x1=node.location[0] 
          y1=node.location[1]
          z1=node.location[2]
          double g=fun(x1,y1,z1)
          //println "TOWARDS CIRCUMFERENCE "+node.location+"  "+g

          if(g<30.1 && g>29.9)
          {
              flag=0     
              //println "REACHED CIRCUMFERENCE "+node.location+"  "+g   
              c=0  
          }
       }

       if(flag==4)                                                            //towards max depth
       {
        node.speed=0
        double g=fun(node.location[0],node.location[1],node.location[2])
        if(g>50.2)
        {
          node.diveRate=-2.mps
        }
        else if(g<49.8)
        {
          node.diveRate=2.mps
        }
        else
        {
          node.diveRate=0
          flag=3
          println "Current depth at location "+node.location+"  "+node.Address
        }
       }

       
     })
     
  }
  if (msg instanceof DatagramNtf && msg.protocol == PING_PROTOCOL && addr==102 && msg.from==101)
  {
                        println "Received data from ${msg.from} by ${addr} :" +msg.data;
                        ms=1
    
  }
 }
}