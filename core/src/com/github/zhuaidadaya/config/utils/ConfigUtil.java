package com.github.zhuaidadaya.config.utils;

import android.util.SparseArray;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.*;


public class ConfigUtil {
    /**
     *
     */
    private final LinkedHashMap<Object, Config<Object, Object>> configs = new LinkedHashMap<>();
    private final LinkedHashMap<Object, Object> utilConfigs = new LinkedHashMap<>();
    private EncryptionType encryptionType = EncryptionType.COMPOSITE_SEQUENCE;
    /**
     *
     */
    private Logger logger;
    private boolean encryption = false;
    /**
     * if true
     * run <code>writeConfig()</code> when config has updated
     */
    private boolean autoWrite = true;
    private String entrust;
    private String note;
    private boolean empty = false;
    private int splitRange = 20;
    private int libraryOffset = 5;
    private boolean encryptionHead = false;
    private boolean canShutdown = true;
    private boolean shuttingDown = false;
    private boolean shutdown = false;

    public ConfigUtil(String entrust) {
        utilConfigs.put("path", System.getProperty("user.dir"));
        utilConfigs.put("name", "settings.conf");
        utilConfigs.put("version", "1.1");
        logger = LogManager.getLogger("ConfigUtil-" + entrust);
        readConfig(true);
    }

    public ConfigUtil(String configPath, String entrust) {
        utilConfigs.put("path", configPath);
        utilConfigs.put("name", "settings.conf");
        utilConfigs.put("version", "1.1");
        logger = LogManager.getLogger("ConfigUtil-" + entrust);
        readConfig(true);
    }

    public ConfigUtil(String configPath, String configName, String entrust) {
        utilConfigs.put("path", configPath);
        utilConfigs.put("name", configName);
        utilConfigs.put("version", "1.1");
        this.entrust = entrust;
        logger = LogManager.getLogger("ConfigUtil-" + entrust);
        readConfig(true);
    }

    public ConfigUtil(String configPath, String configName, String configVersion, String entrust) {
        utilConfigs.put("path", configPath);
        utilConfigs.put("name", configName);
        utilConfigs.put("version", configVersion);
        this.entrust = entrust;
        logger = LogManager.getLogger("ConfigUtil-" + entrust);
        readConfig(true);
    }

    public ConfigUtil(String configPath, String configName, String configVersion, String entrust, boolean empty) {
        utilConfigs.put("path", configPath);
        utilConfigs.put("name", configName);
        utilConfigs.put("version", configVersion);
        this.entrust = entrust;
        logger = LogManager.getLogger("ConfigUtil-" + entrust);
        this.empty = empty;
        if(! empty)
            readConfig(true);
    }

    public static ConfigUtil emptyConfigUtil() {
        return new ConfigUtil(null, null, "1.1", null, true);
    }

    public ConfigUtil setLibraryOffset(int offset) {
        if(offset != - 1)
            this.libraryOffset = Math.max(1, offset);
        else
            this.libraryOffset = 25565;
        return this;
    }

    public ConfigUtil setSplitRange(int range) {
        splitRange = range;
        return this;
    }

    public ConfigUtil setEncryptionType(EncryptionType type) {
        this.encryptionType = type;
        return this;
    }

    public ConfigUtil setEmpty(boolean empty) {
        this.empty = empty;
        return this;
    }

    public ConfigUtil setEntrust(String entrust) {
        this.entrust = entrust;
        logger = LogManager.getLogger("ConfigUtil/" + entrust);
        return this;
    }

    public ConfigUtil setEncryptionHead(boolean encryptionHead) {
        this.encryptionHead = encryptionHead;
        return this;
    }

    public ConfigUtil setAutoWrite(boolean autoWrite) {
        this.autoWrite = autoWrite;
        return this;
    }

    public ConfigUtil setNote(String note) {
        this.note = note;
        return this;
    }

    public ConfigUtil fuse(ConfigUtil parent) {
        for(Object o : parent.configs.keySet())
            this.setConf(true, o.toString(), parent.configs.get(o.toString()).getValue());
        return this;
    }

    public ConfigUtil setEncryption(boolean encryption) {
        this.encryption = encryption;
        if(autoWrite) {
            try {
                writeConfig();
            } catch (Exception e) {

            }
        }
        return this;
    }

