package org.fabric_python.mod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.util.UUID.randomUUID;

import py4j.GatewayServer;

@SuppressWarnings("unused")
public class PythonProxy implements ClientModInitializer {
	private static PythonProxy pythonProxy;
	public static Inbox inbox;
	public static Outbox outbox;
	public static Set<String> noRenderList;
	public static Map<String, String> globalMap;
	public static final ConcurrentHashMap<Long, Integer> guardianCount = new ConcurrentHashMap<>();
	public static Map<String, String> loadedLectern;
	public static Logger logger = LogManager.getFormatterLogger("Fabric-Python");

	public static PythonProxy getInstance(){
		return pythonProxy;
	}

	public static Logger getLogger() {
		return logger;
	}

	@Override
	public void onInitializeClient() {
		pythonProxy = this;

		inbox = new Inbox();
		outbox = new Outbox();

		noRenderList = new HashSet<>();
		noRenderList.add("netherrack");
		noRenderList.add("stone");
		noRenderList.add("granite");
		noRenderList.add("diorite");
		noRenderList.add("andesite");

		globalMap = new HashMap<>();
		loadedLectern = new HashMap<>();

		inbox.addWorker("find_safe_mine_block", new org.fabric_python.mod.block.FindSafeMineBlock());
		inbox.addWorker("start_mine", new org.fabric_python.mod.block.StartMine());
		inbox.addWorker("finish_mine", new org.fabric_python.mod.block.FinishMine());

		inbox.addWorker("chest_cache", new org.fabric_python.mod.container.ChestCache());
		inbox.addWorker("close_container", new org.fabric_python.mod.container.CloseContainer());
		inbox.addWorker("register_chests", new org.fabric_python.mod.container.RegisterChests());
		inbox.addWorker("nospace", new org.fabric_python.mod.container.NoSpace());
		inbox.addWorker("move_items_in_shulker_box", new org.fabric_python.mod.container.MoveItemsInShulkerBox());

		inbox.addWorker("attack_entity", new  org.fabric_python.mod.player.AttackEntity());
		inbox.addWorker("hungry", new org.fabric_python.mod.player.Hungry());
		inbox.addWorker("switch_items", new org.fabric_python.mod.player.SwitchItems());
		inbox.addWorker("use_item", new org.fabric_python.mod.player.UseItem());
		inbox.addWorker("use_block", new org.fabric_python.mod.player.UseBlock());
		inbox.addWorker("attack_block", new org.fabric_python.mod.player.AttackBlock());
		inbox.addWorker("move", new org.fabric_python.mod.player.Move());
		inbox.addWorker("rowing", new org.fabric_python.mod.player.Rowing());
		inbox.addWorker("send_chat_message", new org.fabric_python.mod.player.SendChatMessage());

		inbox.addWorker("nearby_mods", new org.fabric_python.mod.world.NearbyMods());

		GatewayServer gatewayServer = new GatewayServer(this);
		gatewayServer.start();

		getLogger().info("Fabric-Python has been initialized");

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
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
			int attempts = 50;

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
