package com.ejc.processor;

import com.ejc.ApplicationContext;
import java.lang.NoSuchMethodException;
import java.lang.Override;
import java.lang.RuntimeException;
import java.lang.String;
import java.lang.Throwable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Implementation(
    forClass = "com.ejc.processor.AdviceTestBean"
)
public class AdviceTestBeanImpl extends AdviceTestBean {
  @Override
  public int xyz(String s) {
    List<InvocationHandler> advices = new ArrayList<>();
    advices.add(ApplicationContext.getInstance().getBean(com.ejc.processor.Test123Advice.class));
    Method method;
    try {
      method = getClass().getSuperclass().getDeclaredMethod("xyz",String.class);} catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
    method.setAccessible(true);
    Object[] args = new Object[]{s};
    Object rv = null;
    try {
      for (InvocationHandler advice : advices) {
        rv = advice.invoke(this, method, args);
      }
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
    return (int) rv;
  }
}
