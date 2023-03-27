package ca.bc.gov.bchealth.ui.inappupdate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.ActivityInAppUpdateBinding
import ca.bc.gov.bchealth.utils.InAppUpdateHelper
import ca.bc.gov.bchealth.utils.viewBindings
import com.google.android.play.core.install.model.AppUpdateType

class InAppUpdateActivity : AppCompatActivity() {
    private val binding by viewBindings(ActivityInAppUpdateBinding::bind)
    private lateinit var inAppUpdate: InAppUpdateHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_in_app_update)

        inAppUpdate = InAppUpdateHelper(this, lifecycle) {
            // no operation in case of IMMEDIATE update.
        }
        binding.btnUpdate.setOnClickListener { inAppUpdate.checkForUpdate(AppUpdateType.IMMEDIATE) }
    }
}
