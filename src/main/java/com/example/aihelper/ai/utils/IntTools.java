package com.example.aihelper.ai.utils;


public class IntTools {
    public static final Integer getRandomInt(Integer count) {
        // 最小值：10^(count-1)  → 如 count=5 → 10000
        int min = (int) Math.pow(10, count - 1);
        // 最大值：10^count - 1  → 如 count=5 → 99999
        int max = (int) Math.pow(10, count) - 1;

        // 生成 [min, max] 之间的随机数
        Integer randomNum = min + (int) (Math.random() * (max - min + 1));

        // 返回字符串格式
        return randomNum;
    }
}
