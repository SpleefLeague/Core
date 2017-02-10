package com.spleefleague.core.utils.fakeentity;

import com.comphenix.packetwrapper.AbstractPacket;
import com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import com.comphenix.packetwrapper.WrapperPlayServerNamedEntitySpawn;
import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.spleefleague.core.utils.Task;
import com.spleefleague.core.utils.UUIDFetcher;
import com.spleefleague.core.utils.UtilAlgo;
import com.spleefleague.core.utils.UtilChat;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public class FakeNpc extends FakeEquippableCreature {
    
    private final static long tablistRemovalDelay = 60l;
    
    private final UUID uuid;
    
    private String name;
    
    private String uncoloredName;
    
    private WrappedGameProfile profile;
    
    private SkinFactory.TexturesPropertyInfo skin;
    
    private final boolean fixedUUID;
    private String rawName;
    
    public FakeNpc(UUID uuid, String name, Location location) {
        super(location);
        if(uuid == null) {
            this.fixedUUID = false;
            this.uuid = UUID.randomUUID();
        }else {
            this.fixedUUID = true;
            this.uuid = uuid;
        }
        this.name = UtilChat.c(name);
        this.uncoloredName = UtilChat.s(this.name);
        this.rawName = name;
        this.profile = new WrappedGameProfile(this.uuid, this.name);
    }
    
    public UUID getUuid() {
        return uuid;
    }
    
    public String getName() {
        return name;
    }
    
    public String getUncoloredName() {
        return uncoloredName;
    }
    
    public WrappedGameProfile getProfile() {
        return profile;
    }
    
    public SkinFactory.TexturesPropertyInfo getSkin() {
        return skin;
    }
    
    public void sendMessage(Player p, String msg) {
        UtilChat.s(p, "&7[NPC] %s&8: &e%s", getName().trim(), msg);
    }
    
    public void sendMessage(Player p, String msg, Object... args) {
        sendMessage(p, String.format(msg, args));
    }
    
    public void saveToConfig() {
        FakeEntitiesManager.save(this);
    }
    
    public void deleteFromConfig() {
        FakeEntitiesManager.delete(this);
    }
    
    public void setName(String name) {
        name = name.replace("_", " ");
        boolean valid = isValid();
        if(valid)
            despawn();
        if(name != null) {
            if(name.length() < 16 && !name.endsWith(" "))
                name += " ";
            this.rawName = name;
            this.name = UtilChat.c(name);
            this.uncoloredName = UtilChat.s(this.name);
        }
        updateProfile(valid);
    }
    
    public void setupSkinByNickname(String skinOwnerName) {
        setupSkinByProperty(SkinFactory.getSkinEncryptedData(UUIDFetcher.getUUIDOf(skinOwnerName)));
    }
    
    public void setupSkinByUUID(UUID skinOwnerUuid) {
        setupSkinByProperty(SkinFactory.getSkinEncryptedData(skinOwnerUuid));
    }
    
    public void setupSkinByEncryptedData(String skinEncryptedValue, String skinEncryptedSignature) {
        if(skinEncryptedSignature != null && skinEncryptedSignature.equals("null"))
            skinEncryptedSignature = null;
        setupSkinByProperty(new SkinFactory.TexturesPropertyInfo(skinEncryptedValue, skinEncryptedSignature));
    }
    
    public void setupSkinByProperty(SkinFactory.TexturesPropertyInfo skin) {
        this.skin = skin;
        SkinFactory.setupSkinForProfile(this.profile, skin);
    }
    
    private void updateProfile(boolean respawn) {
        this.profile = new WrappedGameProfile(this.uuid, this.name);
        if(respawn)
            spawn();
    }
    
    /**
     * Sends packets containing information about this npc to the specified player.
     * @param p receiver
     */
    @Override
    protected void show(Player p) {
        super.show(p);
        Task.schedule(() -> {
            if(p.isOnline())
                removeFromTablist(p);
        }, tablistRemovalDelay);
    }
    
    @Override
    protected void hide(Player p) {
        if(p.isOnline()) {
            WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
            destroy.setEntityIds(new int[]{getId()});
            WrapperPlayServerPlayerInfo info = new WrapperPlayServerPlayerInfo();
            info.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
            info.setData(Collections.singletonList(new PlayerInfoData(
                    profile, 0, EnumWrappers.NativeGameMode.SURVIVAL, null
            )));
            AbstractPacket[] packets = new AbstractPacket[]{destroy, info};
            for(AbstractPacket packet : packets)
                packet.sendPacket(p);
        }
        onHiding(p);
    }
    
    /**
     * Despawns an npc (if it's not despawned yet) and deletes all information about it.
     */
    @Override
    public void invalidate() {
        if(invalidated)
            throw new IllegalStateException(String.format("FakeNpc {%d;\"%s\"} is already invalidated!", getId(), getUncoloredName()));
        if(isValid())
            despawn();
        FakeEntitiesManager.removeCreature(this);
        FakeCreaturesWorker.removeWorkingWith(this);
        invalidated = true;
    }
    
    /**
     * Despawns an npc, but saves all information about it (location, equipment, etc).
     */
    @Override
    public void despawn() {
        validate();
        valid = false;
        WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
        destroy.setEntityIds(new int[]{getId()});
        WrapperPlayServerPlayerInfo info = new WrapperPlayServerPlayerInfo();
        info.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
        info.setData(Collections.singletonList(new PlayerInfoData(
                profile, 0, EnumWrappers.NativeGameMode.SURVIVAL, null
        )));
        AbstractPacket[] packets = new AbstractPacket[]{destroy, info};
        affectedPlayers.forEach(p -> {
            for(AbstractPacket packet : packets)
                packet.sendPacket(p);
            onHiding(p);
        });
        affectedPlayers.clear();
        removeAliveCreature(this);
        FakeNpcScoreboard.removeNpc(this);
    }
    
    /**
     * Sends packets containing info about this npc to all players on the server (and to all new players).
     */
    @Override
    public void spawn() {
        if(invalidated)
            throw new IllegalStateException(String.format("FakeNpc {%d;\"%s\"} had been invalidated, it can't be spawned again!", getId(), getUncoloredName()));
        if(isValid())
            throw new IllegalStateException(String.format("FakeNpc {%d;\"%s\"} is already spawned!", getId(), getUncoloredName()));
        AbstractPacket[] info = getInfo();
        Set<Player> players = new HashSet<>();
        players.addAll(affectedPlayers);
        players.forEach(p -> {
            for(AbstractPacket packet : info)
                packet.sendPacket(p);
        });
        Task.schedule(() -> players.stream().filter(Player::isOnline).forEach(this::removeFromTablist), tablistRemovalDelay);
        updateRotation();
        tick();
        addAliveCreature(this);
        FakeNpcScoreboard.addNpc(this);
        valid = true;
        affectedPlayers.forEach(this::onShowing);
    }
    
    @Override
    protected AbstractPacket[] getInfo() {
        WrapperPlayServerPlayerInfo info = new WrapperPlayServerPlayerInfo();
        WrapperPlayServerNamedEntitySpawn spawn = new WrapperPlayServerNamedEntitySpawn();
        info.setAction(EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        info.setData(Collections.singletonList(new PlayerInfoData(
                profile, 0, EnumWrappers.NativeGameMode.SURVIVAL, null
        )));
        spawn.setEntityID(id);
        spawn.setPosition(new Vector(getX(), getY(), getZ()));
        spawn.setYaw(getYaw());
        spawn.setPlayerUUID(profile.getUUID());
        spawn.setMetadata(new WrappedDataWatcher());
        return new AbstractPacket[]{info, spawn};
    }
    
    private AbstractPacket removalPacket = null;
    
    private void removeFromTablist(Player p) {
        if(removalPacket == null) {
            WrapperPlayServerPlayerInfo info = new WrapperPlayServerPlayerInfo();
            info.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
            info.setData(Collections.singletonList(new PlayerInfoData(
                    profile, 0, EnumWrappers.NativeGameMode.SURVIVAL, null
            )));
            removalPacket = info;
        }
        removalPacket.sendPacket(p);
    }
    
    /**
     * Throws an exception whether npc is not valid.
     */
    @Override
    public void validate() {
        if(!isValid())
            throw new IllegalStateException(String.format("FakeNpc {%d;\"%s\"} is not valid!", getId(), getUncoloredName()));
    }
    
    @SuppressWarnings("unchecked")
    public String serialize(JSONObject json) {
        if(json == null)
            json = new JSONObject();
        json.put("class", getClass().getName());
        if(fixedUUID)
            json.put("uuid", uuid.toString());
        json.put("name", rawName);
        json.put("location", UtilAlgo.locToStr(getLocation()));
        JSONObject equipment = new JSONObject();
        for(ItemSlot fi : ItemSlot.values())
            equipment.put(fi.name().toLowerCase(), UtilAlgo.serialize(getEquipment().getItems().get(fi)));
        json.put("equipment", equipment);
        if(skin != null) {
            JSONObject skin = new JSONObject();
            skin.put("value", this.skin.getValue());
            skin.put("signature", this.skin.getSignature());
            json.put("skin", skin);
        }
        return json.toJSONString();
    }
    
    protected static Object deserialize(Constructor constructor, JSONObject json) throws Exception {
        UUID uuid = json.containsKey("uuid") ? UUID.fromString((String) json.get("uuid")) : null;
        String name = (String) json.get("name");
        Location loc = UtilAlgo.strToLoc((String) json.get("location"));
        JSONObject equipment = (JSONObject) json.get("equipment");
        
        FakeNpc npc = (FakeNpc) constructor.newInstance(uuid, name, loc);
        
        for(ItemSlot is : ItemSlot.values())
            npc.getEquipment().updateUnsafe(is, UtilAlgo.deserialize((JSONObject) equipment.get(is.name().toLowerCase())));
        
        if(json.containsKey("skin")) {
            JSONObject skin = (JSONObject) json.get("skin");
            npc.setupSkinByProperty(new SkinFactory.TexturesPropertyInfo((String) skin.get("value"), (String) skin.get("signature")));
        }
            
        return npc;
    }
    
    @SuppressWarnings("unchecked")
    public static Object deserialize(String data) throws Exception {
        JSONObject json = parse(data);
        Class clazz = Class.forName((String) json.get("class"));
        boolean end = false;
        Constructor constructor = null;
        while(!end && clazz != Object.class) {
            try {
                constructor = clazz.getConstructor(UUID.class, String.class, Location.class);
                end = true;
            }catch(Exception ex) {
                clazz = clazz.getSuperclass();
            }
        }
        end = false;
        Method m = null;
        while(!end && clazz != Object.class) {
            try {
                m = clazz.getDeclaredMethod("deserialize", Constructor.class, JSONObject.class);
                end = true;
            }catch(Exception ex) {
                clazz = clazz.getSuperclass();
            }
        }
        m.setAccessible(true);
        try {
            return m.invoke(null, constructor, json);
        }finally {
            m.setAccessible(false);
        }
    }
    
    protected static JSONObject parse(String data) throws ParseException {
        return (JSONObject) new JSONParser().parse(data);
    }

}