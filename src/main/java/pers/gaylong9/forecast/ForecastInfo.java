package pers.gaylong9.forecast;

import java.util.List;

/**
 * 天气网站API返回结果的封装类
 */
public class ForecastInfo {
    public int nums;
    /**城市编号*/
    public String cityid;
    /**城市名*/
    public String city;
    /**数据更新时间*/
    public String update_time;
    /**7日数据，0号是本日，1号是明日*/
    public List<DayInfo> data;

}
