package me.sofiiak.sharedplay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import dagger.hilt.android.AndroidEntryPoint
import me.sofiiak.sharedplay.ui.CommentSection
import me.sofiiak.sharedplay.ui.HomeScreen
import me.sofiiak.sharedplay.ui.PlaylistDetails
import me.sofiiak.sharedplay.ui.SignInScreen


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()

            val inviteId = intent?.data?.lastPathSegment


            NavHost(
                navController = navController,
                startDestination = "home" // always start from home and go back to sign in if user isn't signed in
            ) {
                composable(
                    route = "sign-in"
                ) {
                    SignInScreen(
                        navController = navController,
                    )
                }
                composable(route = "home?inviteId={inviteId}",
                    arguments = listOf(navArgument("inviteId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }),
                    deepLinks = listOf(
                        navDeepLink {
                            uriPattern = "https://shared-play/invite/{inviteId}"
                        }
                    )
                ){
                    HomeScreen(
                        navController = navController,
                    )
                }

                composable(
                    route = "playlist_details/{playlistId}",
                    arguments = listOf(navArgument("playlistId") {
                        type = NavType.StringType
                    })
                ) {
                    PlaylistDetails(navController = navController)
                }

                composable(
                    route = "playlist_details/{playlistId}/song/{songId}",
                    arguments = listOf(
                        navArgument("playlistId") { type = NavType.StringType },
                        navArgument("songId") { type = NavType.StringType }
                    )
                ) {
                    CommentSection(navController = navController)
                }
            }
        }
    }
//    private fun askPreferredName(user: FirebaseUser?) {
//
//        val input = EditText(this)
//        AlertDialog.Builder(this)
//            .setTitle("Choose your display name")
//            .setView(input)
//            .setPositiveButton("Save") { _, _ ->
//                val preferredName = input.text.toString().trim()
//                if (preferredName.isNotEmpty()) {
//                    val profileUpdates = UserProfileChangeRequest.Builder()
//                        .setDisplayName(preferredName)
//                        .build()
//
//                    user?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
//                        if (task.isSuccessful) {
//                            Toast.makeText(this, "Name updated!", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                }
//            }
//            .setNegativeButton("Cancel", null)
//            .show()
//    }

}
