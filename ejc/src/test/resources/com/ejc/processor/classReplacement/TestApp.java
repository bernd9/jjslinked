package com.ejc.processor.classReplacement;

import com.ejc.Application;
import com.ejc.ApplicationRunner;
import com.ejc.Singleton;
import com.ejc.processor.Implementation;

@Application
class TestApp {
    public static void main(String[] args) {
        ApplicationRunner.run(TestApp.class);
    }
}

@Singleton
class Singleton1 {


}

@Singleton(replace = Singleton1.class)
class Singleton2 {
}

@Singleton
class Interface1 {

}

@Implementation(forClass = Interface1.class)
class Implementation1 {

}


@Singleton(replace = Singleton2.class)
class Singleton3 {
}






