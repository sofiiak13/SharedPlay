package me.sofiiak.sharedplay


import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import me.sofiiak.sharedplay.ui.HomeScreen
import me.sofiiak.sharedplay.ui.PlaylistDetails
import me.sofiiak.sharedplay.ui.theme.SharedPlayTheme
import java.net.InetAddress
import java.net.UnknownHostException
private const val TAG = "MainActivity"

/**
 * Kind of like my main.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            setContent {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    composable("home") {
                        HomeScreen(
                            onPlaylistClick = { playlistId ->
                                navController.navigate("playlist/$playlistId")
                            }
                        )
                    }

                    composable(
                        route = "playlist/{playlistId}",
                        arguments = listOf(navArgument("playlistId") {
                            type = NavType.StringType
                        })
                    ) {
                        PlaylistDetails(navController = navController)
                    }
                }
            }
        }
    }

    private fun testDns() {
        Thread {
            try {
                val address = InetAddress.getByName("143.244.50.123")
                Log.d("DNS_TEST", "Resolved: $address")
            } catch (e: UnknownHostException) {
                Log.e("DNS_TEST", "Cannot resolve host", e)
            }
        }.start()
    }
}
