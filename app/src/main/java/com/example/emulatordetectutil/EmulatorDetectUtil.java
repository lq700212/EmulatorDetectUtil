package com.example.emulatordetectutil;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import static android.content.Context.SENSOR_SERVICE;
import static com.example.emulatordetectutil.CheckResult.RESULT_EMULATOR;
import static com.example.emulatordetectutil.CheckResult.RESULT_MAYBE_EMULATOR;
import static com.example.emulatordetectutil.CheckResult.RESULT_UNKNOWN;

/**
 * Author: ryan.lei
 * Date: 2020/1/22 10:24
 * Description: 新的模拟器识别工具类
 */
public class EmulatorDetectUtil {

    private static final String TAG = "EmulatorDetectUtil";

    private EmulatorDetectUtil() {

    }

    private static class SingletonHolder {
        private static final EmulatorDetectUtil INSTANCE = new EmulatorDetectUtil();
    }

    public static final EmulatorDetectUtil getSingleInstance() {
        return SingletonHolder.INSTANCE;
    }

    public boolean isEmulator(Context context) {
        if (context == null)
            throw new IllegalArgumentException("context must not be null");

        int suspectCount = 0;

        //在检测特征值前面可以先进性检测cpu架构
        if(isPcKernel()) {
            return true;
        }

        //检测硬件名称
        CheckResult hardwareResult = checkFeaturesByHardware();
        Log.d(TAG, "hardwareResult: " + hardwareResult.result);
        switch (hardwareResult.result) {
            case RESULT_MAYBE_EMULATOR:
                ++suspectCount;
                break;
            case RESULT_EMULATOR:
                return true;
        }

        //查询桌面应用
        if(queryAppsWithLauncher(context, "com.bignox.app.store.hd")
                || queryAppsWithLauncher(context, "com.microvirt.market")){
            return true;
        }

//        //检测渠道
//        CheckResult flavorResult = checkFeaturesByFlavor();
//        Log.d(TAG, "flavorResult: " + flavorResult.result);
//        switch (flavorResult.result) {
//            case RESULT_MAYBE_EMULATOR:
//                ++suspectCount;
//                break;
//            case RESULT_EMULATOR:
//                return true;
//        }
//
//        //检测设备型号
//        CheckResult modelResult = checkFeaturesByModel();
//        Log.d(TAG, "modelResult: " + modelResult.result);
//        switch (modelResult.result) {
//            case RESULT_MAYBE_EMULATOR:
//                ++suspectCount;
//                break;
//            case RESULT_EMULATOR:
//                return true;
//        }
//
//        //检测硬件制造商
//        CheckResult manufacturerResult = checkFeaturesByManufacturer();
//        Log.d(TAG, "manufacturerResult: " + manufacturerResult.result);
//        switch (manufacturerResult.result) {
//            case RESULT_MAYBE_EMULATOR:
//                ++suspectCount;
//                break;
//            case RESULT_EMULATOR:
//                return true;
//        }
//
//        //检测主板名称
//        CheckResult boardResult = checkFeaturesByBoard();
//        Log.d(TAG, "boardResult: " + boardResult.result);
//        switch (boardResult.result) {
//            case RESULT_MAYBE_EMULATOR:
//                ++suspectCount;
//                break;
//            case RESULT_EMULATOR:
//                return true;
//        }
//
//        //检测主板平台
//        CheckResult platformResult = checkFeaturesByPlatform();
//        Log.d(TAG, "platformResult: " + platformResult.result);
//        switch (platformResult.result) {
//            case RESULT_MAYBE_EMULATOR:
//                ++suspectCount;
//                break;
//            case RESULT_EMULATOR:
//                return true;
//        }
//
//        //检测基带信息
//        CheckResult baseBandResult = checkFeaturesByBaseBand();
//        Log.d(TAG, "baseBandResult: " + baseBandResult.result);
//        switch (baseBandResult.result) {
//            case RESULT_MAYBE_EMULATOR:
//                suspectCount += 2;//模拟器基带信息为null的情况概率相当大
//                break;
//            case RESULT_EMULATOR:
//                return true;
//        }
//
//        //检测传感器数量
//        int sensorNumber = getSensorNumber(context);
//        Log.d(TAG, "sensorNumber = " + sensorNumber);
//        //传感器太少的有可能是低配或是老旧的真机平板
//        if (sensorNumber <= 7 && sensorNumber >= 4) {
//            ++suspectCount;
//        }

        //检测已安装第三方应用数量
//        int userAppNumber = getUserAppNumber();
//        Log.d(TAG, "userAppNumber = " + userAppNumber);
//        if (userAppNumber <= 5) {
//            ++suspectCount;
//        }

//        //检测是否支持闪光灯
//        boolean supportCameraFlash = supportCameraFlash(context);
//        Log.d(TAG, "supportCameraFlash = " + supportCameraFlash);
//        if (!supportCameraFlash) {
//            ++suspectCount;
//        }
//        //检测是否支持相机
//        boolean supportCamera = supportCamera(context);
//        Log.d(TAG, "supportCamera = " + supportCamera);
//        if (!supportCamera) {
//            ++suspectCount;
//        }
//        //检测是否支持蓝牙
//        boolean supportBluetooth = supportBluetooth(context);
//        Log.d(TAG, "supportBluetooth = " + supportBluetooth);
//        if (!supportBluetooth) {
//            ++suspectCount;
//        }

//        //检测光线传感器
//        boolean hasLightSensor = hasLightSensor(context);
//        Log.d(TAG, "hasLightSensor = " + hasLightSensor);
//        if (!hasLightSensor) {
//            ++suspectCount;
//        }

        //先去掉，有的平板真机也没有该传感器
        //检测距离传感器
//        boolean hasProximitySensor = hasProximitySensor(context);
//        Log.d(TAG, "hasProximitySensor = " + hasProximitySensor);
//        if (!hasProximitySensor) {
//            ++suspectCount;
//        }

//        检测进程组信息
//        CheckResult cgroupResult = checkFeaturesByCgroup();
//        Log.d(TAG, "cgroupResult = " + cgroupResult);
//        if (cgroupResult.result == RESULT_MAYBE_EMULATOR) {
//            ++suspectCount;
//        }

//        return suspectCount > 3;
        return false;
    }

