package com.example.marks;

import java.util.Map;

public class Course {

    public String course;
    public Map<String, String> tests;

    Course(String course, Map<String, String> tests) {
        this.course = course;
        this.tests = tests;
    }

    public String singleTests() {
        String result = "";
        for(Map.Entry<String, String> entry: tests.entrySet()) {
            result += entry.getKey() + "\n";
        }
        if (result.equals("")) {
            return result;
        }
        return result.substring(0, result.length() - 1);
    }

    public String singleMarks() {
        String result = "";
        for(Map.Entry<String, String> entry: tests.entrySet()) {
            result += entry.getValue().split("\\|")[0] + "|" + entry.getValue().split("\\|")[1] + "%\n";
        }
        if (result.equals("")) {
            return result;
        }
        return result.substring(0, result.length() - 1);
    }

    public String getAverage() {
        float totalOutOf = 0;
        for(Map.Entry<String, String> entry: tests.entrySet()) {
            totalOutOf += Float.parseFloat(entry.getValue().split("\\|")[1]);
        }
        float mutiplier = 100f / totalOutOf;
        Float result = 0f;
        for(Map.Entry<String, String> entry: tests.entrySet()) {
            result += Float.parseFloat(entry.getValue().split("\\|")[0]) *
                    Float.parseFloat(entry.getValue().split("\\|")[1]) * mutiplier / 100;
        }
        return (result.toString() + "0000").substring(0, 5);
    }

    public void updateAdd(String test, String mark, String weight) {
        tests.put(test, mark + "|" + weight);
    }

    public void updateRemove(String test) {
        tests.remove(test);
    }
}