    public LinkedHashMap<Object, Config<Object, Object>> getConfigs() {
        return configs;
    }

    public Config<Object, Object> getConfig(Object conf) {
        return configs.get(conf);
    }

    public String getConfigValue(Object conf) {
        return getConfig(conf).getValue();
    }

    public boolean readConfig() {
        return readConfig(false);
    }

    public boolean readConfig(boolean log) {
        return readConfig(log, false);
    }

    public boolean readConfig(boolean log, boolean forceLoad) {
        if(shuttingDown) {
            return false;
        }
        canShutdown = false;

        if(empty) {
            canShutdown = true;

            return false;
        }
        int configSize = 0;
        try {
            if(log)
                logger.info("loading config from: " + utilConfigs.get("name").toString());

            JSONArray configs;

            File configFile = new File(utilConfigs.get("path").toString() + "/" + utilConfigs.get("name").toString());

            BufferedReader br = new BufferedReader(new FileReader(configFile, Charset.forName("unicode")));
            StringBuilder builder = decryption(br, false);


            configs = new JSONObject(builder.toString()).getJSONArray("configs");
            configSize = builder.length();

            br.close();

            for(Object o : configs) {
                JSONObject config = new JSONObject(o.toString());
                String configKey = config.keySet().toArray()[0].toString();
                if(log)
                    logger.info("loading for config: " + configKey);
                JSONObject configDetailed = config.getJSONObject(configKey);
                if(configDetailed.getBoolean("listTag")) {
                    JSONArray array = configDetailed.getJSONArray("values");
                    LinkedList<Object> addToConfig = new LinkedList<>();
                    for(Object inArray : array)
                        addToConfig.add(inArray);
                    setListConf(true, configKey, addToConfig.toArray());
                } else {
                    setConf(true, configKey, configDetailed.get("value").toString());
                }
            }

            if(log)
                logger.info("load config done");

            canShutdown = true;

            return true;
        } catch (IllegalArgumentException e) {
            canShutdown = true;

            throw e;
        } catch (Exception e) {
            if(! shuttingDown) {
                logger.error(empty ? ("failed to load config") : ("failed to load config: " + utilConfigs.get("name").toString()));
                if(! empty) {
                    File configFile = new File(utilConfigs.get("path").toString() + "/" + utilConfigs.get("name").toString());
                    if(! configFile.isFile() || configFile.length() == 0 || configSize == 0) {
                        try {
                            configFile.getParentFile().mkdirs();
                            configFile.createNewFile();
                            writeConfig();
                            logger.info("created new config file for " + entrust);
                        } catch (Exception ex) {
                            logger.error("failed to create new config file for " + entrust);
                        }
                    }
                }
            }

            canShutdown = true;

            return false;
        }
    }

