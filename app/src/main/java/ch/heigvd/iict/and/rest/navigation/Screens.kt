package ch.heigvd.iict.and.rest.navigation

import androidx.annotation.StringRes
import ch.heigvd.iict.and.rest.R

enum class AppScreens(@StringRes val title: Int) {
    Contacts(title = R.string.screen_list_title),
    NewContact(title = R.string.screen_detail_title_new),
    EditContact(title = R.string.screen_detail_title_edit)
}