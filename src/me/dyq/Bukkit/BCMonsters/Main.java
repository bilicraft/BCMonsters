package me.dyq.Bukkit.BCMonsters;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
	
	public static Map<String,UserData> savedata = new HashMap<String,UserData>(Bukkit.getServer().getMaxPlayers());  
	private Random rand = new Random();
	
	@Override
	public void onEnable()
	{
		this.getServer().getPluginManager().registerEvents(this, this);
	}
	
	@Override
	public void onDisable()
	{
		
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e)
	{
		if(e.getEntity() instanceof Monster && e.getDamager() instanceof Player)
		{
			Player p = (Player)e.getDamager();
			Monster m = (Monster)e.getEntity();
			Location loc = p.getLocation();
			UserData ud = getUser(p.getName());
			Location sloc = ud.getLocation();
			if(sloc != null && loc.getWorld().equals(sloc.getWorld()) && 
					loc.getBlockX() == sloc.getBlockX() && loc.getBlockY() == sloc.getBlockY() && loc.getBlockZ() == sloc.getBlockZ())
			{
				ud.addAttack();
				if(ud.getAttack() > 10)
				{
					if(ud.announced == false)
					{
						p.sendMessage("怪物们看你不爽了, 小心被报复...");
						ud.announced = true;
					}
					if( ud.getAttack()+rand.nextInt(200) > 220 )
					{
						p.teleport(m);
						ud.resetWithLocation(loc);
						Bukkit.getServer().getLogger().info("teleport "+p.getName()+" to "+m);
					}
				}
			}
			else
			{
				ud.resetWithLocation(loc);
				
				if(rand.nextInt(100) > 90)
				{
					Location tpl = p.getLocation();
					tpl.setX(tpl.getX()+rand.nextInt(2)-1);
					//tpl.setY(tpl.getY()+rand.nextInt(2)-1);
					tpl.setZ(tpl.getZ()+rand.nextInt(2)-1);
					m.teleport(tpl);
					Bukkit.getServer().getLogger().info("teleport "+m+" to "+p.getName());
				}
			}
			
			
			
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		savedata.remove(e.getPlayer().getName());
	}
	
	public static class UserData
	{
		private Location lastloc;
		private int attack = 0;
		public boolean announced = false;
		
		public void setLocation(Location loc)
		{
			this.lastloc = loc;
		}
		
		public Location getLocation()
		{
			return this.lastloc;
		}
		
		public void addAttack()
		{
			this.attack++;
		}
		
		public int getAttack()
		{
			return this.attack; 
		}
		
		public void setAttack(int attack)
		{
			this.attack = attack;
		}
		
		public void resetWithLocation(Location loc)
		{
			this.announced = false;
			this.lastloc = loc;
			this.attack = 0;
		}
	}
	
	private static UserData getUser(String pname)
	{
		UserData ud;
		ud = savedata.get(pname);
		if(ud == null)
		{
			ud = new UserData();
			savedata.put(pname, ud);
		}
		return ud;
	}

}
