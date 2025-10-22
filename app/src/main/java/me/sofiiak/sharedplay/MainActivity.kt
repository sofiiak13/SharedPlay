package me.sofiiak.sharedplay


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import me.sofiiak.sharedplay.ui.theme.HomeScreen
import me.sofiiak.sharedplay.ui.theme.SharedPlayTheme


/**
 * Kind of like my main.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SharedPlayTheme {
                HomeScreen()
            }
        }
    }
}
