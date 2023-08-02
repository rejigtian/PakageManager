package com.rejig.pakagemanager.utils;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Integer> fileCacheList = new ArrayList<>();
        fileCacheList.add(2);
        fileCacheList.add(1);
        fileCacheList.add(3);
        fileCacheList.add(4);
        Collections.sort(fileCacheList, (file, newFile) -> Long.compare(file, newFile));
        System.out.println(fileCacheList);
    }
}
