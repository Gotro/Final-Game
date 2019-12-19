package edu.utc.game.Math;
import java.lang.Math;

public class Vector2f {
	public float x;
	public float y;

	public Vector2f(float x, float y)
	{
		this.x = x;
		this.y = y;
	}

	/* Copy constructor to make deep copies */
	public Vector2f(Vector2f original) {
		this.x = original.x;
		this.y = original.y;
	}

	/* Find the projection of one vector onto another, many uses! */
	public float dotProduct(Vector2f other) {
		float result = 0;
		result += this.x * other.x;
		result += this.y * other.y;
		return result;
	}

	/* Find the magnitude of a vector */
	public float magnitude() {
		float magnitudeSquared = this.dotProduct(this);
		return (float) Math.sqrt(magnitudeSquared);
	}

	/* Normalize the vector if you are concerned with direction */
	public void normalize() {
		float magnitude = this.magnitude();
		this.x /= magnitude;
		this.y /= magnitude;
	}

	/* Returns the sum vector of these two vectors */
	public Vector2f add(Vector2f other) {
		return new Vector2f(this.x + other.x, this.y + other.y);
	}

	/* Returns the difference vector of these two vectors */
	public Vector2f subtract(Vector2f other) {
		return new Vector2f(this.x - other.x, this.y - other.y);
	}
}
