use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::jstring;
use sha2::{Sha256, Digest};
use ark_ed_on_bn254::{EdwardsProjective as JubJubProjective, Fr, EdwardsAffine as JubJubAffine};
use ark_ff::{UniformRand, PrimeField, BigInteger};
use ark_ec::{CurveGroup, Group};
use ark_std::rand::SeedableRng;

/// Генерация ключей через Arkworks
#[no_mangle]
pub extern "system" fn Java_crypto_BabyJubJubNative_generate_1keys(
    mut env: JNIEnv,
    _class: JClass,
) -> jstring {
    use std::time::{SystemTime, UNIX_EPOCH};

    // Генерируем seed
    let timestamp = SystemTime::now()
        .duration_since(UNIX_EPOCH)
        .unwrap()
        .as_nanos();

    let random_bytes: [u8; 16] = rand::random();

    let mut seed = Vec::new();
    seed.extend_from_slice(&random_bytes);
    seed.extend_from_slice(&timestamp.to_le_bytes());

    // Хешируем
    let mut hasher = Sha256::new();
    hasher.update(&seed);
    let hash = hasher.finalize();

    // Создаём RNG из seed
    let mut seed_array = [0u8; 32];
    seed_array.copy_from_slice(&hash[..32]);
    let mut rng = ark_std::rand::rngs::StdRng::from_seed(seed_array);

    // Генерируем приватный ключ (scalar field element)
    let private_key: Fr = Fr::rand(&mut rng);

    // Получаем базовую точку генератора
    let base_point = JubJubProjective::generator();

    // Вычисляем публичный ключ: public_key = private_key * base_point
    let public_key_projective = base_point * private_key;

    // Конвертируем в affine координаты
    let public_key: JubJubAffine = public_key_projective.into_affine();

    // Сериализуем в байты
    let private_bytes = private_key.into_bigint().to_bytes_le();
    let pub_x_bytes = public_key.x.into_bigint().to_bytes_le();
    let pub_y_bytes = public_key.y.into_bigint().to_bytes_le();

    let private_hex = hex::encode(&private_bytes);
    let pub_x_hex = hex::encode(&pub_x_bytes);
    let pub_y_hex = hex::encode(&pub_y_bytes);

    let result = format!("{}|{}|{}", private_hex, pub_x_hex, pub_y_hex);

    match env.new_string(result) {
        Ok(jstr) => jstr.into_raw(),
        Err(_) => std::ptr::null_mut(),
    }
}

/// Подпись (временно mock, потом добавим настоящую EdDSA)
#[no_mangle]
pub extern "system" fn Java_crypto_BabyJubJubNative_sign_1message(
    mut env: JNIEnv,
    _class: JClass,
    private_key_hex: JString,
    message_hex: JString,
) -> jstring {
    let priv_hex: String = match env.get_string(&private_key_hex) {
        Ok(s) => s.into(),
        Err(_) => return std::ptr::null_mut(),
    };

    let msg_hex: String = match env.get_string(&message_hex) {
        Ok(s) => s.into(),
        Err(_) => return std::ptr::null_mut(),
    };

    // Временно mock подпись
    // TODO: Реализовать настоящую EdDSA подпись
    let mut hasher = Sha256::new();
    hasher.update(priv_hex.as_bytes());
    hasher.update(msg_hex.as_bytes());
    let sig1 = hasher.finalize();

    let mut hasher2 = Sha256::new();
    hasher2.update(&sig1);
    let sig2 = hasher2.finalize();

    let mut signature = Vec::new();
    signature.extend_from_slice(&sig1);
    signature.extend_from_slice(&sig2);

    let sig_hex = hex::encode(&signature);

    match env.new_string(sig_hex) {
        Ok(jstr) => jstr.into_raw(),
        Err(_) => std::ptr::null_mut(),
    }
}

/// Верификация
#[no_mangle]
pub extern "system" fn Java_crypto_BabyJubJubNative_verify_1signature(
    mut env: JNIEnv,
    _class: JClass,
    _public_key_x_hex: JString,
    _public_key_y_hex: JString,
    _message_hex: JString,
    signature_hex: JString,
) -> bool {
    let sig_hex: String = match env.get_string(&signature_hex) {
        Ok(s) => s.into(),
        Err(_) => return false,
    };

    let sig_bytes = match hex::decode(&sig_hex) {
        Ok(b) => b,
        Err(_) => return false,
    };

    sig_bytes.len() == 64
}