package me.Tarsh.PufferParty;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import net.minecraft.server.v1_14_R1.NBTTagList;
import net.milkbowl.vault.economy.EconomyResponse;

public class PufferListener implements Listener{
	PufferParty party;
	
	public PufferListener() {
		party = PufferParty.party;
		
	}
	@EventHandler
	public void onKill(PlayerDeathEvent e) {
		String killed = "" + e.getEntity().getUniqueId();
		Player killer = e.getEntity().getKiller();
		if (PufferParty.playerStatus.get(killed) != null && PufferParty.playerStatus.get(killed)) {
			killer.sendMessage(String.format(ChatColor.AQUA + "You have slain a Puffer Party player"));
			EconomyResponse r = PufferParty.econ.depositPlayer(killer, PufferParty.reward);
			killer.sendMessage(String.format(ChatColor.GREEN + "+" + PufferParty.reward));
			e.getEntity().sendMessage(ChatColor.RED + "You have died during a puffer party");
			r = PufferParty.econ.withdrawPlayer(e.getEntity(), PufferParty.penalty);
			e.getEntity().sendMessage(String.format(ChatColor.RED + "-" + PufferParty.penalty));
		}
		
		
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerUse(PlayerInteractEvent event){
	    Player player = event.getPlayer();
	    if(player.getItemInHand().getType() == Material.PUFFERFISH && player.getItemInHand().getItemMeta().hasLore()){
			if (PufferParty.playerStatus.get("" + player.getUniqueId()) != null && PufferParty.playerStatus.get("" + player.getUniqueId())) {
				player.sendMessage(String.format(ChatColor.AQUA + "There is already an active Pufferfish Party!"));
				return;
			}
	
			player.sendMessage(ChatColor.AQUA + "Puffer Party Started!");
			player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
			player.updateInventory();
			
			PufferParty.playerStatus.put("" + player.getUniqueId(), true);
			
			PufferParty.party.getServer().dispatchCommand(PufferParty.party.getServer().getConsoleSender(), "tab player " + player.getName() + " tagsuffix &e&l PufferParty &d✪");
			PufferParty.party.getServer().dispatchCommand(PufferParty.party.getServer().getConsoleSender(), "shop addmodifier global " + player.getName() + " 1.2 sell");

		

			Timer timer = new Timer();
	
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					PufferParty.playerStatus.put("" + player.getUniqueId(), false);
					player.sendMessage(ChatColor.AQUA + "Puffer Party has ended!");
					PufferParty.party.getServer().dispatchCommand(PufferParty.party.getServer().getConsoleSender(), "shop addmodifier global " + player.getName() + " 1 sell");
					PufferParty.party.getServer().dispatchCommand(PufferParty.party.getServer().getConsoleSender(), "tab player " + player.getName() + " tagsuffix");
					
				}
			}, 60 * 60 * 1000);
	    }
	    
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
	    if (PufferParty.playerStatus.get("" + e.getPlayer().getUniqueId()) == null) {
	    	PufferParty.server.dispatchCommand(PufferParty.server.getConsoleSender(), "shop addmodifier global " + e.getPlayer().getName() + " 1 sell");
	    	PufferParty.server.dispatchCommand(PufferParty.server.getConsoleSender(), "tab player " + e.getPlayer().getName() + " tagsuffix");
	    }
	}
}
