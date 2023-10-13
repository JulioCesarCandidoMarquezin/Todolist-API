package com.example.Julio.TodoList.utils;

import java.util.HashSet;
import java.util.Set;

import java.beans.PropertyDescriptor;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class Utils {

    public static void copyNonNullProperties(Object source, Object target)
    {
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

    public static String[] getNullPropertyNames(Object source)
    {
        final BeanWrapper src = new BeanWrapperImpl(source);

        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> empytNames = new HashSet<>();

        for(PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if(srcValue == null) empytNames.add(pd.getName());
        }

        String[] result = new String[empytNames.size()];
        return empytNames.toArray(result);
    }
}
