package com.gematriga.scruffy.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.gematriga.scruffy.R
import com.gematriga.scruffy.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var googleSignInClient : GoogleSignInClient
    private lateinit var number : String
    private lateinit var mProgressBar : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gba()

        binding.sendOTPBtn.setOnClickListener {

            val countryCode = binding.ccp.selectedCountryCode.toString()
            number = binding.phoneEditTextNumber.text!!.trim().toString()
            if (number.isNotEmpty()){

                println(countryCode)
                println(number)
                println("$countryCode $number")

                try {
                    if(number.length == 10){

                        number = "+$countryCode $number"
                        mProgressBar.visibility = View.VISIBLE
                        val options = PhoneAuthOptions.newBuilder(auth)
                            .setPhoneNumber(number)       // Phone number to verify
                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(this)                 // Activity (for callback binding)
                            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                            .build()
                        PhoneAuthProvider.verifyPhoneNumber(options)


                    }else{

                        Toast.makeText(this,"Please Enter Correct Number", Toast.LENGTH_LONG).show()

                    }
                }catch (e : Exception){

                    Toast.makeText(this, e.localizedMessage, Toast.LENGTH_LONG).show()

                }


            }else{

                Toast.makeText(this,"Please Enter Correct Number", Toast.LENGTH_LONG).show()

            }

        }

        binding.signInGoogle.setOnClickListener {

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("534496438948-ou5j0huba6uqkmkms03nhhrgj257bm7e.apps.googleusercontent.com")
                .requestEmail()
                .build()

            googleSignInClient = GoogleSignIn.getClient(this, gso)

            signInGoogle()

        }

    }

    private fun signInGoogle() {

        val signInIntent = googleSignInClient.signInIntent

        launcher.launch(signInIntent)

    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){

        result ->

            if(result.resultCode == Activity.RESULT_OK){

                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResults(task)

            }

    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful){

            val account : GoogleSignInAccount? = task.result
            if (account != null){

                updateUI(account)

            }

        }else{

            Toast.makeText(this,task.exception.toString(),Toast.LENGTH_LONG).show()

        }
    }

    private fun updateUI(account: GoogleSignInAccount) {

        val credential = GoogleAuthProvider.getCredential(account.idToken,null)
        auth.signInWithCredential(credential).addOnCompleteListener {

            if (it.isSuccessful){

                val intent : Intent = Intent(this, HomeActivity::class.java)
                intent.putExtra("email", account.email)
                startActivity(intent)

            }else{

                Toast.makeText(this,it.exception.toString(),Toast.LENGTH_LONG).show()

            }

        }

    }

    private fun gba(){
        
        mProgressBar = findViewById(R.id.phoneProgressBar)
        mProgressBar.visibility = View.INVISIBLE
        auth = FirebaseAuth.getInstance()

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(this, "Authenticate Successfully", Toast.LENGTH_LONG).show()
                    sendToMain()

                } else {
                    // Sign in failed, display a message and update the UI
                    Log.d("TAG","signInWithPhoneAuthCredential: ${task.exception.toString()}")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
                mProgressBar.visibility = View.INVISIBLE
            }
    }

    private fun sendToMain(){

        startActivity(Intent(this, HomeActivity::class.java))

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

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.

            // Save verification ID and resending token so we can use them later
            val intent = Intent(this@MainActivity, OTPActivity::class.java)
            intent.putExtra("OTP", verificationId)
            intent.putExtra("resendToken", token)
            intent.putExtra("phoneNumber", number)
            startActivity(intent)
            mProgressBar.visibility = View.INVISIBLE

        }
    }

    override fun onStart() {
        super.onStart()

        if (auth.currentUser != null){

            startActivity(Intent(this@MainActivity, HomeActivity::class.java))

        }

    }

}