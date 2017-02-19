package com.spleefleague.core.utils.fakeentity;

import com.comphenix.packetwrapper.AbstractPacket;
import com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import com.comphenix.packetwrapper.WrapperPlayServerNamedEntitySpawn;
import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo;
import com.comphenix.protocol.wrappers.EnumWrappers;
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
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
/*
Copy pasted out of java decompiler. Please fix weird stuff you notice
*/
public class FakeNpc extends FakeEquippableCreature {

    private static final long tablistRemovalDelay = 60;
    private final UUID uuid;
    private String name;
    private String uncoloredName;
    private WrappedGameProfile profile;
    private SkinFactory.TexturesPropertyInfo skin;
    private final boolean fixedUUID;
    private String rawName;
    private AbstractPacket removalPacket = null;

    public FakeNpc(UUID uuid, String name, Location location) {
        super(location);
        if (uuid == null) {
            this.fixedUUID = false;
            this.uuid = UUID.randomUUID();
        } else {
            this.fixedUUID = true;
            this.uuid = uuid;
        }
        this.name = UtilChat.c(name);
        this.uncoloredName = UtilChat.s(this.name);
        this.rawName = name;
        this.profile = new WrappedGameProfile(this.uuid, this.name);
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public String getUncoloredName() {
        return this.uncoloredName;
    }

    public WrappedGameProfile getProfile() {
        return this.profile;
    }

    public SkinFactory.TexturesPropertyInfo getSkin() {
        return this.skin;
    }

    public void sendMessage(Player p, String msg) {
        UtilChat.s(p, "&7[NPC] %s&8: &e%s", this.getName().trim(), msg);
    }

    public /* varargs */ void sendMessage(Player p, String msg, Object... args) {
        this.sendMessage(p, String.format(msg, args));
    }

    public void saveToConfig() {
        FakeEntitiesManager.save(this);
    }

    public void deleteFromConfig() {
        FakeEntitiesManager.delete(this);
    }

    public void setName(String name) {
        name = name.replace("_", " ");
        boolean valid = this.isValid();
        if (valid) {
            this.despawn();
        }
        if (name != null) {
            if (name.length() < 16 && !name.endsWith(" ")) {
                name = name + " ";
            }
            this.rawName = name;
            this.name = UtilChat.c(name);
            this.uncoloredName = UtilChat.s(this.name);
        }
        this.updateProfile(valid);
    }

    public void setupSkinByNickname(String skinOwnerName) {
        this.setupSkinByProperty(SkinFactory.getSkinEncryptedData(UUIDFetcher.getUUIDOf(skinOwnerName)));
    }

    public void setupSkinByUUID(UUID skinOwnerUuid) {
        this.setupSkinByProperty(SkinFactory.getSkinEncryptedData(skinOwnerUuid));
    }

    public void setupSkinByEncryptedData(String skinEncryptedValue, String skinEncryptedSignature) {
        if (skinEncryptedSignature != null && skinEncryptedSignature.equals("null")) {
            skinEncryptedSignature = null;
        }
        this.setupSkinByProperty(new SkinFactory.TexturesPropertyInfo(skinEncryptedValue, skinEncryptedSignature));
    }

    public void setupSkinByProperty(SkinFactory.TexturesPropertyInfo skin) {
        this.skin = skin;
        SkinFactory.setupSkinForProfile(this.profile, skin);
    }

    private void updateProfile(boolean respawn) {
        this.profile = new WrappedGameProfile(this.uuid, this.name);
        if (respawn) {
            this.spawn();
        }
    }

    @Override
    protected void show(Player p) {
        super.show(p);
        Task.schedule(() -> {
            if (p.isOnline()) {
                this.removeFromTablist(p);
            }
        },
                60);
    }

    @Override
    protected void hide(Player p) {
        if (p.isOnline()) {
            AbstractPacket[] packets;
            WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
            destroy.setEntityIds(new int[]{this.getId()});
            WrapperPlayServerPlayerInfo info = new WrapperPlayServerPlayerInfo();
            info.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
            info.setData(Collections.singletonList(new PlayerInfoData(this.profile, 0, EnumWrappers.NativeGameMode.SURVIVAL, null)));
            for (AbstractPacket packet : packets = new AbstractPacket[]{destroy, info}) {
                packet.sendPacket(p);
            }
        }
        this.onHiding(p);
    }

    @Override
    public void invalidate() {
        if (this.invalidated) {
            throw new IllegalStateException(String.format("FakeNpc {%d;\"%s\"} is already invalidated!", this.getId(), this.getUncoloredName()));
        }
        if (this.isValid()) {
            this.despawn();
        }
        FakeEntitiesManager.removeCreature(this);
        FakeCreaturesWorker.removeWorkingWith(this);
        this.invalidated = true;
    }

    @Override
    public void despawn() {
        this.validate();
        this.valid = false;
        WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
        destroy.setEntityIds(new int[]{this.getId()});
        WrapperPlayServerPlayerInfo info = new WrapperPlayServerPlayerInfo();
        info.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
        info.setData(Collections.singletonList(new PlayerInfoData(this.profile, 0, EnumWrappers.NativeGameMode.SURVIVAL, null)));
        AbstractPacket[] packets = new AbstractPacket[]{destroy, info};
        this.affectedPlayers.forEach(p -> {
            for (AbstractPacket packet : packets) {
                packet.sendPacket(p);
            }
            this.onHiding(p);
        }
        );
        this.affectedPlayers.clear();
        FakeNpc.removeAliveCreature(this);
        FakeNpcScoreboard.removeNpc(this);
    }

    @Override
    public void spawn() {
        if (this.invalidated) {
            throw new IllegalStateException(String.format("FakeNpc {%d;\"%s\"} had been invalidated, it can't be spawned again!", this.getId(), this.getUncoloredName()));
        }
        if (this.isValid()) {
            throw new IllegalStateException(String.format("FakeNpc {%d;\"%s\"} is already spawned!", this.getId(), this.getUncoloredName()));
        }
        AbstractPacket[] info = this.getInfo();
        HashSet<Player> players = new HashSet<>();
        players.addAll(this.affectedPlayers);
        players.forEach(p -> {
            for (AbstractPacket packet : info) {
                packet.sendPacket(p);
            }
        }
        );
        Task.schedule(() -> players.stream().filter(Player::isOnline).forEach(this::removeFromTablist), tablistRemovalDelay);
        this.updateRotation();
        this.tick();
        FakeNpc.addAliveCreature(this);
        FakeNpcScoreboard.addNpc(this);
        this.valid = true;
        this.affectedPlayers.forEach(this::onShowing);
    }

    @Override
    protected AbstractPacket[] getInfo() {
        WrapperPlayServerPlayerInfo info = new WrapperPlayServerPlayerInfo();
        WrapperPlayServerNamedEntitySpawn spawn2 = new WrapperPlayServerNamedEntitySpawn();
        info.setAction(EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        info.setData(Collections.singletonList(new PlayerInfoData(this.profile, 0, EnumWrappers.NativeGameMode.SURVIVAL, null)));
        spawn2.setEntityID(this.id);
        spawn2.setPosition(new Vector(this.getX(), this.getY(), this.getZ()));
        spawn2.setYaw(this.getYaw());
        spawn2.setPlayerUUID(this.profile.getUUID());
        spawn2.setMetadata(new WrappedDataWatcher());
        return new AbstractPacket[]{info, spawn2};
    }

    private void removeFromTablist(Player p) {
        if (this.removalPacket == null) {
            WrapperPlayServerPlayerInfo info = new WrapperPlayServerPlayerInfo();
            info.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
            info.setData(Collections.singletonList(new PlayerInfoData(this.profile, 0, EnumWrappers.NativeGameMode.SURVIVAL, null)));
            this.removalPacket = info;
        }
        this.removalPacket.sendPacket(p);
    }

    @Override
    public void validate() {
        if (!this.isValid()) {
            throw new IllegalStateException(String.format("FakeNpc {%d;\"%s\"} is not valid!", this.getId(), this.getUncoloredName()));
        }
    }

    public String serialize(JSONObject json) {
        if (json == null) {
            json = new JSONObject();
        }
        json.put((Object) "class", (Object) this.getClass().getName());
        if (this.fixedUUID) {
            json.put((Object) "uuid", (Object) this.uuid.toString());
        }
        json.put((Object) "name", (Object) this.rawName);
        json.put((Object) "location", (Object) UtilAlgo.locToStr(this.getLocation()));
        JSONObject equipment = new JSONObject();
        for (EnumWrappers.ItemSlot fi : EnumWrappers.ItemSlot.values()) {
            equipment.put((Object) fi.name().toLowerCase(), (Object) UtilAlgo.serialize(this.getEquipment().getItems().get((Object) fi)));
        }
        json.put((Object) "equipment", (Object) equipment);
        if (this.skin != null) {
            JSONObject skin = new JSONObject();
            skin.put((Object) "value", (Object) this.skin.getValue());
            skin.put((Object) "signature", (Object) this.skin.getSignature());
            json.put((Object) "skin", (Object) skin);
        }
        return json.toJSONString();
    }

    protected static Object deserialize(Constructor constructor, JSONObject json) throws Exception {
        UUID uuid = json.containsKey((Object) "uuid") ? UUID.fromString((String) json.get((Object) "uuid")) : null;
        String name = (String) json.get((Object) "name");
        Location loc = UtilAlgo.strToLoc((String) json.get((Object) "location"));
        JSONObject equipment = (JSONObject) json.get((Object) "equipment");
        FakeNpc npc = (FakeNpc) constructor.newInstance(new Object[]{uuid, name, loc});
        for (EnumWrappers.ItemSlot is : EnumWrappers.ItemSlot.values()) {
            npc.getEquipment().updateUnsafe(is, UtilAlgo.deserialize((JSONObject) equipment.get((Object) is.name().toLowerCase())));
        }
        if (json.containsKey((Object) "skin")) {
            JSONObject skin = (JSONObject) json.get((Object) "skin");
            npc.setupSkinByProperty(new SkinFactory.TexturesPropertyInfo((String) skin.get((Object) "value"), (String) skin.get((Object) "signature")));
        }
        return npc;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SuppressWarnings("unchecked")
    public static Object deserialize(String data) throws Exception {
        JSONObject json = parse(data);
        Class clazz = Class.forName((String) json.get("class"));
        boolean end = false;
        Constructor constructor = null;
        while (!end && clazz != Object.class) {
            try {
                constructor = clazz.getConstructor(UUID.class, String.class, Location.class);
                end = true;
            } catch (Exception ex) {
                clazz = clazz.getSuperclass();
            }
        }
        end = false;
        Method m = null;
        while (!end && clazz != Object.class) {
            try {
                m = clazz.getDeclaredMethod("deserialize", Constructor.class, JSONObject.class);
                end = true;
            } catch (Exception ex) {
                clazz = clazz.getSuperclass();
            }
        }
        m.setAccessible(true);
        try {
            return m.invoke(null, constructor, json);
        } finally {
            m.setAccessible(false);
        }
    }

    protected static JSONObject parse(String data) throws ParseException {
        return (JSONObject) new JSONParser().parse(data);
    }
}
