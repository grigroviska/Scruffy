package com.gematriga.scruffy.activity

import android.annotation.SuppressLint
import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.gematriga.scruffy.R
import com.gematriga.scruffy.databinding.ActivityOtpactivityBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.concurrent.TimeUnit

class OTPActivity : AppCompatActivity() {

    private lateinit var binding : ActivityOtpactivityBinding
    private lateinit var auth : FirebaseAuth
    private var auId : String? = null

    private lateinit var OTP : String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var phoneNumber: String

    private lateinit var progressBar : ProgressBar
    private lateinit var inputOTP1 : EditText
    private lateinit var inputOTP2 : EditText
    private lateinit var inputOTP3 : EditText
    private lateinit var inputOTP4 : EditText
    private lateinit var inputOTP5 : EditText
    private lateinit var inputOTP6 : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        OTP = intent.getStringExtra("OTP").toString()
        resendToken = intent.getParcelableExtra("resendToken")!!
        phoneNumber = intent.getStringExtra("phoneNumber")!!

        auth = FirebaseAuth.getInstance()

        binding.verifyNumberText.text = "Verify Number  $phoneNumber"

        val appSettingPrefs : SharedPreferences = getSharedPreferences("AppSettingPrefs",0)
        val isNightModeOn : Boolean = appSettingPrefs.getBoolean("NightMode",false)

