package com.vincenterc.rectangledestroyer

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage

class TilemapActor(filename: String, theStage: Stage) : Actor() {
    companion object {
        var windowWidth = 800
        var windowHeight = 600
    }

    private var tiledMap = TmxMapLoader().load(filename)
    private var tiledCamera: OrthographicCamera
    private var tiledMapRenderer: OrthoCachedTiledMapRenderer

    init {
        var tileWidth = tiledMap.properties.get("tilewidth") as Int
        var tileHeight = tiledMap.properties.get("tileheight") as Int
        var numTilesHorizontal = tiledMap.properties.get("width") as Int
        var numTilesVertical = tiledMap.properties.get("height") as Int
        var mapWidth = tileWidth * numTilesHorizontal
        var mapHeight = tileHeight * numTilesVertical

        BaseActor.setWorldBounds(mapWidth.toFloat(), mapHeight.toFloat())

        tiledMapRenderer = OrthoCachedTiledMapRenderer(tiledMap)
        tiledMapRenderer.setBlending(true)
        tiledCamera = OrthographicCamera()
        tiledCamera.setToOrtho(false, windowWidth.toFloat(), windowHeight.toFloat())
        tiledCamera.update()

        theStage.addActor(this)
    }

    fun getRectangleList(propertyName: String): MutableList<MapObject> {
        var list = mutableListOf<MapObject>()

        for (layer in tiledMap.layers) {
            for (obj in layer.objects) {
                if (obj !is RectangleMapObject) continue

                var props = obj.properties

                if (props.containsKey("name")
                        && (props.get("name") == propertyName))
                    list.add(obj)
            }
        }
        return list
    }

    fun getTileList(propertyName: String): MutableList<MapObject> {
        var list = mutableListOf<MapObject>()

        for (layer in tiledMap.layers) {
            for (obj in layer.objects) {
                if (obj !is TiledMapTileMapObject) continue

                var props = obj.properties

                var t = obj.tile
                var defaultProps = t.properties

                if (defaultProps.containsKey("name")
                        && (defaultProps.get("name") == propertyName))
                    list.add(obj)

                var propertyKeys = defaultProps.keys

                while (propertyKeys.hasNext()) {
                    var key = propertyKeys.next()

                    if (props.containsKey(key)) {
                        continue
                    } else {
                        var value = defaultProps.get(key)
                        props.put(key, value)
                    }
                }
            }
        }
        return list
    }

    override fun act(dt: Float) {
        super.act(dt)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        var mainCamera = stage.camera
        tiledCamera.position.x = mainCamera.position.x
        tiledCamera.position.y = mainCamera.position.y
        tiledCamera.update()
        tiledMapRenderer.setView(tiledCamera)

        batch.end()
        tiledMapRenderer.render()
        batch.begin()
    }
}