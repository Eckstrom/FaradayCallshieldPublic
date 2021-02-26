package edu.gatech.phileckstrom.faradaycallshield.misc

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import edu.gatech.phileckstrom.faradaycallshield.services.PermissionsPrompt
import edu.gatech.phileckstrom.faradaycallshield.services.SingularEvent

abstract class BaseActivity : AppCompatActivity() {
    companion object {
        fun addFragmentToActivity(fragmentManager: FragmentManager,
                                  fragment: Fragment?,
                                  frameId: Int,
                                  tag: String?) {
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(frameId, fragment!!, tag)
            transaction.commit()
        }
    }

    val uiEvent = SingularEvent<PermissionsPrompt>()
}