        if (isNightModeOn){

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)


        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        }


        progressBar.visibility = View.INVISIBLE
        addTextChangeListener()
        resendOTPTvVisibility()
        inputOTP1.requestFocus()

        binding.resendTextView.setOnClickListener {

            resendVerificationCode()
            resendOTPTvVisibility()

        }

        binding.verifyOTPBtn.setOnClickListener {

            val typedOTP = inputOTP1.text.toString() + inputOTP2.text.toString() + inputOTP3.text.toString() + inputOTP4.text.toString() + inputOTP5.text.toString() + inputOTP6.text.toString()

            if (typedOTP.isNotEmpty()){

                if (typedOTP.length == 6){

                    val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(
                        OTP, typedOTP
                    )
                    progressBar.visibility = View.VISIBLE
                    signInWithPhoneAuthCredential(credential)
                }else{

                    Toast.makeText(this@OTPActivity,"Please Enter Correct Verification Code", Toast.LENGTH_LONG).show()

                }

            }else{

                Toast.makeText(this@OTPActivity,"Please Enter Verification Code", Toast.LENGTH_LONG).show()

            }

        }



    }

    private fun resendOTPTvVisibility(){

        inputOTP1.setText("")
        inputOTP2.setText("")
        inputOTP3.setText("")
        inputOTP4.setText("")
        inputOTP5.setText("")
        inputOTP6.setText("")
        binding.resendTextView.visibility = View.INVISIBLE
        binding.resendTextView.isEnabled = false

        Handler(Looper.myLooper()!!).postDelayed({

            binding.resendTextView.visibility = View.VISIBLE
            binding.resendTextView.isEnabled = true

        }, 60000)

    }

    private fun resendVerificationCode(){

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .setForceResendingToken(resendToken)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information

                    auId = auth.currentUser!!.uid
                    progressBar.visibility = View.VISIBLE
                    Toast.makeText(this, "Authenticate Successfully", Toast.LENGTH_LONG).show()
                    oldUser(auId.toString())

                } else {
                    // Sign in failed, display a message and update the UI
                    Log.d("TAG","signInWithPhoneAuthCredential: ${task.exception.toString()}")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this, "Please enter the verification code correctly!", Toast.LENGTH_LONG).show()
                    }
                    // Update UI
                }
                progressBar.visibility = View.VISIBLE
            }
    }

    private fun oldUser(olderUser : String){

        val root = FirebaseDatabase.getInstance().reference
        val users = root.child("users")
        users.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(olderUser).exists()) {
                    startActivity(Intent(this@OTPActivity, HomeActivity::class.java))
                    finish()
                } else {

                    startActivity(Intent(this@OTPActivity, ProfileActivity::class.java))
                    finish()

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }

    private fun addTextChangeListener(){
        try {

            inputOTP1.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (inputOTP1.text.toString().length == 1) {
                        inputOTP2.requestFocus()
                    }
                }

                override fun afterTextChanged(p0: Editable?) {}
            })

            inputOTP2.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (inputOTP2.text.isNullOrEmpty() && p2 > 0) {
                        inputOTP1.requestFocus()
                    } else if (inputOTP2.text.toString().length == 1) {
                        inputOTP3.requestFocus()
                    }

                    addKeyListener()
                }

                override fun afterTextChanged(p0: Editable?) {}
            })

            inputOTP3.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (inputOTP3.text.isNullOrEmpty() && p2 > 0) {
                        inputOTP2.requestFocus()
                    } else if (inputOTP3.text.toString().length == 1) {
                        inputOTP4.requestFocus()
                    }

                    addKeyListener()
                }

                override fun afterTextChanged(p0: Editable?) {}
            })

            inputOTP4.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (inputOTP4.text.isNullOrEmpty() && p2 > 0) {
                        inputOTP3.requestFocus()
                    } else if (inputOTP4.text.toString().length == 1) {
                        inputOTP5.requestFocus()
                    }

                    addKeyListener()
                }

                override fun afterTextChanged(p0: Editable?) {}
            })

            inputOTP5.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (inputOTP5.text.isNullOrEmpty() && p2 > 0) {
                        inputOTP4.requestFocus()
                    } else if (inputOTP5.text.toString().length == 1) {
                        inputOTP6.requestFocus()
                    }

                    addKeyListener()
                }

                override fun afterTextChanged(p0: Editable?) {}
            })

            inputOTP6.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (inputOTP6.text.isNullOrEmpty() && p2 > 0) {
                        inputOTP5.requestFocus()
                    } else if (inputOTP6.text.toString().length == 1) {
                        binding.verifyOTPBtn.requestFocus()
                    }

                    addKeyListener()
                }

                override fun afterTextChanged(p0: Editable?) {}
            })

        }catch (e: Exception){

            Toast.makeText(this@OTPActivity,e.localizedMessage,Toast.LENGTH_LONG).show()

        }

    }

    private fun addKeyListener() {
        try {

            inputOTP2.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN && inputOTP2.text.isNullOrEmpty()) {
                    // Backspace was pressed and the current input field is empty
                    // Move focus to the previous input field
                    inputOTP2.clearFocus()
                    inputOTP1.requestFocus()
                    true // Consume the event
                } else {
                    false // Don't consume the event
                }
            }

            inputOTP3.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN && inputOTP3.text.isNullOrEmpty()) {
                    // Backspace was pressed and the current input field is empty
                    // Move focus to the previous input field
                    inputOTP3.clearFocus()
                    inputOTP2.requestFocus()
                    true // Consume the event
                } else {
                    false // Don't consume the event
                }
            }

            inputOTP4.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN && inputOTP4.text.isNullOrEmpty()) {
                    // Backspace was pressed and the current input field is empty
                    // Move focus to the previous input field
                    inputOTP4.clearFocus()
                    inputOTP3.requestFocus()
                    true // Consume the event
                } else {
                    false // Don't consume the event
                }
            }

            inputOTP5.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN && inputOTP5.text.isNullOrEmpty()) {
                    // Backspace was pressed and the current input field is empty
                    // Move focus to the previous input field
                    inputOTP5.clearFocus()
                    inputOTP4.requestFocus()
                    true // Consume the event
                } else {
                    false // Don't consume the event
                }
            }

            inputOTP6.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN && inputOTP6.text.isNullOrEmpty()) {
                    // Backspace was pressed and the current input field is empty
                    // Move focus to the previous input field
                    inputOTP6.clearFocus()
                    inputOTP5.requestFocus()
                    true // Consume the event
                } else {
                    false // Don't consume the event
                }
            }

        }catch (e: Exception){

            Toast.makeText(this@OTPActivity,e.localizedMessage,Toast.LENGTH_LONG).show()

        }

    }

    private fun init(){

        progressBar = findViewById(R.id.otpProgressBar)
        inputOTP1 = findViewById(R.id.otpEditText1)
        inputOTP2 = findViewById(R.id.otpEditText2)
        inputOTP3 = findViewById(R.id.otpEditText3)
        inputOTP4 = findViewById(R.id.otpEditText4)
        inputOTP5 = findViewById(R.id.otpEditText5)
        inputOTP6 = findViewById(R.id.otpEditText6)

    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.

            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Log.d("TAG","onVerificationFailed: ${e.toString()}")
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Log.d("TAG","onVerificationFailed: ${e.toString()}")
            }
            progressBar.visibility = View.VISIBLE
            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {

            OTP = verificationId
            resendToken = token


        }

    }

}