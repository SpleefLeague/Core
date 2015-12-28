/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.listeners;

import com.comphenix.packetwrapper.WrapperPlayServerAnimation;
import com.comphenix.packetwrapper.WrapperPlayServerAttachEntity;
import com.comphenix.packetwrapper.WrapperPlayServerBed;
import com.comphenix.packetwrapper.WrapperPlayServerBlockBreakAnimation;
import com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import com.comphenix.packetwrapper.WrapperPlayServerEntityEffect;
import com.comphenix.packetwrapper.WrapperPlayServerEntityEquipment;
import com.comphenix.packetwrapper.WrapperPlayServerEntityHeadRotation;
import com.comphenix.packetwrapper.WrapperPlayServerEntityLook;
import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.packetwrapper.WrapperPlayServerEntityMoveLook;
import com.comphenix.packetwrapper.WrapperPlayServerEntityStatus;
import com.comphenix.packetwrapper.WrapperPlayServerEntityTeleport;
import com.comphenix.packetwrapper.WrapperPlayServerEntityVelocity;
import com.comphenix.packetwrapper.WrapperPlayServerNamedEntitySpawn;
import com.comphenix.packetwrapper.WrapperPlayServerRelEntityMove;
import com.comphenix.packetwrapper.WrapperPlayServerRemoveEntityEffect;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntityLiving;
import static com.comphenix.protocol.PacketType.Play.Server.ANIMATION;
import static com.comphenix.protocol.PacketType.Play.Server.ATTACH_ENTITY;
import static com.comphenix.protocol.PacketType.Play.Server.BED;
import static com.comphenix.protocol.PacketType.Play.Server.BLOCK_BREAK_ANIMATION;
import static com.comphenix.protocol.PacketType.Play.Server.ENTITY_EFFECT;
import static com.comphenix.protocol.PacketType.Play.Server.ENTITY_EQUIPMENT;
import static com.comphenix.protocol.PacketType.Play.Server.ENTITY_HEAD_ROTATION;
import static com.comphenix.protocol.PacketType.Play.Server.ENTITY_LOOK;
import static com.comphenix.protocol.PacketType.Play.Server.ENTITY_METADATA;
import static com.comphenix.protocol.PacketType.Play.Server.ENTITY_MOVE_LOOK;
import static com.comphenix.protocol.PacketType.Play.Server.ENTITY_STATUS;
import static com.comphenix.protocol.PacketType.Play.Server.ENTITY_TELEPORT;
import static com.comphenix.protocol.PacketType.Play.Server.ENTITY_VELOCITY;
import static com.comphenix.protocol.PacketType.Play.Server.NAMED_ENTITY_SPAWN;
import static com.comphenix.protocol.PacketType.Play.Server.REL_ENTITY_MOVE;
import static com.comphenix.protocol.PacketType.Play.Server.REMOVE_ENTITY_EFFECT;
import static com.comphenix.protocol.PacketType.Play.Server.SPAWN_ENTITY_LIVING;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.spleefleague.core.SpleefLeague;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 *
 * @author Jonas
 */
public class VisibilityHandler implements Listener {

    private PacketAdapter vc1, vc2, vc3, vc4, vc5, vc6, vc7, vc8, vc9, vc10, vc11, vc12, vc13, vc14, vc15, vc16, vc17;
    
