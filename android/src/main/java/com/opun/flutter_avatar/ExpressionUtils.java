package com.opun.flutter_avatar;

import com.ai7qi.avatarmanager.AvatarManagerHelper;

public class ExpressionUtils {
    //根据动作名称获取动作实体
    public static AvatarManagerHelper.Action77 getAction(String actionStr) {
        switch (actionStr) {
            case "IDLE":
                return AvatarManagerHelper.Action77.IDLE;
            case "HELLO":
                return AvatarManagerHelper.Action77.HELLO;
            case "INTERACTION":
                return AvatarManagerHelper.Action77.INTERACTION;
            case "INTRODUCE":
                return AvatarManagerHelper.Action77.INTRODUCE;
            case "INTRODUCE1":
                return AvatarManagerHelper.Action77.INTRODUCE1;
            case "BYE":
                return AvatarManagerHelper.Action77.BYE;
            case "SAYNO":
                return AvatarManagerHelper.Action77.SAYNO;
            case "SOLUTE":
                return AvatarManagerHelper.Action77.SOLUTE;
            default:
                return null;
        }

    }

    //根据表情名称获取指定表情实体
    public static AvatarManagerHelper.Emotion77 getEmotion(String emotionStr) {
        switch (emotionStr) {
            case "CALM":
                return AvatarManagerHelper.Emotion77.CALM;
            case "SAD":
                return AvatarManagerHelper.Emotion77.SAD;
            case "ANGRY":
                return AvatarManagerHelper.Emotion77.ANGRY;
            case "NOHAPPY":
                return AvatarManagerHelper.Emotion77.NOHAPPY;
            case "DISLIKE":
                return AvatarManagerHelper.Emotion77.DISLIKE;
            case "SHY":
                return AvatarManagerHelper.Emotion77.SHY;
            case "HAPPY":
                return AvatarManagerHelper.Emotion77.HAPPY;
            case "SMILE":
                return AvatarManagerHelper.Emotion77.SMILE;
            case "SMILE1":
                return AvatarManagerHelper.Emotion77.SMILE1;
            case "IDLE":
                return AvatarManagerHelper.Emotion77.IDLE;
            case "IDLE1":
                return AvatarManagerHelper.Emotion77.IDLE1;
            case "UNHAPPY":
                return AvatarManagerHelper.Emotion77.UNHAPPY;
            case "SURPISE":
                return AvatarManagerHelper.Emotion77.SURPISE;
            case "WORRY":
                return AvatarManagerHelper.Emotion77.WORRY;
            default:
                return null;
        }
    }

}
