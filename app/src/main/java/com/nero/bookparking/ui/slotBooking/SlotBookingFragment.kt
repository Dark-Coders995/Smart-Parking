package com.nero.bookparking.ui.slotBooking

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.nero.bookparking.R
import com.nero.bookparking.databinding.FragmentSlotBookingBinding
import com.nero.bookparking.helper.KEY_USER_GOOGLE_ID
import com.nero.bookparking.helper.PreferenceHelper
import com.nero.bookparking.ui.parcalable.ArgsPaymentToConfirmation
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class SlotBookingFragment : Fragment()  {

    private var _binding: FragmentSlotBookingBinding? = null
    private val binding get() = _binding!!
    val viewModel by viewModels<SlotBookingViewModel>()
    val currentUser = FirebaseAuth.getInstance().currentUser?.uid ?: "u5"
    private lateinit var u_id: String

    private var hoursToAdd: Long = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSlotBookingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args by navArgs<SlotBookingFragmentArgs>()
        val data = args.parking
        u_id = PreferenceHelper.getStringFromPreference(KEY_USER_GOOGLE_ID) ?: "u5"

        binding.apply {

            cv13hrs.setOnClickListener {
                onStopButtonClick(requireContext())

                cv13hrs.setCardBackgroundColor(resources.getColor(R.color.app_red))
                tvCost.text = if (cbValetParking.isChecked) "Rs.100.00" else "Rs.20.00"
                tvHiddenText.visibility = View.GONE
                hoursToAdd = 1
            }

            cvVisaCardPayment.setOnClickListener {
                findNavController().navigate(SlotBookingFragmentDirections.actionSlotBookingFragmentToVisaPaymentFragment())
            }

            ibNext.setOnClickListener {
                viewModel.updateSlot(
                    buildingID = data.building,
                    pillerID = data.pillor,
                    boxID = data.parkingBox,
                    floorID = data.floor, currentUserUid = u_id,
                    hoursLeft = hoursToAdd
                )

                val time = Calendar.getInstance().timeInMillis


                val hourValue: Long = TimeUnit.HOURS.toMillis(hoursToAdd)
                val toTime: Long = time + hourValue

                val details = ArgsPaymentToConfirmation(data.floor, time, toTime, data.parkingBox)

                findNavController().navigate(
                    SlotBookingFragmentDirections.actionSlotBookingFragmentToPaymentConfirmationFragment(
                        details
                    )
                )
            }

        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // In the other activity where "Stop" button is clicked
    private fun onStopButtonClick(context: Context) {
        // Retrieve the stored time snapshot from local storage
        val sharedPref = PreferenceHelper.getSharedPreferences(context)
        val storedTimeMillis = sharedPref?.getLong("timeSnapshot", 0) ?: 0

        // Calculate elapsed time
        val currentTimeMillis = System.currentTimeMillis()
        val elapsedTimeMillis = currentTimeMillis - storedTimeMillis

        // Convert elapsed time to desired format (e.g., hours, minutes, seconds)
        val hours = TimeUnit.MILLISECONDS.toHours(elapsedTimeMillis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTimeMillis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTimeMillis) % 60

        // Display or use the elapsed time as needed
        val elapsedTimeString = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
        Log.d("ElapsedTime", "Elapsed Time: $elapsedTimeString")
        binding.cv13hrs.findViewById<TextView>(R.id.textcv13)?.text=elapsedTimeString
    }

}



