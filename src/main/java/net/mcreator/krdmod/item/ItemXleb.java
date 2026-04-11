
package net.mcreator.krdmod.item;

@ElementsKrdModMod.ModElement.Tag
public class ItemXleb extends ElementsKrdModMod.ModElement {

	@GameRegistry.ObjectHolder("krd_mod:xleb")
	public static final Item block = null;

	public ItemXleb(ElementsKrdModMod instance) {
		super(instance, 43);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemFoodCustom());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("krd_mod:xleb", "inventory"));
	}

	public static class ItemFoodCustom extends ItemFood {

		public ItemFoodCustom() {
			super(4, 0.3f, false);
			setUnlocalizedName("xleb");
			setRegistryName("xleb");

			setCreativeTab(TabFoods.tab);
			setMaxStackSize(64);
		}

		@Override
		public EnumAction getItemUseAction(ItemStack par1ItemStack) {
			return EnumAction.EAT;
		}

	}

}
