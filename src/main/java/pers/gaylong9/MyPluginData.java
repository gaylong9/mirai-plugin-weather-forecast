package pers.gaylong9;

import kotlin.reflect.KType;
import net.mamoe.mirai.console.data.Value;
import net.mamoe.mirai.console.data.java.JavaAutoSavePluginData;

import java.util.*;

public class MyPluginData extends JavaAutoSavePluginData {

    public static final MyPluginData INSTANCE = new MyPluginData();

    private MyPluginData() {
        // 文件名，xxx.yml
        super("pers.gaylong9.WeatherForecastPlugin.data");
    }


    /**
     * 群聊订阅, key：群id，
     * value：城市信息map
     *      key：city id
     *      value：city name
     * */
    public final Value<Map<Long, Map<String, String>>> groupSubscribes = typedValue(
            "groupSubscribes",
            createKType(
                    Map.class,
                    createKType(Long.class),
                    createKType(
                            Map.class,
                            createKType(String.class),
                            createKType(String.class)
                    )
            )
    );



    /**好友订阅，key为用户id，value同群聊订阅*/
    public final Value<Map<Long, Map<String, String>>> friendSubscribes = typedValue(
            "friendSubscribes",
            createKType(
                    Map.class,
                    createKType(Long.class),
                    createKType(
                            Map.class,
                            createKType(String.class),
                            createKType(String.class)
                    )
            )
    );

    /**重名市区*/
    public final Value<Set<String>> duplicateRegions = typedValue(
            "duplicateRegions",
            createKType(Set.class,
                    createKType(String.class)
            ),
            Set.of("朝阳",
                    "通州",
                    "宝山",
                    "长宁",
                    "普陀",
                    "和平",
                    "河东",
                    "江北",
                    "甘南",
                    "东安",
                    "西安",
                    "向阳",
                    "郊区",
                    "西林",
                    "大同",
                    "新兴",
                    "梨树",
                    "南山",
                    "兴安",
                    "东山",
                    "兴山",
                    "宽城",
                    "昌邑",
                    "铁西",
                    "铁东",
                    "大安",
                    "龙山",
                    "中山",
                    "海城",
                    "平山",
                    "东港",
                    "太和",
                    "海州",
                    "清河",
                    "连山",
                    "新城",
                    "青山",
                    "海南",
                    "长安",
                    "桥西",
                    "新华",
                    "桥东",
                    "开平",
                    "矿区",
                    "山阳",
                    "市中",
                    "栖霞",
                    "河口",
                    "沙湾",
                    "城关",
                    "大通",
                    "城中",
                    "安宁",
                    "金川",
                    "东乡",
                    "襄城",
                    "鼓楼",
                    "鹤山",
                    "云龙",
                    "华容",
                    "西湖",
                    "象山",
                    "台江",
                    "永定",
                    "昌江",
                    "资阳",
                    "白云",
                    "钟山",
                    "东兴",
                    "九龙",
                    "南沙",
                    "兴宁",
                    "五华",
                    "金平",
                    "江城"
            )
    );

}
