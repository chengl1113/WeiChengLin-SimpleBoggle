package com.example.weichenglin_simpleboggle

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.weichenglin_simpleboggle.databinding.FragmentGameBoardBinding
import com.example.weichenglin_simpleboggle.databinding.FragmentGameScoreBinding

private const val TAG = "GameScoreFragment"

class GameScoreFragment : Fragment() {
    private var _binding: FragmentGameScoreBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val viewModel: GameViewModel by activityViewModels()
    private lateinit var scoreView: TextView
    private lateinit var activity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameScoreBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scoreView = binding.scoreView
        viewModel.score.observe(viewLifecycleOwner, Observer<Int>() {
            score: Int? -> scoreView.text = "SCORE: $score"
        })

        binding.newGameButton.setOnClickListener{
            activity.restartGame()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}