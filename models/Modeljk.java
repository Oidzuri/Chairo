// Made with Blockbench 5.1.3
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class Modeljk extends ModelBase {
	private final ModelRenderer body;
	private final ModelRenderer lower;
	private final ModelRenderer cube_r1;
	private final ModelRenderer cube_r2;
	private final ModelRenderer upper;
	private final ModelRenderer cube_r3;
	private final ModelRenderer hands;
	private final ModelRenderer left_arm;
	private final ModelRenderer lower_r1;
	private final ModelRenderer shipy2;
	private final ModelRenderer bone2;
	private final ModelRenderer right_arm;
	private final ModelRenderer lower_r2;
	private final ModelRenderer shipy3;
	private final ModelRenderer bone6;
	private final ModelRenderer bone7;
	private final ModelRenderer bone;
	private final ModelRenderer head;
	private final ModelRenderer cube_r4;
	private final ModelRenderer head2;
	private final ModelRenderer hair;
	private final ModelRenderer foot;
	private final ModelRenderer left;
	private final ModelRenderer left_upp_r1;
	private final ModelRenderer left_low_r1;
	private final ModelRenderer right;
	private final ModelRenderer left_low_r2;
	private final ModelRenderer right_r1;

	public Modeljk() {
		textureWidth = 128;
		textureHeight = 128;

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 24.0F, 0.0F);
		setRotationAngle(body, -0.0419F, 0.0F, 0.0F);

		lower = new ModelRenderer(this);
		lower.setRotationPoint(1.75F, -45.5F, 4.6125F);
		body.addChild(lower);

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(3.2375F, 18.7625F, -2.425F);
		lower.addChild(cube_r1);
		setRotationAngle(cube_r1, 0.0524F, 0.0F, 0.0F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 56, 58, -9.3125F, 3.957F, -6.6191F, 8, 6, 1, 0.2F, true));

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(-0.0625F, 23.7842F, -2.2985F);
		lower.addChild(cube_r2);
		setRotationAngle(cube_r2, 0.0524F, 0.0F, 0.0F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 35, 57, -6.375F, -3.1055F, -5.4691F, 9, 9, 1, 0.2F, false));

		upper = new ModelRenderer(this);
		upper.setRotationPoint(1.75F, -54.375F, 5.35F);
		body.addChild(upper);

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(-1.0125F, 28.5625F, 0.0F);
		upper.addChild(cube_r3);
		setRotationAngle(cube_r3, 0.0524F, 0.0F, 0.0F);
		cube_r3.cubeList.add(new ModelBox(cube_r3, 0, 20, -5.7625F, -5.9931F, -7.8941F, 10, 16, 5, 0.0F, false));

		hands = new ModelRenderer(this);
		hands.setRotationPoint(0.125F, 0.0F, 0.0F);
		body.addChild(hands);

		left_arm = new ModelRenderer(this);
		left_arm.setRotationPoint(3.6585F, -28.5887F, -0.7323F);
		hands.addChild(left_arm);
		setRotationAngle(left_arm, 0.0611F, 0.0F, -0.1745F);
		left_arm.cubeList.add(new ModelBox(left_arm, 30, 35, 0.9369F, -2.1382F, -1.9272F, 5, 10, 5, 0.0F, false));

		lower_r1 = new ModelRenderer(this);
		lower_r1.setRotationPoint(8.4717F, -14.9168F, 6.2878F);
		left_arm.addChild(lower_r1);
		setRotationAngle(lower_r1, -0.384F, 0.0F, 0.0F);
		lower_r1.cubeList.add(new ModelBox(lower_r1, 45, 45, -7.5238F, 22.3182F, 0.5538F, 5, 7, 5, 0.0F, false));

		shipy2 = new ModelRenderer(this);
		shipy2.setRotationPoint(-3.7362F, -49.3879F, 2.0581F);
		left_arm.addChild(shipy2);

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(17.05F, 30.5F, 5.9875F);
		shipy2.addChild(bone2);
		setRotationAngle(bone2, 0.0F, 0.0F, 1.501F);

		right_arm = new ModelRenderer(this);
		right_arm.setRotationPoint(-4.1112F, -28.4274F, 0.1837F);
		hands.addChild(right_arm);
		setRotationAngle(right_arm, 0.0436F, -0.0008F, 0.192F);
		right_arm.cubeList.add(new ModelBox(right_arm, 30, 35, -5.8948F, -2.2459F, -2.8542F, 5, 10, 5, 0.0F, true));

		lower_r2 = new ModelRenderer(this);
		lower_r2.setRotationPoint(-7.5097F, -22.7294F, -0.6956F);
		right_arm.addChild(lower_r2);
		setRotationAngle(lower_r2, -0.384F, 0.0F, 0.0F);
		lower_r2.cubeList.add(new ModelBox(lower_r2, 45, 45, 1.6035F, 27.2008F, 9.0617F, 5, 7, 5, 0.0F, true));

		shipy3 = new ModelRenderer(this);
		shipy3.setRotationPoint(-0.9539F, 5.5216F, 4.7114F);
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

		bone = new ModelRenderer(this);
		bone.setRotationPoint(1.9146F, 0.9255F, 0.1447F);
		bone6.addChild(bone);
		setRotationAngle(bone, 0.0F, 0.0F, 0.0873F);

		head = new ModelRenderer(this);
		head.setRotationPoint(0.075F, -7.1155F, 0.2582F);
		setRotationAngle(head, 0.0489F, 0.0F, 0.0F);

		cube_r4 = new ModelRenderer(this);
		cube_r4.setRotationPoint(-0.7F, -8.1F, -6.7F);
		head.addChild(cube_r4);
		setRotationAngle(cube_r4, -0.3491F, 0.0F, 0.0F);
		cube_r4.cubeList.add(new ModelBox(cube_r4, 0, 0, 0.0F, -1.0F, -1.0F, 1, 1, 3, 0.0F, false));

		head2 = new ModelRenderer(this);
		head2.setRotationPoint(0.175F, -26.8463F, -0.1473F);
		head.addChild(head2);
		head2.cubeList.add(new ModelBox(head2, 0, 0, -5.325F, 16.6678F, -4.8442F, 5, 10, 10, 0.0F, true));
		head2.cubeList.add(new ModelBox(head2, 0, 0, -0.375F, 16.6678F, -4.8442F, 5, 10, 10, 0.0F, false));

		hair = new ModelRenderer(this);
		hair.setRotationPoint(-0.175F, -5.7535F, 0.0335F);
		head.addChild(hair);
		hair.cubeList.add(new ModelBox(hair, 70, 0, -1.0F, -4.5F, -5.0F, 6, 9, 10, 0.03F, false));
		hair.cubeList.add(new ModelBox(hair, 70, 0, -5.0F, -4.5F, -5.0F, 6, 9, 10, 0.03F, true));

		foot = new ModelRenderer(this);
		foot.setRotationPoint(0.0F, 24.0F, 0.45F);
		setRotationAngle(foot, 0.0349F, 0.0F, 0.0F);

		left = new ModelRenderer(this);
		left.setRotationPoint(3.125F, -17.3125F, 0.875F);
		foot.addChild(left);

		left_upp_r1 = new ModelRenderer(this);
		left_upp_r1.setRotationPoint(-0.4808F, 1.747F, -0.1042F);
		left.addChild(left_upp_r1);
		setRotationAngle(left_upp_r1, -0.0872F, -0.0002F, 0.0019F);
		left_upp_r1.cubeList.add(new ModelBox(left_upp_r1, 15, 50, -2.4943F, -0.5F, -2.5002F, 5, 7, 5, 0.0F, false));

		left_low_r1 = new ModelRenderer(this);
		left_low_r1.setRotationPoint(-0.4875F, 8.2947F, -0.6264F);
		left.addChild(left_low_r1);
		setRotationAngle(left_low_r1, -0.0087F, 0.0F, 0.0F);
		left_low_r1.cubeList.add(new ModelBox(left_low_r1, 40, 0, -2.5F, -0.5001F, -2.4651F, 5, 9, 5, 0.0F, false));

		right = new ModelRenderer(this);
		right.setRotationPoint(-3.05F, -17.5125F, 1.05F);
		foot.addChild(right);

		left_low_r2 = new ModelRenderer(this);
		left_low_r2.setRotationPoint(0.225F, 7.7335F, -0.7545F);
		right.addChild(left_low_r2);
		setRotationAngle(left_low_r2, -0.0349F, 0.0F, 0.0F);
		left_low_r2.cubeList.add(new ModelBox(left_low_r2, 40, 0, -2.5F, 0.1F, -2.5F, 5, 8, 5, 0.0F, true));

		right_r1 = new ModelRenderer(this);
		right_r1.setRotationPoint(0.2952F, 1.7225F, -0.397F);
		right.addChild(right_r1);
		setRotationAngle(right_r1, -0.0697F, 0.0013F, 0.0193F);
		right_r1.cubeList.add(new ModelBox(right_r1, 15, 50, -2.442F, -0.501F, -2.4479F, 5, 7, 5, 0.0F, true));
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