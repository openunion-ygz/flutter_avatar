import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_avatar/avatarActions.dart';
import 'package:flutter_avatar/avatarEmotions.dart';
import 'package:flutter_avatar/flutter_avatar.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  String _speakContext = '';

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: ListView(
          children: <Widget>[
            Container(
              color: Colors.white,
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: <Widget>[
                  _buildTitleSection('初始化'),
                  _buildInitSection(),
                ],
              ),
            ),
            Container(
              color: Colors.white,
              margin: const EdgeInsets.only(top: 10.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: <Widget>[
                  _buildTitleSection('表情'),
                  _buildEmotionSection(),
                ],
              ),
            ),
            /*合并到——综合服务中
          Container(
            color: Colors.white,
            margin: const EdgeInsets.only(top: 10.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: <Widget>[
                _buildTitleSection('支付服务'),
                DividerWidget(),
                _buildPaymentServicesSection(),
              ],
            ),
          ),*/
            Container(
              color: Colors.white,
              margin: const EdgeInsets.only(top: 10.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: <Widget>[
                  _buildTitleSection('动作'),
                  _buildActionSection(),
                ],
              ),
            ),
            Container(
              color: Colors.white,
              margin: const EdgeInsets.only(top: 10.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: <Widget>[
                  _buildTitleSection('其他'),
                  _buildOtherSection(),
                ],
              ),
            ),

            Container(
              color: Colors.white,
              margin: const EdgeInsets.only(top: 10.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: <Widget>[
                  _buildTitleSection('监听结果'),
                  _buildTitleSection('$_speakContext'),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Container _buildTitleSection(String title) {
    return Container(
      child: Text(
        title,
        style: TextStyle(
          fontWeight: FontWeight.bold,
          fontSize: 13.0,
          color: Colors.black,
        ),
      ),
      margin: const EdgeInsets.only(left: 15.0, top: 10.0),
    );
  }

  Widget _buildInitSection() {
    return Container(
      margin: const EdgeInsets.only(top: 10.0, bottom: 10.0),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.center,
        mainAxisAlignment: MainAxisAlignment.spaceAround,
        children:<Widget>[
          RaisedButton(
            child:Text('初始化'),
            onPressed: (){
              FlutterAvatar.instance.initAvatar('640', '20',iAvatarSize: '400');
              FlutterAvatar.avatarListener((data) {
                print('avatarListener ===>$data');
                setState(() {
                  _speakContext = data;
                });
              });
            },
          ),
          // RaisedButton(
          //   child:Text('启动'),
          //   onPressed: (){
          //     FlutterAvatar.instance.avatarStart();
          //   },
          // ),
          // RaisedButton(
          //   child:Text('停止'),
          //   onPressed: (){
          //     FlutterAvatar.instance.avatarStop();
          //   },
          // ),
          RaisedButton(
            child:Text('释放资源'),
            onPressed: (){
              FlutterAvatar.instance.unInitialize();
            },
          ),

        ],
      ),
    );
  }

  Widget _buildActionSection() {
    return Container(
      margin: const EdgeInsets.only(top: 10.0, bottom: 10.0),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.center,
        mainAxisAlignment: MainAxisAlignment.spaceAround,
        children: <Widget>[
          RaisedButton(
            child:Text('交互'),
            onPressed: (){
              FlutterAvatar.instance.avatarActions(action: AvatarAction.INTERACTION);
            },
          ),

          RaisedButton(
            child:Text('单手介绍'),
            onPressed: (){
              FlutterAvatar.instance.avatarActions(action: AvatarAction.INTRODUCE);
            },
          ),

          RaisedButton(
            child:Text('再见'),
            onPressed: (){
              FlutterAvatar.instance.avatarActions(action: AvatarAction.BYE);
            },
          ),
        ],
      ),
    );
  }

  Widget _buildEmotionSection() {
    return Container(
      margin: const EdgeInsets.only(top: 10.0, bottom: 10.0),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.center,
        mainAxisAlignment: MainAxisAlignment.spaceAround,
        children: <Widget>[
          RaisedButton(
            child:Text('高兴'),
            onPressed: (){
              FlutterAvatar.instance.avatarExpression(emotion: AvatarEmotions.HAPPY);
            },
          ),

          RaisedButton(
            child:Text('微笑'),
            onPressed: (){
              FlutterAvatar.instance.avatarExpression(emotion: AvatarEmotions.SMILE);
            },
          ),

          RaisedButton(
            child:Text('难过'),
            onPressed: (){
              FlutterAvatar.instance.avatarExpression(emotion: AvatarEmotions.SAD);
            },
          ),

        ],
      ),
    );
  }

  Widget _buildOtherSection() {
    return Container(
      margin: const EdgeInsets.only(top: 10.0, bottom: 10.0),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.center,
        mainAxisAlignment: MainAxisAlignment.spaceAround,
        children:<Widget>[
          RaisedButton(
            child:Text('语音'),
            onPressed: (){
              FlutterAvatar.instance.avatarSpeak();
            },
          ),

          RaisedButton(
            child:Text('嘴巴张合'),
            onPressed: (){
              FlutterAvatar.instance.avatarSpeechMouth('1');
            },
          ),

          RaisedButton(
            child:Text('设置拖拽模式'),
            onPressed: (){
              FlutterAvatar.instance.avatarSwitchDragMode(isDragMode: true);
            },
          ),

        ],
      ),
    );
  }
  @override
  void dispose() {
    super.dispose();
    FlutterAvatar.instance.unInitialize();
  }
}
