package com.yinfeng.wypzh.http.common;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.yinfeng.wypzh.bean.BaseBean;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author Asen
 */
public class BaseBeanTypedAdapter<T> implements JsonDeserializer<BaseBean<T>> {
    @Override
    public BaseBean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        int code = obj.get("code").getAsInt();
        String message = obj.get("message").getAsString();
        BaseBean bean = new BaseBean();
        bean.setCode(code);
        bean.setMessage(message);
        if (code != ApiContents.CODE_REQUEST_SUCCESS) {
            bean.setResult(new Object());
        } else {
            try {
                bean.setResult(context.deserialize(obj.get("data"), ((ParameterizedType) typeOfT).getActualTypeArguments()[0]));
            } catch (Exception e) {
                String data = obj.get("data").getAsString();
                bean.setResult(data);
            }
        }
        return bean;
    }
}

