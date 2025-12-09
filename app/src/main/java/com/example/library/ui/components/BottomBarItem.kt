package com.example.library.ui.components

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.util.TableInfo

@Composable
fun BottomBarItem(
    icon: Int,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    selectedColor: Color,
    unselectedColor: Color
) {

    val itemColor = if(isSelected) selectedColor else unselectedColor

    Column(modifier = Modifier.width(80.dp)
        .clickable(
            onClick = onClick,
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center)
    {
        Box(
            modifier = Modifier
                .size(if(isSelected) 52.dp else 48.dp)
                .background(
                    color = if (isSelected) selectedColor.copy(alpha = 0.1f) else
                    Color.Transparent
                )
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = "label",
                modifier = Modifier.size(if(isSelected) 28.dp else 24.dp),
                tint = itemColor
            )
        }
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            color = itemColor,
            fontSize = 10.sp,
            fontWeight = if(isSelected) FontWeight.Medium else FontWeight.Normal,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis
        )

    }
}