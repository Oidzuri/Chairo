// Made with Blockbench 5.1.3
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class Modeld2 extends ModelBase {
	private final ModelRenderer head;
	private final ModelRenderer body;
	private final ModelRenderer armright;
	private final ModelRenderer upper_right_r1;
	private final ModelRenderer armleft;
	private final ModelRenderer lower_left_r1;
	private final ModelRenderer leftFood;
	private final ModelRenderer right_foot;

	public Modeld2() {
		textureWidth = 64;
		textureHeight = 64;

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, 24.0F, 0.0F);
		head.cubeList.add(new ModelBox(head, 0, 16, -2.0F, -30.7F, -3.0F, 8, 8, 8, 0.0F, false));
		head.cubeList.add(new ModelBox(head, 32, 16, -2.0F, -30.8F, -3.0F, 8, 8, 8, 0.2F, false));

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 24.0F, 0.0F);
		body.cubeList.add(new ModelBox(body, 16, 32, -2.0F, -22.7F, -1.0F, 8, 12, 4, 0.0F, false));

		armright = new ModelRenderer(this);
		armright.setRotationPoint(-0.9948F, -21.0765F, 0.755F);
		body.addChild(armright);
		armright.cubeList.add(new ModelBox(armright, 40, 33, -5.0612F, 3.7144F, -1.7273F, 4, 7, 4, 0.0F, false));

		upper_right_r1 = new ModelRenderer(this);
		upper_right_r1.setRotationPoint(0.0F, -0.1F, 0.4F);
		armright.addChild(upper_right_r1);
		setRotationAngle(upper_right_r1, 0.0436F, 0.0F, 0.0F);
		upper_right_r1.cubeList
				.add(new ModelBox(upper_right_r1, 40, 34, -5.0591F, -1.4256F, -2.2467F, 4, 6, 4, 0.0F, false));

		armleft = new ModelRenderer(this);
		armleft.setRotationPoint(5.2437F, -21.182F, 1.6929F);
		body.addChild(armleft);
		armleft.cubeList.add(new ModelBox(armleft, 40, 34, 0.5645F, -1.4903F, -2.8129F, 4, 6, 4, 0.0F, true));

		lower_left_r1 = new ModelRenderer(this);
		lower_left_r1.setRotationPoint(2.602F, 4.6584F, -0.9706F);
		armleft.addChild(lower_left_r1);
		setRotationAngle(lower_left_r1, -0.0317F, 0.0025F, -0.0235F);
		lower_left_r1.cubeList
				.add(new ModelBox(lower_left_r1, 40, 33, -2.03F, -0.2352F, -1.7914F, 4, 7, 4, 0.0F, true));

		leftFood = new ModelRenderer(this);
		leftFood.setRotationPoint(4.132F, 12.9136F, 0.7177F);
		leftFood.cubeList.add(new ModelBox(leftFood, 0, 38, -1.9874F, 6.0165F, -1.5819F, 4, 5, 4, 0.0F, true));
		leftFood.cubeList.add(new ModelBox(leftFood, 0, 33, -2.0019F, 0.132F, -1.6588F, 4, 6, 4, 0.0F, true));

		right_foot = new ModelRenderer(this);
		right_foot.setRotationPoint(-0.8035F, 13.0211F, 0.9759F);
		right_foot.cubeList.add(new ModelBox(right_foot, 0, 33, -1.2805F, 0.1576F, -1.9352F, 4, 6, 4, 0.0F, false));
		right_foot.cubeList.add(new ModelBox(right_foot, 0, 38, -1.3154F, 5.7591F, -1.8759F, 4, 5, 4, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		head.render(f5);
		body.render(f5);
		leftFood.render(f5);
		right_foot.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
	}
}