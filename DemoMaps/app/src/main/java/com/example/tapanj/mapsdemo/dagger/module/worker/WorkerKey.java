package com.example.tapanj.mapsdemo.dagger.module.worker;

import androidx.work.Worker;
import dagger.MapKey;
import kotlin.annotation.AnnotationTarget;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

@MapKey
@Retention(RetentionPolicy.RUNTIME)
@Target(METHOD)
public @interface WorkerKey {
    Class<? extends Worker> value();
}
