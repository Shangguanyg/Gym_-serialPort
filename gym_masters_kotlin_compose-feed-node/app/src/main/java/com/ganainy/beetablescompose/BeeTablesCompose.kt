/*
 * Copyright 2023 Breens Mbaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.breens.beetablescompose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.breens.beetablescompose.components.TableHeaderComponent
import com.breens.beetablescompose.components.TableHeaderComponentWithoutColumnDividers
import com.breens.beetablescompose.components.TableRowComponent
import com.breens.beetablescompose.components.TableRowComponentWithoutDividers
import com.breens.beetablescompose.utils.extractMembers
import com.breens.beetablescompose.utils.lightColor
import com.breens.beetablescompose.utils.lightGray

/**
 * 🐝 A Compose UI data table library.
 *
 * @param data The list of data items to display in the table.要在表格中显示的数据项列表。
 * @param enableTableHeaderTitles show or hide the table header titles. If not set, by default the table header titles will be shown.显示或隐藏表格标题。如果未设置，默认情况下将显示表格标题。
 * @param headerTableTitles The list of header titles to display at the top of the table.要在表格顶部显示的标题列表
 * @param headerTitlesBorderColor The color of the border for the header titles, by default it will be [Color.LightGray].标题边框颜色，默认为 [Color.LightGray]
 * @param headerTitlesBorderWidth The width of the border for the header titles in DP, by default it will be "0.4.dp".标题边框宽度（单位为 DP），默认为 "0.4.dp"
 * @param headerTitlesTextStyle The text style to apply to the header titles, by default it will be [MaterialTheme.typography.bodySmall].应用于标题的文本样式，默认为 [MaterialTheme.typography.bodySmall]
 * @param headerTitlesBackGroundColor The background color for the header titles, by default it will be [Color.White].标题背景颜色，默认为 [Color.White]。
 * @param tableRowColors The list of background colors to alternate between rows in the table, by default it will be a list of: [Color.White], [Color.White].表格行交替使用的背景颜色列表，默认为 [Color.White]、[Color.White]。
 * @param rowBorderColor The color of the border for the table rows, by default it will be [Color.LightGray].表格行边框颜色，默认为 [Color.LightGray]
 * @param rowBorderWidth The width of the border for the table rows in DP, by default it will be "0.4.dp". 表格行边框宽度（单位为 DP），默认为 "0.4.dp"。
 * @param rowTextStyle The text style to apply to the data cells in the table rows, by default it will be [MaterialTheme.typography.bodySmall]. 应用于表格数据单元格的文本样式，默认为 [MaterialTheme.typography.bodySmall]
 * @param tableElevation The elevation of the entire table (Card elevation) in DP, by default it will be "6.dp". 整个表格的阴影高度（卡片阴影高度）（单位为 DP），默认为 "6.dp"。
 * @param shape The shape of the table's corners, by default it will be "RoundedCornerShape(4.dp)". 表格角部的形状，默认为 "RoundedCornerShape(4.dp)"。
 * @param disableVerticalDividers show or hide the vertical dividers between the table cells. If not set, by default the vertical dividers will be shown.显示或隐藏表格单元格之间的垂直分隔线。如果未设置，默认情况下将显示垂直分隔线。
 * @param horizontalDividerThickness The thickness of the horizontal dividers in DP, by default it will be "1.dp". Note: This will only be visible if [disableVerticalDividers] is set to true. 水平分隔线的粗细（单位为 DP），默认为 "1.dp"。注意：仅当 [disableVerticalDividers] 设置为 true 时才可见。
 * @param horizontalDividerColor The color of the horizontal dividers, by default it will be [Color.LightGray]. Note: This will only be visible if [disableVerticalDividers] is set to true. 水平分隔线的颜色，默认为 [Color.LightGray]。注意：仅当 [disableVerticalDividers] 设置为 true 时，此选项才可见。
 * @param contentAlignment The alignment of the content in the table cells, by default it will be [Alignment.Center]. 表格单元格内容的对齐方式，默认为 [Alignment.Center]。
 * @param textAlign The alignment of the text in the table cells, by default it will be [TextAlign.Center].表格单元格中文本的对齐方式，默认为 [TextAlign.Center]。
 */
