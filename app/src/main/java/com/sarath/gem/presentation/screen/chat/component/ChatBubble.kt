package com.sarath.gem.presentation.screen.chat.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.compose.components.markdownComponents
import com.mikepenz.markdown.compose.elements.highlightedCodeBlock
import com.mikepenz.markdown.compose.elements.highlightedCodeFence
import com.mikepenz.markdown.model.DefaultMarkdownColors
import com.mikepenz.markdown.model.DefaultMarkdownTypography
import com.sarath.gem.data.local.model.Participant

@Composable
fun ChatBubble(
    modifier: Modifier = Modifier,
    content: String,
    participant: Participant,
    color: DefaultMarkdownColors,
    typography: DefaultMarkdownTypography,
) {

    Column(
        modifier = modifier,
        horizontalAlignment = if (participant == Participant.USER) Alignment.End else Alignment.Start,
    ) {
        if (participant == Participant.USER) {
            Text(text = content, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        } else {
            Markdown(
                content = content,
                components = markdownComponents(codeBlock = highlightedCodeBlock, codeFence = highlightedCodeFence),
                colors = color,
                typography = typography,
            )
        }
    }
}
