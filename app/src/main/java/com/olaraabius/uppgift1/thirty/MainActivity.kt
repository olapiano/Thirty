package com.olaraabius.uppgift1.thirty

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import com.olaraabius.uppgift1.thirty.controller.ThirtyGame
import com.olaraabius.uppgift1.thirty.ui.theme.ThirtyTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

/**
 * The Main View of the dice game Thirty
 *
 * @author Ola Råbius Magnusson
 * @since 2023-06-26
 */

// private const val TAG = "MainActivity"

/** Instance of the controller class */
private var game = ThirtyGame()
/** Data updated from the ViewPointsActivity */
private var restartGame = false

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /** Data received from ViewPointsActivity user clicked Restart Game */
        restartGame = intent.extras?.getBoolean("restart") == true
        /** Restarts game if restartGame = true */
        if (restartGame) {
            game = ThirtyGame()
            restartGame = false
        }

        setContent {
            ThirtyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Layout()
                }
            }
        }
    }
}

@Composable
fun Layout() {
    var currentDiceImages by remember { mutableStateOf (game.getImages()) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var isRollAllowed by remember { mutableStateOf (game.isRollAllowed()) }
    var selectedMenuItem by remember { mutableStateOf(game.getPointsCategorySelected()) }
    var menuItems by remember { mutableStateOf(game.getPointsCategoriesAvailable()) }
    var pointsFromDices by remember { mutableStateOf(game.getPointsFromDices()) }
    var pointsList by remember { mutableStateOf(game.getPoints()) }
    var rolls by remember { mutableStateOf(game.getRolls()) }
    var menuButtonEnabled by remember { mutableStateOf(game.getPointsCategoriesAvailable().isNotEmpty()) }
    var savePointsButtonEnabled by remember { mutableStateOf(game.isSaveAllowed()) }
    val context = LocalContext.current
    val snackState = remember { SnackbarHostState() }
    val snackScope = rememberCoroutineScope()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 64.dp, bottom = 24.dp)
    ) {

        /*
            Top row that contains the dice images
         */
        Row {
            List(currentDiceImages.size) {
                index ->
                    Image(
                        painter = painterResource(currentDiceImages[index]),
                        contentDescription = stringResource(R.string.dice_image_description),
                        modifier = Modifier
                            .padding(10.dp)
                            .clickable {
                                game.clickDice(index)
                                currentDiceImages = game.getImages()
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
                Text(if(game.getPointsCategoriesAvailable().isEmpty()) stringResource(R.string.game_finished_text) else "${stringResource(R.string.category_text)} $selectedMenuItem ${stringResource(R.string.selected_text)}")
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
                            val intent = Intent(context, ViewPointsActivity::class.java)
                            val points = pointsList as ArrayList<Int>
                            intent.putIntegerArrayListExtra("points", points)
                            context.startActivity(intent)
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
                            game.rollDices()
                            pointsFromDices = game.getPointsFromDices()
                            currentDiceImages = game.getImages()
                            isRollAllowed = game.isRollAllowed()
                            rolls = game.getRolls()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isRollAllowed
                    ) {
                        Text("${stringResource(R.string.roll_button)} ($rolls)")
                    }

                    Button (
                        onClick = {
                            if (game.addPoints()) {
                                isRollAllowed = game.isRollAllowed()
                                menuItems = game.getPointsCategoriesAvailable()
                                if (menuItems.isNotEmpty()) {
                                    val message = "${context.resources.getString(R.string.points_added_to_toast)} $selectedMenuItem"
                                    selectedMenuItem = menuItems[0].name
                                    game.selectPointsCategory(menuItems[0])
                                    snackScope.launch { snackState.showSnackbar(message) }
                                } else {
                                    menuButtonEnabled = false
                                    val message = context.resources.getString(R.string.game_finished_text)
                                    snackScope.launch { snackState.showSnackbar(message) }
                                }
                                pointsList = game.getPoints()
                                rolls = game.getRolls()
                                isRollAllowed = game.isRollAllowed()
                                savePointsButtonEnabled = game.isSaveAllowed()
                                currentDiceImages = game.getImages()
                                pointsFromDices = game.getPointsFromDices()
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
                    game.selectPointsCategory(it)
                    dropdownExpanded = false
                    isRollAllowed = game.isRollAllowed()
                    selectedMenuItem = game.getPointsCategorySelected()
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
        Layout()
    }
}