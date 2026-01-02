package com.example.library.ui.screens

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.stylusHoverIcon
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.library.R
import com.example.library.ui.theme.Dark
import com.example.library.ui.theme.Light
import com.example.library.ui.theme.Yellow
import com.example.library.ui.theme.YelowAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    onThemeChanged: ((String) -> Unit)? = null,
    onStartScrenChanged: ((String) -> Unit)? = null
) {

    val context = LocalContext.current

    val sharedPrefs = remember {
        context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    }

    val themeOptions = listOf(
        "Светлая" to "light",
        "Темная" to "dark",
        "Как в системе" to "system"
    )

    var themeMode by remember {
        mutableStateOf(sharedPrefs.getString("theme_mode", "system") ?: "system")
    }

    val selectedThemeName = remember(themeMode) {
        themeOptions.find { it.second == themeMode }?.first ?: "Как в системе"
    }


    val switchBackground by animateColorAsState(
        targetValue = when (themeMode) {
            "light" -> Light
            "dark" -> Dark
            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        },
        animationSpec = tween(durationMillis = 300),
        label = "theme_switch_pos"
    )

    val startScreenOptions = listOf(
        "Главная" to "home",
        "Моя полка" to "book_shelf",
        "История" to "history",
        "Напоминания" to "notifications"
    )

    var selectedStartScreen by remember {
        mutableStateOf(
            sharedPrefs.getString("start_screen", "home") ?: "home"
        )
    }

    val selectedStartScreenName = remember(selectedStartScreen) {
        startScreenOptions.find { it.second == selectedStartScreen }?.first ?: "Главная"
    }

    var expanded by remember { mutableStateOf(false) }

    var notificationsEnabled by remember { mutableStateOf(sharedPrefs.getBoolean("notifications_enabled", true)) }

    Scaffold(
        topBar = {
            MediumTopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                ),
                title = {
                    Text(
                        "Настройки",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = (-0.25).sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    if (navController != null) {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Назад",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f),
                            MaterialTheme.colorScheme.background
                        ),
                        startY = 0f,
                        endY = 600f
                    )
                )
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 12.dp,
                                shape = MaterialTheme.shapes.extraLarge,
                                ambientColor = MaterialTheme.colorScheme.primary.copy(
                                    alpha = 0.2f
                                )
                            ),
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                        ),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(
                                            brush = Brush.linearGradient(
                                                colors = listOf(
                                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                                                )
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.paint_dark),
                                        contentDescription = "Тема",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(28.dp),
                                    )
                                }

                                Column {
                                    Text(
                                        "Тема приложения",
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    Text(
                                        "Выберите оформление",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(32.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(switchBackground),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.clickable {
                                            themeMode = "light"
                                            sharedPrefs.edit()
                                                .putString("theme_mode", "light")
                                                .apply()
                                            onThemeChanged?.invoke("light")
                                        }
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (themeMode == "light") Yellow.copy(alpha = 0.3f)
                                                    else Color.Transparent
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.sun),
                                                contentDescription = "Светлая тема",
                                                tint = if (themeMode == "light") Yellow
                                                else Light,
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                        Text(
                                            "Светлая",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = if (themeMode == "light") MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.onSurface.copy(
                                                alpha = 0.5f
                                            ),
                                            fontWeight = if (themeMode == "light") FontWeight.Bold else
                                                FontWeight.Normal
                                        )
                                    }
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.clickable {
                                            themeMode = "dark"
                                            sharedPrefs.edit()
                                                .putString("theme_mode", "dark")
                                                .apply()
                                            onThemeChanged?.invoke("dark")
                                        }
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (themeMode == "dark") MaterialTheme.colorScheme.primary.copy(
                                                        alpha = 0.3f
                                                    )
                                                    else Color.Transparent
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.moon),
                                                contentDescription = "Темная тема",
                                                tint = if (themeMode == "dark") MaterialTheme.colorScheme.primary
                                                else Dark,
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                        Text(
                                            "Темная",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = if (themeMode == "dark") MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                            fontWeight = if (themeMode == "dark") FontWeight.Bold else FontWeight.Normal
                                        )
                                    }

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.clickable{
                                            themeMode = "system"
                                            sharedPrefs.edit()
                                                .putString("theme_mode", "system") // ПРАВИЛЬНО
                                                .apply()
                                            onThemeChanged?.invoke("system")
                                        }
                                    ) {
                                        Box(modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if(themeMode == "system") MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                                                else Color.Transparent
                                            ),
                                            contentAlignment = Alignment.Center
                                            ) {
                                            Icon(
                                                painter = painterResource(R.drawable.outline_settings_24),
                                                contentDescription = "Системная тема",
                                                tint = if (themeMode == "system") MaterialTheme.colorScheme.secondary
                                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                        Text(
                                            "Системная",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = if (themeMode == "system") MaterialTheme.colorScheme.secondary
                                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                            fontWeight = if (themeMode == "system") FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = when (themeMode) {
                                    "light" -> "☀️ Светлая тема обеспечивает лучшую читаемость при дневном свете"
                                    "dark" -> "🌙 Темная тема снижает нагрузку на глаза в условиях слабого освещения"
                                    else -> "🔄 Тема будет автоматически меняться в зависимости от настроек вашего устройства"
                                },
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    lineHeight = 20.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                            )
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 12.dp,
                                shape = MaterialTheme.shapes.extraLarge,
                                ambientColor = MaterialTheme.colorScheme.secondary.copy(
                                    alpha = 0.2f
                                )
                            ),
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                        ),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(
                                            brush = Brush.linearGradient(
                                                colors = listOf(
                                                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                                                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                                                )
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.home),
                                        contentDescription = "Стартовый экран",
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        "Стартовый экран",
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = MaterialTheme.colorScheme.secondary
                                    )

                                    Text(
                                        "Выберите экран при запуске",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(32.dp))

                            Box(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column {
                                    Text(
                                        "Экран при запуске",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(
                                                MaterialTheme.colorScheme.surfaceVariant.copy(
                                                    alpha = 0.5f
                                                )
                                            )
                                            .clickable { expanded = true }
                                            .padding(vertical = 16.dp, horizontal = 20.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                Icon(
                                                    painter = painterResource(
                                                        when (selectedStartScreen) {
                                                            "home" -> R.drawable.home
                                                            "book_shelf" -> R.drawable.bookshelf
                                                            "history" -> R.drawable.history
                                                            "notifications" -> R.drawable.outline_alarm_24
                                                            else -> R.drawable.home
                                                        }
                                                    ),
                                                    contentDescription = selectedStartScreenName,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(24.dp)
                                                )

                                                Text(
                                                    selectedStartScreenName,
                                                    style = MaterialTheme.typography.titleMedium.copy(
                                                        fontWeight = FontWeight.SemiBold
                                                    ),
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                            Icon(
                                                imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                                                else Icons.Default.ArrowDropDown,
                                                contentDescription = "Раскрыть",
                                                tint = MaterialTheme.colorScheme.onSurface.copy(
                                                    alpha = 0.6f
                                                )
                                            )
                                        }
                                    }

                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                MaterialTheme.colorScheme.surface,
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                    ) {
                                        startScreenOptions.forEach { (name, key) ->
                                            DropdownMenuItem(
                                                text = {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                                    ) {
                                                        Icon(
                                                            painter = painterResource(
                                                                when (key) {
                                                                    "home" -> R.drawable.home
                                                                    "book_shelf" -> R.drawable.bookshelf
                                                                    "history" -> R.drawable.history
                                                                    "notifications" -> R.drawable.outline_alarm_24
                                                                    else -> R.drawable.home
                                                                }
                                                            ),
                                                            contentDescription = name,
                                                            tint = if (selectedStartScreen == key) MaterialTheme.colorScheme.primary
                                                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                                            modifier = Modifier.size(20.dp)
                                                        )

                                                        Text(
                                                            name,
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            color = if (selectedStartScreen == key) MaterialTheme.colorScheme.primary
                                                            else MaterialTheme.colorScheme.onSurface,
                                                            fontWeight = if (selectedStartScreen == key) FontWeight.SemiBold
                                                            else FontWeight.Normal
                                                        )
                                                    }
                                                },
                                                onClick = {
                                                    selectedStartScreen = key
                                                    sharedPrefs.edit()
                                                        .putString("start_screen", key)
                                                        .apply()
                                                    onStartScrenChanged?.invoke(key)
                                                    expanded = false
                                                },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 8.dp)
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "Выбранный экран будет открываться автоматически при каждом запуске приложения",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    lineHeight = 20.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                            )
                        }
                    }
                }
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 12.dp,
                                shape = MaterialTheme.shapes.extraLarge,
                                ambientColor = MaterialTheme.colorScheme.tertiary.copy(
                                    alpha = 0.2f
                                )
                            ),
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                        ),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(
                                            brush = Brush.linearGradient(
                                                colors = listOf(
                                                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f),
                                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                                )
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.outline_alarm_24),
                                        contentDescription = "Уведомления",
                                        tint = MaterialTheme.colorScheme.tertiary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Column {
                                    Text("Уведомления",
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = MaterialTheme.colorScheme.tertiary)

                                    Text(
                                        "Управление уведомлениями",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(32.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        "Включить уведомления",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text("Получайте напоминания книгах",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(
                                            alpha = 0.6f
                                        ),
                                        modifier = Modifier.padding(top = 4.dp))
                                }

                                Switch(
                                    notificationsEnabled,
                                    onCheckedChange = {
                                        isChecked ->
                                        notificationsEnabled = isChecked
                                        sharedPrefs.edit()
                                            .putBoolean("notifications_enabled", isChecked).apply()
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                                        checkedTrackColor = MaterialTheme.colorScheme.primary.copy(
                                            alpha = 0.5f
                                        ),
                                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                                )
                            }
                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = if (notificationsEnabled)
                                    "🔔 Уведомления включены. Вы будете получать напоминания о книгах согласно установленному расписанию."
                                else
                                    "🔕 Уведомления отключены. Вы не будете получать напоминания о книгах.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    lineHeight = 20.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

