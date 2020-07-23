package com.yinfeng.wypzh.bean.order;

/**
 * ============================================
 * 描  述：
 * 包  名：com.yinfeng.wypzh.bean.order
 * 类  名：ConmonSsBean
 * 创建人：liuguodong
 * 创建时间：2019/8/26 14:20
 * ============================================
 **/
public class ConmonSsBean {

    /**
     * code : 200
     * message : 操作成功
     * data : {"code":"20190826053307","createTime":"2019-08-26 14:19:37","price":0,"id":"6b54f24216dc4768bff7a51cf2fb1e69"}
     * now : 2019-08-26 14:19:37
     */

    private int code;
    private String message;
    private DataBean data;
    private String now;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getNow() {
        return now;
    }

    public void setNow(String now) {
        this.now = now;
    }

    public static class DataBean {
        /**
         * code : 20190826053307
         * createTime : 2019-08-26 14:19:37
         * price : 0
         * id : 6b54f24216dc4768bff7a51cf2fb1e69
         */

        private String code;
        private String createTime;
        private int price;
        private String id;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
