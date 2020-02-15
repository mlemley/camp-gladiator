package app.camp.gladiator.model

data class AppPermission(
    val permissionName:String? = null,
    val hasPermission:Boolean = false,
    val permissionRejected:Boolean = false
)
