# Fabric-Python: An interface for AI experiments in Minecraft

This mod is basically loading Fabric-MC and py4j and therefore provides an interface for Python -- a language with wide adoption in data science -- to opearte on the Minecraft client.

### Example: Python-side script
```
from py4j.java_gateway import JavaGateway, GatewayParameters
params = GatewayParameters(auth_token="insecure")
gateway = JavaGateway(gateway_parameters=params)
pp = gateway.entry_point

pp.sendMessageToPlayer("Connected")
pp.movePlayer(1, 0, 0)
pp.movePlayerAI(246, 73, -63)
pp.getPlayerLocation()
pp.blockSearch(146, 88, -62, 207, 88, -1, "minecraft:spruce_log")

# use case in a specific Minecraft server with advanced furnace
import csv
import time
csvfile = open("E:\\ai\\adv_furnace.txt", newline='')
loc = csv.reader(csvfile)
for row in loc:
  if len(row) > 3:
    pp.movePlayerAI(int(row[1]), int(row[2]), int(row[3]))
    pp.changePlayerPosition(0.0,90.0)
    pp.sendChatMessage("/adv_furnace getmoney")
    time.sleep(1)
```
