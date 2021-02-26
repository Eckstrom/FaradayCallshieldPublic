package edu.gatech.phileckstrom.faradaycallshield.services

sealed class PermissionsPrompt()

data class PermissionDenied(val requestCode: Int, val perms: MutableList<String>) : PermissionsPrompt()
data class PermissionGranted(val requestCode: Int, val perms: MutableList<String>) : PermissionsPrompt()
object PermissionsEnabled : PermissionsPrompt()
object ScreeningRoleEnabled : PermissionsPrompt()
