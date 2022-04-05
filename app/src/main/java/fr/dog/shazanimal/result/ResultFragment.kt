package fr.dog.shazanimal.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import fr.dog.shazanimal.MainViewModel
import fr.dog.shazanimal.databinding.FragmentResultBinding

class ResultFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private val adapter: ResultAdapter by lazy { ResultAdapter() }
    private lateinit var binding: FragmentResultBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentResultBinding.inflate(inflater, container, false)
            .also { binding = it }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.results.apply {
            layoutManager = LinearLayoutManager(requireContext())
            isNestedScrollingEnabled = false
            adapter = this@ResultFragment.adapter
        }
        adapter.submitList(viewModel.analysis)
    }
}