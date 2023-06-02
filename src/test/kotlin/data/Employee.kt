package data

import java.sql.Date

data class Employee(
    val id: Int = 1,
    val firstName: String? = "Ivan",
    val lastName: String = "Ivanov",
    val jobTitle: JobTitle = JobTitle.QA,
    val vacationStart: Date? = null,
    val vacationEnd: Date? = null
)

enum class JobTitle {
    QA,
    JAVA_DEVELOPER
}