package com.stho.myfling

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.stho.myfling.databinding.ActivityMainBinding
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var resetButtonAnimation: ViewAnimation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        resetButtonAnimation = ViewAnimation.build(binding.reset)
        setContentView(binding.root)
        setupListener()
        setupObserver()
    }

    override fun onPause() {
        super.onPause()
        resetButtonAnimation.cleanup()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupListener() {
        binding.wheel.setOnRotateListener(object : OnRotateListener{
            override fun onRotate(delta: Double) {
                viewModel.rotate(delta)
            }
        })
        binding.ring.setOnRotateListener(object : OnRotateListener{
            override fun onRotate(delta: Double) {
                viewModel.rotate(delta)
            }
        })
        binding.reset.setOnClickListener { viewModel.reset() }
        binding.plus.setOnTouchListener(RepeatingTouchListener({ viewModel.rotate(+1.0) }))
        binding.minus.setOnTouchListener(RepeatingTouchListener({ viewModel.rotate(-1.0) }))
    }

    private fun setupObserver() {
        viewModel.angleLD.observe(this) { angle -> onObserveAngle(angle) }
        viewModel.canResetLD.observe(this) { value -> onObserveCanReset(value) }
    }

    private fun onObserveAngle(angle: Double) {
        binding.wheel.setRotationAngle(angle)
        binding.ring.setRotationAngle(angle)
        binding.info.text = getString(R.string.label_info_param, angle.roundToInt())
    }

    private fun onObserveCanReset(value: Boolean) {
        if (value)
            resetButtonAnimation.show()
        else
            resetButtonAnimation.dismiss()
    }

}