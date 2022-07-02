// 
// Decompiled by Procyon v0.5.36
// 

package main.java.de.voidtech.alison.annotations;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;
import java.lang.annotation.Annotation;

@Component
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
}
