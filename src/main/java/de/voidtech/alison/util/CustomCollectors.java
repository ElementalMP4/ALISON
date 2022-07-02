// 
// Decompiled by Procyon v0.5.36
// 

package main.java.de.voidtech.alison.util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Collector;

public class CustomCollectors
{
    public static <T> Collector<T, ?, T> toSingleton() {
        return Collectors.collectingAndThen(Collectors.toList(), CustomCollectors::lambda$toSingleton$0);
    }
}
