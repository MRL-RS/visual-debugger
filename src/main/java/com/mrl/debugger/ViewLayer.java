package com.mrl.debugger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Siavash
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewLayer {
    boolean visible() default false;

    String caption() default "";

    String tag() default "";

    boolean drawAllData() default false;
}
