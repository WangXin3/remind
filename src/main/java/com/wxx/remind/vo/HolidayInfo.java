package com.wxx.remind.vo;

import lombok.Data;

@Data
public class HolidayInfo {

    /**
     * 0服务正常。-1服务出错
     */
    private Integer code;

    private Type type;

    private Holiday holiday;

    @Data
    public static class Type {
        /**
         * 节假日类型，分别表示 0-工作日-上班、1-周末-放假、2-节日-放假、3-调休-上班。
         */
        private Integer type;

        /**
         * 节假日类型中文名，可能值为 周一 至 周日、假期的名字、某某调休。
         */
        private String name;

        /**
         * 一周中的第几天。值为 1 - 7，分别表示 周一 至 周日。
         */
        private Integer week;
    }

    @Data
    public static class Holiday {

        /**
         * true表示是节假日，false表示是调休
         */
        private Boolean holiday;

        /**
         * 节假日的中文名。如果是调休，则是调休的中文名，例如'国庆前调休'
         */
        private String name;

        /**
         * 薪资倍数，1表示是1倍工资
         */
        private Integer wage;

        /**
         * 只在调休下有该字段。true表示放完假后调休，false表示先调休再放假
         */
        private Boolean after;

        /**
         * 只在调休下有该字段。表示调休的节假日
         */
        private String target;
    }
}
