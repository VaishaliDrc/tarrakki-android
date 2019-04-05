package org.supportcompact.events

import org.supportcompact.CoreApp
import org.supportcompact.R

data class ShowError(val error: String)

data class ShowErrorDialog(val title: String = CoreApp.getInstance().getString(R.string.app_name), val error: String)