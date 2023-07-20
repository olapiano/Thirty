package com.olaraabius.uppgift1.thirty

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import com.olaraabius.uppgift1.thirty.ui.theme.ThirtyTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import com.olaraabius.uppgift1.thirty.model.PointsCategory
import kotlinx.coroutines.launch

/**
 * The Main View of the dice game Thirty
 *
 * @author Ola Råbius Magnusson
 * @since 2023-06-26
 */

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    private val gameModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, this@MainActivity.toString())

        setContent {
            ThirtyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Layout(
                        gameModel::getImages,
                        gameModel::getPoints,
                        gameModel::getPointsCategoriesAvailable,
                        gameModel::getPointsCategorySelected,
                        gameModel::getPointsFromDices,
                        gameModel::getRolls,
                        gameModel::isRollAllowed,
                        gameModel::isSaveAllowed,

                        gameModel::addPoints,
                        gameModel::clickDice,
                        gameModel::restartGame,
                        gameModel::rollDices,
                        gameModel::selectPointsCategory)
                }
            }
        }
    }
}

@Composable
fun Layout(
    getImages: () -> List<Int>,
    getPoints: () -> List<Int>,
    getPointsCategoriesAvailable: () -> List<PointsCategory>,
    getPointsCategorySelected: () -> String,
    getPointsFromDices: () -> List<Int>,
    getRolls: () -> Int,
    isRollAllowed: () -> Boolean,
    isSaveAllowed: () -> Boolean,
    addPoints: () -> Boolean,
    clickDice: (diceIndex: Int) -> Unit,
    restartGame: () -> Unit,
    rollDices: () -> Unit,
    selectPointsCategory: (category: PointsCategory) -> Unit
    ) {
    var currentDiceImages by remember { mutableStateOf (getImages()) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var rollAllowed by remember { mutableStateOf (isRollAllowed()) }
    var selectedMenuItem by remember { mutableStateOf(getPointsCategorySelected()) }
    var menuItems by remember { mutableStateOf(getPointsCategoriesAvailable()) }
    var pointsFromDices by remember { mutableStateOf(getPointsFromDices()) }
    var pointsList by remember { mutableStateOf(getPoints()) }
    var rolls by remember { mutableStateOf(getRolls()) }
    var menuButtonEnabled by remember { mutableStateOf(getPointsCategoriesAvailable().isNotEmpty()) }
    var savePointsButtonEnabled by remember { mutableStateOf(isSaveAllowed()) }
    val snackState = remember { SnackbarHostState() }
    val snackScope = rememberCoroutineScope()
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp


    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            restartGame()
            currentDiceImages = getImages()
            rollAllowed = isRollAllowed()
            selectedMenuItem = getPointsCategorySelected()
            menuItems = getPointsCategoriesAvailable()
            pointsFromDices = getPointsFromDices()
            pointsList = getPoints()
            rolls = getRolls()
            menuButtonEnabled = getPointsCategoriesAvailable().isNotEmpty()
            savePointsButtonEnabled = isSaveAllowed()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = if (screenWidth < 600) 312.dp else 64.dp, bottom = 24.dp)
    ) {

        /*
            Top row that contains the dice images
         */
        LazyVerticalGrid(
            columns = GridCells.Adaptive(128.dp),
            verticalArrangement = Arrangement.spacedBy(64.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            itemsIndexed(currentDiceImages) {index, item ->
                Image(
                    painter = painterResource(item),
                    contentDescription = stringResource(R.string.dice_image_description),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(48.dp)
                        .clickable {
                            clickDice(index)
                            currentDiceImages = getImages()
                        }
                )
            }
        }

        /*
            Bottom that contains 4 buttons:
            - Select category: Opens a menu with the available points categories left
            - View Result: Opens the activity ViewPointsActivity
                where it is possible to se the current result
                and to restart the game
            - Roll: Roll the dices
            - Add points: Register points depending of the
                current dice values
                and selected points category
         */
        Column {
            Row (
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp)
                    ) {
                Text(if(getPointsCategoriesAvailable().isEmpty()) stringResource(R.string.game_finished_text) else "${stringResource(R.string.category_text)} $selectedMenuItem ${stringResource(R.string.selected_text)}")
            }
            Row {

                Column (
                    Modifier
                        .weight(1F)
                        .padding(6.dp)
                ) {

                    Button (
                        onClick = {
                            dropdownExpanded = !dropdownExpanded
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = menuButtonEnabled
                    ) {
                        Text(stringResource(R.string.select_category_button))
                    }

                    Button (
                        onClick = {
                            val intent = ViewPointsActivity.newIntent(context, pointsList)
                            launcher.launch(intent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringResource(R.string.view_results_button))
                    }
                }

                Column (
                    Modifier
                        .weight(1F)
                        .padding(6.dp)
                ) {
                    Button(
                        onClick = {
                            rollDices()
                            pointsFromDices = getPointsFromDices()
                            currentDiceImages = getImages()
                            rollAllowed = isRollAllowed()
                            rolls = getRolls()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = rollAllowed
                    ) {
                        Text("${stringResource(R.string.roll_button)} ($rolls)")
                    }

                    Button (
                        onClick = {
                            if (addPoints()) {
                                rollAllowed = isRollAllowed()
                                menuItems = getPointsCategoriesAvailable()
                                if (menuItems.isNotEmpty()) {
                                    val message = "${context.resources.getString(R.string.points_added_to_toast)} $selectedMenuItem"
                                    selectedMenuItem = menuItems[0].name
                                    selectPointsCategory(menuItems[0])
                                    snackScope.launch { snackState.showSnackbar(message) }
                                } else {
                                    menuButtonEnabled = false
                                    val message = context.resources.getString(R.string.game_finished_text)
                                    snackScope.launch { snackState.showSnackbar(message) }
                                }
                                pointsList = getPoints()
                                rolls = getRolls()
                                rollAllowed = isRollAllowed()
                                savePointsButtonEnabled = isSaveAllowed()
                                currentDiceImages = getImages()
                                pointsFromDices = getPointsFromDices()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = savePointsButtonEnabled
                    ) {
                        Text(stringResource(R.string.save_points_button))
                    }
                }
            }
        }
    }

    /*
        Menu with the points categories left
     */

    DropdownMenu(
        expanded = dropdownExpanded,
        onDismissRequest = { dropdownExpanded = false }
    ) {
        menuItems.map {
            DropdownMenuItem(
                text = { Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Text(it.name)
                    Text("${pointsFromDices[it.ordinal]}")
                } },
                onClick = {
                    selectPointsCategory(it)
                    dropdownExpanded = false
                    rollAllowed = isRollAllowed()
                    selectedMenuItem = getPointsCategorySelected()
                }
            )
        }
    }

    /*
        Shows messages when points are registered and game finished
     */
    SnackbarHost(hostState = snackState)
}



@Preview(showBackground = true)
@Composable
fun LayoutPreview() {
    ThirtyTheme {

    }
}
