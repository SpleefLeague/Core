package com.spleefleague.core.menus;

import static com.spleefleague.core.utils.inventorymenu.InventoryMenuAPI.item;
import static com.spleefleague.core.utils.inventorymenu.InventoryMenuAPI.menu;

import java.util.Random;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import com.spleefleague.core.utils.inventorymenu.InventoryMenu;
import com.spleefleague.core.utils.inventorymenu.InventoryMenuTemplate;

public class InventoryMenuTemplateRepository {
	
	public static InventoryMenuTemplate devMenu;
	

	
	public static void initTemplates(){
	
		
		
		devMenu = menu()
					.title("DevMenu")
					.displayIcon(Material.SIGN)
					.displayName("DevMenu")
					.description("A selection of")
					.description("more or less important")
					.description("Dev\"tools\"")
					
					//Testing purposes of course
					.component(8,1, item()
							.displayName("Firework")
							.displayIcon(Material.FIREWORK)
							.description("Fires a random firework")
							.description("at the players location")
							.onClick(event ->{
								Player p = event.getPlayer();
								Firework fw = (Firework) p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
								FireworkMeta fwm = fw.getFireworkMeta();
								
								Random r = new Random();
								Type type;
								
								switch(r.nextInt(5)){
									case 0: type = Type.BALL;
											break;
									case 1: type = Type.BALL_LARGE;
											break;
									case 2: type = Type.BURST;
											break;
									case 3: type = Type.CREEPER;
											break;
									case 4: type = Type.STAR;
											break;
									default: type = Type.BALL;
								}
									
								FireworkEffect fwe = FireworkEffect.builder()
								.flicker(r.nextBoolean())
								.withColor(Color.fromRGB(r.nextInt(256), r.nextInt(256), r.nextInt(256)))
								.withFade(Color.fromRGB(r.nextInt(256), r.nextInt(256), r.nextInt(256)))
								.with(type)
								.trail(r.nextBoolean())
								.build();
							
								fwm.addEffect(fwe);
								fwm.setPower(r.nextInt(3) + 1);
								
								fw.setFireworkMeta(fwm);
							}))
					.build();
		
	}
	
	
	public static void showDevMenu(Player p){
		InventoryMenu menu = devMenu.constructFor(p);
		
		menu.open(p);
		
	}
}
