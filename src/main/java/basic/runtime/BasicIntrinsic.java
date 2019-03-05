
package basic.runtime;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BasicIntrinsic {
    public String name() default "";
}

