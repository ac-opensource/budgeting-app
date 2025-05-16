package dev.pandesal.sbp.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun FilterTab(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    tabs: List<String>,
    onSelectedIndexChange: (Int) -> Unit = {}
) {

    SecondaryTabRow(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(50)),
        selectedTabIndex = selectedIndex,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.primary,
        indicator = {
            Box(
                Modifier
                    .tabIndicatorOffset(selectedIndex)
                    .padding(5.dp)
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(50)
                    )
                    .border(BorderStroke(2.dp, Color.White), RoundedCornerShape(50))
            )
        },
        divider = {}
    ) {
        tabs.forEachIndexed { index, option ->
            Tab(
                selected = selectedIndex == index,
                onClick = {
                    onSelectedIndexChange(index)
                },
                selectedContentColor = MaterialTheme.colorScheme.onPrimary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .zIndex(1f)
                    .padding(horizontal = 4.dp, vertical = 8.dp)
            ) {
                Text(
                    text = option,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                )
            }
        }
    }
}
