package com.yinfeng.wypzh.listener;

/**
 * @author Asen
 */
public interface OnGlideCacheGetListener {
    void onSuccess(String filePath, String fileNameWithSuffix, String suffix);

    void onFail();
}
