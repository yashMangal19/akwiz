package com.akwiz.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.akwiz.android.ui.quiz.QuizRoute
import com.akwiz.android.ui.theme.AkwizTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AkwizTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { insets ->
                    QuizRoute(Modifier.padding(insets))
                }
            }
        }
    }
}
