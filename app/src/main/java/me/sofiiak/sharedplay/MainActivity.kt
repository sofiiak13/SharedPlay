package me.sofiiak.sharedplay

import ads_mobile_sdk.h5
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import me.sofiiak.sharedplay.ui.theme.SharedPlayTheme


import me.sofiiak.sharedplay.User
import me.sofiiak.sharedplay.Playlist
import me.sofiiak.sharedplay.Song
import me.sofiiak.sharedplay.Comment
import javax.inject.Inject

// DATA LAYER
class PlaylistRepository @Inject constructor(
    private val db: DatabaseReference,
) {
    /**
     * Retrieves playlist information from the database based on provided ID.
     */
//    fun getPlaylist(playlistId: String, onResult: (Playlist?) -> Unit) {
//        db.child("playlists").child(playlistId)
//            .addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val playlist = snapshot.getValue(Playlist::class.java)
//                    onResult(playlist)
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    onResult(null)
//                }
//            })
//    }

    /**
     * Retrieves a list of songs from the database based on their IDs.
     */
    fun getSongs(songIDs: List<String>, callback: (List<Song>) -> Unit) {
        val result = mutableListOf<Song>()

        songIDs.forEach { id ->
            db.child("songs").child(id)
                .get()
                .addOnSuccessListener { snapshot ->
                    snapshot.getValue(Song::class.java)?.let { result.add(it) }

                    if (result.size == songIDs.size) {
                        callback(result)                    // returns list to ViewModel
                    }
                }
        }
    }
}

// VIEW MODEL
@HiltViewModel
class PlaylistDetailsViewModel @Inject constructor(
    private val repository: PlaylistRepository
//    private val repository: PlaylistRepository = PlaylistRepository(Firebase.database.reference)
) : ViewModel() {

    private val _playlist = MutableLiveData<Playlist>()
    val playlist: LiveData<Playlist> get() = _playlist

    private val _songs = MutableLiveData<List<Song>>()
    val songs: LiveData<List<Song>> get() = _songs

    fun loadPlaylist(songIDs: List<String>) {
        repository.getSongs(songIDs) { list ->
            _songs.value = list             // updates LiveData → UI observes it
        }
    }
//    fun loadPlaylist(playlistId: String) {
//        repository.getPlaylist(playlistId) { playlist ->
//            playlist?.let {
//                _playlist.value = it
//                repository.getSongs(it.songs) { list ->
//                    _songs.value = list
//                }
//            }
//        }
//    }
}



private val TAG = "MainActivity"


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val songs = mutableListOf<Song>().apply {
        add(Song(title = "Song 1", artist = "Artist 1", addedBy = "u1"))
        add(Song(title = "Song 2", artist = "Artist 2", addedBy = "u2"))
        add(Song(title = "Song 3", artist = "Artist 3", addedBy = "u1"))
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        FirebaseApp.initializeApp(this)
        val database = Firebase.database.reference

        // Write a message to the database

        val myObjectsRef = database.child("songs") // "myObjects" is your parent node
//        songs.forEach { song ->
//            myObjectsRef.push().setValue(song)
//                .addOnSuccessListener {
//                    // Object successfully saved
//                    Log.e(TAG, "addOnSuccessListener: ", )
//                }
//                .addOnFailureListener {
//                    // Handle error
//                    Log.e(TAG, "addOnFailureListener: ", )
//                }
//        }

//        val myRef = database.getReference("song")
        // Read from the database
//        myObjectsRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                val songs = dataSnapshot.getValue<Song>()
//                Log.e(TAG, "Value is: $songs")
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Failed to read value
//                Log.e(TAG, "Failed to read value.", error.toException())
//            }
//        })
//        myRef.setValue("Hello, World!")


        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SharedPlayTheme {

                PlaylistDetailsScreen("abc1")

            }
        }
    }
}

//
// UI LAYER
@Composable //rendering starts
fun PlaylistDetailsScreen(
    playlistId: String,                              // ID of the playlist we want to display
) {
    val viewModel: PlaylistDetailsViewModel = hiltViewModel()

    // LaunchedEffect runs code when the Composable first appears or when 'playlistId' changes
    LaunchedEffect(playlistId) {
        // Call the ViewModel to load songs for this playlist
        // Here we just pass fake song IDs for testing
        viewModel.loadPlaylist(listOf("-OaNPyqs3S-D8tCn56VQ", "-OaNPyqtaYxveQnVvgoA", "-OaNPyqtaYxveQnVvgoB"))
    }

    // observeAsState converts LiveData<List<Song>> from ViewModel into Compose state
    // so the UI automatically updates when the list changes
    val songs by viewModel.songs.observeAsState(emptyList()) // default to empty list if null

    // Column arranges UI elements vertically
    Column {
        // Display playlist title
        Text(
            text = "Playlist Details",             // Static title for now
        )

        // LazyColumn is like RecyclerView; it efficiently displays scrolling lists
        LazyColumn {
            items(songs.size) { index ->
                val song = songs[index]
                Text("${song.title} - ${song.artist}")
            }
        }

    }
}

//
//@Preview(showBackground = true)
//@Composable
//fun Preview() {
//    SharedPlayTheme {
//        PlaylistDetailsScreen("1")
//    }
//}