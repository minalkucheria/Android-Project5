// IMyAidlInterface.aidl
package com.example.hp.common;

// Declare any non-default types here with import statements

interface IMyAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    int[] monthlyCash(int year);
    int[] dailyCash(int year, int day, int month, int workingDays);
    int yearlyAvg(int year);
}
