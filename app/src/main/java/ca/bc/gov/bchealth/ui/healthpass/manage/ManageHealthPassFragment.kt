package ca.bc.gov.bchealth.ui.healthpass.manage

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.analytics.AnalyticsAction
import ca.bc.gov.bchealth.analytics.SelfDescribingEvent
import ca.bc.gov.bchealth.databinding.FragmentManageHealthPassesBinding
import ca.bc.gov.bchealth.ui.healthpass.HealthPassViewModel
import ca.bc.gov.bchealth.utils.viewBindings
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.snowplowanalytics.snowplow.Snowplow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Collections

/**
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class ManageHealthPassFragment : Fragment(R.layout.fragment_manage_health_passes) {
    private val viewModel: HealthPassViewModel by viewModels()
    private val binding by viewBindings(FragmentManageHealthPassesBinding::bind)
    private lateinit var manageHealthPassAdapter: ManageHealthPassAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpToolbar()

        setUpRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                collectHealthPasses()
            }
        }
    }

    private fun setUpRecyclerView() {
        manageHealthPassAdapter = ManageHealthPassAdapter(emptyList(), deleteClickListener = {
            confirmUnlinking(it)
        })
        binding.recManageCards.adapter = manageHealthPassAdapter
        binding.recManageCards.layoutManager =
            LinearLayoutManager(requireContext())

        /*
        * Add cards movement functionality
        * */
        val callback = RecyclerDragCallBack(
            manageHealthPassAdapter,
            ItemTouchHelper.UP.or(ItemTouchHelper.DOWN), 0
        )
        val helper = ItemTouchHelper(callback)
        helper.attachToRecyclerView(binding.recManageCards)
        manageHealthPassAdapter.notifyItemRangeChanged(0, manageHealthPassAdapter.itemCount)
    }

    private fun setUpToolbar() {
        binding.toolbar.apply {
            ivLeftOption.visibility = View.VISIBLE
            ivLeftOption.setImageResource(R.drawable.ic_action_back)
            ivLeftOption.setOnClickListener {
                findNavController().popBackStack()
            }

            tvTitle.visibility = View.VISIBLE
            tvTitle.text = getString(R.string.bc_vaccine_passes)

            tvRightOption.visibility = View.VISIBLE
            tvRightOption.text = getString(R.string.done)
            tvRightOption.setOnClickListener {
                viewModel.updateHealthPassOrder(manageHealthPassAdapter.healthPasses)
                    .invokeOnCompletion {
                        findNavController().popBackStack()
                    }
            }
        }
    }

    private suspend fun collectHealthPasses() {
        viewModel.healthPasses.collect { healthPasses ->
            if (::manageHealthPassAdapter.isInitialized) {
                manageHealthPassAdapter.healthPasses = healthPasses
                manageHealthPassAdapter.notifyDataSetChanged()
            }
        }
    }

    inner class RecyclerDragCallBack(
        private val adapter: ManageHealthPassAdapter,
        dragDirs: Int,
        swipeDirs: Int
    ) : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            Collections.swap(
                manageHealthPassAdapter.healthPasses,
                viewHolder.absoluteAdapterPosition,
                target.absoluteAdapterPosition
            )
            adapter.notifyItemMoved(
                viewHolder.absoluteAdapterPosition,
                target.absoluteAdapterPosition
            )
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        }
    }

    private fun confirmUnlinking(vaccineRecordId: Long) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.unlink_card))
            .setCancelable(false)
            .setMessage(getString(R.string.do_you_want_to_unlink))
            .setPositiveButton(getString(R.string.unlink)) { dialog, _ ->

                // Snowplow event
                Snowplow.getDefaultTracker()?.track(
                    SelfDescribingEvent
                        .get(AnalyticsAction.RemoveCard.value, "")
                )

                viewModel.deleteHealthPass(vaccineRecordId)
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.not_now)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
