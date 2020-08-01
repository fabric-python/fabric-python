package org.fabric_python.mod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import py4j.GatewayServer;

import java.util.Map;

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
}
