# flutter_avatar

#注意事项

1.本插件仅仅支持Android；

2.插件要求Android sdk最小版本为24，即：gradle中配置必须为：minSdkVersion 24；

#使用

1.插件github地址：

    https://github.com/openunion-ygz/flutter_avatar

2.initAvatar():

    初始化方法，改方法主要进行资源的初始化：必要权限的申请，以及插件资源的加载 初始化等，推荐程序开始的时候进行初始化，同时，该方法

    是其他方法正常调用的前提，必须首先调用该方法。

3.unInitialize():

    释放资源，在程序退出时，释放资源防止内存溢出

4.avatarListener():

    注册与智能机器人的语音交互监听

    1)通过该方法可以获取用户对智能机器人的语音转化的文字信息，以及智能机器人的应答文字信息：{"orgtext":"嗯","param":"这样对话就不能友好进行下去了……"},

    "orgtext"表示用户对机器人的语音转文字信息，“param”表示智能机器人对用户的应答语音转文字信息

    2)智能机器人初始化结果：{"curModule":"AVATAR","curStatus":"1","curSubmodule":"BODY","failed":"0","progress":"100"}，其中，progress表示

    初始化的进度

5.avatarStart():

    唤醒/显示智能机器人

6.avatarStop():

    休眠/隐藏智能机器人

7.avatarActions():

    智能机器人动作控制

8.avatarExpression():

    智能机器人表情控制

9.avatarSpeechMouth():

    智能机器人张嘴/合嘴动作

10.avatarTowardTo():

    改变智能机器人脸部朝向

11.avatarChangePos():

    改变智能机器人在屏幕的位置

12.avatarSpeak():

    控制智能机器人说话

13.avatarSwitchDragMode():

    允许智能机器人在手机屏幕拖拽（默认不允许拖拽）
