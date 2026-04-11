
package net.mcreator.krdmod.item;

@ElementsKrdModMod.ModElement.Tag
public class ItemOmliet extends ElementsKrdModMod.ModElement {

	@GameRegistry.ObjectHolder("krd_mod:omliet")
	public static final Item block = null;

	public ItemOmliet(ElementsKrdModMod instance) {
		super(instance, 59);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemFoodCustom());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("krd_mod:omliet", "inventory"));
	}

	public static class ItemFoodCustom extends ItemFood {

		public ItemFoodCustom() {
			super(4, 0.3f, false);
			setUnlocalizedName("omliet");
			setRegistryName("omliet");

			setCreativeTab(TabFoods.tab);
			setMaxStackSize(64);
		}

		@Override
		public EnumAction getItemUseAction(ItemStack par1ItemStack) {
			return EnumAction.EAT;
		}

	}

}
