package com.harishkannarao.restdatarabbitmq.steps;

class IsItFriday {
    static String isItFriday(String today) {
        return "Friday".equals(today) ? "Yes" : "Nope";
    }
}
