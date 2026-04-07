
package net.mcreator.krdmod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.World;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;

import net.mcreator.krdmod.ElementsKrdModMod;

import java.util.Iterator;
import java.util.ArrayList;

@ElementsKrdModMod.ModElement.Tag
public class EntitySusamaru extends ElementsKrdModMod.ModElement {
	public static final int ENTITYID = 21;
	public static final int ENTITYID_RANGED = 22;
	public EntitySusamaru(ElementsKrdModMod instance) {
		super(instance, 36);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class).id(new ResourceLocation("krd_mod", "susamaru"), ENTITYID)
				.name("susamaru").tracker(64, 3, true).egg(-1, -1).build());
	}

	@Override
	public void init(FMLInitializationEvent event) {
		Biome[] spawnBiomes = allbiomes(Biome.REGISTRY);
		EntityRegistry.addSpawn(EntityCustom.class, 20, 4, 4, EnumCreatureType.MONSTER, spawnBiomes);
	}

	private Biome[] allbiomes(net.minecraft.util.registry.RegistryNamespaced<ResourceLocation, Biome> in) {
		Iterator<Biome> itr = in.iterator();
		ArrayList<Biome> ls = new ArrayList<Biome>();
		while (itr.hasNext())
			ls.add(itr.next());
		return ls.toArray(new Biome[ls.size()]);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> {
			return new RenderLiving(renderManager, new Modelsusamaru(), 0.5f) {
				protected ResourceLocation getEntityTexture(Entity entity) {
					return new ResourceLocation("krd_mod:textures/susamaru.png");
				}
			};
		});
	}
	public static class EntityCustom extends EntityMob {
		public EntityCustom(World world) {
			super(world);
			setSize(0.6f, 1.8f);
			experienceValue = 0;
			this.isImmuneToFire = false;
			setNoAI(!true);
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.2, false));
			this.tasks.addTask(2, new EntityAIWander(this, 1));
			this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, false));
			this.tasks.addTask(4, new EntityAILookIdle(this));
			this.tasks.addTask(5, new EntityAISwimming(this));
		}

		@Override
		public EnumCreatureAttribute getCreatureAttribute() {
			return EnumCreatureAttribute.UNDEFINED;
		}

		@Override
		protected Item getDropItem() {
			return null;
		}

		@Override
		public net.minecraft.util.SoundEvent getAmbientSound() {
			return (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation(""));
		}

		@Override
		public net.minecraft.util.SoundEvent getHurtSound(DamageSource ds) {
			return (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("entity.generic.hurt"));
		}

		@Override
		public net.minecraft.util.SoundEvent getDeathSound() {
			return (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("entity.generic.death"));
		}

		@Override
		protected float getSoundVolume() {
			return 1.0F;
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			if (this.getEntityAttribute(SharedMonsterAttributes.ARMOR) != null)
				this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(0D);
			if (this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED) != null)
				this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
			if (this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH) != null)
				this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10D);
			if (this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE) != null)
				this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3D);
		}
	}

	// Made with Blockbench 5.1.3
	// Exported for Minecraft version 1.7 - 1.12
	// Paste this class into your mod and generate all required imports
	public static class Modelsusamaru extends ModelBase {
		private final ModelRenderer head;
		private final ModelRenderer bone5;
		private final ModelRenderer body;
		private final ModelRenderer armleft;
		private final ModelRenderer bone;
		private final ModelRenderer cube_r1;
		private final ModelRenderer cube_r2;
		private final ModelRenderer boll;
		private final ModelRenderer cube_r3;
		private final ModelRenderer bone6;
		private final ModelRenderer bone7;
		private final ModelRenderer armright;
		private final ModelRenderer bone2;
		private final ModelRenderer cube_r4;
		private final ModelRenderer cube_r5;
		private final ModelRenderer boll2;
		private final ModelRenderer cube_r6;
		private final ModelRenderer bone8;
		private final ModelRenderer bone9;
		private final ModelRenderer leftFood;
		private final ModelRenderer bone3;
		private final ModelRenderer cube_r7;
		private final ModelRenderer cube_r8;
		private final ModelRenderer rigtfoot;
		private final ModelRenderer bone4;
		private final ModelRenderer cube_r9;
		private final ModelRenderer cube_r10;
		public Modelsusamaru() {
			textureWidth = 64;
			textureHeight = 64;
			head = new ModelRenderer(this);
			head.setRotationPoint(0.0F, 1.75F, 0.5F);
			head.cubeList.add(new ModelBox(head, 32, 0, -4.0F, -8.55F, -3.5F, 8, 8, 8, 0.2F, false));
			bone5 = new ModelRenderer(this);
			bone5.setRotationPoint(-8.0F, -1.45F, -0.5F);
			head.addChild(bone5);
			bone5.cubeList.add(new ModelBox(bone5, 0, 16, 8.0F, -7.0F, -3.0F, 4, 8, 8, 0.0F, true));
			bone5.cubeList.add(new ModelBox(bone5, 0, 16, 4.0F, -7.0F, -3.0F, 4, 8, 8, 0.0F, false));
			body = new ModelRenderer(this);
			body.setRotationPoint(0.0F, 24.2F, 0.0F);
			body.cubeList.add(new ModelBox(body, 16, 32, -4.0F, -22.7F, -1.0F, 8, 12, 4, 0.02F, false));
			armleft = new ModelRenderer(this);
			armleft.setRotationPoint(3.54F, -21.6991F, 1.0F);
			body.addChild(armleft);
			setRotationAngle(armleft, 0.0F, 0.0F, 0.1396F);
			bone = new ModelRenderer(this);
			bone.setRotationPoint(3.5052F, -2.5486F, 0.1115F);
			armleft.addChild(bone);
			cube_r1 = new ModelRenderer(this);
			cube_r1.setRotationPoint(3.3962F, 6.6791F, -3.4115F);
			bone.addChild(cube_r1);
			setRotationAngle(cube_r1, -0.696F, -0.001F, -0.7006F);
			cube_r1.cubeList.add(new ModelBox(cube_r1, 40, 37, -3.5851F, -3.5733F, -0.0745F, 4, 7, 4, 0.0F, true));
			cube_r2 = new ModelRenderer(this);
			cube_r2.setRotationPoint(1.2576F, 2.963F, -0.1115F);
			bone.addChild(cube_r2);
			setRotationAngle(cube_r2, 0.0F, 0.0F, -0.7069F);
			cube_r2.cubeList.add(new ModelBox(cube_r2, 40, 32, -4.3853F, -3.9782F, -2.0F, 4, 8, 4, 0.0F, true));
			boll = new ModelRenderer(this);
			boll.setRotationPoint(7.513F, 7.6266F, -3.3687F);
			armleft.addChild(boll);
			setRotationAngle(boll, -0.7348F, 0.3434F, -1.1652F);
			boll.cubeList.add(new ModelBox(boll, 52, 50, -1.2217F, 0.9805F, -1.5F, 3, 1, 3, 0.0F, false));
			cube_r3 = new ModelRenderer(this);
			cube_r3.setRotationPoint(-2.2217F, 3.9805F, -0.5F);
			boll.addChild(cube_r3);
			setRotationAngle(cube_r3, 0.0F, 0.0F, 3.1416F);
			cube_r3.cubeList.add(new ModelBox(cube_r3, 52, 50, -4.0F, -1.0F, -1.0F, 3, 1, 3, 0.0F, false));
			bone6 = new ModelRenderer(this);
			bone6.setRotationPoint(1.2783F, 1.9805F, -1.0F);
			boll.addChild(bone6);
			setRotationAngle(bone6, 0.0F, 0.0F, -3.1416F);
			bone6.cubeList.add(new ModelBox(bone6, 48, 56, 1.0F, -2.0F, -1.0F, 2, 1, 4, 0.0F, true));
			bone6.cubeList.add(new ModelBox(bone6, 48, 56, -1.0F, -2.0F, -1.0F, 2, 1, 4, 0.0F, false));
			bone7 = new ModelRenderer(this);
			bone7.setRotationPoint(-0.7217F, 1.9805F, -1.0F);
			boll.addChild(bone7);
			setRotationAngle(bone7, 0.0F, 0.0F, 0.0F);
			bone7.cubeList.add(new ModelBox(bone7, 48, 56, -1.0F, 0.0F, -1.0F, 2, 1, 4, 0.0F, false));
			bone7.cubeList.add(new ModelBox(bone7, 48, 56, 1.0F, 0.0F, -1.0F, 2, 1, 4, 0.0F, true));
			armright = new ModelRenderer(this);
			armright.setRotationPoint(-3.66F, -21.7991F, 1.0F);
			body.addChild(armright);
			bone2 = new ModelRenderer(this);
			bone2.setRotationPoint(4.06F, 7.3991F, 0.0F);
			armright.addChild(bone2);
			setRotationAngle(bone2, 0.0F, 0.0F, 1.2217F);
			cube_r4 = new ModelRenderer(this);
			cube_r4.setRotationPoint(-5.2F, 5.8F, -3.3F);
			bone2.addChild(cube_r4);
			setRotationAngle(cube_r4, -0.696F, -0.001F, -0.7006F);
			cube_r4.cubeList.add(new ModelBox(cube_r4, 40, 37, -2.3872F, -1.9713F, 1.2621F, 4, 7, 4, 0.0F, false));
			cube_r5 = new ModelRenderer(this);
			cube_r5.setRotationPoint(-7.3386F, 2.0839F, 0.0F);
			bone2.addChild(cube_r5);
			setRotationAngle(cube_r5, 0.0F, 0.0F, -0.7069F);
			cube_r5.cubeList.add(new ModelBox(cube_r5, 40, 32, -3.1006F, -1.8837F, -2.0F, 4, 8, 4, 0.0F, false));
			boll2 = new ModelRenderer(this);
			boll2.setRotationPoint(-6.1543F, 9.7884F, -4.686F);
			armright.addChild(boll2);
			setRotationAngle(boll2, -0.6545F, 0.48F, 0.0873F);
			boll2.cubeList.add(new ModelBox(boll2, 52, 50, -2.0F, -0.5F, -1.3F, 3, 1, 3, 0.0F, false));
			cube_r6 = new ModelRenderer(this);
			cube_r6.setRotationPoint(-3.0F, 2.5F, -0.3F);
			boll2.addChild(cube_r6);
			setRotationAngle(cube_r6, 0.0F, 0.0F, 3.1416F);
			cube_r6.cubeList.add(new ModelBox(cube_r6, 52, 50, -4.0F, -1.0F, -1.0F, 3, 1, 3, 0.0F, false));
			bone8 = new ModelRenderer(this);
			bone8.setRotationPoint(0.5F, 0.5F, -0.8F);
			boll2.addChild(bone8);
			setRotationAngle(bone8, 0.0F, 0.0F, -3.1416F);
			bone8.cubeList.add(new ModelBox(bone8, 48, 56, 1.0F, -2.0F, -1.0F, 2, 1, 4, 0.0F, true));
			bone8.cubeList.add(new ModelBox(bone8, 48, 56, -1.0F, -2.0F, -1.0F, 2, 1, 4, 0.0F, false));
			bone9 = new ModelRenderer(this);
			bone9.setRotationPoint(-1.5F, 0.5F, -0.8F);
			boll2.addChild(bone9);
			setRotationAngle(bone9, 0.0F, 0.0F, 0.0F);
			bone9.cubeList.add(new ModelBox(bone9, 48, 56, -1.0F, 0.0F, -1.0F, 2, 1, 4, 0.0F, false));
			bone9.cubeList.add(new ModelBox(bone9, 48, 56, 1.0F, 0.0F, -1.0F, 2, 1, 4, 0.0F, true));
			leftFood = new ModelRenderer(this);
			leftFood.setRotationPoint(2.1034F, 13.0791F, 0.9F);
			setRotationAngle(leftFood, 0.0F, 0.0F, 0.2618F);
			bone3 = new ModelRenderer(this);
			bone3.setRotationPoint(-0.2171F, -0.3718F, -0.1F);
			leftFood.addChild(bone3);
			cube_r7 = new ModelRenderer(this);
			cube_r7.setRotationPoint(1.8794F, -0.684F, 0.0F);
			bone3.addChild(cube_r7);
			setRotationAngle(cube_r7, -0.1309F, 0.0F, -0.3491F);
			cube_r7.cubeList.add(new ModelBox(cube_r7, 0, 33, -4.0106F, 0.545F, -1.7246F, 4, 6, 4, 0.0F, true));
			cube_r8 = new ModelRenderer(this);
			cube_r8.setRotationPoint(3.7263F, 4.3903F, -0.6F);
			bone3.addChild(cube_r8);
			setRotationAngle(cube_r8, 0.0F, 0.0F, -0.3491F);
			cube_r8.cubeList.add(new ModelBox(cube_r8, 0, 39, -4.0115F, 0.8516F, -1.964F, 4, 5, 4, 0.0F, true));
			rigtfoot = new ModelRenderer(this);
			rigtfoot.setRotationPoint(-2.2F, 12.9F, 1.0F);
			setRotationAngle(rigtfoot, 0.0F, 0.0F, 0.0873F);
			bone4 = new ModelRenderer(this);
			bone4.setRotationPoint(2.171F, -0.2302F, -0.2F);
			rigtfoot.addChild(bone4);
			setRotationAngle(bone4, 0.0F, 0.0F, 0.3491F);
			cube_r9 = new ModelRenderer(this);
			cube_r9.setRotationPoint(0.0F, 0.0F, 0.0F);
			bone4.addChild(cube_r9);
			setRotationAngle(cube_r9, -0.1309F, 0.0F, -0.3491F);
			cube_r9.cubeList.add(new ModelBox(cube_r9, 0, 33, -3.991F, 0.5536F, -1.7235F, 4, 6, 4, 0.0F, false));
			cube_r10 = new ModelRenderer(this);
			cube_r10.setRotationPoint(1.8469F, 5.0743F, -0.6F);
			bone4.addChild(cube_r10);
			setRotationAngle(cube_r10, 0.0F, 0.0F, -0.3491F);
			cube_r10.cubeList.add(new ModelBox(cube_r10, 0, 39, -3.9919F, 0.8603F, -1.964F, 4, 5, 4, 0.0F, false));
		}

		@Override
		public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
			head.render(f5);
			body.render(f5);
			leftFood.render(f5);
			rigtfoot.render(f5);
		}

		public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
			modelRenderer.rotateAngleX = x;
			modelRenderer.rotateAngleY = y;
			modelRenderer.rotateAngleZ = z;
		}

		public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
			super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
			this.armright.rotateAngleX = MathHelper.cos(f * 0.6662F + (float) Math.PI) * f1;
			this.armleft.rotateAngleX = MathHelper.cos(f * 0.6662F) * f1;
			this.rigtfoot.rotateAngleX = MathHelper.cos(f * 1.0F) * 1.0F * f1;
			this.leftFood.rotateAngleX = MathHelper.cos(f * 1.0F) * -1.0F * f1;
		}
	}
}
