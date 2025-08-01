package me.sosedik.delightfulfarming.feature.sugar;

public enum MealTime {

	BREAKFAST(23000, 5000), // 5AM - 11AM
	DINNER(5000, 11000), // 11AM - 5PM
	SUPPER(11000, 17000), // 5PM - 11PM
	NIGHT_SNACK(17000, 23000); // 11PM - 5AM

	private final int timeFrom;
	private final int timeTo;

	MealTime(int timeFrom, int timeTo) {
		this.timeFrom = timeFrom;
		this.timeTo = timeTo;
	}

	public int getTimeFrom() {
		return this.timeFrom;
	}

	public int getTimeTo() {
		return this.timeTo;
	}

	public boolean is(long time) {
		if (this.timeFrom < this.timeTo)
			return time >= this.timeFrom && time < this.timeTo;
		return time >= this.timeFrom || time < this.timeTo;
	}

	public static MealTime getMealTime(long time) {
		for (MealTime mealTime : values()) {
			if (mealTime.is(time))
				return mealTime;
		}
		return DINNER;
	}

}
