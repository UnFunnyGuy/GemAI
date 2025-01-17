package com.sarath.gem.core.base

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.Chat
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Devices
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Flight
import androidx.compose.material.icons.rounded.Help
import androidx.compose.material.icons.rounded.HistoryEdu
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.MedicalServices
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.QuestionMark
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Science
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.ui.graphics.vector.ImageVector

enum class PromptIcon(val icon: ImageVector, val description: String) {
    CODE(Icons.Rounded.Code, "Coding-related prompts"),
    QUESTION_MARK(Icons.Rounded.QuestionMark, "Queries or FAQs"),
    IDEA(Icons.Rounded.Lightbulb, "Creative or idea-generation prompts"),
    SURPRISE(Icons.Rounded.CardGiftcard, "Fun or surprising prompts"),
    INFO(Icons.Rounded.Info, "Informational or fact-based prompts"),
    WARNING(Icons.Rounded.Warning, "Caution or sensitive prompts"),
    HELP(Icons.Rounded.Help, "Help or assistance-related prompts"),
    CHAT(Icons.Rounded.Chat, "Conversational or general discussion prompts"),
    SCIENCE(Icons.Rounded.Science, "Science-related or technical prompts"),
    ART(Icons.Rounded.Brush, "Art or design-related prompts"),
    LITERATURE(Icons.AutoMirrored.Rounded.MenuBook, "Literature or writing-related prompts"),
    EDUCATION(Icons.Rounded.School, "Educational or learning-related prompts"),
    HEALTH(Icons.Rounded.MedicalServices, "Health or wellness-related prompts"),
    ENTERTAINMENT(Icons.Rounded.Movie, "Entertainment or media-related prompts"),
    FINANCE(Icons.Rounded.AttachMoney, "Finance or business-related prompts"),
    TRAVEL(Icons.Rounded.Flight, "Travel or exploration-related prompts"),
    FOOD(Icons.Rounded.Fastfood, "Food or culinary-related prompts"),
    FITNESS(Icons.Rounded.FitnessCenter, "Fitness or physical activity prompts"),
    ENVIRONMENT(Icons.Rounded.Eco, "Environment or sustainability-related prompts"),
    HISTORY(Icons.Rounded.HistoryEdu, "History-related prompts"),
    TECHNOLOGY(Icons.Rounded.Devices, "Technology or gadget-related prompts"),
    TRANSLATION(Icons.Rounded.Translate, "Translation or language-related prompts"),
}
