package com.parentguard.parent.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.parentguard.parent.ui.screens.*

object Routes {
    const val Home = "home"
    const val Dashboard = "dashboard"
    const val Time = "time"
    const val Apps = "apps"
    const val AppDetail = "app_detail"
    const val AppBatch = "app_batch"
    const val Downtime = "downtime"
    const val Content = "content"
    const val Stats = "stats"
    const val Location = "location"
    const val Approve = "approve"
    const val Pair = "pair"
}

@Composable
fun ParentNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.Home) {
        composable(Routes.Home) {
            HomeScreen(navController)
        }
        composable(Routes.Dashboard) {
            DashboardScreen(navController)
        }
        composable(Routes.Time) {
            TimeScreen(navController)
        }
        composable(Routes.Apps) {
            AppsScreen(navController)
        }
        composable(Routes.AppDetail) {
            AppDetailScreen(navController)
        }
        composable(Routes.AppBatch) {
            AppBatchScreen(navController)
        }
        composable(Routes.Downtime) {
            DowntimeScreen(navController)
        }
        composable(Routes.Content) {
            ContentScreen(navController)
        }
        composable(Routes.Stats) {
            StatsScreen(navController)
        }
        composable(Routes.Location) {
            LocationScreen(navController)
        }
        composable(Routes.Approve) {
            ApproveScreen(navController)
        }
        composable(Routes.Pair) {
            PairScreen(navController)
        }
    }
}