@Composable
inline fun <reified T : Any> BeeTablesCompose(
    data: List<T>,
    enableTableHeaderTitles: Boolean = true,
    headerTableTitles: List<String>,
    headerTitlesBorderColor: Color = lightGray(),
    headerTitlesTextStyle: TextStyle = MaterialTheme.typography.bodySmall,
    headerTitlesBackGroundColor: Color = lightColor(),
    tableRowColors: List<Color> = listOf(
        lightColor(),
        lightColor(),
    ),
    rowBorderColor: Color = lightGray(),
    rowTextStyle: TextStyle = MaterialTheme.typography.bodySmall,
    tableElevation: Dp = 0.dp,
    shape: RoundedCornerShape = RoundedCornerShape(4.dp),
    borderStroke: BorderStroke = BorderStroke(
        width = 1.dp,
        color = lightGray(),
    ),
    disableVerticalDividers: Boolean = false,
    dividerThickness: Dp = 1.dp,
    horizontalDividerColor: Color = lightGray(),
    contentAlignment: Alignment = Alignment.Center,
    textAlign: TextAlign = TextAlign.Center,
    tablePadding: Dp = 0.dp,
    columnToIndexIncreaseWidth: Int? = null,
) {
    OutlinedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = tableElevation),
        shape = shape,
        border = borderStroke,
    ) {
        Column {
            if (enableTableHeaderTitles) {
                if (disableVerticalDividers) {
                    TableHeaderComponentWithoutColumnDividers(
                        headerTableTitles = headerTableTitles,
                        headerTitlesTextStyle = headerTitlesTextStyle,
                        headerTitlesBackGroundColor = headerTitlesBackGroundColor,
                        dividerThickness = dividerThickness,
                        contentAlignment = contentAlignment,
                        textAlign = textAlign,
                        tablePadding = tablePadding,
                        columnToIndexIncreaseWidth = columnToIndexIncreaseWidth,
                    )
                } else {
                    TableHeaderComponent(
                        headerTableTitles = headerTableTitles,
                        headerTitlesBorderColor = headerTitlesBorderColor,
                        headerTitlesTextStyle = headerTitlesTextStyle,
                        headerTitlesBackGroundColor = headerTitlesBackGroundColor,
                        contentAlignment = contentAlignment,
                        textAlign = textAlign,
                        tablePadding = tablePadding,
                        dividerThickness = dividerThickness,
                        columnToIndexIncreaseWidth = columnToIndexIncreaseWidth,
                    )
                }
            }

            data.forEachIndexed { index, data ->
                val rowData = extractMembers(data).map {
                    it.second // getting the value from the returned Pair
                }

                // alternate background colors between rows
                val tableRowBackgroundColor = if (index % 2 == 0) {
                    tableRowColors[0]
                } else {
                    tableRowColors[1]
                }

                if (disableVerticalDividers) {
                    TableRowComponentWithoutDividers(
                        data = rowData,
                        rowTextStyle = rowTextStyle,
                        rowBackGroundColor = tableRowBackgroundColor,
                        dividerThickness = dividerThickness,
                        horizontalDividerColor = horizontalDividerColor,
                        contentAlignment = contentAlignment,
                        textAlign = textAlign,
                        tablePadding = tablePadding,
                        columnToIndexIncreaseWidth = columnToIndexIncreaseWidth,
                    )
                } else {
                    TableRowComponent(
                        data = rowData,
                        rowBorderColor = rowBorderColor,
                        dividerThickness = dividerThickness,
                        rowTextStyle = rowTextStyle,
                        rowBackGroundColor = tableRowBackgroundColor,
                        contentAlignment = contentAlignment,
                        textAlign = textAlign,
                        tablePadding = tablePadding,
                        columnToIndexIncreaseWidth = columnToIndexIncreaseWidth,
                    )
                }
            }
        }
    }
}
