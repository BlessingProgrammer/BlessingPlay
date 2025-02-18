package com.blessingsoftware.blessingplay.core.presentation.utils

fun normalizeFirstChar(char: Char?): Char {
    char ?: return '#'

    val map = mapOf(
        'À' to 'A', 'Á' to 'A', 'Ả' to 'A', 'Ã' to 'A', 'Ạ' to 'A',
        'Ă' to 'A', 'Ằ' to 'A', 'Ắ' to 'A', 'Ẳ' to 'A', 'Ẵ' to 'A', 'Ặ' to 'A',
        'Â' to 'A', 'Ầ' to 'A', 'Ấ' to 'A', 'Ẩ' to 'A', 'Ẫ' to 'A', 'Ậ' to 'A',

        'È' to 'E', 'É' to 'E', 'Ẻ' to 'E', 'Ẽ' to 'E', 'Ẹ' to 'E',
        'Ê' to 'E', 'Ề' to 'E', 'Ế' to 'E', 'Ể' to 'E', 'Ễ' to 'E', 'Ệ' to 'E',

        'Ì' to 'I', 'Í' to 'I', 'Ỉ' to 'I', 'Ĩ' to 'I', 'Ị' to 'I',

        'Ò' to 'O', 'Ó' to 'O', 'Ỏ' to 'O', 'Õ' to 'O', 'Ọ' to 'O',
        'Ô' to 'O', 'Ồ' to 'O', 'Ố' to 'O', 'Ổ' to 'O', 'Ỗ' to 'O', 'Ộ' to 'O',
        'Ơ' to 'O', 'Ờ' to 'O', 'Ớ' to 'O', 'Ở' to 'O', 'Ỡ' to 'O', 'Ợ' to 'O',

        'Ù' to 'U', 'Ú' to 'U', 'Ủ' to 'U', 'Ũ' to 'U', 'Ụ' to 'U',
        'Ư' to 'U', 'Ừ' to 'U', 'Ứ' to 'U', 'Ử' to 'U', 'Ữ' to 'U', 'Ự' to 'U',

        'Ỳ' to 'Y', 'Ý' to 'Y', 'Ỷ' to 'Y', 'Ỹ' to 'Y', 'Ỵ' to 'Y',

        'Đ' to 'D'
    )

    val normalized = map[char.uppercaseChar()] ?: char.uppercaseChar()
    return if (normalized in 'A'..'Z') normalized else '#'
}