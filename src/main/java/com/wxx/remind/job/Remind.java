package com.wxx.remind.job;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.wxx.remind.vo.HolidayInfo;
import com.wxx.remind.vo.HolidayTts;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
@EnableScheduling
public class Remind {

    /**
     * 获取今天是否上班信息
     */
    private static final String HOLIDAY_INFO = "http://timor.tech/api/holiday/info/";

    /**
     * 获取下班提醒
     */
    private static final String HOLIDAY_TTS = "http://timor.tech/api/holiday/tts";

    /**
     * server酱的推送地址
     */
    private static final String PUSH_URL = "https://sc.ftqq.com/SCU81873T41164b9f636e62afc01dd8eb552c0ddd5e4200c6d1404.send";

    /**
     * 每天早上8：45提醒
     */
    @Scheduled(cron = "0 45 08 * * ?")
    public void morningRemind() {
        String day = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String json = HttpUtil.get(HOLIDAY_INFO + day);
        HolidayInfo holidayInfo = JSONObject.parseObject(json, HolidayInfo.class);
        if (holidayInfo.getCode().equals(0)) {
            // 0节假日服务正常
            HolidayInfo.Type typeVO = holidayInfo.getType();
            Integer type = typeVO.getType();
            if (type.equals(0) || type.equals(3)) {
                // 如果是工作日和调休，发送打卡提醒
                this.push("记得打卡", "");
            }


        } else {
            // -1节假日服务出错
            this.push("节假日服务出错", "");
        }
    }

    @Scheduled(cron = "0 03 18 * * ?")
    public void afternoonRemind() {
        String json = HttpUtil.get(HOLIDAY_TTS);
        HolidayTts holidayTts = JSONObject.parseObject(json, HolidayTts.class);
        if (holidayTts.getCode().equals(0)) {
            // 如果是工作日和调休，发送打卡提醒
            this.push(holidayTts.getTts(), "");
        } else {
            // -1节假日服务出错
            this.push("节假日服务出错", "");
        }
    }

    /**
     * 周末提醒
     */
    @Scheduled(cron = "0 45 17 ? * 6")
    public void weekEndRemind() {
        this.push("周五发送周报", "");
    }

    /**
     * 月末提醒
     */
    @Scheduled(cron = "0 45 17 L * ?")
    public void monthEndRemind() {
        this.push("月末发送月报", "");
    }

    /**
     * 推送消息
     * @param text 消息标题，最长为256，必填。
     * @param desp 消息内容，最长64Kb，可空，支持MarkDown。
     */
    public void push(String text, String desp) {
        Map<String, Object> paramMap = new HashMap<>(2);
        paramMap.put("text", text);
        if (StrUtil.isNotEmpty(desp)) {
            paramMap.put("desp", desp);
        }

        HttpRequest.post(PUSH_URL)
                .form(paramMap)//表单内容
                .execute().body();
    }

}
