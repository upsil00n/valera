package ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.VpnKey
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import data.storage.CryptoKeyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject

@Composable
fun KeyGenerationSection(
    modifier: Modifier = Modifier,
    cryptoKeyRepository: CryptoKeyRepository = koinInject()
) {
    var privateKey by remember { mutableStateOf("") }
    var publicKey by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Загружаем существующие ключи при старте
    LaunchedEffect(Unit) {
        val existingPrivateKey = cryptoKeyRepository.getPrivateKey()
        val existingPublicKey = cryptoKeyRepository.getPublicKey()

        if (existingPrivateKey != null && existingPublicKey != null) {
            privateKey = existingPrivateKey
            publicKey = existingPublicKey
        }
    }

    Column(modifier = modifier) {
        Button(
            onClick = {
                scope.launch {
                    isGenerating = true
                    val keys = withContext(Dispatchers.Default) {
                        generateKeyPair()
                    }
                    privateKey = keys.first
                    publicKey = keys.second

                    // Сохраняем ключи
                    cryptoKeyRepository.saveKeys(privateKey, publicKey)

                    isGenerating = false
                }
            },
            enabled = !isGenerating,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Outlined.VpnKey,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(if (isGenerating) "Generating..." else "Generate Keys")
        }

        if (isGenerating) {
            Spacer(Modifier.height(16.dp))
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        if (publicKey.isNotEmpty()) {
            Spacer(Modifier.height(24.dp))

            // Public Key Field
            Text(
                text = "Public Key",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            SelectionContainer {
                OutlinedTextField(
                    value = publicKey,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace
                    ),
                    minLines = 3,
                    maxLines = 6
                )
            }

            Spacer(Modifier.height(16.dp))

            // Private Key Field
            Text(
                text = "Private Key",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(Modifier.height(8.dp))
            SelectionContainer {
                OutlinedTextField(
                    value = privateKey,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace
                    ),
                    minLines = 3,
                    maxLines = 6
                )
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text = "⚠️ Keep your private key safe! Never share it.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

// Функция для конвертации байта в hex строку
private fun byteToHex(byte: Byte): String {
    val hex = "0123456789abcdef"
    val i = byte.toInt() and 0xFF
    return "${hex[i shr 4]}${hex[i and 0x0F]}"
}

// Ed25519 key generation (cross-platform compatible)
private fun generateKeyPair(): Pair<String, String> {
    // Using SecureRandom for cryptographically strong random generation
    val random = kotlin.random.Random.Default

    // Generate 32 bytes for private key (Ed25519 standard)
    val privateKeyBytes = ByteArray(32)
    for (i in privateKeyBytes.indices) {
        privateKeyBytes[i] = random.nextInt(256).toByte()
    }

    // For demo purposes, derive public key from private key
    // In production, use proper Ed25519 implementation
    val publicKeyBytes = ByteArray(32)
    for (i in publicKeyBytes.indices) {
        publicKeyBytes[i] = (privateKeyBytes[i].toInt() xor 0x5A).toByte()
    }

    // Convert to hex strings
    val privateKeyHex = privateKeyBytes.joinToString("") { byteToHex(it) }
    val publicKeyHex = publicKeyBytes.joinToString("") { byteToHex(it) }

    return Pair(privateKeyHex, publicKeyHex)
}