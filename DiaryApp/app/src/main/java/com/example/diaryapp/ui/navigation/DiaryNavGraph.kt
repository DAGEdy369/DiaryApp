package com.example.diaryapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.*
import com.example.diaryapp.data.repository.DiaryRepository
import com.example.diaryapp.domain.ImageStorageManager
import com.example.diaryapp.ui.screens.detail.DetailScreen
import com.example.diaryapp.ui.screens.detail.DetailViewModel
import com.example.diaryapp.ui.screens.detail.DetailViewModelFactory
import com.example.diaryapp.ui.screens.edit.EditScreen
import com.example.diaryapp.ui.screens.edit.EditViewModel
import com.example.diaryapp.ui.screens.edit.EditViewModelFactory
import com.example.diaryapp.ui.screens.home.HomeScreen
import com.example.diaryapp.ui.screens.home.HomeViewModel
import com.example.diaryapp.ui.screens.home.HomeViewModelFactory

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Detail : Screen("detail/{entryId}") {
        fun createRoute(id: Long) = "detail/$id"
    }
    object Edit : Screen("edit?entryId={entryId}") {
        fun createRoute(id: Long? = null) = if (id != null) "edit?entryId=$id" else "edit"
    }
}

@Composable
fun DiaryNavGraph(
    navController: NavHostController,
    repository: DiaryRepository,
    imageManager: ImageStorageManager
) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {

        composable(Screen.Home.route) {
            val vm: HomeViewModel = viewModel(factory = HomeViewModelFactory(repository))
            HomeScreen(
                vm = vm,
                onEntryClick = { navController.navigate(Screen.Detail.createRoute(it)) },
                onNewEntry = { navController.navigate(Screen.Edit.createRoute()) }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("entryId") { type = NavType.LongType })
        ) { backStack ->
            val id = backStack.arguments!!.getLong("entryId")
            val vm: DetailViewModel = viewModel(factory = DetailViewModelFactory(repository, id))
            DetailScreen(
                vm = vm,
                onEdit = { navController.navigate(Screen.Edit.createRoute(id)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Edit.route,
            arguments = listOf(navArgument("entryId") {
                nullable = true
                defaultValue = null
                type = NavType.StringType
            })
        ) { backStack ->
            val id = backStack.arguments?.getString("entryId")?.toLongOrNull()
            val vm: EditViewModel = viewModel(factory = EditViewModelFactory(repository, imageManager, id))
            EditScreen(
                vm = vm,
                onSaved = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
