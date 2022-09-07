package com.harishkannarao.restdatarabbitmq.steps;

import com.harishkannarao.restdatarabbitmq.steps.holder.StepDataHolder;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenSteps {

    private final StepDataHolder stepDataHolder;

    public WhenSteps(StepDataHolder stepDataHolder) {
        this.stepDataHolder = stepDataHolder;
    }

    @When("I ask whether it's Friday yet")
    public void i_ask_whether_it_s_Friday_yet() {
        stepDataHolder.setActualAnswer(IsItFriday.isItFriday(stepDataHolder.getToday()));
    }

    @Then("I should be told {string}")
    public void i_should_be_told(String expectedAnswer) {
        assertThat(stepDataHolder.getActualAnswer()).isEqualTo(expectedAnswer);
    }
}