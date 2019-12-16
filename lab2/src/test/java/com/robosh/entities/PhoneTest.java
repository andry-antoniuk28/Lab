package com.robosh.entities;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.robosh.handlers.MyInvocationHandler;
import java.lang.reflect.Proxy;
import org.junit.jupiter.api.Test;


public class PhoneTest {

  private Phone phone;

  @Test
  public void shouldThrowExceptionWhenSetMethodIsCall() {
    ClassLoader classLoader = Phone.class.getClassLoader();
    Class<?>[] interfaces = Phone.class.getInterfaces();
    MyInvocationHandler invocationHandler = new MyInvocationHandler(phone);
    PhoneImpl proxyPhone = (PhoneImpl) Proxy
        .newProxyInstance(classLoader, interfaces, invocationHandler);
    assertThrows(UnsupportedOperationException.class, () -> {
      proxyPhone.setName("Huawei");
    });
  }

  private Phone getPhone() {
    return new Phone("Nokia", 200, 2.2f, 16, true);
  }
}