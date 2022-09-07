package com.harishkannarao.restdatarabbitmq.steps;

import com.harishkannarao.restdatarabbitmq.steps.holder.StepDataHolder;
import io.cucumber.java.en.Given;



public class GivenSteps {

    private final StepDataHolder stepDataHolder;

    public GivenSteps(StepDataHolder stepDataHolder) {
        this.stepDataHolder = stepDataHolder;
    }

    @Given("today is {string}")
    public void today_is(String day) {
        stepDataHolder.setToday(day);
    }
}