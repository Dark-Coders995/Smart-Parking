package com.nero.bookparking.views.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.nero.bookparking.databinding.FragmentPaymentConfirmationBinding
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Random
import java.util.Timer
import kotlin.concurrent.timerTask
import kotlin.math.abs

class PaymentConfirmationFragment : Fragment() {

    private var _binding: FragmentPaymentConfirmationBinding? = null
    private val binding get() = _binding!!

    private lateinit var timer: Timer
    private val random = Random()
    private var isNavigationEnabled  = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentConfirmationBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment

        val args by navArgs<PaymentConfirmationFragmentArgs>()

        val data = args.data

        val fromTime: LocalDateTime = Instant.ofEpochMilli(data.fromTime ?: 556456)
            .atZone(ZoneId.systemDefault()).toLocalDateTime()

        val toTime: LocalDateTime = Instant.ofEpochMilli(data.toTime ?: 556456)
            .atZone(ZoneId.systemDefault()).toLocalDateTime()
        // Initialize timer
        timer = Timer()

        // Start generating OTP every 15 seconds
        startGeneratingOTP()


        binding.apply {
            tvFloor.text = "Floor: ${data.floor}"
            tvSpot.text = "Parking Spot: ${data.spot}"
            tvTime.text = "From ${fromTime.hour}:${fromTime.minute} to ${toTime.hour}:${toTime.minute}"
            tvOTP.text = ""

            btnMyBookings.setOnClickListener {
                if(isNavigationEnabled)
                    findNavController().navigate(PaymentConfirmationFragmentDirections.actionPaymentConfirmationFragmentToNavMyBookings())
            }

            btnHome.setOnClickListener {
                if(isNavigationEnabled)
                    findNavController().navigate(PaymentConfirmationFragmentDirections.actionPaymentConfirmationFragmentToLocationFragment())
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Cancel the timer when the fragment is destroyed
        timer.cancel()
        _binding = null
    }

    private fun startGeneratingOTP() {
        isNavigationEnabled = false
        // Schedule the generation of OTP every 15 seconds
        timer.scheduleAtFixedRate(timerTask {
            val otp = generateOTP()
            // Update the UI with the generated OTP2\
            Handler(Looper.getMainLooper()).post {
                binding.tvOTP.text = otp
            }
        }, 0, 15000) // 15 seconds in milliseconds
        Handler(Looper.getMainLooper()).postDelayed({
            isNavigationEnabled = true
        }, 15000)
    }

    private fun generateOTP(): String {
        // Generate a random 6-digit OTP
        return "%04d".format(random.nextInt(1000000))
    }
}
