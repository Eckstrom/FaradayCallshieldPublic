package edu.gatech.phileckstrom.faradaycallshield.requestors.model

import android.content.Intent

interface RoleRequestorModel {
    fun invokeCapabilitiesRequest()
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {}
}