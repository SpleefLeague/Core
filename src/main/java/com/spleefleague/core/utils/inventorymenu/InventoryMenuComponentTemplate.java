package com.spleefleague.core.utils.inventorymenu;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import com.spleefleague.core.utils.function.PlayerToValueMapper;

public abstract class InventoryMenuComponentTemplate<C> {
	
	//private InventoryMenuTemplate parent;

	//Optional -> if exists,components will be overwritten 
	//			by following settings (displayName,displayIcon,displayNumber)
	private ItemStack displayItem;
	private PlayerToValueMapper<ItemStack> displayItemPlayerSpecific;
	
	private String displayName;
	private PlayerToValueMapper<String> displayNamePlayerSpecific;
	
	private MaterialData displayIcon;
	private PlayerToValueMapper<MaterialData> displayIconPlayerSpecific;
    
	private Integer displayNumber;
	private PlayerToValueMapper<Integer> displayNumberPlayerSpecific;
	

	//Forgive me father for i have sinned
	private List<Object> displayDescription;
	
	InventoryMenuComponentTemplate(){	
		displayItem = new ItemStack(Material.STONE);
		
	/*
		this.displayName = "";
		this.displayIcon = new MaterialData(Material.STONE);
		this.displayNumber = 1;
		*/
		
		
		this.displayDescription = new LinkedList<>();
		
		
		
	}
	
	public abstract C construct();
	public abstract C constructFor(Player p);
		
	
	public String getDisplayName() {
		return displayName != null ? displayName : displayItem.getItemMeta().getDisplayName();
	}
	
	public String getDisplayNameFor(Player p){
		return displayNamePlayerSpecific != null ? displayNamePlayerSpecific.toValue(p) : getDisplayName();
	}
	
	String getDisplayNameForWithNull(Player p){
		return displayNamePlayerSpecific != null ? displayNamePlayerSpecific.toValue(p) : displayName;
	}
	
	
	public ItemStack getDisplayItemStack(){
		return constructDisplayItem();
	}
	
	public ItemStack getDisplayItemStackFor(Player p){
		return constructDisplayItemFor(p);
	}
	
	
	protected ItemStack getDisplayItem() {
		return displayItem;
	}
	
	protected ItemStack getDisplayItemFor(Player p) {
		return displayItemPlayerSpecific != null ? displayItemPlayerSpecific.toValue(p) : getDisplayItem();
	}
		
	
	public MaterialData getDisplayIcon(){
		return displayIcon != null  ? displayIcon :  displayItem.getData();
	}
	
	public MaterialData getDisplayIconFor(Player p){
		return displayIconPlayerSpecific != null ? displayIconPlayerSpecific.toValue(p) : getDisplayIcon();
	}
	
	MaterialData getDisplayIconForWitNull(Player p){
		return displayIconPlayerSpecific != null ? displayIconPlayerSpecific.toValue(p) : displayIcon;
		
	}
	

	
	
	public int getDisplayNumber() {
		return displayNumber != null ? displayNumber : displayItem.getAmount();
	}
	
	public int getDisplayNumberFor(Player p) {
		return displayNumberPlayerSpecific != null ? displayNumberPlayerSpecific.toValue(p) : getDisplayNumber();
	}
	
	Integer getDisplayNumberForWithNull(Player p) {
			return displayNumberPlayerSpecific != null ? displayNumberPlayerSpecific.toValue(p) : displayNumber;
	}
	
	

	public List<String> getDisplayDescription() {
		List<String> description  = displayDescription.stream()
				.filter(obj -> obj instanceof String)
				.map(obj -> (String)obj)
				.collect(Collectors.toList());
		
		return description;
	}
	
	public List<String> getDisplayDescriptionFor(Player p) {
		List<String> description = displayDescription.stream()
				.map(obj -> mapObjToString(obj,p))
				.collect(Collectors.toList());
		return description;
	}
		
	protected ItemStack constructDisplayItem(){
		ItemStack is = constructDisplayItemFromValues(displayItem,displayIcon,displayName,displayNumber,getDisplayDescription());
		return is;
	}
	
	protected ItemStack constructDisplayItemFor(Player p){
		ItemStack  is = constructDisplayItemFromValues(getDisplayItemFor(p),getDisplayIconForWitNull(p),
														getDisplayNameForWithNull(p),getDisplayNumberForWithNull(p),
														getDisplayDescriptionFor(p));
		return is;
	}
	
	private String mapObjToString(Object o,Player p){
		if(o instanceof PlayerToValueMapper<?>)
			return (String)((PlayerToValueMapper<?>)o).toValue(p);
		else
			return (String)o;
	}
	
	@SuppressWarnings("deprecation")
	private ItemStack constructDisplayItemFromValues(ItemStack baseStack,MaterialData icon,String name,Integer number,List<String> description){
		ItemStack is = baseStack.clone();
		
		if(icon != null){
			is.setType(icon.getItemType());
			//is.setData() is not working...
			is.getData().setData(icon.getData());
		}
		
		if(number != null){
			is.setAmount(number);
		}
		
		ItemMeta im = is.getItemMeta();
			
		if(name != null)
			im.setDisplayName(name);
		if(!description.isEmpty())
			im.setLore(description);
		
		is.setItemMeta(im);
		
		return is;
	}

	
	void setDisplayItem(ItemStack displayItem){
		this.displayItem = displayItem;
	}
	
	void setDisplayItem(PlayerToValueMapper<ItemStack> displayItemPlayerSpecific){
		this.displayItemPlayerSpecific = displayItemPlayerSpecific;
	}
	
	
	void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	void setDisplayName(PlayerToValueMapper<String> displayNamePlayerSpecific) {
		this.displayNamePlayerSpecific = displayNamePlayerSpecific;
	}
	


	void setDisplayIcon(MaterialData displayIcon) {
		this.displayIcon = displayIcon;
	}

	void setDisplayIcon(PlayerToValueMapper<MaterialData> displayIconPlayerSpecific) {
		this.displayIconPlayerSpecific = displayIconPlayerSpecific;
	}
		

	
	void setDisplayNumber(int displayNumber) {
		this.displayNumber = displayNumber;
	}

	void setDisplayNumber(PlayerToValueMapper<Integer> displayNumberPlayerSpecific) {
		this.displayNumberPlayerSpecific = displayNumberPlayerSpecific;
	}
	

	
	void addDescriptionLine(String line){
		this.displayDescription.add((Object)line);
	}
	
	void addDescriptionLine(PlayerToValueMapper<String> line){
		this.displayDescription.add((Object)line);
	}
}