    private VisibilityHandler() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            hidden.put(player.getUniqueId(), new HashSet<>());
        }
        initPacketListeners();
    }

    private void initPacketListeners() {
        vc1 = new PacketAdapter(SpleefLeague.getInstance(), ListenerPriority.NORMAL, ENTITY_EQUIPMENT) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                
            }

            @Override
            public void onPacketSending(PacketEvent event) {
                WrapperPlayServerEntityEquipment wrapper = new WrapperPlayServerEntityEquipment(event.getPacket());
                if(getHidden(event.getPlayer().getUniqueId()).contains(wrapper.getEntityID())) {
                    event.setCancelled(true);
                }
            }
        };
        vc2 = new PacketAdapter(SpleefLeague.getInstance(), ListenerPriority.NORMAL, BED) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                
            }

            @Override
            public void onPacketSending(PacketEvent event) {
                WrapperPlayServerBed wrapper = new WrapperPlayServerBed(event.getPacket());
                if(getHidden(event.getPlayer().getUniqueId()).contains(wrapper.getEntityID())) {
                    event.setCancelled(true);
                }
            }
        };
        vc3 = new PacketAdapter(SpleefLeague.getInstance(), ListenerPriority.NORMAL, ANIMATION) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                
            }

            @Override
            public void onPacketSending(PacketEvent event) {
                WrapperPlayServerAnimation wrapper = new WrapperPlayServerAnimation(event.getPacket());
                if(getHidden(event.getPlayer().getUniqueId()).contains(wrapper.getEntityID())) {
                    event.setCancelled(true);
                }
            }
        };
        vc4 = new PacketAdapter(SpleefLeague.getInstance(), ListenerPriority.NORMAL, NAMED_ENTITY_SPAWN) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                
            }

            @Override
            public void onPacketSending(PacketEvent event) {
                WrapperPlayServerNamedEntitySpawn wrapper = new WrapperPlayServerNamedEntitySpawn(event.getPacket());
                if(getHidden(event.getPlayer().getUniqueId()).contains(wrapper.getEntityID())) {
                    event.setCancelled(true);
                }
            }
        };
        vc5 = new PacketAdapter(SpleefLeague.getInstance(), ListenerPriority.NORMAL, SPAWN_ENTITY_LIVING) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                
            }

            @Override
            public void onPacketSending(PacketEvent event) {
                WrapperPlayServerSpawnEntityLiving wrapper = new WrapperPlayServerSpawnEntityLiving(event.getPacket());
                if(getHidden(event.getPlayer().getUniqueId()).contains(wrapper.getEntityID())) {
                    event.setCancelled(true);
                }
            }
        };
        vc6 = new PacketAdapter(SpleefLeague.getInstance(), ListenerPriority.NORMAL, ENTITY_VELOCITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                
            }

            @Override
            public void onPacketSending(PacketEvent event) {
                WrapperPlayServerEntityVelocity wrapper = new WrapperPlayServerEntityVelocity(event.getPacket());
                if(getHidden(event.getPlayer().getUniqueId()).contains(wrapper.getEntityID())) {
                    event.setCancelled(true);
                }
            }
        };
        vc7 = new PacketAdapter(SpleefLeague.getInstance(), ListenerPriority.NORMAL, REL_ENTITY_MOVE) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                
            }

            @Override
            public void onPacketSending(PacketEvent event) {
                WrapperPlayServerRelEntityMove wrapper = new WrapperPlayServerRelEntityMove(event.getPacket());
                if(getHidden(event.getPlayer().getUniqueId()).contains(wrapper.getEntityID())) {
                    event.setCancelled(true);
                }
            }
        };
        vc8 = new PacketAdapter(SpleefLeague.getInstance(), ListenerPriority.NORMAL, ENTITY_LOOK) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                
            }

            @Override
            public void onPacketSending(PacketEvent event) {
                WrapperPlayServerEntityLook wrapper = new WrapperPlayServerEntityLook(event.getPacket());
                if(getHidden(event.getPlayer().getUniqueId()).contains(wrapper.getEntityID())) {
                    event.setCancelled(true);
                }
            }//ENTITY_MOVE_LOOK
        };
        vc9 = new PacketAdapter(SpleefLeague.getInstance(), ListenerPriority.NORMAL, ENTITY_MOVE_LOOK) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                
            }

            @Override
            public void onPacketSending(PacketEvent event) {
                WrapperPlayServerEntityMoveLook wrapper = new WrapperPlayServerEntityMoveLook(event.getPacket());
                if(getHidden(event.getPlayer().getUniqueId()).contains(wrapper.getEntityID())) {
                    event.setCancelled(true);
                }
            }
        };
        vc10 = new PacketAdapter(SpleefLeague.getInstance(), ListenerPriority.NORMAL, ENTITY_TELEPORT) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                
            }

            @Override
            public void onPacketSending(PacketEvent event) {
                WrapperPlayServerEntityTeleport wrapper = new WrapperPlayServerEntityTeleport(event.getPacket());
                if(getHidden(event.getPlayer().getUniqueId()).contains(wrapper.getEntityID())) {
                    event.setCancelled(true);
                }
            }
        };
        vc11 = new PacketAdapter(SpleefLeague.getInstance(), ListenerPriority.NORMAL, ENTITY_HEAD_ROTATION) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                
            }

            @Override
            public void onPacketSending(PacketEvent event) {
                WrapperPlayServerEntityHeadRotation wrapper = new WrapperPlayServerEntityHeadRotation(event.getPacket());
                if(getHidden(event.getPlayer().getUniqueId()).contains(wrapper.getEntityID())) {
                    event.setCancelled(true);
                }
            }
        };
        vc12 = new PacketAdapter(SpleefLeague.getInstance(), ListenerPriority.NORMAL, ENTITY_STATUS) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                
            }

            @Override
            public void onPacketSending(PacketEvent event) {
                WrapperPlayServerEntityStatus wrapper = new WrapperPlayServerEntityStatus(event.getPacket());
                if(getHidden(event.getPlayer().getUniqueId()).contains(wrapper.getEntityID())) {
                    event.setCancelled(true);
                }
            }
        };
        vc13 = new PacketAdapter(SpleefLeague.getInstance(), ListenerPriority.NORMAL, ATTACH_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                
            }

            @Override
            public void onPacketSending(PacketEvent event) {
                WrapperPlayServerAttachEntity wrapper = new WrapperPlayServerAttachEntity(event.getPacket());
                if(getHidden(event.getPlayer().getUniqueId()).contains(wrapper.getEntityID())) {
                    event.setCancelled(true);
                }
            }
        };
        vc14 = new PacketAdapter(SpleefLeague.getInstance(), ListenerPriority.NORMAL, ENTITY_METADATA) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                
            }

            @Override
            public void onPacketSending(PacketEvent event) {
                WrapperPlayServerEntityMetadata wrapper = new WrapperPlayServerEntityMetadata(event.getPacket());
                if(getHidden(event.getPlayer().getUniqueId()).contains(wrapper.getEntityID())) {
                    event.setCancelled(true);
                }
            }
        };
        vc15 = new PacketAdapter(SpleefLeague.getInstance(), ListenerPriority.NORMAL, ENTITY_EFFECT) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                
            }

            @Override
            public void onPacketSending(PacketEvent event) {
                WrapperPlayServerEntityEffect wrapper = new WrapperPlayServerEntityEffect(event.getPacket());
                if(getHidden(event.getPlayer().getUniqueId()).contains(wrapper.getEntityID())) {
                    event.setCancelled(true);
                }
            }
        };
        vc16 = new PacketAdapter(SpleefLeague.getInstance(), ListenerPriority.NORMAL, REMOVE_ENTITY_EFFECT) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                
            }

            @Override
            public void onPacketSending(PacketEvent event) {
                WrapperPlayServerRemoveEntityEffect wrapper = new WrapperPlayServerRemoveEntityEffect(event.getPacket());
                if(getHidden(event.getPlayer().getUniqueId()).contains(wrapper.getEntityID())) {
                    event.setCancelled(true);
                }
            }
        };
        vc17 = new PacketAdapter(SpleefLeague.getInstance(), ListenerPriority.NORMAL, BLOCK_BREAK_ANIMATION) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                
            }

            @Override
            public void onPacketSending(PacketEvent event) {
                WrapperPlayServerBlockBreakAnimation wrapper = new WrapperPlayServerBlockBreakAnimation(event.getPacket());
                if(getHidden(event.getPlayer().getUniqueId()).contains(wrapper.getEntityID())) {
                    event.setCancelled(true);
                }
            }
        };
        manager.addPacketListener(vc1);
        manager.addPacketListener(vc2);
        manager.addPacketListener(vc3);
        manager.addPacketListener(vc4);
        manager.addPacketListener(vc5);
        manager.addPacketListener(vc6);
        manager.addPacketListener(vc7);
        manager.addPacketListener(vc8);
        manager.addPacketListener(vc9);
        manager.addPacketListener(vc10);
        manager.addPacketListener(vc11);
        manager.addPacketListener(vc12);
        manager.addPacketListener(vc13);
        manager.addPacketListener(vc14);
        manager.addPacketListener(vc15);
        manager.addPacketListener(vc16);
        manager.addPacketListener(vc17);
    }
    
    @EventHandler
    public void onQuit(PlayerJoinEvent event) {
        hidden.remove(event.getPlayer().getUniqueId());
    }
    
    private static final Map<UUID, Set<Integer>> hidden = new HashMap<>();

    private static final ProtocolManager manager;
    private static VisibilityHandler instance;
    
    public static void hide(Player toHide, Player... players) {
        destroyEntity(toHide, players);
        for(Player player : players) {
            hidden.get(player.getUniqueId()).add(toHide.getEntityId());
        }
    }
    
    public static void hide(Player toHide, Collection<Player> players) {
        destroyEntity(toHide, players);
        for(Player player : players) {
            hidden.get(player.getUniqueId()).add(toHide.getEntityId());
        }
    }
    
    public static void show(Player toShow, Player... players) {
        for(Player player : players) {
            hidden.get(player.getUniqueId()).remove(toShow.getEntityId());
        }
        manager.updateEntity(toShow, Arrays.asList(players));
    }
    
    public static void show(Player toShow, List<Player> players) {
        for(Player player : players) {
            hidden.get(player.getUniqueId()).remove(toShow.getEntityId());
        }
        manager.updateEntity(toShow, players);
    }
    
    private static boolean canSee(UUID seeing, int id) {
        return !getHidden(seeing).contains(id);
    }
    
    public static boolean canSee(Player seeing, Player target) {
        return canSee(seeing.getUniqueId(), target.getEntityId());
    }
    
    private static Set<Integer> getHidden(UUID uuid) {
        Set<Integer> result;
        if(!hidden.containsKey(uuid)) {
            result = new HashSet<>();
            hidden.put(uuid, result);
        }
        else {
            result = hidden.get(uuid);
        }
        return result;
    }
    
    private static void destroyEntity(Entity toDestroy, Player... targets) {
        WrapperPlayServerEntityDestroy wrapper = new WrapperPlayServerEntityDestroy();
        wrapper.setEntityIds(new int[]{toDestroy.getEntityId()});
        for(Player player : targets) {
            wrapper.sendPacket(player);
        }
    }
    
    private static void destroyEntity(LivingEntity toDestroy, Collection<Player> targets) {
        WrapperPlayServerEntityDestroy wrapper = new WrapperPlayServerEntityDestroy();
        wrapper.setEntityIds(new int[]{toDestroy.getEntityId()});
        for(Player player : targets) {
            wrapper.sendPacket(player);
        }
    }
    
    public static void stop() {
        if (instance != null) {
            manager.removePacketListener(instance.vc1);
            manager.removePacketListener(instance.vc2);
            manager.removePacketListener(instance.vc3);
            manager.removePacketListener(instance.vc4);
            manager.removePacketListener(instance.vc5);
            manager.removePacketListener(instance.vc6);
            manager.removePacketListener(instance.vc7);
            manager.removePacketListener(instance.vc8);
            manager.removePacketListener(instance.vc9);
            manager.removePacketListener(instance.vc10);
            manager.removePacketListener(instance.vc11);
            manager.removePacketListener(instance.vc12);
            manager.removePacketListener(instance.vc13);
            manager.removePacketListener(instance.vc14);
            manager.removePacketListener(instance.vc15);
            manager.removePacketListener(instance.vc16);
            manager.removePacketListener(instance.vc17);
            HandlerList.unregisterAll(instance);
            instance = null;
        }
    }

    public static void init() {
        if (instance == null) {
            instance = new VisibilityHandler();
            Bukkit.getPluginManager().registerEvents(instance, SpleefLeague.getInstance());
        }
    }
    
    static {
        manager = ProtocolLibrary.getProtocolManager();
    }
}
