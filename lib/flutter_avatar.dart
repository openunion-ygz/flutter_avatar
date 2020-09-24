import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_avatar/avatarActions.dart';
import 'package:flutter_avatar/avatarEmotions.dart';
import 'package:flutter_avatar/constants.dart';

class FlutterAvatar {
  static const MethodChannel _channel =
      const MethodChannel('${Constants.NAMESPACE}');
  static EventChannel _avatarListenerChannel = const EventChannel('listener');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  FlutterAvatar._() {
    // initAvatar();
  }

  static FlutterAvatar _instance = new FlutterAvatar._();

  static FlutterAvatar get instance => _instance;
  ///初始化
  void initAvatar(String iLeft,String iTop,{String iAvatarSize = '200'}){
    Map<String, dynamic> initConfigMap = new Map();
    initConfigMap['iLeft'] = iLeft;
    initConfigMap['iTop'] = iTop;
    initConfigMap['iAvatarSize'] = iAvatarSize;
    _channel.invokeListMethod('initialize',initConfigMap);
  }

  ///释放资源
  Future<bool> unInitialize(){
    return _channel
        .invokeMethod('unInitialize')
        .then<bool>((isUnInitialize) => isUnInitialize);
  }

  ///显示智能机器人
  Future<bool> avatarStart(){
    return _channel
        .invokeMethod('avatarStart')
        .then<bool>((isStart) => isStart);
  }

  ///隐藏智能机器人
  Future<bool> avatarStop(){
    return _channel
        .invokeMethod('avatarStop')
        .then<bool>((isStop) => isStop);
  }
  ///智能机器人动作
  Future<bool> avatarActions({String action = AvatarAction.INTERACTION}){
    Map<String, dynamic> actionsMap = new Map();
    actionsMap['action'] = action;
    return _channel
        .invokeMethod('avatarActions',actionsMap)
        .then<bool>((isDone) => isDone);
  }

  ///智能机器人表情
  Future<bool> avatarExpression({String emotion = AvatarEmotions.CALM}){
    Map<String, dynamic> emotionsMap = new Map();
    emotionsMap['emotion'] = emotion;
    return _channel
        .invokeMethod('avatarExpression',emotionsMap)
        .then<bool>((isDone) => isDone);
  }

  ///智能机器人张嘴/合嘴动作
  Future<bool> avatarSpeechMouth(String speechMouth){
    Map<String, dynamic> speechMouthMap = new Map();
    speechMouthMap['speechMouth'] = speechMouth;
    return _channel
        .invokeMethod('avatarSpeechMouth',speechMouthMap)
        .then<bool>((isDone) => isDone);
  }

  ///改变智能机器人脸部朝向
  Future<bool> avatarTowardTo(String fTurnX,String fTurnY){
    Map<String, dynamic> towardToMap = new Map();
    towardToMap['fTurnX'] = fTurnX;
    towardToMap['fTurnY'] = fTurnY;
    return _channel
        .invokeMethod('avatarTowardTo',towardToMap)
        .then<bool>((isDone) => isDone);
  }

  ///改变智能机器人在屏幕的位置
  Future<bool> avatarChangePos(String iLeft,String iTop){
    Map<String, dynamic> posMap = new Map();
    posMap['iLeft'] = iLeft;
    posMap['iTop'] = iTop;
    return _channel
        .invokeMethod('avatarChangePos',posMap)
        .then<bool>((isDone) => isDone);
  }

  ///智能机器人说话
  Future<bool> avatarSpeak({String speakContext = '您好，我是小美，请问有什么可以效劳？'}){
    Map<String, dynamic> speakMap = new Map();
    speakMap['speakContext'] = speakContext;
    return _channel
        .invokeMethod('avatarSpeak',speakMap)
        .then<bool>((isDone) => isDone);
  }


///智能机器人事件监听
  static void avatarListener(void onData(T),
      {bool cancelOnError, void onDone(), Function onError})  {
    _avatarListenerChannel.receiveBroadcastStream().listen(
            (data) {
          if (onData != null) {
            onData(data);
          }
        },
        cancelOnError: cancelOnError,
        onDone: () {
          if (onDone != null) {
            onDone();
          }
        },
        onError: (error) {
          if (onError != null) {
            onError(error);
          }
        });
  }

}
