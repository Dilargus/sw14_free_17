
package com.adventureislands;

import com.e3roid.drawable.modifier.function.Progressive;

import android.util.FloatMath;

public class CannonBallModifier implements Progressive {

	private static CannonBallModifier instance;
	
	public static CannonBallModifier getInstance() {
		if (instance == null) {
			instance = new CannonBallModifier();
		}
		return instance;
	}
	
	@Override
	public float getProgress(float elapsed, float duration, 
			float minValue, float maxValue) {
		return (FloatMath.sin(elapsed / duration * (float)Math.PI)*(minValue-maxValue) + maxValue); 
	}

} 
