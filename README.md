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
```
