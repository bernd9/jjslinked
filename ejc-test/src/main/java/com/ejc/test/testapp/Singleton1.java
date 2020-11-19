package com.ejc.test.testapp;

import com.ejc.Singleton;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor
public class Singleton1 {
    private final Singleton2 singleton2;
}
