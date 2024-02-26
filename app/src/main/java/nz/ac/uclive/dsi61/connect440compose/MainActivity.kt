package nz.ac.uclive.dsi61.connect440compose

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nz.ac.uclive.dsi61.connect440compose.ui.theme.Connect440ComposeTheme
import java.net.URLEncoder

class MainActivity : ComponentActivity() {

    private val friends = listOf<Friend>(
        Friend("Ben Adams", "U05HGL4S030", "Christchurch, NZ", "benjamin.adams@canterbury.ac.nz", "#######"),
        Friend("Danielle Sim", "U04T3HFLQ86", "Rolleston, NZ", "dsi61.@uclive.ac.nz", "0221523531"),
        Friend("Priscilla Ishida-Foale", "U04SVKHVB63", "Christchurch, NZ", "pis19@uclive.ac.nz", "#######"),
        Friend("Bede Nathan", "U04TCKF0RRA", "Christchurch, NZ", "nrb55@uclive.ac.nz", "#######"),
        Friend("Fabian Gilson", "U04KU2TB84E", "Dinant, Belgium", "fabian.gilson@canterbury.ac.nz", "#######"),
        Friend("Jackie Jone", "U04T77HSEE9", "Christchurch, NZ", "jjo134@uclive.ac.nz", "#######"),
        Friend("Nathan Briggs", "U04T3HFNLEA", "Christchurch, NZ", "nbr61@uclive.ac.nz", "#######"),
        Friend("Sofonias Tekele Tesfaye", "U04TA21MYBD", "Christchurch, NZ", "ste73@uclive.ac.nz", "#######"),
        Friend("Justin Meyers", "U04T77J9Z2R", "Christchurch, NZ", "jme196@uclive.ac.nz", "#######"),
        Friend("Angus Kirtlan", "U04TA5VMASE", "Wellington, NZ", "aki81@uclive.ac.nz", "#######"),
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * Returns whether the app has the given permission.
         */
        fun hasPermissions(permissions: Array<String>): Boolean {
            return permissions.all {
                checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED
            }
        }

        // must request to use dangerous permissions like automatically phoning
        val permissions = arrayOf(Manifest.permission.CALL_PHONE)
        if (!hasPermissions(permissions)) {
            requestPermissions(permissions, 1)
        }


        setContent {
            Connect440ComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    var selectedFriend by remember { mutableStateOf(friends[0]) }
                    var openDialog by remember { mutableStateOf(false) }

                    FriendsList(friends, onFriendClick = { friend ->
                        Toast.makeText(this, friend.name, Toast.LENGTH_LONG).show()
                        selectedFriend = friend
                        openDialog = true
                    })

                    if (openDialog) {
                        AlertDialog(
                            onDismissRequest = { openDialog = false },
                            title = {
                                Text(
                                    style = MaterialTheme.typography.body1,
                                    fontWeight = FontWeight.Bold,
                                    text = "Connect how? 🤔"
                                )
                            },
                            text = {
                                val options = listOf("Map", "Email", "Text", "Call", "Slack")
                                LazyColumn {
                                    items(options) { option ->
                                        Text(
                                            modifier = Modifier.clickable {
                                                openDialog = false
                                                dispatchAction(option, selectedFriend)
                                            },
                                            style = MaterialTheme.typography.body1,
                                            text = option
                                        )
                                    }
                                }
                            },
                            buttons = {}
                        )
                    }
                }
            }
        }
    }

    private fun dispatchAction(option: String, friend: Friend) {
        when (option) {
            "Map" -> {
                val uri = Uri.parse("geo:0,0?q=${URLEncoder.encode(friend.home, "UTF-8")}")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
            "Email" -> {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_EMAIL, friend.email)
                startActivity(intent)
            }
            "Text" -> {
                val uri = Uri.parse("smsto:${friend.phone}")
                val intent = Intent(Intent.ACTION_SEND, uri)
                startActivity(intent)
            }
            "Call" -> {
                val uri = Uri.parse("tel:${friend.phone}")
                val intent = Intent(Intent.ACTION_DIAL, uri)
                startActivity(intent)
            }
            "Slack" -> {
                val uri = Uri.parse("slack://user?team=TR8N4694&id=${friend.slackId}")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
        }
    }
}

// doesn't matter if this fun is defined in/out of Activity, but if use @Preview, it must be defined outside
@Composable
fun FriendsList(friends: List<Friend>, onFriendClick: (Friend) -> Unit) {
    // LazyColumn is the Compose equivalent of RecyclerView
    // Only renders the visible elements passed to the items function.
    LazyColumn {
        items(friends) { friend ->
            // Styling for an individual item in the list
            Text(
                modifier = Modifier
                    .padding(all = 40.dp)
                    .clickable {
                        onFriendClick(friend)
                    },
                style = MaterialTheme.typography.body1,
                text = friend.name,
            )
        }
    }
}

