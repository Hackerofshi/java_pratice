package com.shixin.test.algorithm;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Solution {


    public static void main(String[] args) {

        int[] arr = {2, -1, -2, 4};
        maxSubArr(arr);
    }

    public static void maxSubArray(int[] arr) {
        int size = arr.length;
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            int sum = 0;
            for (int i1 = i; i1 < size; i1++) {
                sum += arr[i1];
                if (sum > max) {
                    max = sum;
                }
            }
        }
        log.debug("max={}", max);
    }

    public static void maxSubArr(int[] arr) {
        int pre = 0;
        int max = 0;
        for (int value : arr) {
            pre = Math.max(pre + value, value);
            max = Math.max(max, pre);
        }
        log.debug("max={}", max);
    }

}
