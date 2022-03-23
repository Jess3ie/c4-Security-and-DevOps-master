package com.example.demo;

import java.lang.reflect.Field;

public class TestUtils {
    //this class helps to inject objects
    public static void injectObjects(Object target, String filedName, Object toInject) {
        boolean wasPrivate = false;

        try {
            Field f = target.getClass().getDeclaredField(filedName);

            if(!f.isAccessible()) { //checking if field is private
                f.setAccessible(true);
                wasPrivate = true;
            }
            f.set(target, toInject);
            if(wasPrivate){
                f.setAccessible(false);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


    }
}
