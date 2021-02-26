package edu.gatech.phileckstrom.faradaycallshield.requestors

import android.Manifest
import edu.gatech.phileckstrom.faradaycallshield.R
import edu.gatech.phileckstrom.faradaycallshield.misc.BaseActivity
import edu.gatech.phileckstrom.faradaycallshield.requestors.model.PermissionRequesterModel
import edu.gatech.phileckstrom.faradaycallshield.services.PermissionDenied
import edu.gatech.phileckstrom.faradaycallshield.services.PermissionGranted
import edu.gatech.phileckstrom.faradaycallshield.services.PermissionsEnabled
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.lang.ref.WeakReference

class PermissionRequestor : PermissionRequesterModel,
        EasyPermissions.PermissionCallbacks {

    var activity: WeakReference<BaseActivity>? = null

    private val permissions =
            arrayOf(
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.READ_CONTACTS
            )

    @AfterPermissionGranted(RC_PERMISSIONS)
    override fun getPermissions() {
        activity?.get()?.let {
            if (EasyPermissions.hasPermissions(it, *permissions)) {
                it.uiEvent.postValue(PermissionsEnabled)
            } else {
                // Do not have permissions, request them now
                EasyPermissions.requestPermissions(
                        it, it.getString(R.string.enable_contact_phone_state_permissions),
                        RC_PERMISSIONS, *permissions
                )
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        activity?.get()?.let {
            it.uiEvent.postValue(PermissionGranted(requestCode, perms))
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        activity?.get()?.let {
            it.uiEvent.postValue(PermissionDenied(requestCode, perms))
        }
    }

    companion object {
        private const val RC_PERMISSIONS = 1212
    }

}