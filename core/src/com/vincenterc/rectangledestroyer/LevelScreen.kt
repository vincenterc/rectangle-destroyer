package com.vincenterc.rectangledestroyer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.ui.Label

class LevelScreen : BaseScreen() {
    private lateinit var paddle: Paddle
    private lateinit var ball: Ball

    var score = 0
    private var balls = 0

    private lateinit var scoreLabel: Label
    private lateinit var ballsLabel: Label
    private lateinit var messageLabel: Label

    private lateinit var bounceSound: Sound
    private lateinit var brickBumpSound: Sound
    private lateinit var wallBumpSound: Sound
    private lateinit var itemAppearSound: Sound
    private lateinit var itemCollectSound: Sound
    private lateinit var backgroundMusic: Music

    override fun initialize() {
//        val background = BaseActor(0f, 0f, mainStage)
//        background.loadTexture("space.png")
//        BaseActor.setWorldBounds(background)
//
//        paddle = Paddle(320f, 32f, mainStage)
//
//        Wall(0f, 0f, 20f, 600f, mainStage)
//        Wall(780f, 0f, 20f, 600f, mainStage)
//
//        // top wall has large height to create area for UI text.
//        Wall(0f, 550f, 800f, 50f, mainStage)

//        val tempBrick = Brick(0f, 0f, mainStage)
//        val brickWidth = tempBrick.width
//        val brickHeight = tempBrick.height
//        tempBrick.remove()

//        val totalRows = 10
//        val totalCols = 10
//        val marginX = (800 - totalCols * brickWidth) / 2
//        val marginY = 600 - totalRows * brickHeight - 120

//        for (rowNum in 0 until totalRows) {
//            for (colNum in 0 until totalCols) {
//                val x = marginX + brickWidth * colNum
//                val y = marginY + brickHeight * rowNum
//                Brick(x, y, mainStage)
//            }
//        }

        var tma = TilemapActor("map.tmx", mainStage)

        for (obj in tma.getTileList("Wall")) {
            var props = obj.properties
            Wall(props.get("x") as Float, props.get("y") as Float,
                    props.get("width") as Float, props.get("height") as Float,
                    mainStage)
        }

        for (obj in tma.getTileList("Brick")) {
            var props = obj.properties
            var b = Brick(props.get("x") as Float, props.get("y") as Float, mainStage)
            b.setSize(props.get("width") as Float, props.get("height") as Float)
            b.setBoundaryRectangle()

            var colorName = props.get("color")
            when (colorName) {
                "red" -> {
                    b.color = Color.RED
                }
                "orange" -> {
                    b.color = Color.ORANGE
                }
                "yellow" -> {
                    b.color = Color.YELLOW
                }
                "green" -> {
                    b.color = Color.GREEN
                }
                "blue" -> {
                    b.color = Color.BLUE
                }
                "purple" -> {
                    b.color = Color.PURPLE
                }
                "white" -> {
                    b.color = Color.WHITE
                }
                "gray" -> {
                    b.color = Color.GRAY
                }
            }
        }

        var startPoint = tma.getRectangleList("start")[0]
        var props = startPoint.properties
        paddle = Paddle(props.get("x") as Float, props.get("y") as Float, mainStage)

        ball = Ball(0f, 0f, mainStage)

        score = 0
        balls = 3

        scoreLabel = Label("Score: $score", BaseGame.labelStyle)
        ballsLabel = Label("Balls: $balls", BaseGame.labelStyle)
        messageLabel = Label("click to start", BaseGame.labelStyle)
        messageLabel.color = Color.CYAN

        uiTable.pad(5f)
        uiTable.add(scoreLabel)
        uiTable.add().expandX()
        uiTable.add(ballsLabel)
        uiTable.row()
        uiTable.add<Label>(messageLabel).colspan(3).expandY()

        bounceSound = Gdx.audio.newSound(Gdx.files.internal("boing.wav"))
        brickBumpSound = Gdx.audio.newSound(Gdx.files.internal("bump.wav"))
        wallBumpSound = Gdx.audio.newSound(Gdx.files.internal("bump-low.wav"))
        itemAppearSound = Gdx.audio.newSound(Gdx.files.internal("swoosh.wav"))
        itemCollectSound = Gdx.audio.newSound(Gdx.files.internal("pop.wav"))

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("Rollin-at-5.mp3"))
        backgroundMusic.isLooping = true
        backgroundMusic.volume = 0.50f
        backgroundMusic.play()
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (ball.isPaused) {
            ball.isPaused = false
            messageLabel.isVisible = false
        }
        return false
    }

    override fun update(dt: Float) {
        // get mouse position and move the paddle
        val mouseX = Gdx.input.x.toFloat()
        paddle.x = mouseX - paddle.width / 2
        paddle.boundToWorld()

        if (ball.isPaused) {
            ball.x = paddle.x + paddle.width / 2 - ball.width / 2
            ball.y = paddle.y + paddle.height / 2 + ball.height / 2
        }

        for (wall in BaseActor.getList(mainStage, "com.vincenterc.rectangledestroyer.Wall")) {
            if (ball.overlaps(wall)) {
                ball.bounceOff(wall)
                wallBumpSound.play()
            }
        }

        if (ball.overlaps(paddle)) {
            val ballCenterX = ball.x + ball.width / 2
            val paddlePercentHit = (ballCenterX - paddle.x) / paddle.width
            val bounceAngle = MathUtils.lerp(150f, 30f, paddlePercentHit)
            ball.motionAngle = bounceAngle
            bounceSound.play()
        }

        for (brick in BaseActor.getList(mainStage, "com.vincenterc.rectangledestroyer.Brick")) {
            if (ball.overlaps(brick)) {
                ball.bounceOff(brick)
                brick.remove()
                score += 100
                scoreLabel.setText("Score: $score")
                val spawnProbability = 20f
                if (MathUtils.random(0, 100) < spawnProbability) {
                    val i = Item(0f, 0f, mainStage)
                    i.centerAtActor(brick)
                    itemAppearSound.play()
                }
                brickBumpSound.play()
            }
        }

        if (BaseActor.count(mainStage, "com.vincenterc.rectangledestroyer.Brick") == 0) {
            messageLabel.setText("you win!")
            messageLabel.color = Color.LIME
            messageLabel.isVisible = true
        }

        if (ball.y < -50 && BaseActor.count(mainStage, "com.vincenterc.rectangledestroyer.Brick") > 0) {
            ball.remove()
            if (balls > 0) {
                balls -= 1
                ballsLabel.setText("Balls: $balls")
                ball = Ball(0f, 0f, mainStage)
                messageLabel.setText("click to start")
                messageLabel.color = Color.CYAN
                messageLabel.isVisible = true
            } else {
                messageLabel.setText("game over")
                messageLabel.color = Color.RED
                messageLabel.isVisible = true
            }
        }

        for (item in BaseActor.getList(mainStage, "com.vincenterc.rectangledestroyer.Item")) {
            if (paddle.overlaps(item)) {
                val realItem = item as Item
                if (realItem.type == Item.Type.PADDLE_EXPAND) paddle.width = paddle.width * 1.25f else if (realItem.type == Item.Type.PADDLE_SHRINK) paddle.width = paddle.width * 0.80f else if (realItem.type == Item.Type.BALL_SPEED_UP) ball.speed = ball.speed * 1.50f else if (realItem.type == Item.Type.BALL_SPEED_DOWN) ball.speed = ball.speed * 0.90f

                // update boundary data
                paddle.setBoundaryRectangle()
                item.remove()
                itemCollectSound.play()
            }
        }
    }
}