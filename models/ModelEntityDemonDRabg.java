
@ElementsKrdModMod.ModElement.Tag
public static class ModelEntityDemonDRabg extends ElementsKrdModMod.ModElement {
	public static final int ENTITYID = 29;
	public static final int ENTITYID_RANGED = 30;
	public ModelEntityDemonDRabg(ElementsKrdModMod instance) {
		super(instance, 6);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
				.id(new ResourceLocation("krd_mod", "demon_d_rabg"), ENTITYID).name("demon_d_rabg").tracker(64, 3, true)
				.egg(-1, -1).build());
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
			return new RenderLiving(renderManager, new Modelkrd_mod(), 0.5f) {
				protected ResourceLocation getEntityTexture(Entity entity) {
					return new ResourceLocation("krd_mod:textures/demondnew1.png");
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
			return (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
					.getObject(new ResourceLocation(""));
		}

		@Override
		public net.minecraft.util.SoundEvent getHurtSound(DamageSource ds) {
			return (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
					.getObject(new ResourceLocation("entity.generic.hurt"));
		}

		@Override
		public net.minecraft.util.SoundEvent getDeathSound() {
			return (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
					.getObject(new ResourceLocation("entity.generic.death"));
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

	// Made with Blockbench 5.1.2
	// Exported for Minecraft version 1.7 - 1.12
	// Paste this class into your mod and generate all required imports
	// Обновленный класс модели с исправлениями
	// позиционирования и анимации
	public static class Modelkrd_mod extends ModelBase {
		private final ModelRenderer head;
		private final ModelRenderer body;
		private final ModelRenderer armLeft;
		private final ModelRenderer lower_arm_l_child;
		private final ModelRenderer armRight;
		private final ModelRenderer upper_arm_r;
		private final ModelRenderer lower_arm_r_child;
		private final ModelRenderer leftFood;
		private final ModelRenderer lower_leg_l;
		private final ModelRenderer rigtfoot;
		private final ModelRenderer upper_leg_r;
		private final ModelRenderer lower_leg_r;
		private final ModelRenderer bb_main;

		public Modelkrd_mod() {
			textureWidth = 64;
			textureHeight = 64;

			// ГОЛОВА: Поднята вверх (Y=0), чтобы не была в
			// животе
			head = new ModelRenderer(this);
			head.setRotationPoint(0.0F, 0.0F, 0.0F);
			head.cubeList.add(new ModelBox(head, 0, 16, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
			head.cubeList.add(new ModelBox(head, 32, 16, -4.0F, -8.1F, -4.0F, 8, 8, 8, 0.2F, false));

			// ТЕЛО
			body = new ModelRenderer(this);
			body.setRotationPoint(0.0F, 0.0F, 0.0F);
			body.cubeList.add(new ModelBox(body, 16, 32, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));

			// ЛЕВАЯ РУКА (Симметрично -5)
			armLeft = new ModelRenderer(this);
			armLeft.setRotationPoint(-5.0F, 2.0F, 0.0F);
			setRotationAngle(armLeft, -0.1309F, 0.0F, 0.0F);
			armLeft.cubeList.add(new ModelBox(armLeft, 40, 34, -3.0F, -2.0F, -2.0F, 4, 6, 4, 0.0F, false));

			lower_arm_l_child = new ModelRenderer(this);
			lower_arm_l_child.setRotationPoint(-1.0F, 4.0F, 0.0F);
			armLeft.addChild(lower_arm_l_child);
			setRotationAngle(lower_arm_l_child, -0.4363F, 0.0F, 0.0F);
			lower_arm_l_child.cubeList
					.add(new ModelBox(lower_arm_l_child, 40, 33, -2.0F, 0.0F, -2.0F, 4, 7, 4, 0.0F, false));

			// ПРАВАЯ РУКА (Симметрично 5)
			armRight = new ModelRenderer(this);
			armRight.setRotationPoint(5.0F, 2.0F, 0.0F);
			setRotationAngle(armRight, 0.4278F, -0.1423F, 0.0669F);

			upper_arm_r = new ModelRenderer(this);
			upper_arm_r.setRotationPoint(0.0F, 0.0F, 0.0F);
			armRight.addChild(upper_arm_r);
			upper_arm_r.cubeList.add(new ModelBox(upper_arm_r, 40, 34, -1.0F, -2.0F, -2.0F, 4, 6, 4, 0.0F, true));

			lower_arm_r_child = new ModelRenderer(this);
			lower_arm_r_child.setRotationPoint(1.0F, 4.0F, 0.0F);
			upper_arm_r.addChild(lower_arm_r_child);
			setRotationAngle(lower_arm_r_child, -0.3491F, 0.0F, 0.0F);
			lower_arm_r_child.cubeList
					.add(new ModelBox(lower_arm_r_child, 40, 33, -2.0F, 0.0F, -2.0F, 4, 7, 4, 0.0F, true));

			// НОГ�?
			leftFood = new ModelRenderer(this);
			leftFood.setRotationPoint(-2.0F, 12.0F, 0.0F);
			leftFood.cubeList.add(new ModelBox(leftFood, 0, 33, -2.0F, 0.0F, -2.0F, 4, 6, 4, 0.0F, false));

			rigtfoot = new ModelRenderer(this);
			rigtfoot.setRotationPoint(2.0F, 12.0F, 0.0F);
			rigtfoot.cubeList.add(new ModelBox(rigtfoot, 0, 33, -2.0F, 0.0F, -2.0F, 4, 6, 4, 0.0F, true));

			bb_main = new ModelRenderer(this);
			bb_main.setRotationPoint(0.0F, 24.0F, 0.0F);
		}

		@Override
		public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
			head.render(f5);
			body.render(f5);
			armLeft.render(f5);
			armRight.render(f5);
			leftFood.render(f5);
			rigtfoot.render(f5);
			bb_main.render(f5);
		}

		public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
			modelRenderer.rotateAngleX = x;
			modelRenderer.rotateAngleY = y;
			modelRenderer.rotateAngleZ = z;
		}

		@Override
		public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
			super.setRotationAngles(f, f1, f2, f3, f4, f5, e);

			// Движение ног
			this.leftFood.rotateAngleX = MathHelper.cos(f * 1.0F) * -1.0F * f1;
			this.rigtfoot.rotateAngleX = MathHelper.cos(f * 1.0F) * 1.0F * f1;

			// Движение рук (амплитуда 0.6662)
			// Прибавляем к базовому углу из
			// конструктора
			this.armRight.rotateAngleX = 0.4278F + (MathHelper.cos(f * 0.6662F + (float) Math.PI) * f1);
			this.armLeft.rotateAngleX = -0.1309F + (MathHelper.cos(f * 0.6662F) * f1);

			// Поворот головы за игроком
			this.head.rotateAngleY = f3 / (180F / (float) Math.PI);
			this.head.rotateAngleX = f4 / (180F / (float) Math.PI);
		}
	}
}
