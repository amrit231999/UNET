import org.arl.unet.utils.GPlot
import java.awt.Color

// read trace.nam to get simulation results
z = [[],[],[],[]]
x = [[],[],[],[]]
y = [[],[],[],[]]

n = 0
new File('logs/trace.nam').eachLine { s ->
  
  if (s.startsWith('# BEGIN SIMULATION ')){ 
      n = s.substring(19) as int
      
    }
  else if (n >= 1 && n <= 50 && s.startsWith('n ')) {
    x[n-1] << ((s =~ /-x ([\-0-9\.]+)/)[0][1] as double)
    y[n-1] << ((s =~ /-y ([\-0-9\.]+)/)[0][1] as double)
    z[n-1] << ((s =~ /-Z ([\-0-9\.]+)/)[0][1] as double)
  }
}

// plot the paths
new GPlot('AUV Tracks', 600, 600).with {
  xlabel('meters')
  ylabel('meters')
  plot("AUV-1", x[0] as double[], y[0] as double[], Color.blue)
  plot("AUV-2", x[1] as double[], y[1] as double[], Color.red)
  plot("AUV-3", x[2] as double[], y[2] as double[], Color.magenta)
  plot("AUV-4", x[4] as double[], y[3] as double[], Color.pink)
  plot("AUV-5", x[5] as double[], y[4] as double[], Color.pink)
  plot("AUV-6", x[6] as double[], y[5] as double[], Color.pink)
  axis(-200, 4000, -200, 4000)
  drawnow()
}
