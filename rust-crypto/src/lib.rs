use std::ffi::{CStr, CString};
use std::os::raw::c_char;

/// Генерация ключей Baby JubJub
/// Возвращает: "privateKey|publicKeyX|publicKeyY"
#[no_mangle]
pub extern "C" fn generate_keys() -> *mut c_char {
    // TODO: Настоящая генерация Baby JubJub
    // Пока возвращаем mock данные для проверки что загрузка работает
    let private_key = "c6b5f8a5e9d7c3b2f8e9a7c5d3b1f9e7c5a3d1b9f7e5c3a1d9b7f5e3c1a9b7f5";
    let public_x = "ae7b8d5c3a1f9e7c5a3d1b9f7e5c3a1d9b7f5e3c1a9b7f5e3c1a9b7f5e3c1a9";
    let public_y = "1c5f8a3e9d7c5b3f1e9a7c5d3b1f9e7c5a3d1b9f7e5c3a1d9b7f5e3c1a9b7f5";

    let result = format!("{}|{}|{}", private_key, public_x, public_y);
    CString::new(result).unwrap().into_raw()
}

/// Подпись сообщения Baby JubJub
#[no_mangle]
pub extern "C" fn sign_message(
    private_key: *const c_char,
    message_hex: *const c_char,
) -> *mut c_char {
    unsafe {
        let _priv_key = CStr::from_ptr(private_key).to_str().unwrap_or("");
        let _msg_hex = CStr::from_ptr(message_hex).to_str().unwrap_or("");

        // TODO: Настоящая подпись Baby JubJub
        // Пока возвращаем mock подпись
        let signature = "a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0";
        CString::new(signature).unwrap().into_raw()
    }
}

/// Верификация подписи Baby JubJub
#[no_mangle]
pub extern "C" fn verify_signature(
    public_key_x: *const c_char,
    public_key_y: *const c_char,
    message_hex: *const c_char,
    signature_hex: *const c_char,
) -> bool {
    unsafe {
        let _pub_x = CStr::from_ptr(public_key_x).to_str().unwrap_or("");
        let _pub_y = CStr::from_ptr(public_key_y).to_str().unwrap_or("");
        let _msg = CStr::from_ptr(message_hex).to_str().unwrap_or("");
        let _sig = CStr::from_ptr(signature_hex).to_str().unwrap_or("");

        // TODO: Настоящая верификация Baby JubJub
        // Пока всегда возвращаем true для теста
        true
    }
}

/// Освобождение памяти
#[no_mangle]
pub extern "C" fn free_string(ptr: *mut c_char) {
    unsafe {
        if !ptr.is_null() {
            let _ = CString::from_raw(ptr);
        }
    }
}