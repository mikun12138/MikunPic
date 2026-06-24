package me.mikun.mikunpic.dto.awesome

suspend fun <T> dfs(
    root: T,
    finCheck: (T) -> Boolean,
    grow: (T) -> List<T>,
    checkVisited: Boolean = true,
    finBlock: suspend (T, List<T>) -> Unit,
) {
    val visited = mutableSetOf<T>()
    val stack = ArrayDeque<Pair<T, List<T>>>().apply {
        addLast(root to emptyList())
    }

    while (stack.isNotEmpty()) {
        val (last, path) = stack.removeLast()
        if (checkVisited) {
            if (!visited.add(last)) continue
        }

        if (finCheck(last)) {
            finBlock(last, path)
        } else {
            stack.addAll(
                grow(last).asReversed().map {
                    it to path + it
                },
            )
        }
    }
}

suspend fun <T> dfs(
    root: T,
    finCheck: (T) -> Boolean,
    grow: (T) -> List<T>,
    checkVisited: Boolean = true,
    finBlock: suspend (T) -> Unit,
) {
    val visited = mutableSetOf<T>()
    val stack = ArrayDeque<T>().apply {
        addLast(root)
    }

    while (stack.isNotEmpty()) {
        val last = stack.removeLast()
        if (checkVisited) {
            if (!visited.add(last)) continue
        }

        if (finCheck(last)) {
            finBlock(last)
        } else {
            stack.addAll(
                grow(last).asReversed(),
            )
        }
    }
}
