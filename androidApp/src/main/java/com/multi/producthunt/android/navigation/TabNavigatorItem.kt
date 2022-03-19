package com.multi.producthunt.android.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab

@Composable
fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current

    NavigationBarItem(
        selected = tabNavigator.current == tab,
        onClick = { tabNavigator.current = tab },
        icon = { tab.icon?.let { Icon(painter = it, contentDescription = tab.title) } })
}