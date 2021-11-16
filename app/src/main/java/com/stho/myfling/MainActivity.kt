package com.stho.myfling

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.stho.myfling.databinding.ActivityMainBinding
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        setContentView(binding.root)
        setupListener()
        setupObserver()
    }

    private fun setupListener() {
        binding.wheel.setOnRotateListener(object : FlingView.OnRotateListener{
            override fun onRotate(delta: Double) {
                viewModel.rotate(delta)
            }
        })
        binding.ring.setOnRotateListener(object : CircleView.OnRotateListener{
            override fun onRotate(delta: Double) {
                viewModel.rotate(delta)
            }
        })
        binding.reset.setOnClickListener { viewModel.reset() }
    }

    private fun setupObserver() {
        viewModel.angleLD.observe(this) { angle -> onObserveAngle(angle) }
    }

    private fun onObserveAngle(angle: Double) {
        binding.wheel.setRotationAngle(angle)
        binding.ring.setRotationAngle(angle)
        binding.info.text = angle.roundToInt().toString()
    }

}