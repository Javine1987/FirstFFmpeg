//
// Created by javine on 18-11-24.
//

#ifndef FIRSTFFMPEG_LOG_UTIL_H
#define FIRSTFFMPEG_LOG_UTIL_H

#include <jni.h>
#include <android/log.h>
#define TAG "JAVINE_APP"
#define LOGE(format, ...)  __android_log_print(ANDROID_LOG_ERROR, TAG, format, ##__VA_ARGS__)
#define LOGI(format, ...)  __android_log_print(ANDROID_LOG_INFO,  TAG, format, ##__VA_ARGS__)

#endif //FIRSTFFMPEG_LOG_UTIL_H
