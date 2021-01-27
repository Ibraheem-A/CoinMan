package com.coinman.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;

public class CoinMan extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] man;
	Rectangle manRectangle;
	int manState = 0;
	int pause = 0;
	float gravity = 0.2f;
	float velocity = 0f;
	int manY = 0;

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

		manY = Gdx.graphics.getHeight() / 2;

		coin = new Texture("coin.png");
		random = new Random();

		bomb = new Texture("bomb.png");
		manRectangle = new Rectangle();
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

		// On screen touch
		if (Gdx.input.justTouched()) {
			velocity = -10;
		}

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


		velocity += gravity;
		manY -= velocity;

		batch.draw(man[manState], (float) (Gdx.graphics.getWidth()/2 - man[manState].getWidth() / 2), (float) (manY - man[manState].getHeight() / 2));
//		manRectangle.set(Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY - man[manState].getHeight()/2)
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
