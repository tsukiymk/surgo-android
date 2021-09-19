package app.surgo.common.compose.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Preference(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable RowScope.() -> Unit = {}
) {
    Column(modifier) {
        Row(
            Modifier.fillMaxWidth()
                .height(64.dp)
                .clickable { onClick() }
                .padding(horizontal = 16.dp)
        ) {
            if (leading != null) {
                Row(
                    modifier = Modifier.fillMaxHeight()
                        .width(68.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CompositionLocalProvider(
                        LocalContentAlpha provides ContentAlpha.high,
                        content = leading
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxHeight()
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.h6.copy(fontSize = 16.sp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (subtitle != null) {
                            Text(
                                text = subtitle,
                                color = MaterialTheme.colors.onPrimary.copy(alpha = 0.6f),
                                style = MaterialTheme.typography.subtitle2,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Row(
                    modifier = Modifier.fillMaxHeight(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    content = trailing
                )
            }
        }
    }
}

@Composable
fun SwitchPreference(
    title: String,
    value: Boolean,
    onValueChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leading: @Composable (() -> Unit)? = null
) {
    Preference(
        title = title,
        onClick = { onValueChange(!value) },
        modifier = modifier,
        subtitle = subtitle,
        leading = leading,
        trailing = {
            Switch(
                checked = value,
                onCheckedChange = { onValueChange(!value) }
            )
        }
    )
}

@Composable
fun RadioPreference(
    selected: Boolean,
    title: String,
    onValueChange: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leading: @Composable (() -> Unit)? = null
) {
    Preference(
        title = title,
        onClick = onValueChange,
        modifier = modifier,
        subtitle = subtitle,
        leading = leading,
        trailing = {
            RadioButton(
                selected = selected,
                onClick = onValueChange,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    )
}

@Composable
fun <T> SelectListPreference(
    title: String,
    value: T,
    values: Map<T, String>,
    onValueChange: (T) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (RowScope.() -> Unit) = {}
) {
    var openDialog by remember { mutableStateOf(false) }

    Preference(
        title = title,
        onClick = {
            openDialog = true
        },
        modifier = modifier,
        subtitle = subtitle,
        leading = leading,
        trailing = trailing
    )
    if (openDialog) {
        AlertDialog(
            onDismissRequest = { openDialog = false },
            title = {
                Text(
                    text = "{title}",
                    style = MaterialTheme.typography.h6
                )
            },
            text = {
                LazyColumn {
                    values.forEach { (key, string) ->
                        item {
                            Row(
                                Modifier.fillMaxWidth()
                                    .height(48.dp)
                                    .clickable { onValueChange(key) }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxHeight(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = value == key,
                                        onClick = { onValueChange(key) }
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxHeight()
                                        .padding(start = 16.dp)
                                        .weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = string,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { openDialog = false }
                ) {
                    Text(
                        text = "Agree",
                        style = MaterialTheme.typography.button,
                        color = MaterialTheme.colors.primary
                    )
                }
            }
        )
    }
}

@Composable
fun PreferenceGroup(
    modifier: Modifier = Modifier,
    title: String,
    content: @Composable () -> Unit
) {
    Column(modifier) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = title.uppercase(),
            color = MaterialTheme.colors.secondary,
            fontWeight = FontWeight.Bold
        )
        content()
    }
}
