package com.example.sid_fu.blecentral.db.entity;

import com.google.gson.Gson;

/**
 * author：Administrator on 2017/5/22 15:33
 * company: xxxx
 * email：1032324589@qq.com
 */

public class Result {

    /**
     * status : 0
     * msg : 登录成功
     */

    private StatusBean status;
    /**
     * phone : null
     * isDefault : 0
     * userCode : 13229100502
     * img : null
     * userName : 13229100502
     */

    private UserBean user;

    public static Result objectFromData(String str) {

        return new Gson().fromJson(str, Result.class);
    }

    public StatusBean getStatus() {
        return status;
    }

    public void setStatus(StatusBean status) {
        this.status = status;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public static class StatusBean {
        private String status;
        private String msg;

        public static StatusBean objectFromData(String str) {

            return new Gson().fromJson(str, StatusBean.class);
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }

    public static class UserBean {
        private String phone;
        private String isDefault;
        private String userCode;
        private Object img;
        private String userName;

        public static UserBean objectFromData(String str) {

            return new Gson().fromJson(str, UserBean.class);
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getIsDefault() {
            return isDefault;
        }

        public void setIsDefault(String isDefault) {
            this.isDefault = isDefault;
        }

        public String getUserCode() {
            return userCode;
        }

        public void setUserCode(String userCode) {
            this.userCode = userCode;
        }

        public Object getImg() {
            return img;
        }

        public void setImg(Object img) {
            this.img = img;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
}
