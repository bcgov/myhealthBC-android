package ca.bc.gov.bchealth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import ca.bc.gov.bchealth.databinding.ActivityMainBinding
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint

/**
 * [MainActivity]
 *
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by viewBindings(ActivityMainBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNav.setupWithNavController(navController)
    }
}
