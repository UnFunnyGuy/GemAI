package com.sarath.gem.presentation.screen.chat.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.sarath.gem.core.base.PromptIcon
import com.sarath.gem.domain.model.StartUpPrompt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatStartup(modifier: Modifier = Modifier, startups: List<StartUpPrompt>, onSelect: (String) -> Unit) {

    FlowRow(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        maxItemsInEachRow = 2,
    ) {
        startups.forEach { startup ->
            ChatStartupItem(prompt = startup.text, icon = startup.icon, onClick = { onSelect(startup.text) })
        }
    }
}

// TODO : Tablet UI
@Composable
fun ChatStartupItem(modifier: Modifier = Modifier, prompt: String, icon: PromptIcon, onClick: () -> Unit) {
    Row(
        modifier =
            modifier
                .clip(MaterialTheme.shapes.medium)
                .clickable(onClick = onClick)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.medium,
                )
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                    shape = MaterialTheme.shapes.medium,
                )
                .fillMaxWidth()
                .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(imageVector = icon.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = prompt,
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSecondaryContainer),
        )
    }
}
