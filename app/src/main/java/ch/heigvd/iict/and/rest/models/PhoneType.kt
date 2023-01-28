package ch.heigvd.iict.and.rest.models

enum class PhoneType {
    HOME, OFFICE, MOBILE, FAX
}

fun PhoneType?.toString(): String {
    return when (this) {
        PhoneType.HOME -> "Home"
        PhoneType.OFFICE -> "Office"
        PhoneType.MOBILE -> "Mobile"
        PhoneType.FAX -> "Fax"
        else -> "Unknown"
    }
}