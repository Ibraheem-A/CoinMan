package com.coinman.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;

import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.Random;

public class CoinMan extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] man;
	Texture manDead;
	Rectangle manRectangle;
	BitmapFont font;
	int manState = 0;
	int pause = 0;
	float gravity = 0.2f;
	float velocity = 0f;
	int manY = 0;
	int score = 0;
	int gameState = 0;

	Texture coin;
	int coinCount;
	ArrayList<Integer> coinXs = new ArrayList<>();
	ArrayList<Integer> coinYs = new ArrayList<>();
	ArrayList<Rectangle> coinRectangles = new ArrayList<>();

	Texture bomb;
	int bombCount;
	ArrayList<Integer> bombXs = new ArrayList<>();
	ArrayList<Integer> bombYs = new ArrayList<>();
	ArrayList<Rectangle> bombRectangles = new ArrayList<>();

	Random random;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		man = new Texture[4];
		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");
		manDead = new Texture("dizzy-1.png");

		manY = Gdx.graphics.getHeight() / 2;

		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");

		random = new Random();
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
	}

	public void makeCoin () {
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		coinYs.add((int)height);
		coinXs.add(Gdx.graphics.getHeight());
	}

	public void makeBomb () {
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		bombYs.add((int)height);
		bombXs.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		// Game State
		if (gameState == 1) {
			// Game is live
			startDisplayingBombs();
			startDisplayingCoins();
			onScreenTouch();
			startManRunning();
			onManReachBottomOfScreen();

			velocity += gravity;
			manY -= velocity;
		} else if (gameState == 0) {
			// Waiting to start
			if (Gdx.input.justTouched()) {
				gameState = 1;
			}
		} else if (gameState == 2) {
			// Game over
			if (Gdx.input.justTouched()) {
				gameState = 1;
				resetGameParameters();
				batch.draw(manDead, manRectangle.getX(), manRectangle.getY());
			}
		}

		if (gameState == 2) {
			batch.draw(manDead, manRectangle.getX(), manRectangle.getY());
		} else {
			batch.draw(man[manState], (float) (Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2), (float) (manY - man[manState].getHeight() / 2));
		}
		manRectangle = new Rectangle((float)Gdx.graphics.getWidth()/2 - (float)(man[manState].getWidth()/2), (float)(manY - man[manState].getHeight()/2), man[manState].getWidth(), man[manState].getHeight());

		for(int i=0; i < coinRectangles.size(); i++) {
			if (Intersector.overlaps(manRectangle, coinRectangles.get(i))) {
				Gdx.app.log("Coin!", "Collision!!!");
				score++;
				coinRectangles.remove(i);
				coinXs.remove(i);
				coinYs.remove(i);
			}
		}
		for(int i=0; i < bombRectangles.size(); i++) {
			if (Intersector.overlaps(manRectangle, bombRectangles.get(i))) {
				Gdx.app.log("Bomb!", "Collision!!!");
				gameState = 2;
				bombRectangles.remove(i);
				bombXs.remove(i);
				bombYs.remove(i);
			}
		}

		font.draw(batch, String.valueOf(score), 100, 200);

		batch.end();
	}

	private void resetGameParameters() {
		manY = Gdx.graphics.getHeight()/2;
		score = 0;
		velocity = 0;
		coinXs.clear();
		coinYs.clear();
		coinRectangles.clear();
		coinCount = 0;
		bombXs.clear();
		bombYs.clear();
		bombRectangles.clear();
		bombCount = 0;
	}

	private void onManReachBottomOfScreen() {
		// when coinMan reaches bottom
		if (manY <= man[manState].getHeight()/2){
			if (Gdx.input.justTouched()) {
				gravity = 0.2f;
				velocity = -10;
				manY = man[manState].getHeight()/2;
			} else {
				manY = man[manState].getHeight() / 2;
				velocity = 0;
				gravity = 0;
			}
		}
	}

	private void startManRunning() {
		if (pause < 8) {
			pause++;
		} else {
			pause = 0;
			if (manState < 3) {
				manState++;
			} else {
				manState = 0;
			}
		}
	}

	private void onScreenTouch() {
		// On screen touch
		if (Gdx.input.justTouched()) {
			velocity = -10;
		}
	}

	private void startDisplayingCoins() {
		// Coins
		if (coinCount < 100) {
			coinCount++;
		} else {
			coinCount = 0;
			makeCoin();
		}

		coinRectangles.clear();
		for (int i=0; i<coinXs.size(); i++){
			batch.draw(coin, coinXs.get(i), coinYs.get(i));
			coinXs.set(i, coinXs.get(i) - 4);
			coinRectangles.add(new Rectangle(coinXs.get(i), coinYs.get(i), coin.getWidth(), coin.getHeight()));
		}
	}

	private void startDisplayingBombs() {
		// Bombs
		if (bombCount < 500) {
			bombCount ++;
		} else {
			bombCount = 0;
			makeBomb();
		}

		bombRectangles.clear();
		for (int i=0; i < bombXs.size(); i++) {
			batch.draw(bomb, bombXs.get(i), bombYs.get(i));
			bombXs.set(i, bombXs.get(i) - 6);
			bombRectangles.add(new Rectangle(bombXs.get(i), bombYs.get(i), bomb.getWidth(), bomb.getHeight()));
		}
	}

	@Override
	public void dispose () {
		batch.dispose();
	}
}
