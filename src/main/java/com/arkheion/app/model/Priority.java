package com.arkheion.app.model;

public enum Priority {
	GENERAL(1),LEVEL1(2),LEVEL2(3),LEVEL3(4),LEVEL4(5),LEVEL5(6),TEST(7);
	
	private int id;

	private Priority(int id) {
		this.id = id;
	}

	public static Priority getById(int id) {
		for (Priority e : values()) {
			if (e.id == id)
				return e;
		}

		return null;
	}

	public int getId() {
		return id;
	}
}
