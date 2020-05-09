import minesweeper.MineSweeper
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.*
import kotlin.test.*

object SampleSpec : Spek({
    val n = 9
    val ms = MineSweeper
    beforeEachGroup { ms.makeBoard(n) }
    Feature("MineSweeper") {
        Scenario("game end") {
            When("set two mines") {
                ms.setMine(1, 1)
                ms.setMine(1, 2)
            }
            Then("game not finished") {
                assertFalse(ms.userWin())
            }
            When("mark a mine") {
                ms.mark(1, 1)
            }
            Then("game not finished") {
                assertFalse(ms.userWin())
            }
            When("mark a mine") {
                ms.mark(1, 2)
            }
            Then("finished") {
                assertTrue(ms.userWin())
            }
        }
        Scenario("adjacent cells") {
            lateinit var cells: List<MineSweeper.Cell>
            When("a cell exists at corner") {
                cells = ms.getCell(1, 1).adjacentCells()
            }
            Then("it has three neighbors") {
                assertEquals(cells.size, 3)
            }
            When("a cell exists at border") {
                cells = ms.getCell(1, 5).adjacentCells()
            }
            Then("it has five neighbors") {
                assertEquals(cells.size, 5)
            }
            When("a cell exists elsewhere") {
                cells = ms.getCell(5, 5).adjacentCells()
            }
            Then("it has eight neighbors") {
                assertEquals(cells.size, 8)
            }
        }
        Scenario("first explore") {
            When("set a mine, and explore it") {
                ms.setMine(1, 1)
                ms.explore(1, 1)
            }
            Then("not lose") {
                assertFalse(ms.userLose())
            }
        }
        Scenario("user lose") {
            When("explore a mine") {
                ms.setMine(1, 1)
                ms.explore(2, 2)
                ms.explore(1, 1)
            }
            Then("lose") {
                assertTrue(ms.userLose())
            }
        }
        Scenario("explore all cells leaving mines to win") {
            When("explore cells next to a mine") {
                ms.setMine(1, 1)
                ms.explore(1, 2)
                ms.explore(2, 1)
                ms.explore(2, 2)
            }
            Then("not win") {
                assertFalse(ms.userWin())
            }
            When("explore others") {
                ms.explore(3, 3)
            }
            Then("win") {
                assertTrue(ms.userWin())
            }
        }
        Scenario("mark all mines to win") {
            When("mark one of two mines") {
                ms.setMine(1, 1)
                ms.setMine(2, 2)
                ms.mark(1, 1)
            }
            Then("not win") {
                assertFalse(ms.userWin())
            }
            When("mark the mine") {
                ms.mark(2, 2)
            }
            Then("win") {
                assertTrue(ms.userWin())
            }
        }
        // TODO: 余分につけてたマークを消して終了、の場合も書く
    }
})