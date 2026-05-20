package top.pythagodzilla.courser.ui.types

sealed class TasksType(
    open val id: String,
    open val courseId: String,
    open val taskTitle: String,
    open val courseName: String,
    open val endTime: String,
    open val startTime: String
)

data class HomeworkClass(
    override val id: String,
    override val courseId: String,
    override val taskTitle: String,
    override val courseName: String,
    override val endTime: String,
    override val startTime: String
) : TasksType(id, courseId, taskTitle, courseName, endTime, startTime)

data class ExamClass(
    override val id: String,
    override val courseId: String,
    override val taskTitle: String,
    override val courseName: String,
    override val endTime: String,
    override val startTime: String
) : TasksType(id, courseId, taskTitle, courseName, endTime, startTime)