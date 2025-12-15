package me.sofiiak.sharedplay.ui

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import me.sofiiak.sharedplay.viewmodel.SignInViewModel


@Composable
fun SignInScreen(
    navController: NavController,
    signInViewModel: SignInViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    BackHandler(enabled = true) {
        (context as? Activity)?.finish()
    }

    val signInLauncher = rememberLauncherForActivityResult(
        contract = FirebaseAuthUIActivityResultContract(),
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            signInViewModel.onUiEvent(
                SignInViewModel.UiEvent.SignedIn
            )
            navController.popBackStack()
        } else {
            Toast.makeText(
                context,
                "Sign-in failed: ${result.idpResponse?.error?.localizedMessage}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val startSignInFlow: () -> Unit = {
        val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }

    // Center everything on the screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Welcome Text
        Text(
            text = "Welcome to SharedPlay!\nPlease click the button below to sign in.",
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Sign In Button
        Button(onClick = startSignInFlow) {
            Text(text = "Sign In with Google")
        }
    }
}