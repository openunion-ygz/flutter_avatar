package com.opun.flutter_avatar;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ai7qi.avatarmanager.AvatarManagerHelper;
import com.alibaba.fastjson.JSON;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * FlutterAvatarPlugin
 */
public class FlutterAvatarPlugin implements FlutterPlugin, ActivityAware, MethodCallHandler, PluginRegistry.RequestPermissionsResultListener {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private MethodChannel channel;
    private static EventChannel.EventSink eventSink;
    private boolean isCheckPermission = false;
    private List<String> mPermissionList;
    private AvatarManagerHelper mAvatarMgr;
    private static Context mContext;
    private static Activity activity;
    public static final int REQUEST_CODE_PERMISSION = 10011;
    private boolean isInit = false;
    private int mAvatarPosLeft = 0;
    private int mAvatarPosTop = 0;
    private int mAvatarSize = 0;
    private boolean isInitializing = false;
    private static Registrar mRegistrar;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "flutter_avatar");
        channel.setMethodCallHandler(this);
        //*****插件的使用场景不一样，入口也对应不一样，因此mContext对象的获取需要在所有入口都获取，才能保证mContext不为null****
        mContext = flutterPluginBinding.getApplicationContext();
        //1.渠道名
        EventChannel eventChannel = new EventChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "listener");
        EventChannel.StreamHandler streamHandler = new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object o, EventChannel.EventSink sink) {
                //2.发射器
                eventSink = sink;
                Log.e("onAttachedToEngine ===>1",(eventSink == null)+"");
            }

            @Override
            public void onCancel(Object o) {
//                eventSink = null;
            }
        };
        eventChannel.setStreamHandler(streamHandler);
        Log.e("onAttachedToEngine ===>",(eventSink == null)+"");
    }

    @Override
    public void onAttachedToActivity(ActivityPluginBinding binding) {
        activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {

    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {

    }

    @Override
    public void onDetachedFromActivity() {

    }

    public static void registerWith(Registrar registrar) {
        mRegistrar = registrar;
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_avatar");
        channel.setMethodCallHandler(new FlutterAvatarPlugin());
        /*
        //1.渠道名
        EventChannel eventChannel = new EventChannel(registrar.messenger(), "listener");
        EventChannel.StreamHandler streamHandler = new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object o, EventChannel.EventSink sink) {
                //2.发射器
                eventSink = sink;
                Log.e("registerWith ===>1",(eventSink == null)+"");
            }

            @Override
            public void onCancel(Object o) {
//                eventSink = null;
            }
        };
        eventChannel.setStreamHandler(streamHandler);
        */
        //*****插件的使用场景不一样，入口也对应不一样，因此mContext对象的获取需要在所有入口都获取，才能保证mContext不为null****
        mContext = registrar.activeContext();
        activity = registrar.activity();

        Log.e("registerWith ===>2",(eventSink == null)+"");
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        if (call.method.equals("getPlatformVersion")) {
            result.success("Android " + android.os.Build.VERSION.RELEASE);
        } else if (call.method.equals("initialize")) {
            mAvatarPosLeft = Integer.parseInt(((String) call.argument("iLeft")));
            mAvatarPosTop = Integer.parseInt(((String) (call.argument("iTop"))));
            mAvatarSize = Integer.parseInt(((String) (call.argument("iAvatarSize"))));
            initialize();
            result.success(null);
        } else if (call.method.equals("unInitialize")) {
            result.success(unInitialize());
        } else if (call.method.equals("avatarStart")) {
            result.success(avatarStart());
        } else if (call.method.equals("avatarStop")) {
            result.success(avatarStop());
        } else if (call.method.equals("avatarActions")) {
            result.success(avatarActions(call));
        } else if (call.method.equals("avatarExpression")) {
            result.success(avatarExpression(call));
        } else if (call.method.equals("avatarSpeechMouth")) {
            result.success(avatarSpeechMouth(call));
        } else if (call.method.equals("avatarTowardTo")) {
            result.success(avatarTowardTo(call));
        } else if (call.method.equals("avatarChangePos")) {
            result.success(avatarChangePos(call));
        } else if (call.method.equals("avatarSpeak")) {
            result.success(avatarSpeak(call));
        } else if (call.method.equals("avatarSwitchDragMode")) {
            result.success(avatarSwitchDragMode(call));
        } else {
            result.notImplemented();
        }
    }

    private void initialize() {
        //检查权限
        try {
            String[] permissions = new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_NETWORK_STATE};

            mPermissionList = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(mContext, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permissions[i]);
                }
            }
            if (!mPermissionList.isEmpty()) {
                Toast.makeText(mContext, "正在请求权限", Toast.LENGTH_SHORT).show();
                String[] mPermissions = mPermissionList.toArray(new String[mPermissionList.size()]);
                ActivityCompat.requestPermissions(activity, mPermissions, REQUEST_CODE_PERMISSION);
            } else {
                isCheckPermission = true;
                avatarManagerInit();
            }
        } catch (Exception e) {
            e.printStackTrace();
            isInit = false;
        }
    }

    private void avatarManagerInit() {
        if (eventSink == null){
            //1.渠道名
            EventChannel eventChannel = new EventChannel(mRegistrar.messenger(), "listener");
            EventChannel.StreamHandler streamHandler = new EventChannel.StreamHandler() {
                @Override
                public void onListen(Object o, EventChannel.EventSink sink) {
                    //2.发射器
                    eventSink = sink;
                    Log.e("avatarManagerInit ===>1",(eventSink == null)+"");
                }

                @Override
                public void onCancel(Object o) {
//                eventSink = null;
                }
            };
            eventChannel.setStreamHandler(streamHandler);
        }

        if (isCheckPermission && isInit) {
            Toast.makeText(mContext, "初始化已完成", Toast.LENGTH_SHORT).show();
            return;
        }
        mAvatarMgr = new AvatarManagerHelper(activity);
//        mAvatarMgr.UnInitialize();
        mAvatarMgr.Initialize(mAvatarPosLeft, mAvatarPosTop, mAvatarSize, 0);
        mAvatarMgr.setEventListener(new AvatarManagerHelper.AvatarEventListen() {
            @Override
            public void onEvent(final String strJson) {
                Log.e("FlutterAvatarPlugin onEvent====>", strJson);
                /**
                 * {
                 * "head": { //事件头
                 * "name": "Event4AuthorizationInfo", //事件名称，区分不同的事件
                 * "time": "2019-04-29 09:52:49", //事件产生时间
                 * "ver": "I.001" //事件信息格式版本号，目前统一为 I.001
                 * },
                 * "body": { //事件体
                 * "xxxxx": "xxxx", //事件体字段，根据事件名称定义具体内容
                 * } }
                 */
                JSONObject jsonObject = null;
                JSONObject jsonObjectEvent = null;
                JSONObject jsonObjectBody = null;
                try {
                    jsonObject = new JSONObject(strJson);
                    String eventNameJsonStr = jsonObject.getString("head");
                    String bodyJsonStr = jsonObject.getString("body");
                    jsonObjectEvent = new JSONObject(eventNameJsonStr);
                    jsonObjectBody = new JSONObject(bodyJsonStr);
                    String authorResult = jsonObjectBody.getString("authorResult");
                    String eventName = jsonObjectEvent.getString("name");
                    // {"body":{"authorInfo":"Authorization from Cloud...FAILED && Authorization from license file: FAILED","authorResult":"FAILED","productSN":"2E8AF13B-8D87-523B-99D1-0B1F61C9C353"},"head":{"name":"Event4AuthorizationInfo","time":"2020-10-20 14:36:37","ver":"I.001"}}
                    if ("FAILED".equals(authorResult)) {
                        isInit = false;
                        isInitializing = false;
                        final String initResult = "{\"curModule\":\"AVATAR\",\"curStatus\":\"1\",\"curSubmodule\":\"BODY\",\"failed\":\"100\",\"failedMsg\":\"初始化异常，请检查是否安装智能机器人app\",\"progress\":\"0\"}";
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                eventSink.success(initResult);
                                eventSink.endOfStream();
                            }
                        });

                    }

                    if ("Event4ModuleLoaded".equals(eventName)) {
                        //{"body":{"curModule":"AVATAR","curStatus":"1","curSubmodule":"BODY","failed":"0","progress":"100"},"head":{"name":"Event4ModuleLoaded","time":"2020-09-22 13:35:47","ver":"I.001"}}
                        //启动事件监听
                        String progress = jsonObjectBody.getString("progress");
                        if (Integer.parseInt(progress) == 100) {
                            final String initResult = "{\"curModule\":\"AVATAR\",\"curStatus\":\"1\",\"curSubmodule\":\"BODY\",\"failed\":\"0\",\"failedMsg\":\"\",\"progress\":\"100\"}";
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    eventSink.success(initResult);
                                    eventSink.endOfStream();
                                    Toast.makeText(mContext, "虚拟机器人初始化完成", Toast.LENGTH_SHORT).show();
                                }
                            });
                            isInit = true;
                            isInitializing = false;
                        } else {
                            isInitializing = true;
                        }
                    } else if ("Event4VoiceParsed".equals(eventName)) {
                        // {"body":{"domain":"Chat","intent":"FreeTalk","orgtext":"嗯","param":"这样对话就不能友好进行下去了……"},"head":{"name":"Event4VoiceParsed","time":"2020-09-22 13:41:18","ver":"I.001"}}
                        //语音转文字监听
                        String orgtext = jsonObjectBody.getString("orgtext");
                        String param = jsonObjectBody.getString("param");
                        Map event4VoiceParseMap = new HashMap<String, String>();
                        event4VoiceParseMap.put(Constants.Event4VoiceParsed_ORGTEXT_KEY, orgtext);
                        event4VoiceParseMap.put(Constants.Event4VoiceParsed_PARAM_KEY, param);
                        final String result = JSON.toJSONString(event4VoiceParseMap);
                        Log.e("FlutterAvatarPlugin event4VoicePars ====>", result);
                        if (!orgtext.isEmpty() && !param.isEmpty()) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    eventSink.success(result);
                                    eventSink.endOfStream();
                                }
                            });
                        } else {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    eventSink.error("500", "Event4VoiceParsed Error", "语音转义异常");
                                    eventSink.endOfStream();
                                }
                            });
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    isInit = false;
                }
            }
        });

    }

    //释放资源
    private boolean unInitialize() {
        eventSink = null;
        isInit = false;
        if (mAvatarMgr != null) {
            mAvatarMgr.UnInitialize();
            mAvatarMgr = null;
            return true;
        } else {
            mAvatarMgr = new AvatarManagerHelper(activity);
            mAvatarMgr.UnInitialize();
            mAvatarMgr = null;
            return false;
        }
    }

    //启动/显示虚拟机器人
    private boolean avatarStart() {
        if (mAvatarMgr != null && isInit) {
            mAvatarMgr.showAvatar(true);
            return true;
        } else {
            if (!isInitializing) {
                initialize();
            }
            Toast.makeText(mContext, "正在初始化，请稍后", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //隐藏机器人
    private boolean avatarStop() {
        if (mAvatarMgr != null && isInit) {
            mAvatarMgr.showAvatar(false);
            return true;
        } else {
            if (!isInitializing) {
                initialize();
            }
            Toast.makeText(mContext, "正在初始化，请稍后", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //机器人动作控制
    private boolean avatarActions(MethodCall call) {
        String actionStr = call.argument("action");
        AvatarManagerHelper.Action77 action77 = ExpressionUtils.getAction(actionStr);
        if (mAvatarMgr != null && isInit) {
            mAvatarMgr.switch2Action(action77 == null ? AvatarManagerHelper.Action77.INTERACTION : action77);
            return true;
        } else {
            if (!isInitializing) {
                initialize();
            }
            Toast.makeText(mContext, "正在初始化，请稍后", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //机器人表情控制
    private boolean avatarExpression(MethodCall call) {
        String actionStr = call.argument("emotion");
        AvatarManagerHelper.Emotion77 emotion77 = ExpressionUtils.getEmotion(actionStr);
        if (mAvatarMgr != null && isInit) {
            mAvatarMgr.switch2Expression(emotion77 == null ? AvatarManagerHelper.Emotion77.CALM : emotion77);
            return true;
        } else {
            if (!isInitializing) {
                initialize();
            }
            Toast.makeText(mContext, "正在初始化，请稍后", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //机器人嘴巴张合控制
    private boolean avatarSpeechMouth(MethodCall call) {
        String speechMouth = call.argument("speechMouth");
        if (mAvatarMgr != null && isInit) {
            if ("1".equals(speechMouth)) {
                mAvatarMgr.switch2SpeechMouth(1);
            } else {
                mAvatarMgr.switch2SpeechMouth(0);
            }
            return true;
        } else {
            if (!isInitializing) {
                initialize();
            }
            Toast.makeText(mContext, "正在初始化，请稍后", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //机器人面部朝向控制
    private boolean avatarTowardTo(MethodCall call) {
        if (mAvatarMgr != null && isInit) {
            String fTurnXStr = call.argument("fTurnX");
            String fTurnYStr = call.argument("fTurnY");
            float fTurnX = Float.parseFloat(fTurnXStr);
            float fTurnY = Float.parseFloat(fTurnYStr);
            mAvatarMgr.switch2TowardTo(fTurnX, fTurnY);
            return true;
        } else {
            if (!isInitializing) {
                initialize();
            }
            Toast.makeText(mContext, "正在初始化，请稍后", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //机器人屏幕显示位置控制
    private boolean avatarChangePos(MethodCall call) {
        if (mAvatarMgr != null && isInit) {
            String iLeftStr = call.argument("iLeft");
            String iTopStr = call.argument("iTop");
            int iLeft = Integer.parseInt(iLeftStr);
            int iTop = Integer.parseInt(iTopStr);
            mAvatarMgr.switch2TowardTo(iLeft, iTop);
            return true;
        } else {
            if (!isInitializing) {
                initialize();
            }
            Toast.makeText(mContext, "正在初始化，请稍后", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //允许机器人在手机屏幕拖拽
    private boolean avatarSwitchDragMode(MethodCall call) {
        //dragMode
        if (mAvatarMgr != null && isInit) {
            boolean isDragMode = call.argument("dragMode");
            mAvatarMgr.switchDragMode(isDragMode);
            return true;
        } else {
            if (!isInitializing) {
                initialize();
            }
            Toast.makeText(mContext, "正在初始化，请稍后", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //机器人语音控制
    private boolean avatarSpeak(MethodCall call) {
        if (mAvatarMgr != null && isInit) {
            String speakContext = call.argument("speakContext");
            mAvatarMgr.speak(speakContext, AvatarManagerHelper.Priority77.NORMAL);
            return true;
        } else {
            if (!isInitializing) {
                initialize();
            }
            Toast.makeText(mContext, "正在初始化，请稍后", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSION) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    if (mPermissionList != null) {
                        mPermissionList.remove(0);
                        if (mPermissionList.isEmpty()) {
                            isCheckPermission = true;
                            avatarManagerInit();
                            return true;
                        }
                    }
                } else {
                    Toast.makeText(mContext, "虚拟人运行需要获得指定的全部权限", Toast.LENGTH_SHORT).show();
                    isCheckPermission = false;
                    isInit = false;
                    //重现申请权限
                    initialize();
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }
}
