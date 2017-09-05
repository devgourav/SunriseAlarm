package com.beeblebroxlabs.sunrisealarm;

/**
 * Created by devgr on 08-Jul-17.
 */

public class AlarmModel {

  private String alarmLabel, alarmId;
  private int alarmHour, alarmMinute;
  private Boolean isAlarmEnabled;

  public String getAlarmLabel() {

    return alarmLabel;
  }

  public void setAlarmLabel(String alarmLabel) {

    this.alarmLabel = alarmLabel;
  }

  public String getAlarmId() {

    return alarmId;
  }

  public void setAlarmId(String alarmId) {

    this.alarmId = alarmId;
  }

  public int getAlarmHour() {
    
    return alarmHour;
  }

  public void setAlarmHour(int alarmHour) {
    this.alarmHour = alarmHour;
  }

  public int getAlarmMinute() {
    return alarmMinute;
  }

  public void setAlarmMinute(int alarmMinute) {
    this.alarmMinute = alarmMinute;
  }

  public Boolean getAlarmEnabled() {
    return isAlarmEnabled;
  }

  public void setAlarmEnabled(Boolean alarmEnabled) {
    isAlarmEnabled = alarmEnabled;
  }
}
