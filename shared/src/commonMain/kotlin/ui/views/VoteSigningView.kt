package ui.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import ui.composables.Logo
import ui.composables.ScreenHeading
import ui.composables.VoteSigningRequestCard
import ui.viewmodels.VoteSigningViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoteSigningView(
    onClickLogo: () -> Unit,
    onClickSettings: () -> Unit,
    vm: VoteSigningViewModel = koinViewModel()
) {
    val pendingRequests by vm.pendingRequests.collectAsStateWithLifecycle()
    var showSignDialog by remember { mutableStateOf(false) }
    var selectedRequestId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ScreenHeading(
                            "Vote Signing Requests",
                            Modifier.weight(1f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { vm.refreshRequests() }) {
                        Icon(Icons.Outlined.Refresh, "Refresh")
                    }
                    Logo(onClick = onClickLogo)
                    Column(modifier = Modifier.clickable(onClick = onClickSettings)) {
                        Icon(Icons.Outlined.Settings, null)
                    }
                    Spacer(Modifier.width(15.dp))
                }
            )
        }
    ) { scaffoldPadding ->
        Box(modifier = Modifier.padding(scaffoldPadding)) {
            if (pendingRequests.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Key,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "No pending requests",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Vote signing requests will appear here",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // List of requests
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "${pendingRequests.size} pending request(s)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(8.dp))
                    }

                    items(pendingRequests, key = { it.id }) { request ->
                        VoteSigningRequestCard(
                            request = request,
                            onSign = {
                                selectedRequestId = request.id
                                showSignDialog = true
                            },
                            onReject = {
                                vm.rejectVote(request.id)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }

    // Dialog for entering private key
    if (showSignDialog && selectedRequestId != null) {
        var privateKey by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showSignDialog = false },
            title = { Text("Sign Vote") },
            text = {
                Column {
                    Text("Enter your private key to sign this vote:")
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = privateKey,
                        onValueChange = { privateKey = it },
                        label = { Text("Private Key") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        vm.signVote(selectedRequestId!!, privateKey)
                        showSignDialog = false
                        privateKey = ""
                    },
                    enabled = privateKey.isNotEmpty()
                ) {
                    Text("Sign")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showSignDialog = false
                    privateKey = ""
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}