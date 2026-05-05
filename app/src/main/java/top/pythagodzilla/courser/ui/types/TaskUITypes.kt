package top.pythagodzilla.courser.ui.types

sealed class TaskUITypes(
    open val title: String,
    open val courseName: String,
    open val endTime: String,
    open val startTime: String
)

data class HomeworkUIClass(
    override val title: String,
    override val courseName: String,
    override val endTime: String,
    override val startTime: String
) : TaskUITypes(title, courseName, endTime, startTime)

data class ExamUIClass(
    override val title: String,
    override val courseName: String,
    override val endTime: String,
    override val startTime: String
) : TaskUITypes(title, courseName, endTime, startTime)