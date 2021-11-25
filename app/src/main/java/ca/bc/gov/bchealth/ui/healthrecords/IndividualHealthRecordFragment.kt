package ca.bc.gov.bchealth.ui.healthrecords

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentIndividualHealthRecordBinding
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IndividualHealthRecordFragment : Fragment(R.layout.fragment_individual_health_record) {

    private val binding by viewBindings(FragmentIndividualHealthRecordBinding::bind)

    private val args: IndividualHealthRecordFragmentArgs by navArgs()

    private lateinit var individualHealthRecordAdapter: IndividualHealthRecordAdapter

    private val viewModel: IndividualHealthRecordViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolBar()

        setUpRecyclerView()
    }

    // Toolbar setup
    private fun setupToolBar() {

        binding.toolbar.ivLeftOption.visibility = View.VISIBLE
        binding.toolbar.ivLeftOption.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.toolbar.tvTitle.visibility = View.VISIBLE
        binding.toolbar.tvTitle.text = args.healthRecordDto.name.plus(
                getString(R.string.member_records_toolbar_title)
        )

        binding.toolbar.tvRightOption.visibility = View.VISIBLE
        binding.toolbar.tvRightOption.text = getString(R.string.edit)
        binding.toolbar.tvRightOption.setOnClickListener {
            // TODO: 24/11/21 rearrange logic
        }
    }

    // Recycler view setup
    private fun setUpRecyclerView() {


        individualHealthRecordAdapter = IndividualHealthRecordAdapter(
                viewModel.prepareVaccineDataList(args.healthRecordDto)
        )

        individualHealthRecordAdapter.clickListener = {
            // TODO: 25/11/21 Redirection to be implemented 
        }

        val recyclerView = binding.rvHealthRecords

        recyclerView.adapter = individualHealthRecordAdapter

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
}
