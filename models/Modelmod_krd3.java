// Made with Blockbench 5.1.3
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class Modelmod_krd3 extends ModelBase {
	private final ModelRenderer body;
	private final ModelRenderer lower;
	private final ModelRenderer cube_r1;
	private final ModelRenderer upper;
	private final ModelRenderer cube_r2;
	private final ModelRenderer left_arm2;
	private final ModelRenderer lower_r1;
	private final ModelRenderer left_arm;
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
	private final ModelRenderer hands;
	private final ModelRenderer head;
	private final ModelRenderer head2;
	private final ModelRenderer cube_r7;
	private final ModelRenderer cube_r8;
	private final ModelRenderer cube_r9;
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

	public Modelmod_krd3() {
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
		cube_r1.cubeList.add(new ModelBox(cube_r1, 40, 40, -5.6125F, -0.55F, -1.475F, 6, 9, 6, 0.2F, true));
		cube_r1.cubeList.add(new ModelBox(cube_r1, 40, 40, 0.3875F, -0.55F, -1.475F, 6, 9, 6, 0.2F, false));

		upper = new ModelRenderer(this);
		upper.setRotationPoint(1.75F, -54.375F, 5.35F);
		body.addChild(upper);

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(0.0F, 21.25F, 0.0F);
		upper.addChild(cube_r2);
		setRotationAngle(cube_r2, 0.0524F, 0.0F, 0.0F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 0, 75, -0.0625F, -0.5F, -2.75F, 6, 9, 6, 0.0F, false));
		cube_r2.cubeList.add(new ModelBox(cube_r2, 0, 75, -6.0625F, -0.5F, -2.75F, 6, 9, 6, 0.0F, true));

		left_arm2 = new ModelRenderer(this);
		left_arm2.setRotationPoint(7.8104F, -31.43F, 6.325F);
		body.addChild(left_arm2);
		setRotationAngle(left_arm2, 0.0F, 0.0F, -0.1309F);
		left_arm2.cubeList.add(new ModelBox(left_arm2, 0, 46, 0.1763F, -2.1412F, -4.0104F, 6, 11, 6, 0.0F, false));

		lower_r1 = new ModelRenderer(this);
		lower_r1.setRotationPoint(2.7288F, 8.2207F, -0.9329F);
		left_arm2.addChild(lower_r1);
		setRotationAngle(lower_r1, -0.384F, 0.0F, 0.0F);
		lower_r1.cubeList.add(new ModelBox(lower_r1, 42, 63, -2.565F, -0.6251F, -3.1015F, 6, 7, 6, 0.0F, false));

		left_arm = new ModelRenderer(this);
		left_arm.setRotationPoint(-0.1041F, -22.2255F, 0.0F);
		left_arm2.addChild(left_arm);
		setRotationAngle(left_arm, 0.0611F, 0.0F, -0.1745F);

		shipy2 = new ModelRenderer(this);
		shipy2.setRotationPoint(-9.375F, -25.025F, -5.1625F);
		left_arm.addChild(shipy2);

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(17.05F, 30.5F, 5.9875F);
		shipy2.addChild(bone2);
		setRotationAngle(bone2, 0.0F, 0.0F, 1.501F);

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone2.addChild(cube_r3);
		setRotationAngle(cube_r3, 0.0F, 0.0F, 0.0436F);
		cube_r3.cubeList.add(new ModelBox(cube_r3, 0, 24, 16.106F, 2.8485F, -3.8816F, 2, 3, 2, 0.0F, false));
		cube_r3.cubeList.add(new ModelBox(cube_r3, 0, 24, 20.3844F, 3.9855F, -4.6526F, 2, 3, 2, 0.0F, false));

		cube_r4 = new ModelRenderer(this);
		cube_r4.setRotationPoint(-0.1375F, -2.75F, 0.1625F);
		bone2.addChild(cube_r4);
		setRotationAngle(cube_r4, 0.0F, 0.0F, 0.0436F);
		cube_r4.cubeList.add(new ModelBox(cube_r4, 7, 32, 16.7935F, 3.836F, -3.5066F, 1, 2, 1, 0.0F, false));
		cube_r4.cubeList.add(new ModelBox(cube_r4, 7, 31, 21.0719F, 4.673F, -4.2776F, 1, 2, 1, 0.0F, false));

		right_arm = new ModelRenderer(this);
		right_arm.setRotationPoint(-2.7987F, -32.6149F, 5.4212F);
		body.addChild(right_arm);
		setRotationAngle(right_arm, 0.0436F, -0.0008F, 0.192F);
		right_arm.cubeList.add(new ModelBox(right_arm, 0, 46, -7.4786F, -0.6842F, -2.9585F, 6, 11, 6, 0.0F, true));

		lower_r2 = new ModelRenderer(this);
		lower_r2.setRotationPoint(-6.3025F, 9.5506F, 0.0577F);
		right_arm.addChild(lower_r2);
		setRotationAngle(lower_r2, -0.384F, 0.0F, 0.0F);
		lower_r2.cubeList.add(new ModelBox(lower_r2, 42, 63, -1.2177F, -0.189F, -2.9339F, 6, 7, 6, 0.0F, true));

		shipy3 = new ModelRenderer(this);
		shipy3.setRotationPoint(-2.7468F, 8.8017F, -0.5353F);
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
		cube_r5.cubeList.add(new ModelBox(cube_r5, 0, 24, -5.2585F, 3.7919F, -1.7772F, 2, 3, 2, 0.0F, false));
		cube_r5.cubeList.add(new ModelBox(cube_r5, 6, 31, -4.8284F, 6.738F, -1.2397F, 1, 2, 1, 0.0F, false));

		bone = new ModelRenderer(this);
		bone.setRotationPoint(1.9146F, 0.9255F, 0.1447F);
		bone6.addChild(bone);
		setRotationAngle(bone, 0.0F, 0.0F, 0.0873F);

		cube_r6 = new ModelRenderer(this);
		cube_r6.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone.addChild(cube_r6);
		setRotationAngle(cube_r6, 0.3058F, 0.0057F, 0.1744F);
		cube_r6.cubeList.add(new ModelBox(cube_r6, 0, 24, -7.2808F, 3.3998F, -0.2474F, 2, 3, 2, 0.0F, false));
		cube_r6.cubeList.add(new ModelBox(cube_r6, 6, 31, -6.8506F, 6.3459F, 0.2901F, 1, 2, 1, 0.0F, false));

		hands = new ModelRenderer(this);
		hands.setRotationPoint(0.125F, 0.0F, 0.0F);
		body.addChild(hands);

		head = new ModelRenderer(this);
		head.setRotationPoint(1.2625F, 24.0F, 0.0F);

		head2 = new ModelRenderer(this);
		head2.setRotationPoint(-1.0125F, -58.625F, 0.0F);
		head.addChild(head2);
		head2.cubeList.add(new ModelBox(head2, 1, 23, -3.625F, 14.0F, 0.25F, 5, 11, 11, 0.0F, true));
		head2.cubeList.add(new ModelBox(head2, 1, 23, 1.325F, 14.0F, 0.25F, 5, 11, 11, 0.0F, false));

		cube_r7 = new ModelRenderer(this);
		cube_r7.setRotationPoint(6.6375F, 23.25F, 0.875F);
		head2.addChild(cube_r7);
		setRotationAngle(cube_r7, 0.0F, 0.0F, 0.2182F);
		cube_r7.cubeList.add(new ModelBox(cube_r7, 3, 65, -1.25F, -1.0F, -0.125F, 6, 2, 0, 0.0F, false));

		cube_r8 = new ModelRenderer(this);
		cube_r8.setRotationPoint(1.2625F, 23.45F, 0.0F);
		head2.addChild(cube_r8);
		setRotationAngle(cube_r8, 0.0F, 0.9599F, 1.5708F);
		cube_r8.cubeList.add(new ModelBox(cube_r8, 3, 65, -1.55F, -1.0F, -0.125F, 7, 2, 0, 0.0F, false));

		cube_r9 = new ModelRenderer(this);
		cube_r9.setRotationPoint(-2.9875F, 23.0F, 1.125F);
		head2.addChild(cube_r9);
		setRotationAngle(cube_r9, 0.0503F, -0.1209F, -0.4394F);
		cube_r9.cubeList.add(new ModelBox(cube_r9, 1, 65, -4.296F, -1.0789F, 0.1523F, 7, 2, 0, 0.0F, true));

		hair = new ModelRenderer(this);
		hair.setRotationPoint(0.0F, 0.0F, 0.0F);
		head.addChild(hair);
		hair.cubeList.add(new ModelBox(hair, 79, 66, -0.6375F, -45.0F, 0.25F, 7, 19, 12, 0.0F, false));
		hair.cubeList.add(new ModelBox(hair, 79, 66, -5.5125F, -45.0F, 0.25F, 7, 19, 12, 0.0F, true));

		shipu = new ModelRenderer(this);
		shipu.setRotationPoint(0.0F, 0.0F, 0.0F);
		head.addChild(shipu);

		cube_r10 = new ModelRenderer(this);
		cube_r10.setRotationPoint(-3.6993F, -47.4469F, 2.1477F);
		shipu.addChild(cube_r10);
		setRotationAngle(cube_r10, 0.0F, -0.4363F, 0.0F);
		cube_r10.cubeList.add(new ModelBox(cube_r10, 6, 31, -0.475F, -2.2813F, -0.4813F, 1, 2, 1, 0.0F, false));
		cube_r10.cubeList.add(new ModelBox(cube_r10, 0, 24, -1.025F, -0.2188F, -1.0188F, 2, 3, 2, 0.0F, false));

		shipu3 = new ModelRenderer(this);
		shipu3.setRotationPoint(3.9257F, -42.9469F, 2.1477F);
		head.addChild(shipu3);
		setRotationAngle(shipu3, -0.0532F, 0.6102F, -0.0305F);

		cube_r11 = new ModelRenderer(this);
		cube_r11.setRotationPoint(0.0F, 0.0F, 0.0F);
		shipu3.addChild(cube_r11);
		setRotationAngle(cube_r11, 0.0F, -0.4363F, 0.0F);
		cube_r11.cubeList.add(new ModelBox(cube_r11, 6, 31, -0.4409F, -6.777F, -0.6746F, 1, 2, 1, 0.0F, false));
		cube_r11.cubeList.add(new ModelBox(cube_r11, 0, 24, -0.9909F, -4.7145F, -1.2121F, 2, 3, 2, 0.0F, false));

		shipu2 = new ModelRenderer(this);
		shipu2.setRotationPoint(0.3007F, -42.9469F, 2.1477F);
		head.addChild(shipu2);
		setRotationAngle(shipu2, 0.0F, 0.3491F, 0.0F);

		cube_r12 = new ModelRenderer(this);
		cube_r12.setRotationPoint(0.0F, 0.0F, 0.0F);
		shipu2.addChild(cube_r12);
		setRotationAngle(cube_r12, 0.0F, -0.4363F, 0.0F);
		cube_r12.cubeList.add(new ModelBox(cube_r12, 6, 31, -0.475F, -6.7813F, -0.4813F, 1, 2, 1, 0.0F, false));
		cube_r12.cubeList.add(new ModelBox(cube_r12, 0, 24, -1.025F, -4.7188F, -1.0188F, 2, 3, 2, 0.0F, false));

		foot = new ModelRenderer(this);
		foot.setRotationPoint(0.0F, 24.0F, 0.0F);

		left = new ModelRenderer(this);
		left.setRotationPoint(4.625F, -17.375F, 4.825F);
		foot.addChild(left);

		left_upp_r1 = new ModelRenderer(this);
		left_upp_r1.setRotationPoint(0.0F, 1.5F, 0.0F);
		left.addChild(left_upp_r1);
		setRotationAngle(left_upp_r1, -0.0869F, -0.0076F, -0.085F);
		left_upp_r1.cubeList.add(new ModelBox(left_upp_r1, 66, 23, -2.875F, -0.375F, -2.3625F, 6, 7, 6, 0.0F, false));

		left_low_r1 = new ModelRenderer(this);
		left_low_r1.setRotationPoint(0.6875F, 7.7875F, -0.2875F);
		left.addChild(left_low_r1);
		setRotationAngle(left_low_r1, -0.0436F, 0.0F, 0.0F);
		left_low_r1.cubeList.add(new ModelBox(left_low_r1, 48, 0, -3.0F, -0.125F, -2.625F, 6, 10, 6, 0.0F, false));

		right = new ModelRenderer(this);
		right.setRotationPoint(-1.25F, -15.8375F, 4.825F);
		foot.addChild(right);
		right.cubeList.add(new ModelBox(right, 48, 0, -3.725F, 6.0875F, -2.8F, 6, 10, 6, 0.0F, true));

		right_r1 = new ModelRenderer(this);
		right_r1.setRotationPoint(0.0F, -0.5F, 0.0F);
		right.addChild(right_r1);
		setRotationAngle(right_r1, -0.0869F, 0.0076F, 0.0888F);
		right_r1.cubeList.add(new ModelBox(right_r1, 66, 23, -3.1305F, 0.125F, -2.2492F, 6, 7, 6, 0.0F, true));
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
		this.left_arm2.rotateAngleX = MathHelper.cos(f * 0.6662F) * f1;
		this.right.rotateAngleX = MathHelper.cos(f * 1.0F) * 1.0F * f1;
		this.left.rotateAngleX = MathHelper.cos(f * 1.0F) * -1.0F * f1;
	}
}