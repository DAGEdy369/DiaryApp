package com.example.diaryapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.diaryapp.ui.navigation.DiaryNavGraph
import com.example.diaryapp.ui.theme.DiaryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as DiaryApplication
        setContent {
            DiaryTheme {
                val navController = rememberNavController()
                DiaryNavGraph(
                    navController = navController,
                    repository = app.repository,
                    imageManager = app.imageManager
                )
            }
        }
    }
}
