package pers.gaylong9;

import kotlin.reflect.KType;
import net.mamoe.mirai.console.data.Value;
import net.mamoe.mirai.console.data.java.JavaAutoSavePluginData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyPluginData extends JavaAutoSavePluginData {

    public static final MyPluginData INSTANCE = new MyPluginData();

    private MyPluginData() {
        // 文件名，xxx.yml
        super("pers.gaylong9.WeatherForecastPlugin.data");
    }


    /** 群聊订阅, key：群id， value：城市名列表*/
    // private HashMap<Long, ArrayList<String>> groupSubscribes = new HashMap<>();
    public final Value<Map<Long, List<String>>> groupSubscribes = typedValue(
            "groupSubscribes",
            createKType(Map.class,
                    createKType(Long.class),
                    createKType(List.class, createKType(String.class)))
    );



    /**好友订阅*/
    // private HashMap<Long, ArrayList<String>> friendSubscribes = new HashMap<>();
    public final Value<Map<Long, List<String>>> friendSubscribes = typedValue(
            "friendSubscribes",
            createKType(Map.class,
                    createKType(Long.class),
                    createKType(List.class, createKType(String.class)))
    );

}
