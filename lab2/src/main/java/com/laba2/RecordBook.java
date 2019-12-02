package com.laba2;

import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordBook extends MyDate {

  private String firstName;
  private String secondName;
  private String lastName;
  private String phone;

  @Builder
  public RecordBook(String firstName, String secondName, String lastName, String phone,
      int day, int month, int year) {
    super(day, month, year);
    this.firstName = firstName;
    this.secondName = secondName;
    this.lastName = lastName;
    this.phone = phone;
  }

  @MyAnnotation
  public void getDaysBeforeBirthday() {
    Calendar current = Calendar.getInstance();
    int currentDate = current.get(Calendar.DATE);
    int currentMonth = current.get(Calendar.MONTH) + 1;
    int currentYear = current.get(Calendar.YEAR);

    Period period = Period.between(LocalDate.of(currentYear, currentMonth, currentDate),
        LocalDate.of(currentYear + 1, getMonth(), getDay()));
    System.out.println("Days: " + period.getDays() + " Months: " + period.getMonths());
  }
}
