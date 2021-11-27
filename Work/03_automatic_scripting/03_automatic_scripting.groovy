println("hii KP, Good evening! ")
println(routes)
println("\nEstablishing up connection to Node C via Node B...\n")
rreq host('C')
println("SHE SAID YES!!\n\n")
rsh 74,'addroute 232,31'
rsh 74,'?"hii ${me}, this is ${node.address}. Looking forward to work with you"'
//ping host('C')