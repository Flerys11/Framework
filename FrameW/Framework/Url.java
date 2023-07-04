package etu2044.framework;

import java.lang.annotation.*;
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)

public @interface Url {
    String url() default " ";
}
