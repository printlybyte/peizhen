package com.yinfeng.wypzh.download;

/**
 * @author Asen
 */
public interface JsDownloadListener {
        void onStartDownload(long length);
        void onProgress(int progress);
        void onFail(String errorInfo);
}
