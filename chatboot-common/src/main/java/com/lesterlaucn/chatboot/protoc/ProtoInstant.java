package com.lesterlaucn.chatboot.protoc;

public class ProtoInstant {

    /**
     * 魔数，可以通过配置获取
     */
    public static final short MAGIC_CODE = 0x86;

    /**
     * 版本号
     */
    public static final short VERSION_CODE = 0x01;

    /**
     * 客户端平台
     */
    public interface Platform {
        /**
         * windows
         */
        int WINDOWS = 1;

        /**
         * mac
         */
        int MAC = 2;
        /**
         * android端
         */
        int ANDROID = 3;
        /**
         * IOS端
         */
        int IOS = 4;
        /**
         * WEB端
         */
        int WEB = 5;
        /**
         * 未知
         */
        int UNKNOWN = 6;


    }


    /**
     * 返回码枚举类
     */
    public enum ResultCodeEnum {

        SUCCESS(0, "Success"),  // 成功
        AUTH_FAILED(1, "登录失败"),
        NO_TOKEN(2, "没有授权码"),
        UNKNOW_ERROR(3, "未知错误"),
        ;

        private Integer code;
        private String desc;

        ResultCodeEnum(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public Integer getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }

    }

}
