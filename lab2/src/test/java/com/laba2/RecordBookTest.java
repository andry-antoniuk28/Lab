package com.laba2;

import static junit.framework.TestCase.assertEquals;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;

public class RecordBookTest {


  private RecordBook recordBook;

  @Before
  public void init() {
    recordBook = getRecordBook();
  }

  @Test
  public void shouldReturnClassName() {
    assertEquals("RecordBook", recordBook.getClass().getSimpleName());
  }

  @Test
  public void shouldShowConstructorParams() {
    for (Constructor constructor : recordBook.getClass().getConstructors()) {
      System.out.println(constructor);
    }
  }

  @Test
  public void shouldReturnClassModifier() {
    assertEquals("public", Modifier.toString(recordBook.getClass().getModifiers()));
  }


  @Test
  @SneakyThrows
  public void shouldInvokeMethod(){
    for (Method method: recordBook.getClass().getMethods()) {
       if(method.isAnnotationPresent(MyAnnotation.class)){
         method.invoke(recordBook);
       }
    }
  }

  private RecordBook getRecordBook() {
    return RecordBook.builder()
        .firstName("Андрій")
        .secondName("Миколайович")
        .lastName("Антонюк")
        .phone("0981155452")
        .day(20)
        .month(1)
        .year(200)
        .build();
  }
}