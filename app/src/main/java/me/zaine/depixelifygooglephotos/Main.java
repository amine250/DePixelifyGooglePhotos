/*
 * Copyright (C) 2022 Amine Zaine
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.zaine.depixelifygooglephotos;

import android.os.Build;
import android.util.Log;
import androidx.annotation.RequiresApi;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class Main implements IXposedHookLoadPackage {

    private static final String TAG = Main.class.getCanonicalName();
    private static Map<String, Object> propsToChange;

    /**
     * Simple message to log messages in lsposed log as well as android log.
     */
    private static void log(String message){
        XposedBridge.log(TAG+": "+message);
        Log.i(TAG, message);
    }


    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        // If we are not inside Google Photos, do nothing
        if (!lpparam.packageName.equals("com.google.android.apps.photos"))
            return;

        // If the persist.sys.pixelprops.gphotos prop is ON, do nothing
        if(SystemProperties.getBoolean("persist.sys.pixelprops.gphotos",false)){
            log("User wants Pixelification. Doing nothing.");
            return;
        }

        // Constructing a Map<K,V> of the props to change
        // We need to store the followings props before they are altered by PixelPropsUtils
        propsToChange = new HashMap<>();
        propsToChange.put("BRAND", Build.BRAND);
        propsToChange.put("MANUFACTURER", Build.MANUFACTURER);
        propsToChange.put("DEVICE", Build.DEVICE);
        propsToChange.put("PRODUCT", Build.PRODUCT);
        propsToChange.put("MODEL", Build.MODEL);
        propsToChange.put("FINGERPRINT", Build.FINGERPRINT);

        // Xposed shenanigans
        Class clazz = XposedHelpers.findClass("com.android.internal.util.evolution.PixelPropsUtils", lpparam.classLoader);
        findAndHookMethod(
                clazz,
                "setProps",
                "java.lang.String",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        log("We are inside " + lpparam.packageName + " and the method parameter is the package : " + param.args[0]);

                        // Double check the parameter name
                        if (param.args[0].equals("com.google.android.apps.photos")) {
                            for (Map.Entry<String, Object> prop : propsToChange.entrySet()) {
                                String key = prop.getKey();
                                Object value = prop.getValue();

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    setPropValue(key, value);
                                } else {
                                    Log.e(TAG, "Android is SDK is too low");
                                }
                            }
                            log("Google Photos dePixelification successful");
                        }
                    }
                });
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static void setPropValue(String key, Object value) {
        try {
            Field field = Build.class.getDeclaredField(key);
            field.setAccessible(true);
            field.set(null, value);
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e(TAG, "Failed to set prop " + key, e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static void getPropValue(String key) {
        try {
            Field field = Build.class.getDeclaredField(key);
            log(field.get(null).toString());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e(TAG, "Failed to set prop " + key, e);
        }
    }
}
