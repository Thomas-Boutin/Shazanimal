package fr.dog.shazanimal.analysis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import fr.dog.shazanimal.MainViewModel
import fr.dog.shazanimal.R
import fr.dog.shazanimal.databinding.FragmentAnalysisBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tensorflow.lite.task.audio.classifier.AudioClassifier

class AnalysisFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private val nextAnimal by lazy {
        viewModel.nextAnimal()
    }
    private lateinit var binding: FragmentAnalysisBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentAnalysisBinding.inflate(inflater, container, false)
            .also { binding = it }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var isAnalyzing = false
        val classifier = AudioClassifier.createFromFile(requireContext(), MODEL_FILE)
        val audioTensor = classifier.createInputTensorAudio()
        val record = classifier.createAudioRecord()
        binding.animalName.text = nextAnimal.name
        binding.animal.text = nextAnimal.emoji
        binding.analyze.setOnClickListener {
            if (!isAnalyzing) {
                binding.analyze.text = getString(R.string.stop_analysis)
                binding.analyze.setBackgroundColor(resources.getColor(R.color.electric_green, requireActivity().theme))
                binding.analyze.setTextColor(resources.getColor(R.color.black, requireActivity().theme))
                binding.analyze.setIconTintResource(R.color.black)
                isAnalyzing = true
                lifecycleScope.launch(Dispatchers.IO) {
                    record.startRecording()
                }
            } else {
                lifecycleScope.launch(Dispatchers.IO) {
                    record.stop()
                    audioTensor.load(record)
                    viewModel.addAnalysis(nextAnimal, classifier, audioTensor)

                    if (viewModel.hasOneAnalysisLeft()) {
                        launch(Dispatchers.Main) {
                            AnalysisFragmentDirections
                                .actionAnalysisFragmentSelf()
                                .let { findNavController().navigate(it) }
                        }
                    } else {
                        launch(Dispatchers.Main) {
                            AnalysisFragmentDirections
                                .actionAnalysisFragmentToResultFragment()
                                .let { findNavController().navigate(it) }
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val MODEL_FILE = "yamnet.tflite"
    }
}