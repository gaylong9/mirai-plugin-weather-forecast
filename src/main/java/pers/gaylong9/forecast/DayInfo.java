package pers.gaylong9.forecast;

/**
 * 天气网站API返回结果中一天信息的封装类
 */
public class DayInfo {
    /**
     * 日期
     */
    public String date;
    /**
     * 天气
     */
    public String wea;
    /**
     * 天气图标名
     */
    public String wea_img;
    /**
     * 最高温
     */
    public String tem_day;
    /**
     * 最低温
     */
    public String tem_night;
    /**
     * 风向
     */
    public String win;
    /**
     * 风级
     */
    public String win_speed;
}
