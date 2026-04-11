
package net.mcreator.krdmod.item;

@ElementsKrdModMod.ModElement.Tag
public class ItemIabloko extends ElementsKrdModMod.ModElement {

	@GameRegistry.ObjectHolder("krd_mod:iabloko")
	public static final Item block = null;

	public ItemIabloko(ElementsKrdModMod instance) {
		super(instance, 44);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemFoodCustom());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("krd_mod:iabloko", "inventory"));
	}

	public static class ItemFoodCustom extends ItemFood {

		public ItemFoodCustom() {
			super(4, 0.3f, false);
			setUnlocalizedName("iabloko");
			setRegistryName("iabloko");

			setCreativeTab(TabFoods.tab);
			setMaxStackSize(64);
		}

		@Override
		public EnumAction getItemUseAction(ItemStack par1ItemStack) {
			return EnumAction.EAT;
		}

	}

}