    private int getUserAppNum(String userApps) {
        if (TextUtils.isEmpty(userApps)) return 0;
        String[] result = userApps.split("package:");
        return result.length;
    }

    private String getProperty(String propName) {
        String property = CommandUtil.getSingleInstance().getProperty(propName);
        return TextUtils.isEmpty(property) ? null : property;
    }

    /**
     * cat /proc/cpuinfo
     * 从cpuinfo中读取cpu架构
     */
    private boolean isPcKernel(){
        String str = "";
        try {
            Process start = new ProcessBuilder(new String[]{"/system/bin/cat", "/proc/cpuinfo"}).start();
            StringBuffer stringBuffer = new StringBuffer();
            String str2 = "";
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(start.getInputStream(), "utf-8"));
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                stringBuffer.append(readLine);
            }
            bufferedReader.close();
            str = stringBuffer.toString().toLowerCase();
        } catch (Exception e) {
        }
        if (str.contains("intel") || str.contains("amd")) {
            return true;
        }
        return false;
    }

    /**
     * 特征参数-硬件名称
     *
     * @return 0表示可能是模拟器，1表示模拟器，2表示可能是真机
     */
    private CheckResult checkFeaturesByHardware() {
        String hardware = getProperty("ro.hardware");
        if (null == hardware) return new CheckResult(RESULT_MAYBE_EMULATOR, null);
        int result;
        String tempValue = hardware.toLowerCase();
        Log.d(TAG, "checkFeaturesByHardware: " + tempValue);
        switch (tempValue) {
            case "ttvm"://天天模拟器
            case "ttvm_x86":
            case "nox"://夜神模拟器4
            case "cancro"://网易MUMU模拟器
            case "intel"://逍遥模拟器
            case "vbox":
            case "vbox86"://腾讯手游助手
            case "android_x86"://雷电模拟器
                result = RESULT_EMULATOR;
                break;
            default:
                result = RESULT_UNKNOWN;
                break;
        }
        return new CheckResult(result, hardware);
    }

    /**
     * 特征参数-渠道
     *
     * @return 0表示可能是模拟器，1表示模拟器，2表示可能是真机
     */
    private CheckResult checkFeaturesByFlavor() {
        String flavor = getProperty("ro.build.flavor");
        if (null == flavor) return new CheckResult(RESULT_MAYBE_EMULATOR, null);
        int result;
        String tempValue = flavor.toLowerCase();
        if (tempValue.contains("vbox")) result = RESULT_EMULATOR;
        else if (tempValue.contains("sdk_gphone")) result = RESULT_EMULATOR;
        else result = RESULT_UNKNOWN;
        return new CheckResult(result, flavor);
    }

    /**
     * 特征参数-设备型号
     *
     * @return 0表示可能是模拟器，1表示模拟器，2表示可能是真机
     */
    private CheckResult checkFeaturesByModel() {
        String model = getProperty("ro.product.model");
        if (null == model) return new CheckResult(RESULT_MAYBE_EMULATOR, null);
        int result;
        String tempValue = model.toLowerCase();
        if (tempValue.contains("google_sdk")) result = RESULT_EMULATOR;
        else if (tempValue.contains("emulator")) result = RESULT_EMULATOR;
        else if (tempValue.contains("android sdk built for x86")) result = RESULT_EMULATOR;
        else result = RESULT_UNKNOWN;
        return new CheckResult(result, model);
    }

    /**
     * 特征参数-硬件制造商
     *
     * @return 0表示可能是模拟器，1表示模拟器，2表示可能是真机
     */
    private CheckResult checkFeaturesByManufacturer() {
        String manufacturer = getProperty("ro.product.manufacturer");
        if (null == manufacturer) return new CheckResult(RESULT_MAYBE_EMULATOR, null);
        int result;
        String tempValue = manufacturer.toLowerCase();
        if (tempValue.contains("genymotion")) result = RESULT_EMULATOR;
        else if (tempValue.contains("netease")) result = RESULT_EMULATOR;//网易MUMU模拟器
        else result = RESULT_UNKNOWN;
        return new CheckResult(result, manufacturer);
    }

    /**
     * 特征参数-主板名称
     *
     * @return 0表示可能是模拟器，1表示模拟器，2表示可能是真机
     */
    private CheckResult checkFeaturesByBoard() {
        String board = getProperty("ro.product.board");
        if (null == board) return new CheckResult(RESULT_MAYBE_EMULATOR, null);
        int result;
        String tempValue = board.toLowerCase();
        if (tempValue.contains("android")) result = RESULT_EMULATOR;
        else if (tempValue.contains("goldfish")) result = RESULT_EMULATOR;
        else result = RESULT_UNKNOWN;
        return new CheckResult(result, board);
    }

    /**
     * 特征参数-主板平台
     *
     * @return 0表示可能是模拟器，1表示模拟器，2表示可能是真机
     */
    private CheckResult checkFeaturesByPlatform() {
        String platform = getProperty("ro.board.platform");
        if (null == platform) return new CheckResult(RESULT_MAYBE_EMULATOR, null);
        int result;
        String tempValue = platform.toLowerCase();
        if (tempValue.contains("android")) result = RESULT_EMULATOR;
        else result = RESULT_UNKNOWN;
        return new CheckResult(result, platform);
    }

    /**
     * 特征参数-基带信息
     *
     * @return 0表示可能是模拟器，1表示模拟器，2表示可能是真机
     */
    private CheckResult checkFeaturesByBaseBand() {
        String baseBandVersion = getProperty("gsm.version.baseband");
        if (null == baseBandVersion) return new CheckResult(RESULT_MAYBE_EMULATOR, null);
        int result;
        if (baseBandVersion.contains("1.0.0.0")) result = RESULT_EMULATOR;
        else result = RESULT_UNKNOWN;
        return new CheckResult(result, baseBandVersion);
    }

    /**
     * 获取传感器数量
     */
    private int getSensorNumber(Context context) {
        SensorManager sm = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        return sm.getSensorList(Sensor.TYPE_ALL).size();
    }

    /**
     * 获取已安装第三方应用数量
     */
    private int getUserAppNumber() {
        String userApps = CommandUtil.getSingleInstance().exec("pm list package -3");
        return getUserAppNum(userApps);
    }

    /**
     * 是否支持相机
     */
    private boolean supportCamera(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /**
     * 是否支持闪光灯
     */
    private boolean supportCameraFlash(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    /**
     * 是否支持蓝牙
     */
    private boolean supportBluetooth(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
    }

    /**
     * 判断是否存在光传感器来判断是否为模拟器
     * 部分真机也不存在温度和压力传感器。其余传感器模拟器也存在。
     *
     * @return false为模拟器
     */
    private boolean hasLightSensor(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT); //光线传感器
        if (null == sensor) return false;
        else return true;
    }

    /**
     * 判断是否存在距离传感器来判断是否为模拟器
     * 部分真机也不存在温度和压力传感器。其余传感器模拟器也存在。
     *
     * @return false为模拟器
     */
    private boolean hasProximitySensor(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY); //距离传感器
        if (null == sensor) return false;
        else return true;
    }

    /**
     * 特征参数-进程组信息
     */
    private CheckResult checkFeaturesByCgroup() {
        String filter = CommandUtil.getSingleInstance().exec("cat /proc/self/cgroup");
        if (null == filter) return new CheckResult(RESULT_MAYBE_EMULATOR, null);
        return new CheckResult(RESULT_UNKNOWN, filter);
    }

    /**
     * 获取桌面有图标的应用 可指定报名查询
     * 模拟器一般会有自己的应用市场app, 可以此做识别依据
     *
     * @param context
     * @param packageName 指定包名查询, 传null则查询所有
     * @return 是否有查询到的应用列表 false没有查到, true有查到
     */
    private boolean queryAppsWithLauncher(Context context, String packageName) {
        Intent intent = new Intent();
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setAction(Intent.ACTION_MAIN);
        if (packageName != null) {
            intent.setPackage(packageName);
        }

        final PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(intent, 0);

        return resolveInfoList.size() > 0;
    }
}
