package one.xis.processor.classReplacement;

import one.xis.Application;
import one.xis.Singleton;
import one.xis.processor.Implementation;

@Application
class TestApp {
 
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






