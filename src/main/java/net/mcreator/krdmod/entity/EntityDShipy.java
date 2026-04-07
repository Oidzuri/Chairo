
package net.mcreator.krdmod.entity;

@ElementsKrdModMod.ModElement.Tag
public class EntityDShipy extends ElementsKrdModMod.ModElement {

	public static final int ENTITYID = 25;
	public static final int ENTITYID_RANGED = 26;

	public EntityDShipy(ElementsKrdModMod instance) {
		super(instance, 35);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class).id(new ResourceLocation("krd_mod", "d_shipy"), ENTITYID)
				.name("d_shipy").tracker(64, 3, true).egg(-1, -1).build());
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
			return new RenderLiving(renderManager, new Modeldemon_shipy(), 0.5f) {
				protected ResourceLocation getEntityTexture(Entity entity) {
					return new ResourceLocation("krd_mod:textures/demon3.png");
				}
			};
		});

	}

	public static class EntityCustom extends EntityMob {

		public EntityCustom(World world) {
			super(world);
			setSize(0.6f, 2.8f);
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

	public static class Modeldemon_shipy extends ModelBase {
		private final ModelRenderer body;
		private final ModelRenderer lower;
		private final ModelRenderer cube_r1;
		private final ModelRenderer upper;
		private final ModelRenderer cube_r2;
		private final ModelRenderer hands;
		private final ModelRenderer left_arm;
		private final ModelRenderer lower_r1;
		private final ModelRenderer shipy2;
		private final ModelRenderer bone2;
		private final ModelRenderer cube_r3;
		private final ModelRenderer cube_r4;
		private final ModelRenderer right_arm;
		private final ModelRenderer lower_r2;
		private final ModelRenderer shipy3;
		private final ModelRenderer bone6;
		private final ModelRenderer bone7;
		private final ModelRenderer cube_r5;
		private final ModelRenderer bone;
		private final ModelRenderer cube_r6;
		private final ModelRenderer head;
		private final ModelRenderer head2;
		private final ModelRenderer head3;
		private final ModelRenderer cube_r9_r1;
		private final ModelRenderer cube_r7_r1;
		private final ModelRenderer hair;
		private final ModelRenderer shipu;
		private final ModelRenderer cube_r10;
		private final ModelRenderer shipu3;
		private final ModelRenderer cube_r11;
		private final ModelRenderer shipu2;
		private final ModelRenderer cube_r12;
		private final ModelRenderer foot;
		private final ModelRenderer left;
		private final ModelRenderer left_upp_r1;
		private final ModelRenderer left_low_r1;
		private final ModelRenderer right;
		private final ModelRenderer right_r1;

		public Modeldemon_shipy() {
			textureWidth = 128;
			textureHeight = 128;

			body = new ModelRenderer(this);
			body.setRotationPoint(0.0F, 24.0F, 0.0F);

			lower = new ModelRenderer(this);
			lower.setRotationPoint(1.75F, -45.5F, 4.6125F);
			body.addChild(lower);

			cube_r1 = new ModelRenderer(this);
			cube_r1.setRotationPoint(-0.625F, 20.825F, 0.0F);
			lower.addChild(cube_r1);
			setRotationAngle(cube_r1, -0.0524F, 0.0F, 0.0F);
			cube_r1.cubeList.add(new ModelBox(cube_r1, 40, 40, -7.4125F, -0.2881F, -6.4681F, 6, 9, 6, 0.2F, true));
			cube_r1.cubeList.add(new ModelBox(cube_r1, 40, 40, -1.4125F, -0.2881F, -6.4681F, 6, 9, 6, 0.2F, false));

			upper = new ModelRenderer(this);
			upper.setRotationPoint(1.75F, -54.375F, 5.35F);
			body.addChild(upper);

			cube_r2 = new ModelRenderer(this);
			cube_r2.setRotationPoint(0.0F, 21.25F, 0.0F);
			upper.addChild(cube_r2);
			setRotationAngle(cube_r2, 0.0524F, 0.0F, 0.0F);
			cube_r2.cubeList.add(new ModelBox(cube_r2, 0, 75, -1.8625F, -0.7619F, -7.7431F, 6, 9, 6, 0.0F, false));
			cube_r2.cubeList.add(new ModelBox(cube_r2, 0, 75, -7.8625F, -0.7619F, -7.7431F, 6, 9, 6, 0.0F, true));

			hands = new ModelRenderer(this);
			hands.setRotationPoint(0.125F, 0.0F, 0.0F);
			body.addChild(hands);

			left_arm = new ModelRenderer(this);
			left_arm.setRotationPoint(4.6125F, -32.1F, 0.325F);
			hands.addChild(left_arm);
			setRotationAngle(left_arm, 0.0611F, 0.0F, -0.1745F);
			left_arm.cubeList.add(new ModelBox(left_arm, 0, 46, 1.2816F, -1.3333F, -2.7002F, 6, 11, 6, 0.0F, false));

			lower_r1 = new ModelRenderer(this);
			lower_r1.setRotationPoint(9.6069F, -11.3542F, 5.3489F);
			left_arm.addChild(lower_r1);
			setRotationAngle(lower_r1, -0.384F, 0.0F, 0.0F);
			lower_r1.cubeList.add(new ModelBox(lower_r1, 42, 63, -8.3377F, 20.1359F, -0.0755F, 6, 7, 6, 0.0F, false));

			shipy2 = new ModelRenderer(this);
			shipy2.setRotationPoint(-2.601F, -45.8253F, 1.1193F);
			left_arm.addChild(shipy2);

			bone2 = new ModelRenderer(this);
			bone2.setRotationPoint(17.05F, 30.5F, 5.9875F);
			shipy2.addChild(bone2);
			setRotationAngle(bone2, 0.0F, 0.0F, 1.501F);

			cube_r3 = new ModelRenderer(this);
			cube_r3.setRotationPoint(0.0F, 0.0F, 0.0F);
			bone2.addChild(cube_r3);
			setRotationAngle(cube_r3, 0.0F, 0.0F, 0.0436F);
			cube_r3.cubeList.add(new ModelBox(cube_r3, 0, 24, 14.9425F, 4.6044F, -7.8532F, 2, 3, 2, 0.0F, false));
			cube_r3.cubeList.add(new ModelBox(cube_r3, 0, 24, 19.1209F, 4.7415F, -8.1242F, 2, 3, 2, 0.0F, false));

			cube_r4 = new ModelRenderer(this);
			cube_r4.setRotationPoint(-0.1375F, -2.75F, 0.1625F);
			bone2.addChild(cube_r4);
			setRotationAngle(cube_r4, 0.0F, 0.0F, 0.0436F);
			cube_r4.cubeList.add(new ModelBox(cube_r4, 6, 31, 15.63F, 5.2919F, -7.4782F, 1, 2, 1, 0.0F, false));
			cube_r4.cubeList.add(new ModelBox(cube_r4, 6, 31, 19.8084F, 5.429F, -7.7492F, 1, 2, 1, 0.0F, false));

			right_arm = new ModelRenderer(this);
			right_arm.setRotationPoint(-5.9237F, -32.6149F, 0.4212F);
			hands.addChild(right_arm);
			setRotationAngle(right_arm, 0.0436F, -0.0008F, 0.192F);
			right_arm.cubeList.add(new ModelBox(right_arm, 0, 46, -6.3004F, -0.9131F, -2.9494F, 6, 11, 6, 0.0F, true));

			lower_r2 = new ModelRenderer(this);
			lower_r2.setRotationPoint(-6.3535F, -19.8035F, -0.9244F);
			right_arm.addChild(lower_r2);
			setRotationAngle(lower_r2, -0.384F, 0.0F, 0.0F);
			lower_r2.cubeList.add(new ModelBox(lower_r2, 42, 63, 0.0114F, 26.4443F, 8.8957F, 6, 7, 6, 0.0F, true));

			shipy3 = new ModelRenderer(this);
			shipy3.setRotationPoint(0.2022F, 8.4475F, 4.4827F);
			right_arm.addChild(shipy3);
			setRotationAngle(shipy3, 0.0095F, -0.3489F, -0.0279F);

			bone6 = new ModelRenderer(this);
			bone6.setRotationPoint(-1.0595F, -2.5556F, 0.0247F);
			shipy3.addChild(bone6);
			setRotationAngle(bone6, 0.0F, 0.0F, 1.501F);

			bone7 = new ModelRenderer(this);
			bone7.setRotationPoint(2.3875F, 18.8022F, 11.6171F);
			bone6.addChild(bone7);
			setRotationAngle(bone7, 0.1309F, 0.0F, 0.0F);

			cube_r5 = new ModelRenderer(this);
			cube_r5.setRotationPoint(1.6738F, -19.687F, -7.3344F);
			bone7.addChild(cube_r5);
			setRotationAngle(cube_r5, 0.1313F, 0.0057F, 0.218F);
			cube_r5.cubeList.add(new ModelBox(cube_r5, 0, 24, -4.8194F, 5.9641F, -6.6072F, 2, 3, 2, 0.0F, false));
			cube_r5.cubeList.add(new ModelBox(cube_r5, 6, 31, -4.3893F, 8.9102F, -6.0697F, 1, 2, 1, 0.0F, false));

			bone = new ModelRenderer(this);
			bone.setRotationPoint(1.9146F, 0.9255F, 0.1447F);
			bone6.addChild(bone);
			setRotationAngle(bone, 0.0F, 0.0F, 0.0873F);

			cube_r6 = new ModelRenderer(this);
			cube_r6.setRotationPoint(0.0F, 0.0F, 0.0F);
			bone.addChild(cube_r6);
			setRotationAngle(cube_r6, 0.3058F, 0.0057F, 0.1744F);
			cube_r6.cubeList.add(new ModelBox(cube_r6, 0, 24, -6.5767F, 5.3229F, -5.1511F, 2, 3, 2, 0.0F, false));
			cube_r6.cubeList.add(new ModelBox(cube_r6, 6, 31, -6.1465F, 8.269F, -4.6136F, 1, 2, 1, 0.0F, false));

			head = new ModelRenderer(this);
			head.setRotationPoint(1.2625F, 24.0F, 0.0F);

			head2 = new ModelRenderer(this);
			head2.setRotationPoint(-1.0125F, -58.625F, 0.0F);
			head.addChild(head2);

			head3 = new ModelRenderer(this);
			head3.setRotationPoint(-2.05F, 58.625F, -5.0F);
			head2.addChild(head3);
			head3.cubeList.add(new ModelBox(head3, 1, 23, 1.675F, -44.625F, 0.25F, 5, 11, 11, 0.0F, false));
			head3.cubeList.add(new ModelBox(head3, 1, 23, -3.275F, -44.625F, 0.25F, 5, 11, 11, 0.0F, true));
			head3.cubeList.add(new ModelBox(head3, 4, 65, 6.5582F, -36.075F, 1.007F, 5, 2, 0, 0.0F, false));

			cube_r9_r1 = new ModelRenderer(this);
			cube_r9_r1.setRotationPoint(1.8F, -0.0524F, 5.9986F);
			head3.addChild(cube_r9_r1);
			setRotationAngle(cube_r9_r1, -1.5275F, 0.0057F, -0.1308F);
			cube_r9_r1.cubeList.add(new ModelBox(cube_r9_r1, 4, 65, -4.547F, 2.193F, -35.8929F, 5, 2, 0, 0.0F, true));

			cube_r7_r1 = new ModelRenderer(this);
			cube_r7_r1.setRotationPoint(1.8F, -0.0524F, 5.9986F);
			head3.addChild(cube_r7_r1);
			setRotationAngle(cube_r7_r1, -3.1416F, 1.1781F, 1.693F);
			cube_r7_r1.cubeList.add(new ModelBox(cube_r7_r1, 4, 65, -8.7879F, -5.4566F, 34.4621F, 5, 2, 0, 0.0F, false));

			hair = new ModelRenderer(this);
			hair.setRotationPoint(0.0F, 0.0F, 0.0F);
			head.addChild(hair);
			hair.cubeList.add(new ModelBox(hair, 79, 66, -2.4375F, -45.0F, -4.75F, 7, 19, 12, 0.0F, false));
			hair.cubeList.add(new ModelBox(hair, 79, 66, -7.3125F, -45.0F, -4.75F, 7, 19, 12, 0.0F, true));

			shipu = new ModelRenderer(this);
			shipu.setRotationPoint(0.0F, 0.0F, 0.0F);
			head.addChild(shipu);

			cube_r10 = new ModelRenderer(this);
			cube_r10.setRotationPoint(-3.6993F, -47.447F, 2.1477F);
			shipu.addChild(cube_r10);
			setRotationAngle(cube_r10, 0.0F, -0.4363F, 0.0F);
			cube_r10.cubeList.add(new ModelBox(cube_r10, 6, 31, -4.2193F, -2.2813F, -4.2522F, 1, 2, 1, 0.0F, false));
			cube_r10.cubeList.add(new ModelBox(cube_r10, 0, 24, -4.7693F, -0.2188F, -4.7897F, 2, 3, 2, 0.0F, false));

			shipu3 = new ModelRenderer(this);
			shipu3.setRotationPoint(3.9257F, -42.947F, 2.1477F);
			head.addChild(shipu3);
			setRotationAngle(shipu3, -0.0532F, 0.6102F, -0.0305F);

			cube_r11 = new ModelRenderer(this);
			cube_r11.setRotationPoint(0.0F, 0.0F, 0.0F);
			shipu3.addChild(cube_r11);
			setRotationAngle(cube_r11, 0.0F, -0.4363F, 0.0F);
			cube_r11.cubeList.add(new ModelBox(cube_r11, 6, 31, -1.346F, -6.5591F, -5.9066F, 1, 2, 1, 0.0F, false));
			cube_r11.cubeList.add(new ModelBox(cube_r11, 0, 24, -1.896F, -4.4966F, -6.4441F, 2, 3, 2, 0.0F, false));

			shipu2 = new ModelRenderer(this);
			shipu2.setRotationPoint(0.3007F, -42.947F, 2.1477F);
			head.addChild(shipu2);
			setRotationAngle(shipu2, 0.0F, 0.3491F, 0.0F);

			cube_r12 = new ModelRenderer(this);
			cube_r12.setRotationPoint(0.0F, 0.0F, 0.0F);
			shipu2.addChild(cube_r12);
			setRotationAngle(cube_r12, 0.0F, -0.4363F, 0.0F);
			cube_r12.cubeList.add(new ModelBox(cube_r12, 6, 31, -2.7036F, -6.7813F, -5.3055F, 1, 2, 1, 0.0F, false));
			cube_r12.cubeList.add(new ModelBox(cube_r12, 0, 24, -3.2536F, -4.7188F, -5.843F, 2, 3, 2, 0.0F, false));

			foot = new ModelRenderer(this);
			foot.setRotationPoint(0.0F, 24.0F, 0.0F);

			left = new ModelRenderer(this);
			left.setRotationPoint(2.925F, -16.375F, 0.825F);
			foot.addChild(left);

			left_upp_r1 = new ModelRenderer(this);
			left_upp_r1.setRotationPoint(1.7F, 0.5F, 4.0F);
			left.addChild(left_upp_r1);
			setRotationAngle(left_upp_r1, -0.0869F, -0.0076F, -0.085F);
			left_upp_r1.cubeList.add(new ModelBox(left_upp_r1, 66, 23, -4.7064F, -0.0945F, -7.3432F, 6, 7, 6, 0.0F, false));

			left_low_r1 = new ModelRenderer(this);
			left_low_r1.setRotationPoint(2.3875F, 6.7875F, 3.7125F);
			left.addChild(left_low_r1);
			setRotationAngle(left_low_r1, -0.0436F, 0.0F, 0.0F);
			left_low_r1.cubeList.add(new ModelBox(left_low_r1, 48, 0, -4.8F, 0.0929F, -7.6202F, 6, 10, 6, 0.0F, false));

			right = new ModelRenderer(this);
			right.setRotationPoint(-3.55F, -15.8375F, 0.725F);
			foot.addChild(right);
			right.cubeList.add(new ModelBox(right, 48, 0, -3.225F, 6.0875F, -3.7F, 6, 10, 6, 0.0F, true));

			right_r1 = new ModelRenderer(this);
			right_r1.setRotationPoint(2.3F, -0.5F, 4.1F);
			right.addChild(right_r1);
			setRotationAngle(right_r1, -0.0869F, 0.0076F, 0.0888F);
			right_r1.cubeList.add(new ModelBox(right_r1, 66, 23, -4.8854F, 0.7192F, -7.2299F, 6, 7, 6, 0.0F, true));
		}

		@Override
		public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
			body.render(f5);
			head.render(f5);
			foot.render(f5);
		}

		public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
			modelRenderer.rotateAngleX = x;
			modelRenderer.rotateAngleY = y;
			modelRenderer.rotateAngleZ = z;
		}

		public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
			super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
			this.right_arm.rotateAngleX = MathHelper.cos(f * 0.6662F + (float) Math.PI) * f1;
			this.left_arm.rotateAngleX = MathHelper.cos(f * 0.6662F) * f1;
			this.right.rotateAngleX = MathHelper.cos(f * 1.0F) * 1.0F * f1;
			this.left.rotateAngleX = MathHelper.cos(f * 1.0F) * -1.0F * f1;
		}
	}

}
