package minesweeper

import java.util.*
import kotlin.math.abs
import kotlin.math.max

object MineSweeper {
    var n: Int = 0
    lateinit var coords: List<Pair<Int, Int>>
    lateinit var cells: List<Cell>
    var exploreCount = 0

    fun makeBoard(nn: Int) {
        n = nn
        coords = (1..n).flatMap { i -> (1..n).map { j -> Pair(i, j) } }
        cells = coords.map { (i, j) ->
            Cell(i, j, checked = false, mine = false, explored = false)
        }
    }

    fun setMine(i: Int, j: Int) {
        cells.find { c -> c.i == i && c.j == j }!!.mine = true
    }

    fun setMines(mineNum: Int) {
        coords.shuffled()
                .take(mineNum)
                .forEach { (i, j) -> setMine(i, j) }
    }

    fun printBoard(showMines: Boolean = false) {
        println(" │" + (1..n).joinToString("") + "│")
        val line = "—│" + "—".repeat(n) + "│"
        println(line)
        for (i in 1..n) {
            val row = cells.filter { c -> c.i == i }
            println("$i│" + row.joinToString("") { c -> c.str(showMines) } + "│")
        }
        println(line)
    }

    fun getCell(i: Int, j: Int) = cells.find { c -> c.i == i && c.j == j }!!

    fun userWin(): Boolean {
        val allMineChecked = cells.all { c ->
            (c.mine && c.checked) || (!c.mine && !c.checked)
        }
        val allEmptyCellExplored = cells.filter { c -> !c.mine }.all { c -> c.explored }
        return allMineChecked || allEmptyCellExplored
    }

    fun userLose(): Boolean {
        return cells.any { c -> c.mine && c.explored }
    }

    fun hasExplored(i: Int, j: Int) = getCell(i, j).explored

    fun mark(i: Int, j: Int) {
        val c = getCell(i, j)
        c.checked = !c.checked
    }

    fun explore(i: Int, j: Int) {
        val c = getCell(i, j)
        if (exploreCount == 0 && c.mine) {
            val other = cells.find { c2 -> !c2.mine }!!
            c.mine = false
            other.mine = true
        }
        exploreCount += 1
        c.explored = true
        c.checked = false
        if (c.mine) return
        if (c.countAdjacentMines() == 0) {
            val adjs = c.adjacentCells()
            adjs.filter { c2 -> !c2.explored && !c2.mine }.forEach { c2 -> explore(c2.i, c2.j) }
        }
    }

    class Cell(val i: Int, val j: Int, var checked: Boolean, var mine: Boolean, var explored: Boolean) {
        fun adjacentCells(): List<Cell> {
            return cells.filter { c ->
                max(abs(c.i - i), abs(c.j - j)) == 1
            }
        }

        fun countAdjacentMines(): Int {
            return adjacentCells().count { c -> c.mine }
        }

        fun str(showMines: Boolean = false): String {
            if (checked) return "*"
            if (mine && showMines) return "X"
            if (mine) return "."
            if (!explored) return "."
            val cnt = countAdjacentMines()
            if (cnt == 0) return "/"
            else return cnt.toString()
        }
    }
}

fun main() {
    val scanner = Scanner(System.`in`)
    val n = 9
    val ms = MineSweeper
    ms.makeBoard(n)
    println("How many mines do you want on the field? ")
    var k: Int
    do {
        print("The number of mines should be between 1 and ${n * n}. > ")
        k = scanner.nextInt()
    } while (k <= 0 || k > n * n)
    ms.setMines(k)
    ms.printBoard()

    var firstCommand = true
    while (!ms.userWin() && !ms.userLose()) {
        print("Set/unset mines marks or claim a cell as free. > ")
        if (firstCommand) {
            print("\nExamples: \n3 4 mine\n9 2 free\nLet's start! > ")
            firstCommand = false
        }
        val x = scanner.nextInt()
        val y = scanner.nextInt()
        val command = scanner.next()
        if (x < 1 || x > n || y < 1 || y > n) {
            println("Each coordinate should be between 1 and $n.")
            continue
        }
        if (command == "mine") {
            ms.mark(y, x)
            if (!ms.userWin()) ms.printBoard()
        } else if (command == "free") {
            if (ms.hasExplored(y, x)) {
                println("Position ($x, $y) has already explored.")
                continue
            }
            ms.explore(y, x)
            if (!ms.userLose()) ms.printBoard()
        } else {
            println("Invalid command. Command should be 'mine' or 'free'.")
        }
    }

    if (ms.userWin()) {
        ms.printBoard()
        println("Congratulations! You found all mines!")
    } else {
        ms.printBoard(showMines = true)
        println("You stepped on a mine and failed!")
    }
}