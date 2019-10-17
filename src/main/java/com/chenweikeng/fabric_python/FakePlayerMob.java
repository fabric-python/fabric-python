package com.chenweikeng.fabric_python;

import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class FakePlayerMob extends AmbientEntity {
	public static EntityType<FakePlayerMob> FAKE_PLAYER_MOB_ENTITY_TYPE;

	public static void registerFakePlayerMob(){
		EntityType.Builder<FakePlayerMob> builder = EntityType.Builder.create(EntityCategory.MISC);

		FAKE_PLAYER_MOB_ENTITY_TYPE = Registry.register(
				Registry.ENTITY_TYPE,
				"fake_player_mob",
				builder.disableSaving().disableSummon().setDimensions(0.6F, 1.8F).build("fake_player_mob")
		);
	}

	public FakePlayerMob(EntityType<? extends FakePlayerMob > entityType_1, World world_1) {
		super(entityType_1, world_1);
	}

	public void copyPlayer(PlayerEntity player){
		this.inWater = player.isInWater();
		this.setBoundingBox(player.getBoundingBox());
		this.x = player.x;
		this.y = player.y;
		this.z = player.z;
		this.onGround = player.onGround;
		this.stepHeight = player.stepHeight;
		this.setMovementSpeed(player.getMovementSpeed());
		this.setForwardSpeed(player.forwardSpeed);
		this.setSidewaysSpeed(player.sidewaysSpeed);
		this.setUpwardSpeed(player.upwardSpeed);
	}
}
