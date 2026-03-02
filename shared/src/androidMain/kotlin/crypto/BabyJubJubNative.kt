package crypto

// ✅ Добавь эти импорты:
import java.lang.System
import java.lang.UnsatisfiedLinkError

object BabyJubJubNative {

    init {
        try {
            System.loadLibrary("valera_crypto")
            println("✅ Baby JubJub native library loaded")
        } catch (e: UnsatisfiedLinkError) {
            println("❌ Failed to load Baby JubJub library: ${e.message}")
            throw e
        }
    }

    data class KeyPair(
        val privateKey: String,
        val publicKeyX: String,
        val publicKeyY: String
    )

    // Native methods
    private external fun generate_keys(): String
    private external fun sign_message(privateKey: String, messageHex: String): String
    private external fun verify_signature(
        publicKeyX: String,
        publicKeyY: String,
        messageHex: String,
        signatureHex: String
    ): Boolean

    fun generateKeys(): KeyPair {
        val result = generate_keys()
        val parts = result.split("|")
        require(parts.size == 3) { "Invalid key generation result" }

        return KeyPair(
            privateKey = parts[0],
            publicKeyX = parts[1],
            publicKeyY = parts[2]
        )
    }

    fun sign(privateKeyHex: String, message: ByteArray): String {
        val messageHex = message.joinToString("") { "%02x".format(it) }
        return sign_message(privateKeyHex, messageHex)
    }

    fun verify(publicKeyX: String, publicKeyY: String, message: ByteArray, signatureHex: String): Boolean {
        val messageHex = message.joinToString("") { "%02x".format(it) }
        return verify_signature(publicKeyX, publicKeyY, messageHex, signatureHex)
    }
}