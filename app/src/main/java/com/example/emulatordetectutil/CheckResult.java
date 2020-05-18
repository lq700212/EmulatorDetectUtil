package com.example.emulatordetectutil;

/**
 * Author: ryan.lei
 * Date: 2020/1/22 10:26
 * Description: 模拟器检测的结果
 */
public class CheckResult {
    public static final int RESULT_MAYBE_EMULATOR = 0;//可能是模拟器
    public static final int RESULT_EMULATOR = 1;//模拟器
    public static final int RESULT_UNKNOWN = 2;//可能是真机

    public int result;
    public String value;

    public CheckResult(int result, String value) {
        this.result = result;
        this.value = value;
    }
}
