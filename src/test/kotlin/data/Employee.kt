package data

import java.sql.Date

data class Employee(
    val id: Int = 1,
    val firstName: String? = "Ivan",
    val lastName: String = "Ivanov",
    val jobTitle: String = "Qa",
    val vacationStart: Date? = null,
    val vacationEnd: Date? = null
)