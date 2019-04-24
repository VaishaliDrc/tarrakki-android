package com.tarrakki.module.support

import org.supportcompact.FragmentViewModel

class SupportVM : FragmentViewModel() {

    val queries = arrayListOf("", "", "", "", "")
    val tickets = arrayListOf<Ticket>()

    init {
        tickets.add(Ticket("Others", "123456789", "October 10, 2018 - 10:15 AM", true))
        tickets.add(Ticket("My Question is not Listed here", "123456789", "October 10, 2018 - 10:15 AM", false))
        tickets.add(Ticket("What is an Auto Pay ?", "123456789", "October 10, 2018 - 10:15 AM", true))
    }
}

data class Ticket(val query: String, val referenceNo: String, val dateTime: String, var isOpen: Boolean)