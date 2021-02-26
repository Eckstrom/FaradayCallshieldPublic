package edu.gatech.phileckstrom.faradaycallshield.requestors

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AlertDialog
import edu.gatech.phileckstrom.faradaycallshield.R
import edu.gatech.phileckstrom.faradaycallshield.misc.BaseActivity
import edu.gatech.phileckstrom.faradaycallshield.requestors.model.RoleRequestorModel
import edu.gatech.phileckstrom.faradaycallshield.services.*
import java.lang.ref.WeakReference

class RoleRequestor : RoleRequestorModel {

    var activityReference: WeakReference<BaseActivity>? = null

    override fun invokeCapabilitiesRequest() {
        activityReference?.get()?.let {
            if (!it.hasDialerCapability()) {
                requestDialerPermission()
            }
        }
    }

    private fun requestDialerPermission() {
        activityReference?.get()?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                it.startCallScreeningPermissionScreen(REQUEST_ID_CALL_SCREENING)
            } else {
                it.startSelectDialerScreen(REQUEST_ID_SET_DEFAULT_DIALER)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ID_CALL_SCREENING || requestCode == REQUEST_ID_SET_DEFAULT_DIALER) {
            if (resultCode == android.app.Activity.RESULT_OK) {
                activityReference?.get()?.let {
                    it.uiEvent.postValue(ScreeningRoleEnabled)
                }
            } else {
                displayCallScreeningPermissionDialog {
                    requestDialerPermission()
                }
            }
        }
    }

    private fun displayCallScreeningPermissionDialog(positiveButtonHandler: (() -> Unit)?) {
        activityReference?.get()?.let {
            AlertDialog.Builder(it).setTitle(R.string.message_title).setMessage(R.string.call_screening_required_message).setPositiveButton(android.R.string.ok)
            { _, _ ->
                positiveButtonHandler?.invoke()
            }.setNegativeButton(android.R.string.cancel, { dialog, item -> }).create().show()
        }
    }

    companion object {
        const val REQUEST_ID_CALL_SCREENING = 9872
        const val REQUEST_ID_SET_DEFAULT_DIALER = 1144
    }
}