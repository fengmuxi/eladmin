/*
 * Copyright 2019-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.zhengjie.utils;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * @author: liaojinlong
 * @date: 2020/6/11 16:28
 * @apiNote: JDK 8  新日期类 格式化与字符串转换 工具类
 */
public class DateUtil {

    public static final DateTimeFormatter DFY_MD_HMS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DFY_MD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * LocalDateTime 转时间戳
     *
     * @param localDateTime /
     * @return /
     */
    public static Long getTimeStamp(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond();
    }

    /**
     * 时间戳转LocalDateTime
     *
     * @param timeStamp /
     * @return /
     */
    public static LocalDateTime fromTimeStamp(Long timeStamp) {
        return LocalDateTime.ofEpochSecond(timeStamp, 0, OffsetDateTime.now().getOffset());
    }

    /**
     * LocalDateTime 转 Date
     * Jdk8 后 不推荐使用 {@link Date} Date
     *
     * @param localDateTime /
     * @return /
     */
    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * LocalDate 转 Date
     * Jdk8 后 不推荐使用 {@link Date} Date
     *
     * @param localDate /
     * @return /
     */
    public static Date toDate(LocalDate localDate) {
        return toDate(localDate.atTime(LocalTime.now(ZoneId.systemDefault())));
    }


    /**
     * Date转 LocalDateTime
     * Jdk8 后 不推荐使用 {@link Date} Date
     *
     * @param date /
     * @return /
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * 日期 格式化
     *
     * @param localDateTime /
     * @param patten /
     * @return /
     */
    public static String localDateTimeFormat(LocalDateTime localDateTime, String patten) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(patten);
        return df.format(localDateTime);
    }

    /**
     * 日期 格式化
     *
     * @param localDateTime /
     * @param df /
     * @return /
     */
    public static String localDateTimeFormat(LocalDateTime localDateTime, DateTimeFormatter df) {
        return df.format(localDateTime);
    }

    /**
     * 日期格式化 yyyy-MM-dd HH:mm:ss
     *
     * @param localDateTime /
     * @return /
     */
    public static String localDateTimeFormatyMdHms(LocalDateTime localDateTime) {
        return DFY_MD_HMS.format(localDateTime);
    }

    /**
     * 日期格式化 yyyy-MM-dd
     *
     * @param localDateTime /
     * @return /
     */
    public String localDateTimeFormatyMd(LocalDateTime localDateTime) {
        return DFY_MD.format(localDateTime);
    }

    /**
     * 字符串转 LocalDateTime ，字符串格式 yyyy-MM-dd
     *
     * @param localDateTime /
     * @return /
     */
    public static LocalDateTime parseLocalDateTimeFormat(String localDateTime, String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.from(dateTimeFormatter.parse(localDateTime));
    }

    /**
     * 字符串转 LocalDateTime ，字符串格式 yyyy-MM-dd
     *
     * @param localDateTime /
     * @return /
     */
    public static LocalDateTime parseLocalDateTimeFormat(String localDateTime, DateTimeFormatter dateTimeFormatter) {
        return LocalDateTime.from(dateTimeFormatter.parse(localDateTime));
    }

    /**
     * 字符串转 LocalDateTime ，字符串格式 yyyy-MM-dd HH:mm:ss
     *
     * @param localDateTime /
     * @return /
     */
    public static LocalDateTime parseLocalDateTimeFormatyMdHms(String localDateTime) {
        return LocalDateTime.from(DFY_MD_HMS.parse(localDateTime));
    }

    /**
     * 获取当前时间
     */
    public static Date getNowDate(){
        return Calendar.getInstance().getTime();
    }

    /**
     * 获取当前时间 - N个年
     */
    public static Date getNowDateMinusYear(Integer n){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR,- n);
        return cal.getTime();
    }

    /**
     * 获取当前时间 + N个年
     */
    public static Date getNowDateAddYear(Integer n){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR,+ n);
        return cal.getTime();
    }

    /**
     * 获取当前时间 - N个月
     */
    public static Date getNowDateMinusMonth(Integer n){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH,- n);
        return cal.getTime();
    }

    /**
     * 获取当前时间 + N个月
     */
    public static Date getNowDateAddMonth(Integer n){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH,+ n);
        return cal.getTime();
    }

    /**
     * 获取当前时间 - N个周
     */
    public static Date getNowDateMinusWeek(Integer n){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.WEEK_OF_YEAR,- n);
        return cal.getTime();
    }

    /**
     * 获取当前时间 + N个周
     */
    public static Date getNowDateAddWeek(Integer n){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.WEEK_OF_YEAR,+ n);
        return cal.getTime();
    }

    /**
     * 获取当前时间 - N个天
     */
    public static Date getNowDateMinusDay(Integer n){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR,- n);
        return cal.getTime();
    }

    /**
     * 获取当前时间 + N个天
     */
    public static Date getNowDateAddDay(Integer n){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR,+ n);
        return cal.getTime();
    }

    /**
     * 时间 + N个天
     */
    public static Date getDateAddDay(Date date,Integer n){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR,+ n);
        return cal.getTime();
    }

    /**
     * 判断该日期是否是周一
     */
    public static boolean isFirstDayOfWeek(Date dt) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        //java中，一周中的第一天是周日，但我们生活中一周中的第一天指的是周一
        return cal.get(Calendar.DAY_OF_WEEK) == 2;
    }

    /**
     * 判断该日期是否是该月的第一天
     */
    public static boolean isFirstDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH) == 1;
    }

    /**
     * 判断该日期是否是该年的第一天
     */
    public static boolean isFirstDayOfYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_YEAR) == 1;
    }

    /**
     * 判断该日期是否是季度的第一天
     */
    public static boolean isFirstDayOfQuarter(Date date) {
        String mMdd = new SimpleDateFormat("MMdd").format(date);
        return "0101".equals(mMdd) || "0401".equals(mMdd) || "0701".equals(mMdd) || "1001".equals(mMdd);
    }

    /**
     * 比较查询日期是否是早于当前日期，早于true,晚于false
     */
    public static boolean getCompareDate(Date date) {
        Date nowDate = getNowDate();
        if(date.before(nowDate)||date.equals(nowDate)){
            return  true;
        }else{
            return false;
        }
    }
}