    public StringBuilder decryption() {
        try {
            File configFile = new File(utilConfigs.get("path").toString() + "/" + utilConfigs.get("name").toString());

            BufferedReader br = new BufferedReader(new FileReader(configFile, Charset.forName("unicode")));

            return decryption(br, false);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {

        }

        return null;
    }

    public StringBuilder decryption(BufferedReader reader, boolean forceLoad) {
        try {
            StringBuilder builder = new StringBuilder();
            String cache;
            cache = reader.readLine();
            if(cache == null) {
                return null;
            }
            int encryptionType = cache.chars().toArray()[0];
            String encryptionEnable = cache.substring(2);
            boolean encrypted = encryptionEnable.startsWith("encryption") | encryptionEnable.startsWith("MCH DB");
            if(encrypted) {
                switch(encryptionType) {
                    case 0 -> {
                        while((cache = reader.readLine()) != null) {
                            if(! cache.startsWith("/**") & ! cache.startsWith(" *") & ! cache.startsWith(" */")) {
                                if(cache.length() > 0)
                                    builder.append(cache).append("\n");
                            }
                        }

                        int checkCode = Integer.parseInt(String.valueOf(builder.chars().toArray()[0]));

                        BufferedReader configRead = new BufferedReader(new StringReader(builder.toString()));

                        StringBuilder s1 = new StringBuilder();
                        while((cache = configRead.readLine()) != null) {
                            int lim = cache.length() > 1 ? cache.chars().toArray()[0] : 0;

                            boolean checkSkip = false;

                            for(Object o : cache.chars().toArray()) {
                                if(checkSkip) {
                                    int details = Integer.parseInt(o.toString());
                                    if(details != 10) {
                                        s1.append((char) (details - lim - checkCode));
                                    }
                                }
                                checkSkip = true;
                            }
                        }

                        return s1;
                    }
                    case 1 -> {
                        while((cache = reader.readLine()) != null) {
                            if(! cache.startsWith("/**") & ! cache.startsWith(" *") & ! cache.startsWith(" */")) {
                                if(cache.length() > 0)
                                    builder.append(cache).append("\n");
                            }
                        }

                        int checkCode = Integer.parseInt(String.valueOf(builder.chars().toArray()[0]));

                        BufferedReader configRead = new BufferedReader(new StringReader(builder.toString()));

                        while((cache = configRead.readLine()) != null) {
                            if(cache.startsWith("LIBRARY ")) {
                                break;
                            }
                        }

                        StringBuilder libraryInformation = new StringBuilder();

                        while((cache = configRead.readLine()) != null) {
                            if(cache.startsWith("INFORMATION ")) {
                                break;
                            }
                            libraryInformation.append(cache.replace("\b", "\n")).append("\n");
                        }

                        BufferedReader libraryRead = new BufferedReader(new StringReader(libraryInformation.toString()));

                        HashMap<Integer, Integer> libraryMap = new HashMap<>();

                        while((cache = libraryRead.readLine()) != null) {
                            int headCode = cache.chars().toArray()[0];
                            String[] libraryLine = cache.substring(1).split("\t");
                            for(String s : libraryLine) {
                                StringBuilder charCode = new StringBuilder();
                                int signCode = - 1;
                                boolean in = false;
                                for(int i : s.chars().toArray()) {
                                    if(! in) {
                                        signCode = i;
                                        in = true;
                                        continue;
                                    }
                                    charCode.append((char) (i - checkCode - headCode));
                                }
                                if(! charCode.toString().equals(""))
                                    libraryMap.put(signCode, Integer.parseInt(charCode.toString()));
                            }
                        }

                        StringBuilder information = new StringBuilder();

                        while((cache = configRead.readLine()) != null) {
                            information.append(cache.substring(1));
                        }

                        information = new StringBuilder(information.toString().replace("\t", ""));

                        StringBuilder recodeInformation = new StringBuilder();

                        try {
                            for(int i : information.chars().toArray()) {
                                recodeInformation.append((char) libraryMap.get(i).intValue());
                            }
                        } catch (Exception e) {

                        }

                        return recodeInformation;
                    }
                    default -> {
                        if(forceLoad)
                            throw new IllegalArgumentException("unsupported encryption type: " + encryptionType);
                    }
                }

            } else {
                while(true) {
                    String startWith = reader.readLine();
                    if(startWith.replace(" ", "").startsWith("{")) {
                        builder.append(startWith);
                        break;
                    }
                }
                while((cache = reader.readLine()) != null) {
                    if(! cache.startsWith("/**") || cache.startsWith(" *") || cache.startsWith(" */"))
                        builder.append(cache);
                }

                return builder;
            }
        } catch (IOException e) {

        }

        return null;
    }

    public void writeConfig() throws Exception {
        if(shuttingDown) {
            return;
        }

        canShutdown = false;

        BufferedWriter writer = new BufferedWriter(new FileWriter(utilConfigs.get("path").toString() + "/" + utilConfigs.get("name").toString(), Charset.forName("unicode"), false));

        StringBuilder write = new StringBuilder(this.toJSONObject().toString());

        Random r = new SecureRandom();

        if(encryption) {
            switch(encryptionType.getId()) {
                case 0 -> {
                    StringBuffer buffer = encryptionByRandomSequence(write, r);
                    write(writer, buffer);
                    writer.close();
                }
                case 1 -> {
                    StringBuffer buffer = encryptionByCompositeSequence(write);
                    write(writer, buffer);
                    writer.close();
                }
            }
        } else {
            StringBuffer buffer = new StringBuffer();
            buffer.append("no encryption config: [config_size=").append(write.length()).append(", config_version=").append(utilConfigs.get("version")).append("]");
            buffer.append("\n");
            buffer.append(write);
            write(writer, buffer);
            writer.close();
        }

        canShutdown = true;
    }

    public void write(Writer writer, StringBuffer information) throws IOException {
        writer.write(information.toString());
    }

    public void write(Writer writer, String information) throws IOException {
        write(writer, new StringBuffer(information));
    }

    public void write(StringBuffer information) throws IOException {
        write(information.toString());
    }

    public void write(String information) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(utilConfigs.get("path").toString() + "/" + utilConfigs.get("name").toString(), Charset.forName("unicode"), false));
        write(writer, new StringBuffer(information));
        writer.close();
    }

    public StringBuffer encryptionByRandomSequence(StringBuilder write, Random r) {
        int checkingCodeRange = r.nextInt(1024 * 8);
        checkingCodeRange = checkingCodeRange > 13 ? checkingCodeRange : 14;
        int checkingCode = r.nextInt(checkingCodeRange);
        checkingCode = checkingCode > 13 ? checkingCode : 14;
        int split = 0;

        StringWriter writer = new StringWriter();

        if(encryption) {
            int wrap = splitRange;

            for(; wrap > 0; wrap--) {
                int splitIndex = r.nextInt(100);
                if(splitIndex < 50) {
                    splitIndex += 50;
                }
                if((splitIndex + split) < write.length()) {
                    split += splitIndex - 1;
                    write.insert(split, "\n");
                } else {
                    break;
                }
            }
        }

        int[] charArray = write.chars().toArray();

        writer.write(0);

        if(! encryptionHead) {
            writer.write(" encryption: [" + "type=" + encryptionType.getName() + ", " + "SUPPORT=MCH -> https://github.com/zhuaidadaya/ConfigUtil , " + "check code=" + checkingCode + ", " + "offset=" + checkingCodeRange + ", " + "config size=" + write.length() + ", " + "config version=" + utilConfigs.get("version") + ", " + "split=" + split + ", " + "split range=" + splitRange + "]");
            writer.write(10);
            writer.write(formatNote());
            writer.write(10);
        } else {
            writer.write(" MCH DB   ");
            write3RandomByte(writer);
            writer.write(" TYPE?" + encryptionType.getName() + "  ");
            write3RandomByte(writer);
            writer.write(" SUPPORT?" + "MCH -> https://github.com/zhuaidadaya/ConfigUtil  ");
            write2RandomByte(writer);
            writer.write(" OFFSET?" + checkingCodeRange);
            write3RandomByte(writer, checkingCodeRange);
            writer.write(" VER?" + utilConfigs.get("version"));
            write2RandomByte(writer, checkingCodeRange);
            writer.write(" EC?" + checkingCode);
            write2RandomByte(writer, checkingCodeRange);
            writer.write(" SZ?" + write.length());
            write3RandomByte(writer, checkingCodeRange);
            writer.write("\n");
        }

        writer.write(checkingCodeRange);
        writer.write("\n");
        writer.write(checkingCode);

        int count = 0;
        for(Object o : charArray) {
            count++;
            if(Integer.parseInt(o.toString()) == 10) {
                int rand = r.nextInt(checkingCodeRange);
                writer.write(10);
                checkingCode = rand > 13 ? rand : 14;
                if(count != charArray.length)
                    writer.write(checkingCode);
            } else {
                writer.write((Integer.parseInt(o.toString()) + checkingCode + checkingCodeRange));
            }
        }

        return writer.getBuffer();
    }

    public StringBuffer encryptionByCompositeSequence(StringBuilder write) {
        SecureRandom r = new SecureRandom();
        int checkingCodeRange = 1024 * 12;
        int checkingCode = r.nextInt(checkingCodeRange);
        checkingCode = checkingCode > 13 ? checkingCode : 14;
        int[] charArray = write.chars().toArray();

        StringWriter writer = new StringWriter();

        writer.write(1);

        if(! encryptionHead) {
            writer.write(" encryption: [" + "type=" + encryptionType.getName() + ", " + "SUPPORT=MCH -> https://github.com/zhuaidadaya/ConfigUtil , " + "offset=" + checkingCodeRange + ", " + "config size=" + write.length() + ", " + "config version=" + utilConfigs.get("version") + "]");
            writer.write(10);
            writer.write(formatNote());
            writer.write(10);
        } else {
            writer.write(" MCH DB   ");
            write2RandomByte(writer);
            writer.write(" TYPE?" + encryptionType.getName() + "  ");
            write3RandomByte(writer);
            writer.write(" SUPPORT?" + "MCH -> https://github.com/zhuaidadaya/ConfigUtil  ");
            write3RandomByte(writer);
            writer.write(" OFFSET?" + checkingCodeRange);
            write2RandomByte(writer);
            writer.write(" VER?" + utilConfigs.get("version"));
            write2RandomByte(writer);
            writer.write(" SZ?" + write.length());
            write3RandomByte(writer);
            writer.write(10);
        }

        writer.write(checkingCode);
        writer.write(10);
        writer.write("LIBRARY ");
        write2RandomByte(writer);
        writer.write("  ");
        write3RandomByte(writer);
        writer.write("\n");

        HashMap<String, Integer> libraryMap = new HashMap<>();

        int count = 0;
        int lim = r.nextInt(checkingCodeRange);
        int head = lim > 13 ? lim : 14;
        int split = r.nextInt(50);
        writer.write(head);

        int offset;

        //  generate library
        for(Object o : charArray) {
            offset = 1;

            int sourceChar = Integer.parseInt(o.toString());
            int writeChar = sourceChar + checkingCode + head;

            boolean dump = false;
            boolean next = false;

            while(libraryMap.containsKey(sourceChar + "-" + offset)) {
                dump = true;
                offset++;
                if(offset > libraryOffset - 1) {
                    next = true;
                    break;
                }
            }

            if(next) {
                continue;
            }

            count++;

            while(libraryMap.containsValue(writeChar)) {
                dump = true;
                head++;
                writeChar = sourceChar + checkingCode + head;
            }

            if(dump) {
                writer.write("\b");
                writer.write(head);
            } else if(count > split) {
                writer.write(10);
                split = r.nextInt(50);
                count = 0;
                lim = r.nextInt(checkingCodeRange);
                head = lim > 13 ? lim : 14;
                writer.write(head);
            }

            writer.write(writeChar);

            for(int c: o.toString().chars().toArray()) {
                writer.write(c + checkingCode + head);
            }

            if(libraryMap.containsKey(sourceChar + "-0")) {
                libraryMap.put(sourceChar + "-" + offset, writeChar);
            } else {
                libraryMap.put(sourceChar + "-0", writeChar);
            }

            writer.write("\t");
        }

        StringBuilder writeInformation = new StringBuilder();

        for(int c : charArray) {
            Integer integer;
            int tryCount = r.nextInt(libraryOffset);
            while(true) {
                tryCount++;

                if(tryCount < libraryOffset)
                    integer = libraryMap.get(c + "-" + r.nextInt(libraryOffset));
                else
                    integer = libraryMap.get(c + "-0");

                if(integer != null) {
                    writeInformation.append((char) integer.intValue());

                    break;
                }
            }
        }

        writer.write("\n");
        write2RandomByte(writer);
        writer.write("\n");
        writer.write("INFORMATION ");
        write2RandomByte(writer);
        writer.write("   ");
        write3RandomByte(writer);
        writer.write("\n");

        int tabCount = 0;
        int tab = r.nextInt(15);
        count = 0;
        lim = r.nextInt(checkingCodeRange);
        head = lim > 13 ? lim : 14;
        split = r.nextInt(150);
        writer.write(head);
        for(int c : writeInformation.chars().toArray()) {
            count++;
            tabCount++;
            if(count > split) {
                writer.write(10);
                split = r.nextInt(150);
                count = 0;
                lim = r.nextInt(checkingCodeRange);
                head = lim > 13 ? lim : 14;
                writer.write(head);
            } else if(tabCount > tab) {
                writer.write("\t");
                tab = r.nextInt(15);
                tabCount = 0;
            }

            try {
                writer.write(c);
            } catch (Exception e) {

            }
        }

        return writer.getBuffer();
    }

    public void writeRandomByte(Writer writer, int limit, int bytes) {
        SecureRandom r = new SecureRandom();
        try {
            for(int i = 0; i < bytes; i++) {
                int next = r.nextInt(limit);
                writer.write(next > 13 ? next : 14);
            }
        } catch (Exception e) {

        }
    }

    public void write3RandomByte(Writer writer, int limit) {
        writeRandomByte(writer, limit, 3);
    }

    public void write3RandomByte(Writer writer) {
        write3RandomByte(writer, new Random().nextInt(25565));
    }

    public void write2RandomByte(Writer writer, int limit) {
        writeRandomByte(writer, limit, 2);
    }

    public void write2RandomByte(Writer writer) {
        write2RandomByte(writer, new Random().nextInt(25565));
    }

    public ConfigUtil set(Object key, Object... configKeysValues) throws IllegalArgumentException {
        setConf(false, key, configKeysValues);
        return this;
    }

    public ConfigUtil setConf(boolean init, Object key, Object... configKeysValues) throws IllegalArgumentException {
        if(configKeysValues.length > 1 & configKeysValues.length % 2 != 0)
            throw new IllegalArgumentException("values argument size need Integral multiple of 2, but argument size " + configKeysValues.length + " not Integral multiple of 2");
        configs.put(key, new Config<>(key, configKeysValues, false));
        if(autoWrite & ! init) {
            try {
                writeConfig();
            } catch (Exception e) {

            }
        }
        return this;
    }

    public ConfigUtil setList(Object key, Object... configValues) {
        setListConf(false, key, configValues);
        return this;
    }

    public ConfigUtil setListConf(boolean init, Object key, Object... configValues) {
        configs.put(key, new Config<>(key, configValues, true));
        if(autoWrite & ! init) {
            try {
                writeConfig();
            } catch (Exception e) {

            }
        }
        return this;
    }


    public String toString() {
        StringBuilder builder = new StringBuilder();
        for(Object o : configs.keySet()) {
            builder.append(o.toString()).append("=").append(configs.get(o).toString()).append(", ");
        }

        try {
            builder.replace(builder.length() - 2, builder.length(), "");
        } catch (Exception e) {

        }

        return "ConfigUtil(" + builder + ")";
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        JSONArray addToConfig = new JSONArray();
        for(Object o : configs.keySet()) {
            addToConfig.put(configs.get(o.toString()).toJSONObject());
        }

        json.put("configs", addToConfig);

        JSONObject manifest = new JSONObject();
        manifest.put("configVersion", utilConfigs.get("version"));
        manifest.put("configsTotal", configs.size());
        manifest.put("encryption", encryption);
        manifest.put("config", new File(utilConfigs.get("path") + "/" + utilConfigs.get("name")).getAbsolutePath());
        json.put("manifest", manifest);

        return json;
    }

    public boolean equal(ConfigUtil configUtil1, ConfigUtil configUtil2) {
        return configUtil1.toString().equals(configUtil2.toString());
    }

    public boolean equal(ConfigUtil configUtil) {
        return configUtil.toString().equals(this.toString());
    }

    public String formatNote() {
        if(note != null) {
            try {
                BufferedReader reader = new BufferedReader(new StringReader(note));
                StringBuilder builder = new StringBuilder("/**\n");

                String cache;
                while((cache = reader.readLine()) != null)
                    builder.append(" * ").append(cache).append("\n");
                builder.append(" */");

                return builder.toString();
            } catch (Exception e) {
                return "";
            }
        } else {
            return "";
        }
    }

    public boolean canShutdown() {
        return canShutdown;
    }

    public void setShuttingDown(boolean shuttingDown) {
        this.shuttingDown = shuttingDown;
    }

    public void shutdown() {
        logger.info("saving configs and shutting down ConfigUtil");
        try {
            while(! canShutdown()) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {

                }
            }

            writeConfig();

            logger.info("all config are saved, shutting down");
        } catch (Exception e) {
            logger.error("failed to save configs, shutting down");
        }
        setShuttingDown(true);
        while(! canShutdown()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {

            }
        }
        shutdown = true;
        logger.info("ConfigUtil are shutdown");
    }

    public int getConfigTotal() {
        return configs.size();
    }

    public boolean isShutdown() {
        return shutdown;
    }
}

