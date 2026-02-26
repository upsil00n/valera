package ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.composables.buttons.NavigateUpButton
import ui.composables.Logo
import ui.composables.ScreenHeading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignatureRequestsView(
    onClickBack: () -> Unit,
    onClickLogo: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    ScreenHeading("Signature Requests")
                },
                navigationIcon = {
                    NavigateUpButton(onClickBack)
                },
                actions = {
                    Logo(onClick = onClickLogo)
                    Spacer(Modifier.width(15.dp))
                }
            )
        }
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Signature Requests",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "This is where signature requests will appear",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(Modifier.height(32.dp))

            Button(onClick = onClickBack) {
                Text("Back to Settings")
            }
        }
    }
}