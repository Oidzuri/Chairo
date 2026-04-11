
package net.mcreator.krdmod.item;

@ElementsKrdModMod.ModElement.Tag
public class ItemDango extends ElementsKrdModMod.ModElement {

	@GameRegistry.ObjectHolder("krd_mod:dango")
	public static final Item block = null;

	public ItemDango(ElementsKrdModMod instance) {
		super(instance, 40);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemFoodCustom());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("krd_mod:dango", "inventory"));
	}

	public static class ItemFoodCustom extends ItemFood {

		public ItemFoodCustom() {
			super(4, 0.3f, false);
			setUnlocalizedName("dango");
			setRegistryName("dango");

			setCreativeTab(TabFoods.tab);
			setMaxStackSize(64);
		}

		@Override
		public EnumAction getItemUseAction(ItemStack par1ItemStack) {
			return EnumAction.EAT;
		}

	}

}
