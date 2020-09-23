package com.bilibili.comicplugin.base;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author zhumingwei
 * @date 2020/9/21 11:39
 * @email zdf312192599@163.com
 */
public class SetDiff<T> {
    private List<T> addedList = new ArrayList<>();
    private List<T> unchangedList = new ArrayList<>();
    private List<T> removedList = new ArrayList<>();

    public SetDiff(Set<T> beforeSet, Set<T> afterSet) {
        addedList.addAll(afterSet);
        beforeSet.forEach(new Consumer<T>() {
            @Override
            public void accept(T e) {
                boolean b = addedList.remove(e) ? unchangedList.add(e) : removedList.add(e);
            }
        });
    }

    public List<T> getAddedList() {
        return addedList;
    }

    public List<T> getUnchangedList() {
        return unchangedList;
    }

    public List<T> getRemovedList() {
        return removedList;
    }
}
