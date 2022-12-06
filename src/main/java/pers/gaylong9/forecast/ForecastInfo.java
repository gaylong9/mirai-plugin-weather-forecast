package pers.gaylong9.forecast;

import java.util.List;

class DayInfo {
    /**日期*/
    public String date;
    /**天气*/
    public String wea;
    /**天气图标名*/
    public String wea_img;
    /**最高温*/
    public String tem_day;
    /**最低温*/
    public String tem_night;
    /**风向*/
    public String win;
    /**风级*/
    public String win_speed;
}

public class ForecastInfo {
    public int nums;
    public String cityid;
    /**城市名*/
    public String city;
    /**数据更新时间*/
    public String update_time;
    /**7日数据，0号是本日，1号是明日*/
    public List<DayInfo> data;

}
