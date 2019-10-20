# Fabric-Python: An interface for AI experiments in Minecraft

This mod is basically loading Fabric-MC and py4j and therefore provides an interface for Python -- a language with wide adoption in data science -- to opearte on the Minecraft client.

## API Example Python-side script

### Initializing the connection

```
from py4j.java_gateway import JavaGateway, GatewayParameters
params = GatewayParameters(auth_token="insecure")
gateway = JavaGateway(gateway_parameters=params)
pp = gateway.entry_point
```

### Sending message to the player oneself
```
pp.sendMessageToPlayer("Connected")
```

### Sending a chat message
```
pp.sendChatMessage("hi!")
```

### Moving the player for a short distance (distance limited to 10)
```
/* move the player from (x,y,z) to (x+1,y,z) */
pp.movePlayer(1, 0, 0)
```

### Moving the player for a long distance, assisted by AI
```
/* move the player from (x,y,z) to (246,73,-63) */
pp.movePlayerAI(246, 73, -63)
```

### Obtaining the current player's location
```
pp.getPlayerLocation()
```

### Searching the positons of blocks of a specific type
```
pp.blockSearch(146, 88, -62, 207, 88, -1, "minecraft:spruce_sapling")
```

### Moving specific items to the main hand
```
pp.switchItem("minecraft:spruce_sapling")
```

### Attacking a block ("left-click")
```
pp.attackBlock(-29184, 70, 166)
```

### Using an item on a block ("right-click")
```
pp.useBlock(-29178,72, 162)
```  

## Example applications

### Use case in a specific Minecraft server with advanced furnace
```
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

### Use case in a specific Minecraft server with QuickShop and an shulker box unloading machine
```
time.sleep(5)
for i in range(10):
  pp.useBlock(-29178,72, 162)
  time.sleep(3)
  pp.useBlock(-29178,72, 162)
  time.sleep(3)
  pp.attackBlock(-29184, 70, 166)
  time.sleep(1)
  pp.sendChatMessage("2304")
```

### Use case to plant trees in a paved tree farm with preconfigured paramters (paramter 1)
```
import time
def do_one(pos):
  dirt_level = 65
  x_gap = 4
  z_gap = 4
  x_row = 16
  x_start = 30002
  z_start = 304
  i = pos % x_row
  j = pos // x_row
  if i == 0:
    if j != 0:
      pp.movePlayer(0, 0, 2)
  x = x_start+ i * x_gap
  z = z_start + j * z_gap
  sapling_planted = len(pp.blockSearch(x, dirt_level + 1, z, x + 1, dirt_level + 1, z +1, "minecraft:spruce_log")) + len(pp.blockSearch(x, dirt_level + 1, z, x + 1, dirt_level + 1, z +1, "minecraft:spruce_sapling"))
  if sapling_planted == 4:
     return 1
  cur_pos = pp.getPlayerLocation()
  cur_x = cur_pos[0]
  if cur_x < x:
    pp.movePlayerAI(x + 1, dirt_level + 1, z + 1)
  else:
    pp.movePlayerAI(x - 1, dirt_level + 1, z + 1)
  if i == 0:
    if j != 0:
      time.sleep(1)
  time.sleep(0.5)
  res = pp.switchItem("minecraft:spruce_sapling")
  if res == "Failure":
     return 0
  time.sleep(0.4)
  res = pp.useBlock(x, dirt_level, z)
  if res == "Failure":
     return 0
  time.sleep(0.4)
  res = pp.switchItem("minecraft:spruce_sapling")
  if res == "Failure":
     return 0
  time.sleep(0.4)
  res = pp.useBlock(x + 1, dirt_level, z)
  if res == "Failure":
     return 0
  time.sleep(0.4)
  res = pp.switchItem("minecraft:spruce_sapling")
  if res == "Failure":
     return 0
  time.sleep(0.4)
  res = pp.useBlock(x, dirt_level, z + 1)
  if res == "Failure":
     return 0
  time.sleep(0.4)
  res = pp.switchItem("minecraft:spruce_sapling")
  if res == "Failure":
     return 0
  res = pp.useBlock(x + 1, dirt_level, z + 1)
  if res == "Failure":
     return 0
  sapling_planted = len(pp.blockSearch(x, dirt_level + 1, z, x + 1, dirt_level + 1, z +1, "minecraft:spruce_log")) + len(pp.blockSearch(x, dirt_level + 1, z, x + 1, dirt_level + 1, z +1, "minecraft:spruce_sapling"))
  if sapling_planted != 4:
     return 0
  return 1

def do_rest(start_pos):
  for i in range(start_pos, 16 * 9):
    if do_one(i) == 0:
       print("Failed at " + str(i))
       break

do_rest(0)

```

### Use case to plant trees in a paved tree farm with preconfigured paramters (paramter 2)
```
import time
def do_one(pos):
  dirt_level = 87
  x_gap = 3
  z_gap = 3
  x_row = 21
  x_start = 146
  z_start = -62
  i = pos % x_row
  j = pos // x_row
  if i == 0:
    if j != 0:
      pp.movePlayer(0, 0, 2)
  x = x_start+ i * x_gap
  z = z_start + j * z_gap
  sapling_planted = len(pp.blockSearch(x, dirt_level + 1, z, x + 1, dirt_level + 1, z +1, "minecraft:spruce_log")) + len(pp.blockSearch(x, dirt_level + 1, z, x + 1, dirt_level + 1, z +1, "minecraft:spruce_sapling"))
  if sapling_planted == 4:
     return 1
  cur_pos = pp.getPlayerLocation()
  cur_x = cur_pos[0]
  if cur_x < x:
    pp.movePlayerAI(x + 1, dirt_level + 1, z + 1)
  else:
    pp.movePlayerAI(x - 1, dirt_level + 1, z + 1)
  if i == 0:
    if j != 0:
      time.sleep(1)
  time.sleep(0.5)
  res = pp.switchItem("minecraft:spruce_sapling")
  if res == "Failure":
     return 0
  time.sleep(0.4)
  res = pp.useBlock(x, dirt_level, z)
  if res == "Failure":
     return 0
  time.sleep(0.4)
  res = pp.switchItem("minecraft:spruce_sapling")
  if res == "Failure":
     return 0
  time.sleep(0.4)
  res = pp.useBlock(x + 1, dirt_level, z)
  if res == "Failure":
     return 0
  time.sleep(0.4)
  res = pp.switchItem("minecraft:spruce_sapling")
  if res == "Failure":
     return 0
  time.sleep(0.4)
  res = pp.useBlock(x, dirt_level, z + 1)
  if res == "Failure":
     return 0
  time.sleep(0.4)
  res = pp.switchItem("minecraft:spruce_sapling")
  if res == "Failure":
     return 0
  res = pp.useBlock(x + 1, dirt_level, z + 1)
  if res == "Failure":
     return 0
  sapling_planted = len(pp.blockSearch(x, dirt_level + 1, z, x + 1, dirt_level + 1, z +1, "minecraft:spruce_log")) + len(pp.blockSearch(x, dirt_level + 1, z, x + 1, dirt_level + 1, z +1, "minecraft:spruce_sapling"))
  if sapling_planted != 4:
     return 0
  return 1

def do_rest(start_pos):
  for i in range(start_pos, 441):
    if do_one(i) == 0:
       print("Failed at " + str(i))
       break

do_rest(0)

```
