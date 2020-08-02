package org.fabric_python.mod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import py4j.GatewayServer;

import java.util.HashMap;
import java.util.Map;

import static java.util.UUID.randomUUID;

@SuppressWarnings("unused")
public class PythonProxy implements ClientModInitializer {
	private static PythonProxy pythonProxy;
	public static Inbox inbox;
	public static Outbox outbox;

	public static PythonProxy getInstance(){
		return pythonProxy;
	}

	@Override
	public void onInitializeClient() {
		pythonProxy = this;

		inbox = new Inbox();
		outbox = new Outbox();

		inbox.addWorker("attack", new org.fabric_python.mod.player.Attack());
		inbox.addWorker("hungry", new org.fabric_python.mod.player.Hungry());
		inbox.addWorker("switch_items", new org.fabric_python.mod.player.SwitchItems());
		inbox.addWorker("use", new org.fabric_python.mod.player.Use());

		inbox.addWorker("nearby_mods", new org.fabric_python.mod.world.NearbyMods());

		GatewayServer gatewayServer = new GatewayServer(new PythonProxy());
		gatewayServer.start();

		ClientTickCallback.EVENT.register(client -> {
			inbox.run(client);
		});
	}

	public void put(String name, Map<String, String> info) {
		inbox.put(name, info);
	}

	public Map<String, String> get(String sid){
		return outbox.getMsg(sid);
	}

	public Map<String, String> put_and_get(String name, Map<String, String> info) {
		inbox.put(name, info);

		String sid = info.getOrDefault("sid", "null");

		if(sid.equals("null")){
			sid = randomUUID().toString();
			info.put("sid", sid);
		}

		try {
			int attempts = 10;

			while(attempts > 0){
				Thread.sleep(100);

				Map<String, String> map = outbox.getMsg(sid);
				if(!map.isEmpty()){
					return map;
				}

				attempts --;
			}
		} catch(InterruptedException ignored) {}

		Map<String, String> res = new HashMap<String, String>();
		res.put("res", "timeout");
		return res;
	}
}
